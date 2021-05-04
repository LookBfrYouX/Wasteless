<template>
  <div
    class="w-100 d-flex justify-content-center product-page-container gradient-background pb-4"
  >
    <div class="container">
      <form
        class="slightly-transparent-inputs"
        method="POST"
        v-on:submit.prevent="createProduct"
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

        <!-- up for discussion about setting a step price -->
        <div class="row">
          <div class="form-group required col px-3">
            <label>Price {{symbol}} ({{currency}})</label>
            <input
                v-model="price"
                v-bind:placeholder="symbol + ' (' + currency + ')'"
                class="form-control"
                step="0.01"
                min="0.00"
                max="9999.99"
                name="price"
                required
                type="number"
            />
          </div>
        </div>

        <div class="row">
          <div class="form-group required col px-3">
            <label>Manufacturer</label>
            <input
                v-model="manufacturer"
                placeholder="Manufacturer"
                class="form-control"
                name="manufacturer"
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
            /> <!-- v-on to be used for testing -->
          </div>
        </div>

        <div v-if="errorMessage != null" class="row mt-2">
          <div class="col">
            <p class="alert alert-warning">{{ errorMessage }}</p>
          </div>
        </div>
      </form>
    </div>
    <not-acting-as-business/>
  </div>
</template>

<script>
import { ApiRequestError } from '../ApiRequestError';
const { Api } = require("./../Api.js");
const countryData = require("./../assets/countryData.json");
import NotActingAsBusiness from './Errors/NotActingAsBusiness.vue';

export default {
  components: {
    NotActingAsBusiness
  },

  data() {
    return {
      errorMessage: null,

      name: "",
      description: "",
      manufacturer: "",
      price: "",
      symbol: "",
      currency: "",

      typeRequired: false, // If phone entered but not country code
    };
  },

  props: {
    countryData: {
      required: false,
      default: () => countryData
    }
  },

  computed: {
    /**
     * Name of country. If acting as null, returns null
     */
    businessCountry() {
      const business = this.$stateStore.getters.getActingAs();
      return business? business.address.country: null;
    },
    businessId() {
      const business = this.$stateStore.getters.getActingAs();
      return business != null? business.id: null;
    }
  },

  created() {
    this.getCurrencies(this.businessCountry);
  },

  methods: {
    /**
     * Find the currency associated with the country of the user
     */
    getCurrencies: async function (countryName) {
      const country = this.countryData.find(country => country.name == countryName);
      const currency = country? country.currency: this.$constants.CURRENCY.DEFAULT_CURRENCY;

      this.currency = currency.code;
      this.symbol = currency.symbol;
    },
    /**
     * Wrapper which simply calls the sign up method of the api
     */
    callApi: function (data) {
      if (this.businessId == null) throw new ApiRequestError("Must be logged in as a business before making the request");
      return Api.createProduct(this.businessId, data);
    },

    /**
     * Creates a new product Object with attributes
     * `name`, `id`, `recommendedRetailPrice`, `description`
     * @returns {Promise<void>} a promise
     */
    createProduct: async function () {
      let price;
      try {
        price = parseFloat(this.price);
      } catch(err) {
        this.errorMessage = "Please enter a valid price";
      }

      if (price <= 0 || price >= this.$constants.PRODUCTS.MAX_PRICE) {
        this.errorMessage = "Please enter a valid price between 0 and `${this.$constants.PRODUCTS.MAX_PRICE}";
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
        } catch(err) {
          if (await Api.handle401.call(this, err)) return;
          this.errorMessage = err.userFacingErrorMessage;
          return;
        }
        await this.$router.push("productCatalogue");
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
