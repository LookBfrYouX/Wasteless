<template>
  <div class="w-100" v-if="results.length != 0">
    <div class="button-expand-sidebar-wrapper" v-if="!isVisible" >
      <button v-on:click="toggleSidebar()" type="button" class="btn btn-info mt-2">
        <span>Sort results</span>
      </button>
    </div>
    <div class="p-relative w-100 d-flex align-items-stretch">
      <div class="sort-results bg-light" v-if="isVisible">
        <div class="p-3">
          <h3 class="d-inline">Sort by</h3>
          <button v-on:click="toggleSidebar()" type="button" class="float-right btn btn-light">
            <span>&larr;</span>
          </button>
          <ul class="list-unstyled" id="search-headers" v-bind:class='{"table-reversed": reversed}'>
            <li
              v-for="[key, value] in Object.entries({firstName: 'First Name', middleName: 'Middle Name', lastName: 'Last Name', nickname: 'Nickname', region: 'Region', city: 'City', country: 'Country', email: 'Email Address'})"
              v-bind:key="key"
              v-on:click="sortByClicked(key)"
              v-bind:class='{"current-sort": sortBy==key}'
              class="mb-1"
              > {{ value }} </li>
          </ul>
        </div>
      </div>
      <div class="overlay w-100 d-md-none" v-if="isVisible" v-on:click="toggleSidebar()"></div>
      <div class="results-content container d-flex justify-content-end justify-content-md-center">
        <div class="results-wrapper col-12 col-md-8 mt-5">
        Displaying results {{10 * this.pageNum + 1}} -
                         {{Math.min(results.length, 10 * (this.pageNum + 1))}}
                         out of {{results.length}}
          <ul class="list-unstyled list-group">
            <!--viewUser method uses router.push to display profile page-->
            <li class="list-group-item card " v-on:click="viewUser(user.id)" v-for="(user, index) in displayedResults" v-bind:key="index">
              <div class="d-flex flex-wrap justify-content-between">
                <h4 class="card-title mb-0">{{user.firstName}} {{user.middleName}} {{user.lastName}} {{user.nickname? `(${user.nickname})`: ""}}</h4>
                <span class="text-muted">{{ user.role && user.role === 'ROLE_ADMIN' ? 'Admin' : '' }}</span>
              </div>
              <div class="text-muted">{{user.email}}</div>
              <div class="text-muted">{{[user.region, user.city, user.country].filter(Boolean).join(', ')}}</div>
            </li>
          </ul>
          <div aria-label="table-nav" class="mt-2">
            <ul class="paginate list-unstyled d-flex justify-content-center">
              <li class="pageItem">
                <button type="button" name="button" class="page-link" v-if="pageNum > 0" v-on:click="pageNum--">Previous</button>
              </li>
              <!--number of buttons scale to amount of pages-->
              <li class="page-item" v-for="pageNumber in pages.slice(Math.max(0, pageNum - 1), pageNum + 5)" :key="pageNumber">
                <button type="button" name="button" class="page-link" v-on:click="pageNum=pageNumber">{{pageNumber + 1}}</button>
              </li>
              <li>
                <button type="button" name="button" class="page-link" v-if="pageNum < pages.length - 1" v-on:click="pageNum++">Next</button>
              </li>
            </ul>
          </div>
        </div>
      </div>

    </div>
  </div>
  <div v-else>
    No results found
  </div>
</template>

<script>
  const Api = require("./../Api").default;

  const SearchResults = {
    name: "SearchResults",
    /*has a search prop from app.vue*/
    props: ['search'],
    data: function() {
      /* setting intial state */
      return {
        results: [],
        pageNum: 0, // Page number starts from 0 but it will shown as 1 on UI
        resultsPerPage: 10,
        highlightedItem:null,
        pages: [],
        sortBy: null,
        reversed: false,
        isVisible: false
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
      query() {
        /* makes a query to the api to search for the prop value from the app.vue main page*/
        Api.search(this.search)
        .then((response) => {
          /* saves data of response as this.results*/
          this.results = this.parseSearchResults(response.data);
        })
        .catch(err => {
          alert(err.userFacingErrorMessage);
        });
      },

      parseSearchResults: function(results) {
        return results.map(user => {
          let region = '';
          let city = '';
          let country = '';
          if (user.homeAddress != undefined) {
            const address = user.homeAddress.split(",").map(item => item.trim());
            if (address.length > 1) country = address[address.length - 1];
            if (address.length > 2) city = address[address.length - 2];
            if (address.length > 3) region = address[address.length - 3];
          }
          return {
            region,
            city,
            country,
            ...user
          }
        });
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
    sortedResults () {
      if (this.sortBy == null) {
        return this.results;
      }

      let formatter = user => {
        if (user[this.sortBy] == null) return "";
        return user[this.sortBy].toLowerCase();
      }

      return this.results.sort((a, b) => { // sort using this.orderBy
        return (this.reversed? -1: 1) * (formatter(a) > formatter(b)? 1:-1);
        })
      },

    displayedResults () {
       return this.paginate(this.sortedResults);
    }
  },
  watch: {
    results () {
      this.setPages();
    },
    search () {
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
