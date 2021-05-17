<template>
  <div class="w-100">
    <div class="w-100">
      <div class="button-expand-sidebar-wrapper mt-2 mx-2">
        <button v-if="results.length && !isVisible" class="btn btn-info" type="button"
                v-on:click="toggleSidebar()">
          <span>Sort results</span>
        </button>
        <!-- create product button doesn't really belong here, but want it aligned with the sort results button -->
        <button class="btn btn-info float-right" type="button" v-on:click="createProduct">
          <span>Create Product</span>
        </button>
      </div>
      <div v-if="isVisible" class="overlay w-100 d-md-none" v-on:click="toggleSidebar()"></div>
      <div v-if="results.length" class="p-relative w-100 d-flex align-items-stretch">
        <div v-if="isVisible" class="sort-results bg-light">
          <div class="p-3">
            <h3 class="d-inline">Sort by</h3>
            <button class="float-right btn btn-light" type="button" v-on:click="toggleSidebar()">
              <span>&larr;</span>
            </button>
            <ul id="search-headers" class="list-unstyled"
                v-bind:class='{"table-reversed": reversed}'>
              <li
                  v-for="[key, value] in Object.entries({id: 'Product Code', name: 'Name', manufacturer: 'Manufacturer', recommendedRetailPrice: 'RRP', created: 'Created', description: 'Description'})"
                  v-bind:key="key"
                  class="mb-1"
                  v-bind:class='{"current-sort": sortBy==key}'
                  v-on:click="sortByClicked(key)"
              > {{ value }}
              </li>
            </ul>
          </div>
        </div>
        <div v-if="isVisible" class="overlay w-100 d-md-none" v-on:click="toggleSidebar()"></div>
        <div class="results-content container d-flex justify-content-end justify-content-md-center">
          <div class="results-wrapper col-12 col-md-8 mt-5">
            Displaying {{ this.resultsPerPage * this.pageNum + 1 }} -
            {{ Math.min(results.length, this.resultsPerPage * (this.pageNum + 1)) }}
            out of {{ results.length }}
            <ul class="list-unstyled list-group">
              <!--viewUser method uses router.push to display profile page-->
              <li v-for="(product, index) in displayedResults" v-bind:key="index"
                  class="list-group-item card"
                  v-on:click="viewProduct(product.id)">
                <div class="row">
                  <div class="col-3">
                    <img
                        v-if="getThumbnailImage(product.id) != null"
                        alt="Product Image"
                        class="image-fluid w-100 rounded-circle"
                        v-bind:src="getThumbnailImage(product.id)"
                    >
                    <img
                        v-else
                        alt="Product Image"
                        class="image-fluid w-100 rounded-circle"
                        src="./../../assets/images/default-product-thumbnail.svg"
                    >
                  </div>
                  <div class="col-9">
                    <div class="d-flex flex-wrap justify-content-between">
                      <h4 class="card-title mb-0">{{ product.name }} (Id: {{ product.id }})</h4>
                    </div>
                  </div>
                </div>
                <div class="text-muted">Manufacturer: {{ product.manufacturer }}</div>
                <div class="text-muted">Description: {{ product.description }}</div>
                <div class="text-muted">RRP: {{
                    $helper.makeCurrencyString(product.recommendedRetailPrice, currency)
                  }}
                </div>
                <div class="text-muted">Created: {{
                    $helper.isoToDateString(product.created)
                  }}
                </div>
              </li>
            </ul>
            <div aria-label="table-nav" class="mt-2">
              <ul class="paginate list-unstyled d-flex justify-content-center">
                <li class="pageItem">
                  <button v-if="pageNum > 0" class="page-link" name="button" type="button"
                          v-on:click="pageNum--">Previous
                  </button>
                </li>
                <!--number of buttons scale to amount of pages-->
                <li v-for="pageNumber in pages.slice(Math.max(0, pageNum - 1), pageNum + 5)"
                    :key="pageNumber"
                    class="page-item"
                    v-bind:class="{'active': pageNum == pageNumber}"
                >
                  <button class="page-link" name="button" type="button"
                          v-on:click="pageNum = pageNumber">{{ pageNumber + 1 }}
                  </button>
                </li>
                <li>
                  <button v-if="pageNum < pages.length - 1" class="page-link" name="button"
                          type="button" v-on:click="pageNum++">Next
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div v-if="results.length == 0" class="container pt-4">
        <h4>No results found</h4>
      </div>
    </div>
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

const {Api} = require("./../Api.js");

const ProductCatalogue = {
  name: "ProductCatalogue",
  components: {
    ErrorModal,
  },

  /*has a search prop from app.vue*/
  data: function () {
    /* setting intial state */
    return {
      results: [],
      pageNum: 0, // Page number starts from 0 but it will shown as 1 on UI
      resultsPerPage: this.$constants.PRODUCT_CATALOG.RESULTS_PER_PAGE,
      highlightedItem: null,
      pages: [],
      sortBy: null,
      reversed: false,
      isVisible: false,
      apiErrorMessage: null,
      currency: null
    }
  },

  props: {
    businessId: {
      required: true,
      type: Number
    },
  },

  beforeMount: async function () {
    const success = await this.query();
    if (success) await this.loadCurrencies();
  },

  methods: {
    toggleSidebar() {
      this.isVisible = !this.isVisible;
    },

    sortByClicked(newSortBy) {
      if (this.sortBy == newSortBy) {
        this.reversed = !this.reversed;
      }
      this.sortBy = newSortBy;
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

      let data;
      try {
        data = (await Api.getProducts(this.businessId)).data;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
        return false;
      }

      this.results = this.parseSearchResults(data);
      this.setPages();

      return true;
    },

    parseSearchResults: function (results) {
      return results;
    },

    setPages() {
      /* calculates number of pages which is reliant on resultsPerPage set in the data section*/
      let numOfPages = Math.ceil(this.results.length / this.resultsPerPage);
      this.pages = [];
      for (let i = 0; i < numOfPages; i++) {
        this.pages.push(i);

      }
    },

    paginate(results) {
      // calculates information for results
      let page = this.pageNum;
      let resultsPerPage = this.resultsPerPage;
      let from = page * resultsPerPage;
      let to = from + resultsPerPage;
      return results.slice(from, to);
    },

    createProduct() {
      this.$router.push("createProduct");
    },

    /**
     * Retreive product primary image URL to show as a thumbnail
     * If no thumbnail image returns null
     */
    getThumbnailImage(productId) {
      const products = this.results;
      const product = products.find(({id}) => id === productId);
      if (product && product.images && product.images[0]) {
        return product.images[0].thumbnailFilename;
      }
      return null;
    },

    /**
     * Go to product detail page by passing the product id
     */
    viewProduct(productId) {
      this.$router.push({
        name: "productDetail",
        params: {
          businessId: this.businessId,
          productId
        }
      });
    }
  },
  computed: {
    /*computed comes after created*/
    sortedResults() {
      if (this.sortBy == null) {
        return this.results;
      }

      let formatter = product => {
        if (product[this.sortBy] == null) {
          return "";
        }
        return product[this.sortBy];
      }

      return this.results.sort((a, b) => { // sort using this.orderBy
        return (this.reversed ? -1 : 1) * (formatter(a) > formatter(b) ? 1 : -1);
      })
    },
    // used for for loop in html
    displayedResults() {
      return this.paginate(this.sortedResults);
    },
  },

  watch: {
    // /**
    //  * Watch acting as is switched by clicking navbar dropdown
    //  */
    // businessId() {
    //   this.$helper.goToProfile.bind(this)();
    // }
  }
};

export default ProductCatalogue;
</script>

<style>

button.page-link {
  display: inline-block;
  font-size: 20px;
  color: #29b3ed;
  font-weight: 500;
}

.button-expand-sidebar-wrapper {
  position: fixed;
  left: 0;
  right: 0;
  z-index: 910;
}

.sort-results {
  width: 200px;
  position: fixed;
  height: 100vh;
  z-index: 999;
}

.sort-results li.current-sort::after {
  content: '\2191';
}

.sort-results .table-reversed li.current-sort::after {
  content: '\2193';
}

.overlay {
  position: fixed;
  height: 100vh;
  z-index: 900;
}
</style>
