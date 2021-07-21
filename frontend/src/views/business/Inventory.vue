<template>
  <div class="w-100">
    <sorted-paginated-item-list
        :currentSortOption.sync="currentSortOption"
        :items="listings"
        :sortOptions="sortOptions"
    >
      <template v-slot:title>
        <h2>Inventory for {{ businessName ? businessName : "business" }}</h2>
      </template>
      <template v-slot:item="slotProps">
        <inventory-item-card
            :businessId="businessId"
            :currency="currency"
            :item="slotProps.item"
            class="hover-white-bg hover-scale-effect slightly-transparent-white-background my-1 rounded"
        />
      </template>
      <template v-slot:right-button>
        <router-link
            :to="{ name: 'BusinessInventoryCreate', params: { businessId }}"
            class="btn btn-info d-flex"
        >
          <span class="material-icons mr-1">add</span>
          Add Item to Inventory
        </router-link>
      </template>
    </sorted-paginated-item-list>
    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.getInventory"
        :show="apiErrorMessage !== null"
        title="Error viewing inventory"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import SortedPaginatedItemList from '../../components/SortedPaginatedItemList.vue';
import ErrorModal from "@/components/ErrorModal";
import {Api} from "@/Api";
import {helper} from "@/helper";
import InventoryItemCard from "@/components/cards/InventoryCard";

const sortOptions = [
  {
    name: "Inventory Item ID",
    sortMethod: helper.sensibleSorter("id")
  }, {
    name: "Product ID",
    sortMethod: helper.sensibleSorter(el => el.product.id)
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
    name: "Total Price",
    sortMethod: helper.sensibleSorter("totalPrice")
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
    InventoryItemCard,
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
      currentSortOption: {...sortOptions[0], reversed: false},
      businessName: null,
      currency: null,
    };
  },

  beforeMount: async function () {
    return Promise.allSettled([this.getInventory(), this.updateBusinessName(), this.getCurrency()]);
  },

  methods: {
    getInventory: async function () {
      try {
        this.listings = (await Api.getBusinessInventory(this.businessId)).data.results;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return false;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    /**
     * Gets currency object.
     * @returns {Promise<void>} Currency object, null when the currency doesn't exist or API request error.
     */
    getCurrency: async function () {
      this.currency = await this.$helper.tryGetCurrencyForBusiness(this.businessId,
          this.$stateStore);
    },

    updateBusinessName: async function () {
      this.businessName = await this.$helper.tryGetBusinessName(this.businessId);
    },
  },

  watch: {
    businessName() {
      if (this.businessName != null) {
        document.title = `Inventory for ${this.businessName}`
      }
    }
  }
}
</script>
