<template>
  <div class="bprofile-card card container">
    <div>
      <h1 class="title">Business Information</h1>
      <ul class="bprofile-info list-unstyled">
        <li class="row">
          <dt class="col-md label">Business Title:</dt>
          <dd class="col-md value"> {{ businessInfo.name }}</dd>
        </li>
        <li class="row">
          <dt class="col-md label">Description:</dt>
          <dd class="col-md value"> {{ businessInfo.description }}</dd>
        </li>
        <li class="row">
          <dt class="col-md label">Address:</dt>
          <dd class="col-md value"> {{
              [businessInfo.homeAddress.streetNumber + " " +
              businessInfo.homeAddress.streetName, businessInfo.homeAddress.city,
                businessInfo.homeAddress.region, businessInfo.homeAddress.country,
                businessInfo.homeAddress.postcode].join(", ")
            }}
          </dd>
        </li>
        <li class="row">
          <dt class="col-md label">Business Type:</dt>
          <dd class="col-md value"> {{ businessInfo.businessType }}</dd>
        </li>
        <li class="row" v-if="Array.isArray(products) && products.length !== 0">
          <dt class="col-md label">Products:</dt>
        </li>
      </ul>
      <div v-if="products.length != 0" class="w-100">
        <div v-if="!isVisible" class="button-expand-sidebar-wrapper">
          <button class="btn btn-info mt-2" type="button" v-on:click="toggleSidebar()">
            <span>Sort products</span>
          </button>
        </div>
        <div class="p-relative w-100 d-flex align-items-stretch">
          <div v-if="isVisible" class="sort-results bg-light">
            <div class="p-3">
              <h3 class="d-inline">Sort by</h3>
              <button class="float-right btn btn-light" type="button" v-on:click="toggleSidebar()">
                <span>&larr;</span>
              </button>
              <ul id="search-headers" class="list-unstyled"
                  v-bind:class='{"table-reversed": reversed}'>
                <li
                    v-for="[key, value] in Object.entries({name: 'Product Name', recommendedRetailPrice: 'Price', created: 'Date Created', id: 'Product Code'})"
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
              Displaying products {{ 10 * this.pageNum + 1 }} -
              {{ Math.min(products.length, 10 * (this.pageNum + 1)) }}
              out of {{ products.length }}
              <ul class="list-unstyled list-group">
                <!--viewUser method uses router.push to display profile page-->
                <li v-for="(product, index) in displayedProducts" v-bind:key="index"
                    class="list-group-item card">
                  <div class="d-flex flex-wrap justify-content-between">
                    <h4 class="card-title mb-0">{{ product.name }}</h4>
                    <span class="text-muted">{{ product.recommendedRetailPrice }}</span>
                  </div>
                  <div class="text-muted">{{ product.description }}</div>
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
                      class="page-item">
                    <button class="page-link" name="button" type="button"
                            v-on:click="pageNum=pageNumber">{{ pageNumber + 1 }}
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
      </div>
      <div v-if="errorMessage.length > 0" class="row mt-2">
        <div class="col">
          <p class="alert alert-warning">{{ errorMessage }}</p>
        </div>
      </div>
      <button
          class="btn btn-white-bg-primary mx-1 d-flex"
          type="button"
          v-on:click="createProduct()"
      >
        <span class="material-icons mr-1">person</span>
        Add Product To Catalogue
      </button>
    </div>
  </div>
</template>

<script>
const Api = require("./../Api").default;

export default {
  name: 'businessProfile',
  components: {},

  data() {
    return {
      businessInfo: {
        name: "",
        description: "",
        homeAddress: "",
        businessType: "",
      },
      products: [],
      pageNum: 0, // Page number starts from 0 but it will shown as 1 on UI
      resultsPerPage: 10,
      pages: [],
      sortBy: null,
      reversed: false,
      isVisible: false,
      errorMessage: "",
    }
  },

  props: {
    businessId: {
      type: Number,
      required: true
    }
  },

  beforeMount: function () {
    // gets user information from api
    this.parseApiResponse(this.callApi(this.businessId))
    this.getProducts()
  },

  methods: {
    /**
     * Calls the API to get profile information with the given business ID
     * Returns the promise, not the response
     */
    callApi: function (businessId) {
      return Api.businessProfile(businessId);
    },

    createProduct: function() {
      this.$router.push({name: "createProduct"});
    },

    /**
     * Parses the API response given a promise to the request
     */
    parseApiResponse: async function (apiCall) {
      try {
        const response = await apiCall;
        console.log(response.data);
        this.businessInfo = response.data;
      } catch (err) {
        alert(
            err.userFacingErrorMessage == undefined ? err.toString() : err.userFacingErrorMessage);
      }
    },

    /**
     * Calls the API to get product information with the current business ID
     */
    getProducts: async function () {
      try {
        const response = await Api.getProducts(this.businessId);
        console.log(response.data);
        this.products = response.data;
      } catch (err) {
        alert(
            err.userFacingErrorMessage == undefined ? err.toString() : err.userFacingErrorMessage);
      }
    },
    toggleSidebar() {
      this.isVisible = !this.isVisible;
    },
    sortByClicked(newSortBy) {
      if (this.sortBy == newSortBy) {
        this.reversed = !this.reversed;
      }
      this.sortBy = newSortBy;
    },
    setPages() {
      /* calculates number of pages which is reliant on resultsPerPage set in the data section*/
      let numOfPages = Math.ceil(this.products.length / this.resultsPerPage);
      this.pages = [];
      for (let i = 0; i < numOfPages; i++) {
        this.pages.push(i);
      }
    },

    paginate(results) {
      let page = this.pageNum;
      let resultsPerPage = this.resultsPerPage;
      let from = page * resultsPerPage;
      let to = from + resultsPerPage;
      return results.slice(from, to);
    },

    sortProducts() {
      if (this.sortBy == null) {
        return this.products;
      }

      let formatter = product => {
        if (product[this.sortBy] == null) {
          return "";
        }
        return product[this.sortBy].toLowerCase();
      }

      return this.products.sort((a, b) => { // sort using this.orderBy
        return (this.reversed ? -1 : 1) * (formatter(a) > formatter(b) ? 1 : -1);
      })
    }
  },

  computed: {
    sortedProducts: function () {
      return this.sortProducts()
    },

    displayedProducts() {
      return this.paginate(this.sortedProducts);
    }
  },

  watch: {
    businessId: function () {
      this.parseApiResponse(this.callApi(this.businessId))
    },
    products: function () {
      this.pageNum = 0;
      this.setPages();
      this.getProducts()
    },
  },
}
</script>

<style scoped>
.main {
  line-height: 1.1;
}

.bprofile-info li {
  margin-bottom: 0.5em;
}

.bprofile-info {
  font-size: 1.1em;
}

.bprofile-card {
  margin: 30px;
  border: none;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  padding: 30px;
}

</style>
