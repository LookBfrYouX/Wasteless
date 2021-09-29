<template>
  <div>
    <v-container>
      <v-flex>
        <h2>{{ titleString }}</h2>
      </v-flex>

      <v-row md="6">
        <v-col>
          <v-expansion-panels class="px-4" multiple>
            <v-expansion-panel>
              <v-expansion-panel-header>Product filtering</v-expansion-panel-header>
              <v-expansion-panel-content>
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
              </v-expansion-panel-content>
            </v-expansion-panel>
            <listing-search-nutrition-filter
                @newMinNovaGroup="event => this.searchParams.minNovaGroup = event"
                @newMaxNovaGroup="event => this.searchParams.maxNovaGroup = event"
                @newMinNutriScore="event => this.searchParams.minNutriScore = event"
                @newMaxNutriScore="event => this.searchParams.maxNutriScore = event"
                @newDiets="event => Object.assign(this.searchParams, event)"
                @newFat="event => this.searchParams.fat = event"
                @newSaturatedFat="event => this.searchParams.saturatedFat = event"
                @newSugars="event => this.searchParams.sugars = event"
                @newSalts="event => this.searchParams.salts = event"/>
          </v-expansion-panels>
        </v-col>
      </v-row>

      <v-row>
        <v-tooltip top :disabled="searchParams.searchKeys.length > 0">
          <template v-slot:activator="{ on }">
            <div v-on="on">
              <v-btn color="rgb(30, 201, 150)" class="white--text ml-4"
                     @click="getListingsPipeline()" :disabled="searchParams.searchKeys.length === 0">
                Search
              </v-btn>
            </div>
          </template>
          <span>Please select a search by parameter in product filtering</span>
        </v-tooltip>
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
import ListingSearchNutritionFilter from "@/components/ListingSearchNutritionFilter";

export default {
  components: {
    ListingSearchNutritionFilter,
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
        {key: "Closing Soon"    , value: "CLOSES"  , isAscending: true },
        {key: "Closing Latest"  , value: "CLOSES"  , isAscending: false},
        {key: "Name A-Z"        , value: "NAME"    , isAscending: true },
        {key: "Name Z-A"        , value: "NAME"    , isAscending: false},
        {key: "Lowest Price"    , value: "PRICE"   , isAscending: true },
        {key: "Highest Price"   , value: "PRICE"   , isAscending: false},
        {key: "Lowest Quantity" , value: "QUANTITY", isAscending: true },
        {key: "Highest Quantity", value: "QUANTITY", isAscending: false},
        {key: "City A-Z"        , value: "CITY"    , isAscending: true },
        {key: "City Z-A"        , value: "CITY"    , isAscending: false}
      ],

      titleString: "Results",
      searchParams: {
        searchParam: "",
        pagStartIndex: 0, // The default start index. Overridden in beforeMount.
        pagEndIndex: 0, // The default end index. Overridden in beforeMount.
        sortBy: "CLOSES",
        isAscending: true,
        searchKeys: ["PRODUCT_NAME"],
        minPrice: null,
        maxPrice: null,
        filterDates: [],
        businessTypes: [],

        // Nutrition filters
        minNovaGroup: null,
        maxNovaGroup: null,
        minNutriScore: null,
        maxNutriScore: null,

        fat: [],
        saturatedFat: [],
        sugars: [],
        salts: [],

        isGlutenFree: false,
        isDairyFree: false,
        isVegetarian: false,
        isVegan: false,
        isPalmOilFree: false
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
        this.totalResults = response.totalCount;
        this.page = 1;
        if (this.searchParams.searchString && this.searchParams.searchString.trim()) {
          this.titleString = `Listings matching ${this.searchParams.searchString}`;
        } else {
          this.titleString = `Listings`;
        }
        this.apiErrorMessage = null;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return false;
        }
        // For some unknown reason the tests fail without this:
        // apiErrorMessage is still null in the test because of 
        // some sort of async bug
        if (await Api.handle401.call(this, err)) {
          return false;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
        this.titleString = "Listings";
      }
    }
  },
}
</script>
