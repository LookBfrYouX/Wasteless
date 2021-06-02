<template>
  <div class="w-100">
    <div
        class="w-100 d-flex justify-content-center product-page-container gradient-background my-3"
    >
      <div class="container">
        <form
            class="slightly-transparent-inputs"
            method="POST"
            @submit.prevent="createProduct"
        >
          <div class="row">
            <div class="col">
              <h1>Add product to Catalogue</h1>
            </div>
          </div>

          <div class="row">
            <div class="form-group required col px-3">
              <label>Name</label>
              <input
                  v-model="name"
                  class="form-control"
                  maxlength="30"
                  name="name"
                  placeholder="Name"
                  required
                  type="text"
              />
            </div>
          </div>

          <div class="row">
            <div class="form-group required col px-3">
              <label>Price {{ currencyText }}</label>
              <input
                  v-model="price"
                  class="form-control"
                  min="0.00"
                  name="price"
                  required
                  step="0.01"
                  type="number"
                  :placeholder="currencyText"
              />
            </div>
          </div>

          <div class="row">
            <div class="form-group required col px-3">
              <label>Manufacturer</label>
              <input
                  v-model="manufacturer"
                  class="form-control"
                  name="manufacturer"
                  placeholder="Manufacturer"
                  required
                  type="text"
              />
            </div>
          </div>

          <div class="row>">
            <div class="form-group col px-0">
              <label>Description</label>
              <textarea
                  v-model="description"
                  class="form-control"
                  maxlength="500"
                  name="description"
                  placeholder="Description"
                  rows="5"
                  type="text"
              />
            </div>
          </div>

          <div class="row">
            <div class="col">
              <input
                  class="btn btn-block btn-primary"
                  type="submit"
                  value="Add Product"
              />
            </div>
          </div>

          <div v-if="errorMessage != null" class="row mt-2">
            <div class="col">
              <p class="alert alert-warning">{{ errorMessage }}</p>
            </div>
          </div>
        </form>
      </div>
    </div>
    <error-modal
        class="p-absolute w-100"
        title="Could not retrieve business data"
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="false"
        :retry="currencyPipeline"
        :show="apiErrorMessage != null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<script>
import {ApiRequestError} from "@/ApiRequestError";
import ErrorModal from "@/components/ErrorModal";

import { Api } from "@/Api";

export default {
  components: {
    ErrorModal
  },

  data() {
    return {
      apiErrorMessage: null, // if admin and getting currency nifo fails
      errorMessage: null,

      name: "",
      description: "",
      manufacturer: "",
      price: "",
      currency: null,

      typeRequired: false, // If phone entered but not country code
    };
  },

  props: {
    businessId: {
      required: true,
      type: Number
    }
  },

  created() {
    return this.currencyPipeline();
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
        const currency = await this.$helper.getCurrencyForBusiness(this.businessId,
            this.$stateStore);
        this.currency = currency;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;

      }
    },

    /**
     * Wrapper which simply calls the sign up method of the api
     */
    callApi: function (data) {
      if (this.businessId == null) {
        throw new ApiRequestError(
            "Must be signed in as a business before making the request");
      }
      return Api.createProduct(this.businessId, data);
    },

    /**
     * Creates a new product Object with attributes
     * `name`, `id`, `recommendedRetailPrice`, `description`
     * @returns {Promise<void>} a promise
     */
    createProduct: async function () {
      let price = parseFloat(this.price);

      if (isNaN(price)) {
        this.errorMessage = "Please enter a valid price";
        return;
      }

      if (price <= 0) {
        this.errorMessage = "Please enter a valid price greater than 0";
      } else if (this.name.trim().length === 0) {
        this.errorMessage = "Please enter a name for your product";
      } else if (this.manufacturer.trim().length === 0) {
        this.errorMessage = "Please enter a manufacturer for your product";
      } else {
        try {
          await this.callApi({
            name: this.name,
            recommendedRetailPrice: this.price,
            manufacturer: this.manufacturer,
            description: this.description,
          });
        } catch (err) {
          if (await Api.handle401.call(this, err)) {
            return;
          }
          this.errorMessage = err.userFacingErrorMessage;
          return;
        }
        await this.$router.push({
          name: "BusinessProducts",
          params: {
            businessId: this.businessId
          }
        });
      }
    },
  },
};
</script>

<style scoped>
.product-page-container > div {
  max-width: 50em;
}
</style>