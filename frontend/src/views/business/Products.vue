<template>
  <div class="w-100 col-12 col-md-8 col-lg-6">
    <div>
      Displaying page {{ this.page }} out of
      {{ this.totalPages }}
    </div>
    <ul class="list-unstyled">
      <li v-for="product in products" :key="product.id">
        <router-link
            :to="{ name: 'BusinessProductDetail', params: { businessId, productId: product.id }}"
            class="text-decoration-none text-reset d-block hover-white-bg hover-scale-effect slightly-transparent-white-background my-2 p-3 rounded"
        >
          <product-catalogue-list-item
              :currency="currency"
              :product="product"
          />
        </router-link>
      </li>
    </ul>
    <v-pagination v-model="page" :length="6" />
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
import ProductCatalogueListItem from "@/components/cards/ProductCatalogueCard";

import {Api} from "@/Api";


export default {
  name: "ProductCatalogue",
  components: {
    ErrorModal,
    ProductCatalogueListItem,
  },

  props: {
    businessId: {
      required: true,
      type: Number
    },
  },

  data() {
    return {
      page: 1,
      totalPages: 10,
      products: [],
      currency: null,
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
        const response = (await Api.getProducts(this.businessId)).data;
        this.products = response.results;
        this.totalResults = response.totalCount;
        //console.log(this.products);
        console.log(this.totalResults);
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
