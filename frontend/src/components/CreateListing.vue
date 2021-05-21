<template>
  <div class="container">
    <form
        v-on:submit.prevent="addListing"
        >
      <div class="row">
        <div class="form-group required col-md-6">
          <label for="product">Find product</label>
          <select
              id="product"
              class="form-control"
              required
              v-model="selectedProductId"
              >
            <option disabled
                    :value="null"
                    selected> -- Select product to list -- </option>
            <option v-for="product in getProducts"
                    :key="product.id"
                    v-bind:value="product.id"
            >
              {{ product.name }}
            </option>
          </select>
        </div>
        <div class="form-group required col-md-6">
          <label for="inventory">Inventory to List</label>
          <select
              id="inventory"
              class="form-control"
              required
              v-model="selectedInventoryItem"
              :disabled="filteredInventory.length === 0"
              >
            <option disabled
                    :value="null"
                    selected> -- Select inventory to list -- </option>
            <option v-for="inventoryItem in filteredInventory"
                    :key="inventoryItem.id"
                    :value="inventoryItem"
                    >
              Expires at {{ inventoryItem.expires }} (ID: {{ inventoryItem.id }})
            </option>
          </select>
        </div>
        <div class="col-md-6">
          <label for="quantity">Quantity</label>
          <input
              id="quantity"
              v-model="quantity"
              :disabled="selectedInventoryItem == null || maxQuantity <= 0"
              :max="maxQuantity"
              class="form-control"
              name="quantity"
              required
              type="number"
              min="1">
          <small v-if="selectedInventoryItem"
               class="text-muted">
            You can list {{selectedInventoryItem.quantityRemaining}} items out of {{selectedInventoryItem.quantity}} in the inventory.
          </small>
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
      inventory: null,
      selectedProductId: null,
      selectedInventoryItem: null,
      quantity: 0,
      maxQuantity: 0,
    }
  },

  props: {
    businessId: {
      required: false,
      type: Number,
      default: 1
    }
  },

  beforeMount: async function() {
    let success = (await this.getInventory());
    if (!success) return;
    await this.getAvailableInventoryItem();
  },

  watch: {
    selectedInventoryItem: function (selectedInventoryItem) {
      this.maxQuantity = selectedInventoryItem.quantityRemaining;
    },
    selectedProductId: function () {
      this.selectedInventoryItem = null;
      this.quantity = 0;
    }
  },

  methods: {

    async getInventory() {
      try {
        this.inventory = (await Api.getBusinessInventory(1)).data;
        return true;
      } catch(err) {
        this.apiErrorMessage = err.userFacingErrorMessage
        return false;
      }
    },

    async getListings() {
      try {
        return (await Api.getBusinessListings(1)).data;
      } catch(err) {
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },



    /**
     *
     * @returns {Promise<void>}
     */
    async getAvailableInventoryItem() {
      let inventory = this.inventory.map(inventoryItem => {
        inventoryItem.quantityRemaining = inventoryItem.quantity;
        return inventoryItem;
      });
      const listings = await this.getListings();
      listings.forEach(listing => {
        const inventoryItem = inventory.find(({ id }) => id === listing.inventoryItem.id);
        inventoryItem.quantityRemaining -= listing.quantity;
      });
      this.inventory = inventory;
    },

    addListing() {
      alert("Not yet implemented");
    }
    
  },

  computed: {
    getProducts() {
      if (this.inventory == null) return [];
      let products = {};
      this.inventory.forEach(inventoryItem => {
        const product = inventoryItem.product;
        products[product.id] = product;
      });
      return Object.values(products);
    },

    filteredInventory() {
      if (this.inventory == null || this.selectedProductId == null) return [];
      const result = this.inventory.filter(inventoryItem => inventoryItem.product.id === this.selectedProductId);
      console.log(result);
      return result
    }
  }
}
</script>

<style scoped>

</style>