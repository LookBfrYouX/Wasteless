<template>
  <div class="w-100 col-12 pt-5">
    <!--  Page Content Area  -->
    <div v-if="cards.length" class="row align-items-center">
      <!--  Sort and Meta info Bar    -->
      <div class="col-12 col-lg-6 pb-0">
        <simple-sort-bar :items="items" @update="sortUpdate"/>
      </div>
      <div class="col-12 col-lg-6 d-flex flex-lg-row-reverse align-items-center">
        <span>
          Displaying cards {{
            this.searchParams.pagStartIndex + 1
          }} - {{ this.searchParams.pagEndIndex + 1 }} out of
          {{ this.totalResults }}
        </span>
      </div>

      <!-- MarketPlace Card List   -->
      <ul class="list-unstyled pl-0 w-100 m-4">
        <li v-for="card in cards" :key="card.id">
          <marketplace-card
              :card="card"
              class="slightly-transparent-white-background my-4 rounded"
          />
        </li>
      </ul>
      <!-- Pagination Bar   -->
      <v-pagination
          :total-visible="7"
          v-model="page"
          :length="totalPages"
          class="w-100"
          @input="pageUpdate"
      />
    </div>
    <div v-else>
      <div class="text-center">
        No marketplace cards yet
      </div>
    </div>
    <!-- Error Component - Not Visible Component   -->

    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.getCardsFromAPI"
        :show="apiErrorMessage !== null"
        title="Error viewing inventory"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import MarketplaceCard from "@/components/cards/MarketplaceCard";
import {Api} from "@/Api";
import ErrorModal from "./ErrorModal";
import SimpleSortBar from "@/components/SimpleSortBar";

export default {
  components: {SimpleSortBar, MarketplaceCard, ErrorModal},
  props: {
    section: {
      required: true,
      type: String
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
        sortBy: "displayPeriodEnd",
        isAscending: false
      },
      apiErrorMessage: null,
      cards: [],
      items: [ // Sort options. Key is displayed and value is emitted when selection changes.
        {key: "Title A-Z", value: "title", isAscending: true},
        {key: "Title Z-A", value: "title", isAscending: false},
        {key: "Earliest Listings", value: "created", isAscending: true},
        {key: "Latest Listings", value: "created", isAscending: false},
        {key: "Closing Soon", value: "displayPeriodEnd", isAscending: true},
        {key: "Closing Latest", value: "displayPeriodEnd", isAscending: false},
        {key: "Suburb A-Z", value: "suburb", isAscending: true},
        {key: "Suburb Z-A", value: "suburb", isAscending: false},
        {key: "City A-Z", value: "city", isAscending: true},
        {key: "City Z-A", value: "city", isAscending: false}
      ]
    };
  },
  beforeMount: async function () {
    const success = await this.getCardsFromAPI();
    if (success != null) {
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
  watch: {
    section: async function () {
      this.page = 1;
      const success = await this.getCardsFromAPI();
      if (success != null) {
        await this.pageUpdate();
      }
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
      await this.getCardsFromAPI();
      window.scrollTo(0, 0);
    },

    /**
     * Get the marketplace cards from the API for the specified section
     * @returns {Promise<T>} Promise containing an object of a list of the cards (results) and the
     * total count of all of the items (totalCount)
     */
    async getCardsFromAPI() {
      try {
        const response = (await Api.getMarketplaceCards(this.section, this.searchParams)).data;
        this.cards = response.results;
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
  }
}
</script>
