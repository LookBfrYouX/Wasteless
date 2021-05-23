<template>
  <div class="container">
    <div class="text-center">
      <h1>
        Create new sales listing
      </h1>
    </div>
    <form
        v-on:submit.prevent="addListing"
        >
      <div class="row">
        <div class="form-group required col-md-6">
          <label for="product">Find product</label>
          <select
              id="product"
              class="form-control"
              required
              v-model="selectedProductId"
              >
            <option disabled
                    :value="null"
                    selected> -- Select product to list -- </option>
            <option v-for="product in getProducts"
                    :key="product.id"
                    v-bind:value="product.id"
            >
              {{ product.name }}
            </option>
          </select>
        </div>
        <div class="form-group required col-md-6">
          <label for="inventory">Inventory to List</label>
          <select
              id="inventory"
              class="form-control"
              required
              v-model="selectedInventoryItem"
              :disabled="filteredInventory.length === 0"
              >
            <option disabled
                    :value="null"
                    selected> -- Select inventory to list -- </option>
            <option v-for="inventoryItem in filteredInventory"
                    :key="inventoryItem.id"
                    :value="inventoryItem"
                    :disabled="inventoryItem.quantityRemaining === 0"
                    >
              Expires at {{ $helper.isoToDateString(inventoryItem.expires) }} (ID: {{ inventoryItem.id }}, {{ inventoryItem.quantityRemaining}}/{{ inventoryItem.quantity }} unlisted)
            </option>
          </select>
        </div>
        <div class="form-group required col-md-6">
          <label for="quantity">Quantity</label>
          <input
              id="quantity"
              v-model="quantity"
              :disabled="selectedInventoryItem == null || maxQuantity <= 0"
              :max="maxQuantity"
              class="form-control"
              name="quantity"
              required
              type="number"
              min="1">
          <small v-if="selectedInventoryItem"
               class="text-muted">
            You can list {{selectedInventoryItem.quantityRemaining}} items out of {{selectedInventoryItem.quantity}} in the inventory.
          </small>
        </div>
        <div class="form-group required col-md-6">
          <label for="price">Price<span v-if="currency !== null"> in {{ currency.code }}</span></label>
            <input
                id="price"
                v-model="price"
                :disabled="quantity === 0"
                type="number"
                step="0.01"
                min="0.01"
                class="form-control"
                name="price"
                required
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
              rows="4"
              name="moreInfo"
              placeholder="Extra information about the price"
              type="text"
          />
        </div>
        <div class="col-md-6">
          <label for="closeDate">Close this sale on</label>
          <input
              id="closeDate"
              v-model="closeDate"
              :disabled="selectedInventoryItem == null || maxQuantity <= 0"
              class="form-control"
              :min="todayDate"
              :max="expiryDate"
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
            Closing date and time will be set to 23:59 on the expiry date ({{ $helper.isoToDateString(expiryDate) }}) if not specified.
          </small>
        </div>
      </div>
      <div class="d-flex justify-content-end">
        <input class="btn btn-primary" type="submit"/>
      </div>
    </form>
    
  </div>
</template>

<script>

const {Api} = require("./../Api.js");

export default {
  name: "CreateListing",

  data() {
    return {
      inventory: null,
      selectedProductId: null,
      selectedInventoryItem: null,
      quantity: 0,
      maxQuantity: 0,
      price: this.defaultPrice,
      currency: null,
      moreInfo: "",
      closeDate: null,
      closeTime: null,
      todayDate: null,
      expiryDate: null,
    }
  },

  props: {
    businessId: {
      required: false,
      type: Number,
    }
  },

  beforeMount: async function() {
    let success = (await this.getInventory());
    if (!success) return;
    await this.getAvailableInventoryItem();
    this.setTodayDate();
    await this.getCurrency();
  },

  watch: {
    /**
     * When users select a different inventory item from the dropdown, resets maximum quantity and expiry date.
     * When users select a different product, selectedInventoryItem is also set to null and quantity is set to 0.
     */
    selectedInventoryItem: function (selectedInventoryItem) {
      if (selectedInventoryItem != null) {
        this.maxQuantity = selectedInventoryItem.quantityRemaining;
        this.expiryDate = selectedInventoryItem.expires;
      }
      this.quantity = 0;
    },

    /**
     * When users select a different product in the dropdown, resets selectedInventoryItem and quantity to initial value.
     */
    selectedProductId: function () {
      this.selectedInventoryItem = null;
      this.quantity = 0;
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
     * Gets inventory of the business and sets the array as inventory data.
     * @return boolean True when business inventory is successfully retrieved,
     */
    async getInventory() {
      try {
        this.inventory = (await Api.getBusinessInventory(this.businessId)).data;
        return true;
      } catch(err) {
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
      } catch(err) {
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
          const inventoryItem = inventory.find(({ id }) => id === listing.inventoryItem.id);
          inventoryItem.quantityRemaining -= listing.quantity;
        });
        this.inventory = inventory;
      }
    },

    /**
     * Sets today's date to restrict input in date picker.
     */
    setTodayDate() {
      this.todayDate = new Date().toISOString().split("T")[0];
    },

    /**
     * Gets currency of the business.
     * @return Currency object
     */
    async getCurrency() {
      this.currency = await this.$helper.tryGetCurrencyForBusiness(this.businessId, this.$stateStore);
    },

    /**
     * Formats date string and time string into one datetime string.
     * @param closeDate Date string in "YYYY-MM-DD" format
     * @param closeTime Time string in "hh:mm" 24 format
     * @return {string|null} Datetime string in "YYYY-MM-DDThh:mm:00Z" format. When closeTime is not specified, sets the time to 23:59. When both closeDate and closeTime is null, returns null.
     */
    formatString(closeDate, closeTime) {
      const defaultTime = "23:59";
      if (closeDate && closeTime) {
        return closeDate + "T" + closeTime + ":00Z";
      } else if (closeDate && !closeTime) {
        return closeDate +  "T" + defaultTime + ":00Z";
      } else {
        return null;
      }
    },

    /**
     * Called when form submit button is clicked. Calls Api to add listing to the business.
     */
    async addListing() {
      let closes = this.formatString(this.closeDate, this.closeTime);
      let priceFixedString = (parseFloat(this.price)).toFixed(2);
      const listing = {
        "inventoryItemId": this.selectedInventoryItem.id,
        "quantity": parseInt(this.quantity),
        "price": parseFloat(priceFixedString),
        "moreInfo": this.moreInfo,
        "closes": closes
      }
      await Api.addItemToInventory(this.businessId, listing)
      .catch((err) => {
        this.errorMessage=err.userFacingErrorMessage
      });
    }
  },

  computed: {
    /**
     * Get products from inventory of the business. Uses dictionary to eliminate duplicate product that has multiple inventory.
     * @return Array of products.
     */
    getProducts() {
      if (this.inventory == null) return [];
      let products = {};
      this.inventory.forEach(inventoryItem => {
        const product = inventoryItem.product;
        products[product.id] = product;
      });
      return Object.values(products);
    },

    /**
     * Filters in inventory item of a product which users chose in dropdown.
     * @return Array of inventory items if inventory and selectedProductId is non-null. Returns empty array otherwise.
     */
    filteredInventory() {
      if (this.inventory == null || this.selectedProductId == null) return [];
      return this.inventory.filter(inventoryItem => inventoryItem.product.id === this.selectedProductId);
    },

    /**
     * Sets default price on price input field once a user select inventory item and set a valid quantity.
     * When user chose whole quantity in the inventory, returns totalPrice of the inventory item if it's non-null.
     * When user chose partial quantity in the inventory, returns quantity times pricePerItem if pricePerItem is non-null.
     * @return {string} Price string in "X.XX" format.
     */
    defaultPrice() {
      if (this.selectedInventoryItem == null || this.quantity <= 0 || this.quantity > this.maxQuantity || this.selectedInventoryItem.totalPrice == null || this.selectedInventoryItem.pricePerItem == null) {
        return "0.00";
      }
      const quantityInInventory = this.selectedInventoryItem.quantity;
      if (Number(this.quantity) === quantityInInventory) {
        return (this.selectedInventoryItem.totalPrice).toFixed(2);
      } else {
        return (this.quantity * this.selectedInventoryItem.pricePerItem).toFixed(2);
      }
    }
  }
}
</script>

<style scoped>

</style>