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
            <label>Price</label>
            <input
                v-model="price"
                v-bind:placeholder="symbol + ' (' + currency + ')'"
                class="form-control"
                step="0.10"
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

        <div v-if="errorMessage.length > 0" class="row mt-2">
          <div class="col">
            <p class="alert alert-warning">{{ errorMessage }}</p>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
const { Api } = require("./../Api.js");
const countryData = require("./../assets/countryData.json");

export default {
  data() {
    return {
      errorMessage: "",

      name: "",
      description: "",
      manufacturer: "",
      price: "",
      symbol: "",
      currency: "",

      typeRequired: false, // If phone entered but not country code
    };
  },

  props() {
    return {
      countryData: {
        required: false,
        default: countryData
      }
    }
  },

  created() {
    this.getCurrencies(this.$stateStore.getters.getActingAs().address.country);
  },

  methods: {
    /**
     * Find the currency associated with the country of the user
     */
    getCurrencies: async function (countryName) {
      const country = countryData.find(country => country.name == countryName);
      const currency = country? country.currency: {
        code: "NZD",
        symbol: "$"
      };

      this.currency = currency.code;
      this.symbol = currency.symbol;
    },
    /**
     * Wrapper which simply calls the sign up method of the api
     */
    callApi: function (data) {
      const businessId = this.$stateStore.getters.getActingAs().id;
      return Api.createProduct(businessId, data);
    },

    /**
     * Creates a new product Object with attributes
     * `name`, `id`, `recommendedRetailPrice`, `description`
     * @returns {Promise<void>} a promise
     */
    createProduct: async function () {
      if (this.price < 0 || this.price == "") {
        this.errorMessage = "Please enter a valid price";
      } else if (this.name.trim().length === 0) {
        this.errorMessage = "Please enter a name for your product";
      } else {
        await this.callApi({
          name: this.name,
          recommendedRetailPrice: this.price, // API stores the type as businessType not type
          manufacturer: this.manufacturer,
          description: this.description,
        });
        this.errorMessage = "";
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
