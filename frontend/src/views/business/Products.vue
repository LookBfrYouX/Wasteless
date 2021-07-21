<template>
  <div class="w-100">
    <sorted-paginated-item-list
        :currentSortOption.sync="currentSortOption"
        :items="products"
        :sortOptions="sortOptions"
    >
      <template v-slot:title>
        <h2>Product Catalogue {{ businessName ? `for ${businessName}` : "" }}</h2>
      </template>
      <template v-slot:right-button>
        <router-link
            :to="{name: 'BusinessProductCreate', params: { businessId }}"
            class="btn btn-info"
        >
          Create Product
        </router-link>
      </template>
      <template v-slot:item="slotProps">
        <router-link
            :to="{ name: 'BusinessProductDetail', params: { businessId, productId: slotProps.item.id }}"
            class="text-decoration-none text-reset d-block hover-white-bg hover-scale-effect slightly-transparent-white-background my-2 p-3 rounded"
        >
          <product-catalogue-list-item
              :currency="currency"
              :product="slotProps.item"
          />
        </router-link>
      </template>
      <template v-slot:no-items>
        <p>No products found. Why not add one?</p>
      </template>
    </sorted-paginated-item-list>
    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.query"
        :show="apiErrorMessage !== null"
        title="Error viewing business catalog"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import ErrorModal from "@/components/ErrorModal.vue";
import SortedPaginatedItemList from "@/components/SortedPaginatedItemList";
import ProductCatalogueListItem from "@/components/cards/ProductCatalogueCard";

import {helper} from "@/helper";
import {Api} from "@/Api";

// Sort options need to be in [{name, sortMethod}] format but since product is a simple object, it has been put in a more compact and easier to edit form and then immediately mapped to the required format
const sortOptions = Object.entries({
  id: 'Product Code',
  name: 'Name',
  manufacturer: 'Manufacturer',
  recommendedRetailPrice: 'RRP',
  created: 'Date Created',
  // Dates can be sorted as strings in ISO8601 format
  description: 'Description'
}).map(([key, name]) => ({
  name,
  sortMethod: helper.sensibleSorter(key)
}));

export default {
  name: "ProductCatalogue",
  components: {
    ErrorModal,
    SortedPaginatedItemList,
    ProductCatalogueListItem
  },

  props: {
    businessId: {
      required: true,
      type: Number
    },
  },

  data() {
    return {
      products: [],
      currency: null,
      sortOptions,
      // use first sort option as default
      currentSortOption: {...sortOptions[0], reversed: false},
      apiErrorMessage: null,
      businessName: null
    }
  },

  beforeMount: async function () {
    const success = await this.query();
    if (success) {
      await Promise.allSettled([this.loadCurrencies(), this.loadBusinessName()]);
    }
  },

  methods: {
    /**
     * Loads currency info
     * @return true on success
     */
    loadCurrencies: async function () {
      if (!this.$stateStore.getters.canEditBusiness(this.businessId)) {
        return false;
      }

      try {
        this.currency = await this.$helper.getCurrencyForBusiness(this.businessId,
            this.$stateStore);
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        // If can't get currency not that big of a deal
        // this.apiErrorMessage = err.userFacingErrorMessage;
        return false;
      }
      return true;
    },

    /**
     * Sends API request and sets results variable
     * If they are not admin or acting as the business just returns
     * @return true on success
     */
    query: async function () {
      if (!this.$stateStore.getters.canEditBusiness(this.businessId)) {
        return false;
      }

      try {
        this.products = (await Api.getProducts(this.businessId)).data;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
        return false;
      }

      return true;
    },

    /*
     * Attempts to update the business name
     */
    loadBusinessName: async function () {
      this.businessName = await this.$helper.tryGetBusinessName(this.businessId);
    }
  },

  watch: {
    businessName() {
      if (this.businessName != null) {
        document.title = `Product Catalogue for ${this.businessName}`
      }
    }
  }
};
</script>
