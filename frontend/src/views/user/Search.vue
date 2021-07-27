<template>
  <div class="w-100 col-12 col-md-8 col-lg-6 pt-0 pt-md-15 pt-lg-2 align-items-center container-fluid">
    <!--  Page Title Area  -->
    <div class="d-flex flex-sm-wrap pb-3 pb-md-0 align-items-center container-fluid">
      <div class="row mt-4 align-items-center">
        <h2 class="col-lg-8 pl-0">Search Results for '{{ search }}'</h2>
      </div>
    </div>
    <!--  Page Content Area  -->
    <div v-if="userResults.length" class="container-fluid align-items-center">
      <!--  Sort and Meta info Bar    -->
      <div class="col-12 col-lg-6 pb-0">
        <simple-sort-bar @update="sortUpdate" :items="items"/>
      </div>
      <div class="col-12 col-lg-6 d-flex flex-lg-row-reverse align-items-center">
        <span>
          Displaying results {{ this.searchParams.pagStartIndex + 1 }} - {{ this.searchParams.pagEndIndex + 1 }} out of
          {{ this.totalResults }}
        </span>
      </div>

      <!-- Product List   -->
      <ul class="list-unstyled pl-0">
        <li v-for="user in userResults" :key="user.id">
          <router-link
              :to="{ name: 'UserDetail', params: { userId: user.id }}"
              class="text-decoration-none text-reset d-block hover-white-bg hover-scale-effect slightly-transparent-white-background my-1 p-3 rounded"
          >
            <user-list-item
                :user="user"
            />
          </router-link>
        </li>
      </ul>
      <!-- Pagination Bar   -->
      <v-pagination
          class="w-100"
          v-model="page"
          :length="totalPages"
          @input="pageUpdate"
          @next="pageUpdate"
          @previous="pageUpdate"
      />
    </div>
    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.query"
        :show="apiErrorMessage !== null"
        title="Error making search request"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import ErrorModal from "@/components/ErrorModal.vue";
import UserListItem from "@/components/cards/UserCard";

import {Api} from "@/Api"
import SimpleSortBar from "@/components/SimpleSortBar";


export default {
  name: "Search",
  components: {
    SimpleSortBar,
    ErrorModal,
    UserListItem,
  },
  /*has a search prop from app.vue*/
  props: {
    search: {
      required: false,
      type: String
    }
  },

  data() {
    return {
      page: 1, // The default starting page.
      itemsPerPage: this.$constants.SORTED_PAGINATED_ITEM_LIST.RESULTS_PER_PAGE, // The number of items to display on each page.
      totalResults: 0, // The total number of results. Only 1 page is retrieved at a time.
      searchParams: {
        searchQuery: this.search,
        pagStartIndex: 0, // The default start index. Overridden in beforeMount.
        pagEndIndex: 0, // The default end index. Overridden in beforeMount.
        sortBy: "firstName",
        isAscending: true
      },
      userResults: [],
      // Use first sort option as default
      apiErrorMessage: null,
      items: [ // Sort options. Key is displayed and value is emitted when selection changes.
        {key: "First name A-Z", value: "firstName", isAscending: true},
        {key: "First name Z-A", value: "firstName", isAscending: false},
        {key: "Middle name A-Z", value: "middleName", isAscending: true},
        {key: "Middle name Z-A", value: "middleName", isAscending: false},
        {key: "Last name A-Z", value: "lastName", isAscending: true},
        {key: "Last name Z-A", value: "lastName", isAscending: false},
        {key: "Nickname A-Z", value: "nickname", isAscending: true},
        {key: "Nickname Z-A", value: "nickname", isAscending: false},
      ],
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
      this.searchParams.pagEndIndex = Math.max(0, Math.min((this.page * this.itemsPerPage) - 1, this.totalResults - 1));
      await this.query();
      window.scrollTo(0, 0);
    },
    /**
     * Sends API request and sets results variable
     */
    query: async function () {
      /* makes a query to the api to search for the prop value from the app.vue main page*/
      try {
        const response = (await Api.search(this.searchParams)).data;
        this.userResults = response.results;
        this.totalResults = response.totalCount;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },
  },

  watch: {
    async search() {
      this.searchParams.searchQuery = this.search;
      await this.query();
      await this.pageUpdate();
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

  created: async function () {
    await this.query();
    await this.pageUpdate();
    // When this is created, sends the query to the parent
    // Necessary as router.js is aware of the param but App.js,
    // which has the input field, is not
    this.$emit("initial-search-value", this.search);
  },
};
</script>

<style scoped>

button.page-link {
  display: inline-block;
  font-size: 20px;
  color: #29b3ed;
  font-weight: 500;
}
</style>
