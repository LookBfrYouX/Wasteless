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

          <v-card
              class="card"
              elevation="2"
          >
            <div class="row">
              <div class="form-group required col px-3">
                <label>Name</label>
                <v-text-field
                    v-model="queryParams.name"
                    dense
                    elevation="0"
                    label="Product Name"
                    maxlength="100"
                    placeholder="Name"
                    required
                    solo
                    type="text"
                />
              </div>
            </div>

            <div class="row">
              <div class="form-group required col px-3">
                <label>Price {{ currencyText }}</label>
                <input
                    v-model="queryParams.recommendedRetailPrice"
                    :placeholder="currencyText"
                    class="form-control"
                    max="10000000"
                    min="0.01"
                    name="price"
                    required
                    step="0.01"
                    type="number"
                />
              </div>
            </div>

            <div class="row">
              <div class="form-group col px-3">
                <label>Manufacturer</label>
                <input
                    v-model="queryParams.manufacturer"
                    class="form-control"
                    maxlength="100"
                    name="manufacturer"
                    placeholder="Manufacturer"
                    type="text"
                />
              </div>
            </div>

            <div class="row>">
              <div class="form-group col px-0">
                <label>Description</label>
                <textarea
                    v-model="queryParams.description"
                    class="form-control"
                    maxlength="500"
                    name="description"
                    placeholder="Description"
                    rows="5"
                    type="text"
                />
              </div>
            </div>
          </v-card>
          <v-card
              class="card"
              elevation="2"
          >
            <barcode-input/>
            <h4>Nutritional Information</h4>
            <dietary-certifications-input
                @input="event => Object.assign(this.queryParams, event)"
            />
          </v-card>

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
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="false"
        :retry="currencyPipeline"
        :show="apiErrorMessage != null"
        class="p-absolute w-100"
        title="Could not retrieve business data"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<script>
import {ApiRequestError} from "@/ApiRequestError";
import ErrorModal from "@/components/ErrorModal";

import {Api} from "@/Api";
import BarcodeInput from "@/components/BarcodeInput";
import DietaryCertificationsInput from "@/components/DietaryCertificationsInput";

export default {
  components: {
    BarcodeInput,
    DietaryCertificationsInput,
    ErrorModal
  },

  data() {
    return {
      apiErrorMessage: null, // if admin and getting currency nifo fails
      errorMessage: null,

      queryParams: {
        name: "",
        description: "",
        manufacturer: "",
        recommendedRetailPrice: "",
        currency: null,
        isGlutenFree: false,
        isDairyFree: false,
        isVegetarian: false,
        isVegan: false,
        isPalmOilFree: false,
      },
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
      if (this.queryParams.currency == null) {
        return "(Unknown currency)";
      }
      return `${this.queryParams.currency.symbol} (${this.queryParams.currency.code})`;
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
        this.queryParams.currency = currency;
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
      let price = parseFloat(this.queryParams.recommendedRetailPrice);

      if (isNaN(price)) {
        this.errorMessage = "Please enter a valid price";
        return;
      }

      if (price <= 0) {
        this.errorMessage = "Please enter a valid price greater than 0";
      } else if (this.queryParams.name.trim().length === 0) {
        this.errorMessage = "Please enter a name for your product";
      } else {
        try {
          await this.callApi(this.queryParams);
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

.card {
  padding: 10px;
  margin-top: 10px;
  margin-bottom: 10px;
}
</style>
