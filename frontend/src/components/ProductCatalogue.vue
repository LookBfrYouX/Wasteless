<template>
  <div class="w-100">
    <sorted-paginated-item-list
      v-bind:items="products"
      v-bind:sortOptions="sortOptions"
      v-bind:currentSortOption.sync="currentSortOption"
    >
      <template v-slot:title>
        <h2>Product Catalogue</h2>
      </template>
      <template v-slot:right-button>
        <router-link
          v-bind:to="{name: 'createProduct', params: { businessId }}"
          class="btn btn-info"
        >
          Create Product
        </router-link>
      </template>
      <template v-slot:item="slotProps">
        <router-link
          v-bind:to="{ name: 'productDetail', params: { businessId, productId: slotProps.item.id }}"
          class="text-decoration-none text-reset"
        >
          <product-catalogue-list-item
            v-bind:product="slotProps.item"
            v-bind:currency="currency"
          />
        </router-link>
      </template>
      <template v-slot:no-items>
        <p>No products found. Why not add one?</p>
      </template>
    </sorted-paginated-item-list>
    <not-acting-as-business v-bind:businessId="businessId"/>
    <error-modal
        title="Error viewing business catalog"
        v-bind:goBack="false"
        v-bind:hideCallback="() => apiErrorMessage = null"
        v-bind:refresh="true"
        v-bind:retry="this.query"
        v-bind:show="apiErrorMessage !== null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import ErrorModal from "./Errors/ErrorModal.vue";
import NotActingAsBusiness from './Errors/NotActingAsBusiness.vue';
import SortedPaginatedItemList from "./SortedPaginatedItemList";
import ProductCatalogueListItem from "./ProductCatalogueListItem";

import { helper } from "./../helper";
import { Api } from "./../Api";

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
    NotActingAsBusiness,
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
      currentSortOption: { ...sortOptions[0], reversed: false },
      apiErrorMessage: null,
    }
  },

  beforeMount: async function () {
    const success = await this.query();
    if (success) await this.loadCurrencies();
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
  },
};
</script>
