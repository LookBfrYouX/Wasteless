<template>
  <div
      class="w-100 col-12 col-md-8 col-lg-6 pt-0 pt-md-15 pt-lg-2 align-items-center container-fluid">
    <div class="d-flex flex-sm-wrap pb-3 pb-md-0 align-items-center container-fluid">
      <div class="row mt-4 align-items-center">
        <h2 class="col-lg-8 pl-0">Listings for {{ businessName }}</h2>
        <router-link
            v-if="$stateStore.getters.canEditBusiness(businessId)"
            :to="{ name: 'BusinessListingCreate', params: { businessId }}"
            class="btn col-12 col-lg-4 btn-info d-flex h-100">
          <span class="material-icons mr-1">add</span>
          Create Listing
        </router-link>
      </div>
    </div>
    <div v-if="listings.length" class="container-fluid align-items-center">
      <div class="col-12 col-lg-6 pb-0">
        <simple-sort-bar :items="items" @update="sortUpdate"/>
      </div>
      <!-- Product List   -->
      <ul class="list-unstyled pl-0">
        <li v-for="listing in listings" :key="listing.id">
          <router-link
              :to="{ name: 'BusinessListingDetail', params: { businessId, listingId: listing.id }}"
              class="text-decoration-none text-reset"
          >
            <listing-item-card
                :businessId="businessId"
                :currency="currency"
                :item="listing"
                class="hover-white-bg hover-scale-effect slightly-transparent-white-background my-1 rounded"
            />
          </router-link>
        </li>
      </ul>
      <!-- Pagination Bar   -->
      <v-pagination
          v-model="page"
          :length="totalPages"
          class="w-100"
          @input="pageUpdate"
          @next="pageUpdate"
          @previous="pageUpdate"
      />
    </div>
    <div v-else>
      No listings yet
    </div>

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
import ListingItemCard from "@/components/cards/ListingCard";
import ErrorModal from "@/components/ErrorModal";
import {Api} from "@/Api";
import SimpleSortBar from "@/components/SimpleSortBar";

export default {
  components: {
    ListingItemCard,
    SimpleSortBar,
    ErrorModal
  },

  props: {
    businessId: {
      type: Number,
      required: true
    }
  },

  data() {
    return {
      page: 1, // The default starting page.
      itemsPerPage: this.$constants.SORTED_PAGINATED_ITEM_LIST.RESULTS_PER_PAGE, // The number of items to display on each page.
      totalResults: 0, // The total number of results. Only 1 page is retrieved at a time.
      searchParams: {
        pagStartIndex: 0, // The default start index. Overridden in beforeMount.
        pagEndIndex: 0, // The default end index. Overridden in beforeMount.
        sortBy: "quantity",
        isAscending: false
      },
      listings: [],
      apiErrorMessage: null,
      businessName: null,
      currency: null,
      items: [ // Sort options. Key is displayed and value is emitted when selection changes.
        {key: "Lowest Quantity", value: "quantity", isAscending: true},
        {key: "Highest Quantity", value: "quantity", isAscending: false},
      ],
    };
  },

  beforeMount: async function () {
    await this.getListingsPipeline();
    return Promise.allSettled(
        [this.loadBusinessName(), this.getCurrency(), this.pageUpdate()]);
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
        const response = (await Api.getBusinessListings(this.businessId, this.searchParams)).data;
        this.listings = response.results;
        this.totalResults = response.totalCount;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return false;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    loadBusinessName: async function () {
      this.businessName = await this.$helper.tryGetBusinessName(this.businessId);
    },

    /**
     * Gets currency object.
     * @returns {Promise<void>} Currency object, null when the currency doesn't exist or API request error.
     */
    getCurrency: async function () {
      this.currency = await this.$helper.tryGetCurrencyForBusiness(this.businessId,
          this.$stateStore);
    },
  },

  watch: {
    businessName() {
      if (this.businessName != null) {
        document.title = `Listings for ${this.businessName}`
      }
    }
  }
}
</script>
