<template>
  <div>
    <v-container>
      <v-flex>
        <h2>{{ titleString }}</h2>
      </v-flex>

      <v-row no-gutters>
        <v-col cols="12" md="6">
          <multi-search-bar :sort-items="items"
                            @multi-search-bar-update="event => Object.assign(this.searchParams, event)"/>
        </v-col>

        <v-col cols="12" md="6">
          <ListingSearchFilter v-bind:maxPrice="searchParams.maxPrice"
                               v-bind:minPrice="searchParams.minPrice"
                               @newTypes="event => this.searchParams.businessTypes = event"
                               @newMin="event => this.searchParams.minPrice = event ? parseFloat(event) : null"
                               @newMax="event => this.searchParams.maxPrice = event ? parseFloat(event) : null"
                               @newDates="event => this.searchParams.filterDates = event"
          ></ListingSearchFilter>
        </v-col>
      </v-row>

      <v-row no-gutters>
        <div class="btn btn-primary ml-4" @click="getListingsPipeline()">
          Search
        </div>
      </v-row>

      <v-layout v-if="listings.length" row>
        <v-layout row>
          <v-flex
              v-for="listing in listings"
              :key="listing.id"
              class="col-12 col-md-6 col-lg-4 p-4"
          >
            <listing-item-card :item="listing"/>
          </v-flex>

        </v-layout>
        <!-- Pagination Bar   -->
        <v-pagination
            v-model="page"
            :length="totalPages"
            class="w-100 py-4"
            @input="pageUpdate"
        />
      </v-layout>
      <v-layout v-else row>
        <v-flex class="text-center pt-5">
          Sorry! No Results Found
        </v-flex>
      </v-layout>
    </v-container>

    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.getListingsPipeline"
        :show="apiErrorMessage !== null"
        title="Error viewing listings"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import ErrorModal from "@/components/ErrorModal";
import {Api} from "@/Api";
import ListingItemCard from "@/components/cards/ListingCard";
import ListingSearchFilter from "@/components/ListingSearchFilter";
import MultiSearchBar from "@/components/MultiSearchBar";

export default {
  components: {
    MultiSearchBar,
    ListingItemCard,
    ErrorModal,
    ListingSearchFilter,
  },

  data() {
    return {
      page: 1, // The default starting page.
      itemsPerPage: this.$constants.SORTED_PAGINATED_ITEM_LIST.RESULTS_PER_LISTINGS_PAGE, // The number of items to display on each page.
      totalResults: 0, // The total number of results. Only 1 page is retrieved at a time.
      listings: [],
      apiErrorMessage: null,
      items: [ // Sort options. Key is displayed and value is emitted when selection changes.
        {key: "Closing Soon", value: "closes", isAscending: true},
        {key: "Closing Latest", value: "closes", isAscending: false},
        {key: "Name A-Z", value: "name", isAscending: true},
        {key: "Name Z-A", value: "name", isAscending: false},
        {key: "Lowest Price", value: "price", isAscending: true},
        {key: "Highest Price", value: "price", isAscending: false},
        {key: "Lowest Quantity", value: "quantity", isAscending: true},
        {key: "Highest Quantity", value: "quantity", isAscending: false},
        {key: "City A-Z", value: "city", isAscending: true},
        {key: "City Z-A", value: "city", isAscending: false}
      ],

      titleString: "Results",
      searchParams: {
        searchParam: "",
        pagStartIndex: 0, // The default start index. Overridden in beforeMount.
        pagEndIndex: 0, // The default end index. Overridden in beforeMount.
        sortBy: "closes",
        isAscending: true,
        searchKeys: [],
        minPrice: null,
        maxPrice: null,
        filterDates: [],
        businessTypes: [],
      }
    };
  },

  beforeMount: async function () {
    await this.getListingsPipeline();
    return Promise.allSettled(
        [this.pageUpdate()]);
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
      await this.getListingsPipeline();
      window.scrollTo(0, 0);
    },

    getListingsPipeline: async function () {
      try {

        const response = (await Api.getListings(this.searchParams)).data;
        this.listings = response.results;
        this.listings.forEach(listing => {
          listing.inventoryItem.product.isDairyFree = true;
          listing.inventoryItem.product.isGlutenFree = true;
          listing.inventoryItem.product.isPalmOilFree = true;
          listing.inventoryItem.product.isVegan = true;
          listing.inventoryItem.product.isVegetarian = true;
        })
        this.totalResults = response.totalCount;
        this.titleString = `Results ${this.searchParams.searchString ? "for: "
            + this.searchParams.searchString : ""}`;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return false;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
        this.titleString = "Results";
      }
    },
  },
}
</script>
<style scoped>

</style>
