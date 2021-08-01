<template>
  <div
      class="w-100 col-12 col-md-8 col-lg-6 pt-0 pt-md-15 pt-lg-2 align-items-center container-fluid">
    <!--  Page Title Area  -->
    <div class="d-flex flex-sm-wrap pb-3 pb-md-0 align-items-center container-fluid">
      <div class="row mt-4 align-items-center">
        <h2 class="col-lg-8 pl-0">Product Catalogue for {{ businessName }}</h2>
        <router-link :to="{name: 'BusinessProductCreate', params: { businessId }}"
                     class="btn col-12 col-lg-4 btn-info d-flex h-100">
          <span class="material-icons mr-1">add</span>
          Create Product
        </router-link>
      </div>
    </div>
    <!--  Page Content Area  -->
    <div v-if="products.length" class="container-fluid align-items-center">
      <!--  Sort and Meta info Bar    -->
      <div class="col-12 col-lg-6 pb-0">
        <simple-sort-bar :items="items" @update="sortUpdate"/>
      </div>
      <div class="col-12 col-lg-6 d-flex flex-lg-row-reverse align-items-center">
        <span>
          Displaying products {{
            this.searchParams.pagStartIndex + 1
          }} - {{ this.searchParams.pagEndIndex + 1 }} out of
          {{ this.totalResults }}
        </span>
      </div>

      <!-- Product List   -->
      <ul class="list-unstyled pl-0">
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
          class="w-100"
          @input="pageUpdate"
          @next="pageUpdate"
          @previous="pageUpdate"
      />
    </div>
    <div v-else>
      No products yet
    </div>
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
      itemsPerPage: this.$constants.SORTED_PAGINATED_ITEM_LIST.RESULTS_PER_PAGE, // The number of items to display on each page.
      totalResults: 0, // The total number of results. Only 1 page is retrieved at a time.
      searchParams: {
        pagStartIndex: 0, // The default start index. Overridden in beforeMount.
        pagEndIndex: 0, // The default end index. Overridden in beforeMount.
        sortBy: "name",
        isAscending: true
      },
      products: [],
      currency: null,
      apiErrorMessage: null,
      businessName: null,
      items: [ // Sort options. Key is displayed and value is emitted when selection changes.
        {key: "Name A-Z", value: "name", isAscending: true},
        {key: "Name Z-A", value: "name", isAscending: false},
        {key: "Lowest RRP", value: "recommendedRetailPrice", isAscending: true},
        {key: "Highest RRP", value: "recommendedRetailPrice", isAscending: false},
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
    sortUpdate: async function (sortBy, isAscending) {
      this.searchParams.sortBy = sortBy;
      this.searchParams.isAscending = isAscending;
      this.page = 1;
      await this.pageUpdate();
    },
    /**
     * Updates page when pagination buttons are pressed.
     */
    pageUpdate: async function () {
      this.searchParams.pagStartIndex = ((this.page - 1) * this.itemsPerPage);
      this.searchParams.pagEndIndex = Math.max(0,
          Math.min((this.page * this.itemsPerPage) - 1, this.totalResults - 1));
      await this.query();
      window.scrollTo(0, 0);
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
