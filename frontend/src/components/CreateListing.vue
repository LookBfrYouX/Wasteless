<template>
  <div class="container">
    <form 
        method="POST"

        >
      <div class="row">
        <div class="form-group required col-md-6">
          <label for="product">Product to List</label>
          <select
              id="product"
              class="form-control"
              required >
            <option disabled selected value> -- Select product to list -- </option>
            <option v-for="product in productOptions"
                    :key="product.id"
                    v-bind:value="productToList">
              {{ product.name }}
            </option>
          </select>
        </div>
        <div class="col-md-6">
          <label for="quantity">Quantity</label>
          <input
              id="quantity"
              v-model="numberToList"
              class="form-control"
              name="quantity"
              required
              type="text">
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
      productOptions: null,
      productToList: null,
      numberToList: 0,
      productErrorMessage: null,
      quantityErrorMessage: null,
      errorMessage: "",
    }
  },

  props: {
    businessId: {
      required: true,
      type: Number
    }
  },

  beforeMount: async function() {
    await this.getProductOptions();
  },

  methods: {

    async getProductOptions() {
      await Api.getProducts(1)
      .then(({data}) => this.productOptions = data)
      .catch(err => this.apiErrorMessage = err.userFacingErrorMessage);
    }
  }
}
</script>

<style scoped>

</style>