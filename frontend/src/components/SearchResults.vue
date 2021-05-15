<template>
  <div class="w-100">
    <sorted-paginated-item-list
      v-bind:items="results"
      v-bind:sortOptions="sortOptions"
      v-bind:currentSortOption.sync="currentSortOption"
    >
      <template v-slot:title>
        <h2>Search Results</h2>
      </template>
      <template v-slot:item="slotProps">
        <router-link
          v-bind:to="{ name: 'profile', params: { userId: slotProps.item.id }}"
          class="text-decoration-none text-reset d-block hover-white-bg hover-scale-effect slightly-transparent-white-background my-1 p-3 rounded"
        >
          <user-list-item
            v-bind:user="slotProps.item"
          />
        </router-link>
      </template>
      <template v-slot:no-items>
        <p>No results found - please try a different search term</p>
      </template>
    </sorted-paginated-item-list>
    <error-modal
        title="Error making search request"
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
import SortedPaginatedItemList from "./SortedPaginatedItemList";
import UserListItem from "./UserListItem";

import { helper } from "./../helper";
import { Api } from "./../Api"


// Sort options need to be in [{name, sortMethod}] format but since users are simple objects, it has been put in a more compact and easier to edit form and then immediately mapped to the required format
const sortOptions = Object.entries({
  firstName: 'First Name',
  middleName: 'Middle Name',
  lastName: 'Last Name',
  nickname: 'Nickname',
  region: 'Region',
  city: 'City',
  country: 'Country',
}).map(([key, name]) => ({
  name,
  sortMethod: helper.sensibleSorter(key)
}));


export default {
  name: "SearchResults",
  components: {
    ErrorModal,
    SortedPaginatedItemList,
    UserListItem
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
      results: [],
      sortOptions,
      // Use first sort option as default
      currentSortOption: {...sortOptions[0], reversed: false },
      apiErrorMessage: null,
    }
  },
  methods: {
    /**
     * Sends API request and sets results variable
     */
    query: async function () {
      /* makes a query to the api to search for the prop value from the app.vue main page*/
      try {
        this.results = (await Api.search(this.search)).data;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },
  },

  watch: {
    search() {
      return this.query();
    }
  },

  created: async function() {
    await this.query();
    // When this is created, sends the query to the parent
    // Necessary as router.js is aware of the param but App.js,
    // which has the input field, is not
    this.$emit("searchresultscreated", this.search);
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
