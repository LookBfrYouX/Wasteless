<template>
  <div class="w-100">
    <div v-if="results.length != 0" class="w-100">
      <div v-if="!isVisible" class="button-expand-sidebar-wrapper">
        <button class="btn btn-info mt-2" type="button" v-on:click="toggleSidebar()">
          <span>Sort results</span>
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
                  v-for="[key, value] in Object.entries({firstName: 'First Name', middleName: 'Middle Name', lastName: 'Last Name', nickname: 'Nickname', region: 'Region', city: 'City', country: 'Country', email: 'Email Address'})"
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
            Displaying results {{ 10 * this.pageNum + 1 }} -
            {{ Math.min(results.length, 10 * (this.pageNum + 1)) }}
            out of {{ results.length }}
            <ul class="list-unstyled list-group">
              <!--viewUser method uses router.push to display profile page-->
              <li v-for="(user, index) in displayedResults" v-bind:key="index"
                  class="list-group-item card " v-on:click="viewUser(user.id)">
                <div class="d-flex flex-wrap justify-content-between">
                  <h4 class="card-title mb-0">{{ user.firstName }} {{ user.middleName }}
                    {{ user.lastName }} {{ user.nickname ? `(${user.nickname})` : "" }}</h4>
                  <span class="text-muted">{{
                      user.role && user.role === 'ROLE_ADMIN' ? 'Admin' : ''
                    }}</span>
                </div>
                <div class="text-muted">{{ user.email }}</div>
                <div v-if="user.homeAddress" class="text-muted">{{ user.homeAddress.streetNumber }}
                  {{ user.homeAddress.streetName }}, {{ user.homeAddress.city }},
                  {{ user.homeAddress.region }}, {{ user.homeAddress.country }}
                  {{ user.homeAddress.postcode }}
                </div>
                <div v-else class="text-muted">Address unknown</div>
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
    <div v-else class="container pt-4">
      <h4>No results found</h4>
    </div>
    <error-modal
        title="Error making search request"
        v-bind:hideCallback="() => apiErrorMessage = null"
        v-bind:refresh="true"
        v-bind:retry="this.query"
        v-bind:show="apiErrorMessage !== null"
        v-bind:goBack="false"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<script>
import ErrorModal from "./Errors/ErrorModal.vue";

const { Api } = require("./../Api.js");

const SearchResults = {
  name: "SearchResults",
  components: {ErrorModal},
  /*has a search prop from app.vue*/
  props: ['search'],
  data: function () {
    /* setting intial state */
    return {
      results: [],
      pageNum: 0, // Page number starts from 0 but it will shown as 1 on UI
      resultsPerPage: 10,
      highlightedItem: null,
      pages: [],
      sortBy: null,
      reversed: false,
      isVisible: false,
      apiErrorMessage: null,
    }
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
     * Sends API request and sets results variable
     */
    query: async function () {
      /* makes a query to the api to search for the prop value from the app.vue main page*/
      try {
        const {data} = await Api.search(this.search);
        this.results = this.parseSearchResults(data);
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
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
      let page = this.pageNum;
      let resultsPerPage = this.resultsPerPage;
      let from = page * resultsPerPage;
      let to = from + resultsPerPage;
      return results.slice(from, to);
    },

    viewUser(userId) {
      this.$router.push({
        name: "profile",
        params: {
          userId
        }
      });
    }
  },

  computed: {
    sortedResults() {
      if (this.sortBy == null) {
        return this.results;
      }

      let formatter = user => {
        if (user[this.sortBy] == null) {
          return "";
        }
        return user[this.sortBy].toLowerCase();
      }

      return this.results.sort((a, b) => { // sort using this.orderBy
        return (this.reversed ? -1 : 1) * (formatter(a) > formatter(b) ? 1 : -1);
      })
    },

    displayedResults() {
      return this.paginate(this.sortedResults);
    }
  },
  watch: {
    results() {
      this.setPages();
    },
    search() {
      this.pageNum = 0;
      this.setPages();
      this.query();
    }
  },
  created() {
    this.query();
    // When this is created, sends the query to the parent
    // Necessary as router.js is aware of the param but App.js,
    // which has the input field, is not
    this.$emit("searchresultscreated", this.search);
  },
};

export default SearchResults
</script>

<style>
button.page-link {
  display: inline-block;
}

button.page-link {
  font-size: 20px;
  color: #29b3ed;
  font-weight: 500;
}

.offset {
  width: 500px !important;
  margin: 20px auto;
}

.button-expand-sidebar-wrapper {
  position: fixed;
  left: 0;
  padding-left: 15px;
  z-index: 1000;
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
