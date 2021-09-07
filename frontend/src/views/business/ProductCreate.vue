<template>
  <div class="w-100">
    <div
        class="w-100 d-flex justify-content-center product-page-container gradient-background my-3"
    >
      <v-container>
        <form
            class="slightly-transparent-inputs"
            method="POST"
            @submit.prevent="createProduct"
        >
          <v-row>
            <v-col><h1>Add product to Catalogue</h1></v-col>
          </v-row>

          <v-card
              class="card"
          >
            <h4>
              Auto-fill fields via Barcode
              <v-tooltip top>
                <template v-slot:activator="{ on }">
                  <v-icon
                      v-on="on"
                      color="blue"
                  >
                    information
                  </v-icon>
                </template>
                <span>Auto-fill product title and nutritional information by entering the products EAN-13 barcode.</span>
              </v-tooltip>
            </h4>
            <barcode-input/>
          </v-card>

          <v-card
              class="card"
          >
            <h4>Product Information</h4>
            <v-text-field
                v-model="queryParams.name"
                :rules="[() => !!queryParams.name || 'This field is required']"
                class="form-group required"
                dense
                label="Name"
                maxlength="100"
                outlined
                required
                type="text"
            />

            <v-text-field
                v-model="queryParams.recommendedRetailPrice"
                :prefix="currencyText"
                :rules="[() => !!queryParams.recommendedRetailPrice || 'This field is required']"
                class="form-group required"
                dense
                label="Recommended Retail Price"
                max="10000000"
                min="0.01"
                outlined
                required
                step="0.01"
                type="number"
            />

            <v-text-field
                v-model="queryParams.manufacturer"
                dense
                label="Manufacturer"
                maxlength="100"
                outlined
                type="text"
            />

            <v-textarea
                v-model="queryParams.description"
                label="Description"
                maxlength="500"
                outlined
                type="text"
            />
          </v-card>

          <v-card
              class="card"
          >
            <h4>Nutritional Information</h4>
            <dietary-certifications-input
                @input="event => Object.assign(this.queryParams, event)"
            />
          </v-card>

          <v-row>
            <div class="col">
              <input
                  class="btn btn-block btn-primary"
                  type="submit"
                  value="Add Product"
              />
            </div>
          </v-row>

          <div v-if="errorMessage != null" class="row mt-2">
            <div class="col">
              <p class="alert alert-warning">{{ errorMessage }}</p>
            </div>
          </div>
        </form>
      </v-container>
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
      apiErrorMessage: null, // if admin and getting currency info fails
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
        isPalmOilFree: false
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
      } else if (this.queryParams.name === null || this.queryParams.name.trim().length === 0) {
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
