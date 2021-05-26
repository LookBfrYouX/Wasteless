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
          v-bind:to="{ name: 'UserDetail', params: { userId: slotProps.item.id }}"
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
import ErrorModal from "../components/ErrorModal.vue";
import SortedPaginatedItemList from "../components/SortedPaginatedItemList";
import UserListItem from "../components/cards/UserCard";

import { helper } from "../helper";
import { Api } from "@/Api"


const sortOptions = [
  {
    name: "First Name",
    sortMethod: helper.sensibleSorter("firstName")
  }, {
    name: "Middle Name",
    sortMethod: helper.sensibleSorter("middleName") 
  }, {
    name: "Last Name",
    sortMethod: helper.sensibleSorter("lastName")
  }, {
    name: "Nickname",
    sortMethod: helper.sensibleSorter("nickname") 
  }, {
    name: "City",
    sortMethod: helper.sensibleSorter(user => user.homeAddress.city)
  }, {
    name: "Region",
    sortMethod: helper.sensibleSorter(user => user.homeAddress.region)
  }, {
    name: "Country",
    sortMethod: helper.sensibleSorter(user => user.homeAddress.country)
  },
];

export default {
  name: "Search",
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
