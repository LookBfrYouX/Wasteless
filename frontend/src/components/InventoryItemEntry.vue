<template>
  <div class="container">
    <form class="slightly-transparent-inputs"
          method="POST"
          v-on:submit.prevent="addItem"
    >
      <div class="row">
        <div class="col text-center">
          <h1>
            Add item to inventory
          </h1>
        </div>
      </div>
      <div class="row">
        <div class="col-6 form-group required">
          <label for="productDropdown">Select product</label>
          <select required class="form-control" id="productDropdown" v-model="product">
            <option v-for="product in products" :key="product.id" v-bind:value="product">
              {{ product.name }}
            </option>
          </select>
        </div>
        <div class="col-6 form-group required">
          <label for="quantity">Quantity</label>
          <input
              id="quantity"
              v-model="quantity"
              class="form-control"
              name="quantity"
              placeholder="Quantity"
              required
              type="number"
              min="0"
          />
        </div>
      </div>
      <div class="row">
        <div class="col-6 form-group">
          <label for="pricePerItem">Price per item</label>
          <input
              id="pricePerItem"
              v-model="pricePerItem"
              class="form-control"
              name="pricePerItem"
              placeholder="Price per item"
              type="number"
              min="0"
          />
        </div>
        <div class="col-6 form-group">
          <label for="totalPrice">Total price</label>
          <input
              id="totalPrice"
              v-model="totalPrice"
              class="form-control"
              name="totalPrice"
              placeholder="Total price"
              type="number"
              min="0"
          />
        </div>
      </div>
      <div class="row">
        <div class="col-6 form-group">
          <label for="manufactured">Manufactured</label>
          <input
              id="manufactured"
              v-model="manufactured"
              class="form-control"
              maxlength="30"
              name="manufactured"
              placeholder="Manufactured"
              type="date"
          />
        </div>
        <div class="col-6 form-group">
          <label for="sellBy">Sell By</label>
          <input
              id="sellBy"
              v-model="sellBy"
              class="form-control"
              maxlength="30"
              name="sellBy"
              placeholder="Sell By"
              type="date"
          />
        </div>
      </div>
      <div class="row">
        <div class="col-6 form-group">
          <label for="bestBefore">Best before</label>
          <input
              id="bestBefore"
              v-model="bestBefore"
              class="form-control"
              maxlength="30"
              name="bestBefore"
              placeholder="Best before"
              type="date"
          />
        </div>
        <div class="col-6 form-group required">
          <label for="expires">Expires</label>
          <input
              id="expires"
              v-model="expires"
              class="form-control"
              maxlength="30"
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
    </form>
  </div>
</template>

<script>
const {Api} = require("./../Api.js");

export default {
  name: "InventoryItemEntry",
  data() {
    return {
      product: null,
      quantity: null,
      pricePerItem: null,
      totalPrice: null,
      manufactured: null,
      sellBy: null,
      bestBefore: null,
      expires: null,
      products: null,
    }
  },
  props: {
    businessId: {
      type: Number,
      required: true
    },
  },
  mounted() {
    this.setDateInputs();
    this.populateDropdown();
  },
  methods: {
    async addItem() {
      let data = {
        "productId": this.product.id,
        "quantity": this.quantity,
        "pricePerItem": this.pricePerItem,
        "totalPrice": this.totalPrice,
        "manufactured": this.manufactured,
        "sellBy": this.sellBy,
        "bestBefore": this.bestBefore,
        "expires": this.expires,
      }
      alert(JSON.stringify(data));

      // TODO: Uncomment below when controller is added, need to catch its errors and load new page
      // let response = await Api.addItemToInventory(this.businessId, data);
      // console.log(JSON.stringify(response));
    },
    setDateInputs() {
      let today = new Date();
      let dd = today.getDate();
      let mm = today.getMonth() + 1;
      let yyyy = today.getFullYear();
      if (dd < 10) {
        dd = '0' + dd
      }
      if (mm < 10) {
        mm = '0' + mm
      }
      let dateString = yyyy + '-' + mm + '-' + dd

      // Is there a better way to do this?
      document.getElementById("expires").setAttribute("min", dateString);
      document.getElementById("bestBefore").setAttribute("min", dateString);
      document.getElementById("sellBy").setAttribute("min", dateString);
      document.getElementById("manufactured").setAttribute("max", dateString);
    },
    async populateDropdown() {
      let response = await Api.getProducts(this.businessId);
      this.products = response.data;
    }
  }
}
</script>

<style scoped>
.container {
  margin-top: 10px;
}
</style>