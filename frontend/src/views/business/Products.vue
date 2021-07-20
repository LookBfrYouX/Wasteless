<template>
  <div class="w-100 col-12 col-md-8 col-lg-6">
<!--  Page Title Area  -->
    <div>
      <h2>Product Catalogue for {{ businessName }}</h2>
    </div>
<!--  Page Content Area  -->
    <div class="d-inline-flex flex-wrap-reverse col-12">
<!--  Sort and Meta info Bar    -->
      <div class="col-12 col-lg-6">
        <simple-sort-bar @update="sortUpdate" :items="items" />
      </div>
      <div class="col-12 col-lg-6">
        Displaying products {{ this.searchParams.pagStartIndex + 1 }} - {{ this.searchParams.pagEndIndex + 1 }} out of
        {{ this.totalResults }}
      </div>
    </div>
<!-- Product List   -->
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
<!-- Pagination Bar   -->
    <v-pagination
        v-model="page"
        :length="totalPages"
        @input="pageUpdate"
        @next="pageUpdate"
        @previous="pageUpdate"
    />
<!-- Error Component - Not Visible Component   -->
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
import SimpleSortBar from "@/components/SimpleSortBar";


export default {
  name: "ProductCatalogue",
  components: {
    SimpleSortBar,
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
      page: 1, // The default starting page.
      itemsPerPage: 3, // The number of items to display on each page.
      totalResults: 0, // The total number of results. Only 1 page is retrieved at a time.
      searchParams: {
        pagStartIndex: 0, // The default start index. Overridden in beforeMount.
        pagEndIndex: 0, // The default end index. Overridden in beforeMount.
        sortBy: "name-acs",
      },
      products: [],
      currency: null,
      apiErrorMessage: null,
      businessName: null,
      items: [ // Sort options. Key is displayed and value is emitted when selection changes.
        { key: "Name A-Z", value: "name-asc" },
        { key: "Name Z-A", value: "name-desc" },
        { key: "RRP Lowest", value: "recommendedRetailPrice-asc" },
        { key: "RRP Highest", value: "recommendedRetailPrice-desc" },
      ],
    }
  },

  beforeMount: async function () {
    const success = await this.query();
    if (success) {
      await Promise.allSettled([this.loadCurrencies(), this.loadBusinessName(), this.pageUpdate()]);
    }
  },

  computed: {
    /**
     * Computes the total number of pages for the pagination component.
     */
    totalPages: function () {
      return Math.floor((this.totalResults - 1) / this.itemsPerPage) + 1;
    }
  },

  methods: {
    /**
     * Updates the search query and retrieves the new data.
     */
    sortUpdate: async function (sortBy) {
      this.searchParams.sortBy = sortBy;
      this.page = 1;
      await this.pageUpdate();
    },
    /**
     * Updates page when pagination buttons are pressed.
     */
    pageUpdate: async function () {
      this.searchParams.pagStartIndex = ((this.page - 1) * this.itemsPerPage);
      this.searchParams.pagEndIndex = Math.min((this.page * this.itemsPerPage) -1, this.totalResults - 1);
      await this.query();
      window.scrollTo(0,0);
    },
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
        const response = (await Api.getProducts(this.businessId, this.searchParams)).data;
        this.products = response.results;
        this.totalResults = response.totalCount;
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
