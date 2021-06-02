<template>
  <div class="container mt-2">
    <form class="slightly-transparent-inputs"
          method="POST"
          @submit.prevent="addItem"
    >
      <div class="row">
        <div class="col text-center">
          <h1>
            Add item to inventory
          </h1>
        </div>
      </div>
      <div class="row">
        <div class="col-12 col-md-6 form-group required">
          <label for="productDropdown">Select product</label>
          <select required class="form-control" id="productDropdown" v-model="product">
            <option v-for="product in products" :key="product.id" :value="product">
              {{ product.name }}
            </option>
          </select>
        </div>
        <div class="col-12 col-md-6 form-group required">
          <label for="quantity">Quantity</label>
          <input
              id="quantity"
              v-model="quantity"
              class="form-control"
              name="quantity"
              placeholder="Quantity"
              required
              type="number"
              min="1"
          />
        </div>
      </div>
      <div class="row">
        <div class="col-12 col-md-6 form-group">
          <label for="pricePerItem">Price per item {{ currencyText }}</label>
          <input
              id="pricePerItem"
              v-model="pricePerItem"
              class="form-control"
              name="pricePerItem"
              type="number"
              min="0.01"
              step="0.01"
              :placeholder="currencyText"
          />
        </div>
        <div class="col-12 col-md-6 form-group">
          <label for="totalPrice">Total price {{ currencyText }}</label>
          <input
              id="totalPrice"
              v-model="totalPrice"
              class="form-control"
              name="totalPrice"
              type="number"
              :max="pricePerItem * quantity"
              step="0.01"
              min="0.0"
              :placeholder="currencyText"
          />
        </div>
      </div>
      <div class="row">
        <div class="col-12 col-md-6 form-group">
          <label for="manufactured">Manufactured</label>
          <input
              id="manufactured"
              v-model="manufactured"
              class="form-control"
              maxlength="30"
              :max="todayDate"
              name="manufactured"
              placeholder="Manufactured"
              type="date"
          />
        </div>
        <div class="col-12 col-md-6 form-group">
          <label for="sellBy">Sell By</label>
          <input
              id="sellBy"
              v-model="sellBy"
              class="form-control"
              maxlength="30"
              :min="todayDate"
              name="sellBy"
              placeholder="Sell By"
              type="date"
          />
        </div>
      </div>
      <div class="row">
        <div class="col-12 col-md-6 form-group">
          <label for="bestBefore">Best before</label>
          <input
              id="bestBefore"
              v-model="bestBefore"
              class="form-control"
              maxlength="30"
              :min="todayDate"
              name="bestBefore"
              placeholder="Best before"
              type="date"
          />
        </div>
        <div class="col-12 col-md-6 form-group required">
          <label for="expires">Expires</label>
          <input
              id="expires"
              v-model="expires"
              class="form-control"
              maxlength="30"
              :min="todayDate"
              name="expires"
              required
              placeholder="Expires"
              type="date"
          />
        </div>
      </div>
      <div class="row">
        <div class="col text-center">
          <input class="btn btn-block btn-primary" type="submit" value="Add"/>
        </div>
      </div>
      <div v-if="errorMessage" class="row mt-2">
        <div class="col">
          <p class="alert alert-warning">{{ errorMessage }}</p>
        </div>
      </div>
    </form>
    <error-modal
        title="Error fetching business products"
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.populateDropdown"
        :show="apiErrorMessage !== null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<script>
import { Api } from "@/Api";
import ErrorModal from "@/components/ErrorModal";

export default {
  name: "InventoryItemEntry",
  components: {
    ErrorModal
  },

  data() {
    return {
      todayDate: null,
      apiErrorMessage: null,
      product: null,
      quantity: null,
      pricePerItem: null,
      totalPrice: null,
      manufactured: null,
      sellBy: null,
      bestBefore: null,
      expires: null,
      products: null,
      currency: null,
      errorMessage: null,
    }
  },
  props: {
    businessId: {
      type: Number,
      required: true
    },
  },

  beforeMount: async function() {
    this.setDateInputs(new Date());
    await this.populateDropdown();
    await this.currencyPipeline();
  },

  computed: {
    currencyText() {
      if (this.currency == null) {
        return "(Unknown currency)";
      }
      return `${this.currency.symbol} (${this.currency.code})`;
    }
  },
  methods: {
    /**
     * Pipeline that sets currency data
     */
    currencyPipeline: async function () {
      try {
        this.currency = await this.$helper.getCurrencyForBusiness(this.businessId,
            this.$stateStore);
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
      }
    },

    /**
     * Redirects a user to inventory page when a inventory item is successfully created.
     */
    goToInventory() {
      this.$router.push({
        name: "BusinessInventory",
        params: {
          businessId: this.businessId
        }
      });
    },
    async addItem() {
      let parsedPricePerItem = parseFloat(this.pricePerItem);
      let parsedTotalPrice = parseFloat(this.totalPrice);
      let parsedQuantity = parseInt(this.quantity, 10);
      if (isNaN(parsedPricePerItem)) parsedPricePerItem = null;
      if (isNaN(parsedTotalPrice)) parsedTotalPrice = null;
      if (isNaN(parsedQuantity)) {
        this.errorMessage = "You must enter a number for the quantity."
        return;
      }

      let data = {
        "productId": this.product.id,
        "quantity": parsedQuantity,
        "pricePerItem": parsedPricePerItem,
        "totalPrice": parsedTotalPrice,
        "manufactured": this.manufactured,
        "sellBy": this.sellBy,
        "bestBefore": this.bestBefore,
        "expires": this.expires,
      }

      try {
        await Api.addItemToInventory(this.businessId, data);
        this.goToInventory();
      } catch(err) {
        this.errorMessage = err.userFacingErrorMessage;
      }
    },
    setDateInputs(today) {
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
    async populateDropdown() {
      await Api.getProducts(this.businessId)
      .then(({data}) => this.products = data)
      .catch(err => this.apiErrorMessage = err.userFacingErrorMessage);
    }
  }
}
</script>