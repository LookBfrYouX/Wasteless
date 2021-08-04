<template>
  <div
      class="w-100 col-12 col-md-8 col-lg-6 align-items-center">
    <div class="row mt-4 align-items-center">
      <h2 class="col-lg-8">TODO: Header</h2>
    </div>
    <div v-if="listings.length">
      <div class="col-12 col-lg-6 pb-0">
        <simple-sort-bar :items="items" @update="sortUpdate"/>
      </div>
      <!-- Product List   -->
      <div class="row">
        <div v-for="listing in listings" :key="listing.id">
          <div class="my-0">
            <router-link
                :to="{ name: 'BusinessListingDetail', params: { businessId:listing.inventoryItem.businessId, listingId: listing.id }}"
                class="text-decoration-none text-reset"
            >
              <v-card class="col-12 col-md-6 col-lg-4 w-100" fluid>
                <v-card-title>{{ listing.inventoryItem.product.name }}</v-card-title>
              </v-card>
            </router-link>
          </div>
        </div>
      </div>

      <!-- Pagination Bar   -->
      <v-pagination
          v-model="page"
          :length="totalPages"
          class="w-100"
          @input="pageUpdate"
      />
    </div>
    <div v-else>
      TODO: NO LISTINGS FOUND TEXT (MAYBE IMAGE ON VUETIFY ICONS?)
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
import ErrorModal from "@/components/ErrorModal";
import {Api} from "@/Api";
import SimpleSortBar from "@/components/SimpleSortBar";

export default {
  components: {
    SimpleSortBar,
    ErrorModal
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
      items: [ // Sort options. Key is displayed and value is emitted when selection changes.
        {key: "Lowest Quantity", value: "quantity", isAscending: true},
        {key: "Highest Quantity", value: "quantity", isAscending: false},
      ],
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
      console.log('hi')
      window.scrollTo(0, 0);
    },
    getListingsPipeline: async function () {
      try {
        const response = (await Api.getListings(this.searchParams));
        this.listings = response.results;
        this.totalResults = response.totalCount;
      } catch (err) {
        console.log(err)
        if (await Api.handle401.call(this, err)) {
          return false;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },
  },
}
</script>
