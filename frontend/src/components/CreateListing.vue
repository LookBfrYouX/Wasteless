<template>
  <div class="container">
    <form
        v-on:submit.prevent="addListing"
        >
      <div class="row">
        <div class="form-group required col-md-6">
          <label for="product">Product to List</label>
          <select
              id="product"
              class="form-control"
              required
              v-model="selectedInventoryItem"
              placeholder="Select">
            <option disabled
                    :value="null"
                    selected> -- Select product to list -- </option>
            <option v-for="inventoryItem in inventory"
                    :key="inventoryItem.id"
                    v-bind:value="inventoryItem"
                    placeholder="Select a product"
                    >
              {{ inventoryItem.product.name }}
            </option>
          </select>
        </div>
        <div class="col-md-6">
          <label for="quantity">Quantity</label>
          <input
              id="quantity"
              v-model="quantity"
              :max="maxQuantity"
              class="form-control"
              name="quantity"
              required
              type="number"
              min="1">
        </div>
      </div>

      <div class="d-flex justify-content-end">
        <input class="btn btn-primary" type="submit"/>
      </div>
    </form>
    
  </div>
</template>

<script>

// const {Api} = require("./../Api.js");

export default {
  name: "CreateListing",

  data() {
    return {
      inventory: null,
      selectedInventoryItem: null,
      quantity: 0,
      maxQuantity: 0,
    }
  },

  // props: {
  //   businessId: {
  //     required: true,
  //     type: Number
  //   }
  // },

  beforeMount: async function() {
    await this.getAvailableInventoryItem();
  },

  watch: {
    selectedInventoryItem: function (selectedInventoryItem) {
      this.maxQuantity = selectedInventoryItem.quantityRemaining;
    }
  },

  methods: {

    async getInventory() {
      const inventory =
          [
            {
              "id": 101,
              "product": {
                "id": 1,
                "name": "Watties Baked Beans - 420g can",
                "description": "Baked Beans as they should be.",
                "manufacturer": "Heinz Wattie's Limited",
                "recommendedRetailPrice": 2.2,
                "created": "2021-05-17T04:30:33.616Z",
                "images": [
                  {
                    "id": 1234,
                    "filename": "/media/images/23987192387509-123908794328.png",
                    "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                  }
                ]
              },
              "quantity": 4,
              "pricePerItem": 6.5,
              "totalPrice": 21.99,
              "manufactured": "2021-05-17",
              "sellBy": "2021-05-17",
              "bestBefore": "2021-05-17",
              "expires": "2021-05-17"
            },
            {
              "id": 102,
              "product": {
                "id": 2,
                "name": "Other product",
                "description": "Baked Beans as they should be.",
                "manufacturer": "Heinz Wattie's Limited",
                "recommendedRetailPrice": 2.2,
                "created": "2021-05-17T04:30:33.616Z",
                "images": [
                  {
                    "id": 1234,
                    "filename": "/media/images/23987192387509-123908794328.png",
                    "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                  }
                ]
              },
              "quantity": 4,
              "pricePerItem": 6.5,
              "totalPrice": 21.99,
              "manufactured": "2021-05-17",
              "sellBy": "2021-05-17",
              "bestBefore": "2021-05-17",
              "expires": "2021-05-17"
            },
            {
              "id": 103,
              "product": {
                "id": 3,
                "name": "Another product",
                "description": "Baked Beans as they should be.",
                "manufacturer": "Heinz Wattie's Limited",
                "recommendedRetailPrice": 2.2,
                "created": "2021-05-17T04:30:33.616Z",
                "images": [
                  {
                    "id": 1234,
                    "filename": "/media/images/23987192387509-123908794328.png",
                    "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                  }
                ]
              },
              "quantity": 4,
              "pricePerItem": 6.5,
              "totalPrice": 21.99,
              "manufactured": "2021-05-17",
              "sellBy": "2021-05-17",
              "bestBefore": "2021-05-17",
              "expires": "2021-05-17"
            }
          ]
      return inventory;
      // await Api.getBusinessInventory(1)
      // .catch(err => this.apiErrorMessage = err.userFacingErrorMessage);
    },

    async getListings() {
      const listings =
          [
            {
              "id": 57,
              "inventoryItem": {
                "id": 101,
                "product": {
                  "id": 1,
                  "name": "Watties Baked Beans - 420g can",
                  "description": "Baked Beans as they should be.",
                  "manufacturer": "Heinz Wattie's Limited",
                  "recommendedRetailPrice": 2.2,
                  "created": "2021-05-17T04:49:48.728Z",
                  "images": [
                    {
                      "id": 1234,
                      "filename": "/media/images/23987192387509-123908794328.png",
                      "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                    }
                  ]
                },
                "quantity": 4,
                "pricePerItem": 6.5,
                "totalPrice": 21.99,
                "manufactured": "2021-05-17",
                "sellBy": "2021-05-17",
                "bestBefore": "2021-05-17",
                "expires": "2021-05-17"
              },
              "quantity": 3,
              "price": 17.99,
              "moreInfo": "Seller may be willing to consider near offers",
              "created": "2021-07-14T11:44:00Z",
              "closes": "2021-07-21T23:59:00Z"
            }
          ]
      // await Api.getBusinessListings(1)
      // .catch(err => this.apiErrorMessage = err.userFacingErrorMessage);
      return listings;
    },

    async getAvailableInventoryItem() {
      let inventory = await this.getInventory();
      inventory.map(inventoryItem => {
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
    
  }
}
</script>

<style scoped>

</style>