<template>
  <div class="container">
    <form 
        method="POST"

        >
      <div class="row">
        <div class="form-group required col-md-6">
          <label for="product">Product to List</label>
          <select required class="form-control" id="product" v-model="product">
            <option v-for="product in productOptions" :key="product.id" v-bind:value="productToList">
              {{ productToList.name }}
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

  // props: {
  //   businessId: {
  //     required: true,
  //     type: Number
  //   }
  // },

  methods: {

    async getProductOptions() {
      await Api.getProducts(this.businessId)
      .then(({data}) => this.productOptions = data)
      .catch(err => this.apiErrorMessage = err.userFacingErrorMessage);
    }
  }
}
</script>

<style scoped>

</style>