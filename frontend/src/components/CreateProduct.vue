<template>
  <div
    class="w-100 d-flex justify-content-center signup-container gradient-background pb-4"
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

        <div class="row">
          <div class="form-group required col px-3">
            <label>Catalog ID</label>
            <input
                v-model="price"
                v-bind:placeholder="symbol + currency"
                class="form-control"
                min="0.01"
                step="0.01"
                name="price"
                required
                type="number"
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

export default {
  data() {
    return {
      errorMessage: "",

      name: "",
      description: "",
      price: "",
      symbol: "",
      currency: "",

      typeRequired: false, // If phone entered but not country code
    };
  },

  created() {
    this.getCurrencies(this.$stateStore.getters.getActingAs().address.country);
  },

  methods: {
    /**
     * Find the currency associated with the country of the user
     */
    getCurrencies: async function (country) {
      let response = await Api.getCurrencies(country);
      this.currency = response[0].currencies[0].code;
      this.symbol = response[0].currencies[0].symbol;

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
      await this.callApi({
        name: this.name,
        recommendedRetailPrice: this.price, // API stores the type as businessType not type
        description: this.description,
      });
    },
  },
};
</script>

<style scoped>
.signup-container > div {
  max-width: 50em;
}
</style>
