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
              v-model="id"
              class="form-control"
              maxlength="30"
              name="id"
              placeholder="Catalog ID"
              required
              type="text"
            />
          </div>
        </div>

        <div class="row">
          <div class="form-group required col px-3">
            <label>Recommended Price</label>
            <input
              v-model="price"
              class="form-control"
              maxlength="30"
              name="price"
              placeholder="Price"
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
            />
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
const Api = require("../Api").default;

export default {
  data() {
    return {
      errorMessage: "",

      name: "",
      id: "",
      description: "",
      price: "",

      typeRequired: false, // If phone entered but not country code
    };
  },
  methods: {
    /**
     * Wrapper which simply calls the sign up method of the api
     */
    callApi: function (data) {
      const businessId = this.$stateStore.getters.getActingAs().id;
      console.log(businessId);
      Api.createProduct(businessId, data);
      this.$router.push({name: "productcatalogue"});
    },

    /**
     * Creates a new product Object with attributes
     * `name`, `id`, `recommendedRetailPrice`, `description`
     * @returns {Promise<void>} a promise
     */
    createProduct: async function () {
      await this.callApi({
        name: this.name,
        id: this.id,
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
