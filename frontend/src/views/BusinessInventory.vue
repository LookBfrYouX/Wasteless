<template>
  <div class="w-100">
    <sorted-paginated-item-list
      v-bind:items="listings"
      v-bind:sortOptions="sortOptions"
      v-bind:currentSortOption.sync="currentSortOption"
    >
      <template v-slot:title>
        <h2>Inventory for {{businessName? businessName: "business"}}</h2>
      </template>
      <template v-slot:item="slotProps">
        <div class="hover-white-bg hover-scale-effect slightly-transparent-white-background my-1 p-3 rounded">
          {{ slotProps.item.product.name }}
        </div>
      </template>
      <template v-slot:right-button>
        <button
            class="btn btn-info mx-1 d-flex"
            type="button"
            v-on:click="addProductToInventory"
        >
          <span class="material-icons mr-1">add</span>
          Add Product
        </button>
      </template>
      <!--<business-listing v-bind:listing="slotProps.item"/> -->
    </sorted-paginated-item-list>
    <error-modal
      title="Error viewing inventory"
      v-bind:goBack="false"
      v-bind:hideCallback="() => apiErrorMessage = null"
      v-bind:refresh="true"
      v-bind:retry="this.getInventory"
      v-bind:show="apiErrorMessage !== null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import SortedPaginatedItemList from '../components/SortedPaginatedItemList.vue';
import ErrorModal from "../components/Errors/ErrorModal";
import { Api } from "../Api";
import { helper } from "./../helper";

const mockResult = [];
for(let i = 1; i < 12; i++) {
  mockResult.push({
    "id": i,
    "product": {
      "name": "Watties Baked Beans - "+ i * 100 + "g can",
    }
  });
}

const sortOptions = [
  {
    name: "ID",
    sortMethod: helper.sensibleSorter("id")
  }, {
    name: "Product ID",
    sortMethod: helper.sensibleSorter("productId") 
  }, {
    name: "Product Name",
    sortMethod: helper.sensibleSorter(el => el.product.name) 
  }, {
    name: "Quantity",
    sortMethod: helper.sensibleSorter("quantity") 
  }, {
    name: "Price Per Item",
    sortMethod: helper.sensibleSorter("pricePerItem")
  }, {
    name: "Manufacturing Date",
    sortMethod: helper.sensibleSorter("manufactured")
  }, {
    name: "Best Before Date",
    sortMethod: helper.sensibleSorter("bestBefore")
  }, {
    name: "Sell By Date",
    sortMethod: helper.sensibleSorter("sellBy")
  }, {
    name: "Expires",
    sortMethod: helper.sensibleSorter("expires")
  }
];


export default {
  components: {
    SortedPaginatedItemList,
    ErrorModal
  },

  props: {
    businessId: {
      type: Number,
      required: true
    }
  },

  data() {
    return {
      listings: [],
      apiErrorMessage: null,
      sortOptions,
      currentSortOption: { ...sortOptions[0], reversed: false},
      businessName: null,
    };
  },

  beforeMount: async function() {
    // TODO Uncomment once backend endpoint is created
    this.listings = mockResult;
    await this.updateBusinessName();
    // await this.getInventory();
  },
  
  methods: {
    getInventory: async function() {
      try {
        this.listings = (await Api.getBusinessInventory(this.businessId)).data;
      } catch(err) {
        if (await Api.handle401.call(this, err)) return false;
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    /**
     * Simple utility function to route to the add inventory item form.
     * Passes in the businesses Id
     */
     addProductToInventory: function () {
      this.$router.push({
        name: "inventoryItemEntry",
        params: {
          businessId: this.$props.businessId
        }
      });
    },

    updateBusinessName: async function() {
      this.businessName = await this.$helper.tryGetBusinessName(this.businessId);
    },
  },

  watch: {
    businessName() {
      if (this.businessName != null) document.title = `Inventory for ${this.businessName}`
    }
  }
}
</script>
