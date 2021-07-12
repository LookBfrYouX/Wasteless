<template>
  <div class="container my-3">
    <div class="text-center">
      <h1>
        Create new sales listing
      </h1>
    </div>
    <form
        @submit.prevent="addListing"
    >
      <div class="row">
        <div class="form-group required col-md-6">
          <label for="product">Find product</label>
          <v-autocomplete
            solo
            dense
            item-text="name"
            item-value="id"
            :items="products"
            v-model="selectedProductId"
          >
        </v-autocomplete>
        </div>
        <div class="form-group required col-md-6">
          <label for="inventory">Inventory item to list</label>
          <v-autocomplete
            solo
            dense
            item-text="id"
            item-value="id"
            :items="filteredInventory"
            v-model="selectedInventoryItem"
          >
          <template v-slot:selection="{item}">
            {{item.product.name}}, {{item.quantity}}, {{item.expires}}
          </template>
          <template v-slot:item="{ item }">
            <v-list-item-content>
              <v-list-item-title>Name:{{item.product.name}}</v-list-item-title>
              <v-list-item-subtitle>Quantity:{{item.quantity}}</v-list-item-subtitle>
              <v-list-item-subtitle>Expires:{{item.expires}}</v-list-item-subtitle>
            </v-list-item-content>
          </template>
        </v-autocomplete>
        </div>
        <div class="form-group required col-md-6">
          <label for="quantity">Quantity</label>
          <input
              id="quantity"
              v-model="quantity"
              :disabled="selectedInventoryItem == null"
              :max="maxQuantity"
              class="form-control"
              min="1"
              name="quantity"
              required
              type="number">
          <small v-if="selectedInventoryItem"
                 class="text-muted">
            You can list {{ selectedInventoryItem.quantityRemaining }} items out of
            {{ selectedInventoryItem.quantity }} in the inventory.
          </small>
        </div>
        <div class="form-group required col-md-6">
          <label for="price">Total price<span v-if="currency !== null"> in {{
              currency.code
            }}</span></label>
          <input
              id="price"
              v-model="price"
              :disabled="quantity === null"
              class="form-control"
              min="0.01"
              name="price"
              required
              step="0.01"
              type="number"
          >
        </div>
        <div class="form-group col-md-6">
          <label for="moreInfo">More information</label>
          <textarea
              id="moreInfo"
              v-model="moreInfo"
              :disabled="price === 0"
              class="form-control"
              maxlength="200"
              name="moreInfo"
              placeholder="Extra information about the price"
              rows="4"
              type="text"
          />
        </div>
        <div class="form-group col-md-6">
          <label for="closeDate">Close this sale on</label>
          <input
              id="closeDate"
              v-model="closeDate"
              :disabled="selectedInventoryItem == null || maxQuantity <= 0"
              :max="expiryDate"
              :min="todayDate"
              class="form-control"
              name="expires"
              type="date"
          />
          <label for="closeTime"></label>
          <input id="closeTime"
                 v-model="closeTime"
                 :disabled="selectedInventoryItem == null || maxQuantity <= 0 || closeDate == null"
                 class="form-control"
                 name="closeTime"
                 type="time"
          >
          <small v-if="selectedInventoryItem && maxQuantity > 0"
                 class="text-muted">
            Closing date and time will be set to 23:59 on the expiry date
            ({{ $helper.isoToDateString(expiryDate) }}) if not specified.
          </small>
        </div>
      </div>
      <div class="row">
        <div class="col text-center">
          <input class="btn btn-primary btn-block" type="submit" value="Add"/>
        </div>
      </div>
      <div v-if="errorMessage" class="row mt-2">
        <div class="col">
          <p class="alert alert-warning">{{ errorMessage }}</p>
        </div>
      </div>
    </form>
    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.createForm"
        :show="apiErrorMessage !== null"
        title="Error fetching product, inventory or listings data"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<script>

import {Api} from "@/Api";
import ErrorModal from "@/components/ErrorModal";

export default {
  name: "CreateListing",
  components: {
    ErrorModal
  },
  data() {
    return {
      // Array of inventory items
      inventory: [],
      selectedProductId: null,
      selectedInventoryItem: null,
      quantity: null,
      price: this.defaultPrice,
      currency: null,
      moreInfo: "",
      closeDate: null,
      closeTime: null,
      todayDate: null,
      apiErrorMessage: null,
      errorMessage: null,
      products:[],
      filteredInventory:[]
    }
  },

  props: {
    businessId: {
      required: false,
      type: Number,
    }
  },

  beforeMount: async function () {
    await this.createForm();
    await this.getProducts();
  },

  watch: {
    /**
     * When users select a different inventory item from the dropdown, resets maximum quantity and expiry date.
     * When users select a different product, selectedInventoryItem is also set to null and quantity is set to 0.
     */
    selectedInventoryItem: function (selectedInventoryItem) {
      if (selectedInventoryItem == null) {
        this.quantity = null;
      } else {
        this.quantity = selectedInventoryItem.quantityRemaining;
      }
    },

    /**
     * When users select a different product in the dropdown, resets selectedInventoryItem and quantity to initial value.
     */
    selectedProductId: function () {
      this.filterInventory()
      this.selectedInventoryItem = null;
    },

    /**
     * When users change listing quantity, recomputes defaultPrice on price field.
     */
    quantity: function () {
      this.price = this.defaultPrice;
    }
  },

  methods: {
    /**
     * Filters in inventory item of a product which users chose in dropdown.
     * @return Array of inventory items if inventory and selectedProductId is non-null. Returns empty array otherwise.
     */
    async filterInventory() {
      if (this.inventory == [] || this.selectedProductId == null) {
        return [];
      }
      this.filteredInventory = this.inventory.filter(
          inventoryItem => inventoryItem.product.id === this.selectedProductId);
    },

    /**
     * API requests other computation at the time of form creation.
     * Called when the page is loaded or user hit retry button on the error modal.
     */
    async createForm() {
      let success = (await this.getInventory());
      if (!success) {
        return;
      }
      await this.getAvailableInventoryItem();
      this.setTodayDate();
      await this.getCurrency();
    },

    async getProducts() {
      let products = [];
      this.inventory.forEach(inventoryItem => {
        const product = inventoryItem.product;
        products.push(product)
      });
      this.products=products

    },

    /**
     * Gets inventory of the business and sets the array as inventory data.
     * @return boolean True when business inventory is successfully retrieved,
     */
    async getInventory() {
      try {
        this.inventory = (await Api.getBusinessInventory(this.businessId)).data;
        return true;
      } catch (err) {
        this.apiErrorMessage = err.userFacingErrorMessage;
        return false;
      }
    },

    /**
     * Gets sales listings of the business.
     * @returns Listings array if successful.
     */
    async getListings() {
      try {
        return (await Api.getBusinessListings(this.businessId)).data;
      } catch (err) {
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    /**
     * Maps inventory and adds quantityRemaining properties to each item which is set to the quantity of inventory item.
     * It checks the listings if there are inventory items already listed.
     * Subtracts quantityRemaining of inventory item if there are listings of it and updates inventory.
     */
    async getAvailableInventoryItem() {
      let inventory = this.inventory.map(inventoryItem => {
        inventoryItem.quantityRemaining = inventoryItem.quantity;
        return inventoryItem;
      });
      const listings = await this.getListings();
      if (listings !== undefined) {
        listings.forEach(listing => {
          const inventoryItem = inventory.find(({id}) => id === listing.inventoryItem.id);
          inventoryItem.quantityRemaining -= listing.quantity;
        });
        this.inventory = inventory;

      }
    },

    /**
     * Sets today's date to restrict input in date picker.
     */
    setTodayDate() {
      const today = new Date();
      let dd = today.getDate();
      let mm = today.getMonth() + 1;
      let yyyy = today.getFullYear();
      if (dd < 10) {
        dd = '0' + dd
      }
      if (mm < 10) {
        mm = '0' + mm
      }
      this.todayDate = yyyy + '-' + mm + '-' + dd;
    },

    /**
     * Gets currency of the business.
     * @return Currency object
     */
    async getCurrency() {
      this.currency = await this.$helper.tryGetCurrencyForBusiness(this.businessId,
          this.$stateStore);
    },

    /**
     * Formats local date string and time string into one ISO datetime string.
     * @param closeDate Date string in "YYYY-MM-DD" format
     * @param closeTime Time string in "hh:mm" 24 format
     * @return {string|null} Datetime string in "YYYY-MM-DDThh:mm:00Z" format. When closeTime is not specified, sets the time to 23:59. When both closeDate and closeTime is null, returns null.
     */
    toFormattedISOString(closeDate, closeTime) {
      let hour;
      let minute;
      if (closeDate) {
        const date = closeDate.split("-");
        const year = parseInt(date[0], 10);
        const month = parseInt(date[1], 10) - 1;
        const day = parseInt(date[2], 10);
        if (closeTime) {
          const time = closeTime.split(":");
          hour = parseInt(time[0], 10);
          minute = parseInt(time[1], 10);
        } else {
          hour = "23";
          minute = "59";
        }
        return new Date(year, month, day, hour, minute).toISOString();
      } else {
        return null;
      }
    },

    /**
     * Redirects user to listings page when a new listing is created successfully.
     */
    goToListings() {
      this.$router.push({
        name: 'BusinessListings',
        params: {
          businessId: this.businessId
        }
      });
    },
    /**
     * Called when form submit button is clicked. Calls Api to add listing to the business.
     */
    async addListing() {
      let closes = this.toFormattedISOString(this.closeDate, this.closeTime);
      const listing = {
        "inventoryItemId": this.selectedInventoryItem.id,
        "quantity": this.quantityAsNumber,
        "price": parseFloat(this.price),
        "moreInfo": this.moreInfo,
        "closes": closes
      }

      try {
        await Api.addBusinessListings(this.businessId, listing);
        this.goToListings();
      } catch (err) {
        this.errorMessage = err.userFacingErrorMessage;
      }
    }
  },

  computed: {
    /**
     * Sets default price on price input field once a user select inventory item and set a valid quantity.
     * When user chose whole quantity in the inventory, returns totalPrice of the inventory item if it's non-null.
     * When user chose partial quantity in the inventory, returns quantity times pricePerItem if pricePerItem is non-null.
     * @return {string} Price string in "X.XX" format.
     */
    defaultPrice() {
      if (this.selectedInventoryItem == null ||
          !Number.isInteger(this.quantityAsNumber) ||
          this.quantityAsNumber <= 0 ||
          this.quantityAsNumber > this.maxQuantity ||
          this.selectedInventoryItem.totalPrice == null ||
          this.selectedInventoryItem.pricePerItem == null
      ) {
        return "0.00";
      }

      const quantityInInventory = this.selectedInventoryItem.quantity;
      if (this.quantityAsNumber === quantityInInventory) {
        return (this.selectedInventoryItem.totalPrice).toFixed(2);
      } else {
        return (this.quantityAsNumber * this.selectedInventoryItem.pricePerItem).toFixed(2);
      }
    },

    /**
     * Returns maximum quantity available for listing in the selected inventory item.
     * @return Number if inventory is selected, null otherwise.
     */
    maxQuantity() {
      if (this.selectedInventoryItem == null) {
        return null;
      }
      return this.selectedInventoryItem.quantityRemaining;
    },

    /**
     * Returns string of expiry date of selected inventory item.
     * @return Date string in YYYY-MM-DD format if inventory item is selected, null otherwise.
     */
    expiryDate() {
      if (this.selectedInventoryItem == null) {
        return null;
      }
      return this.selectedInventoryItem.expires;
    },

    /**
     * Quantity is bound to input element so it is a string
     * @return {number} quantity as number, possibly NaN
     */
    quantityAsNumber() {
      return parseInt(this.quantity, 10);
    }
  }
}
</script>

<style scoped>

</style>
