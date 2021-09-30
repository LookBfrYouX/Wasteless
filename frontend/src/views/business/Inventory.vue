<template>
  <div
      class="w-100 col-12 col-md-8 col-lg-6 pt-0 pt-md-15 pt-lg-2 align-items-center container-fluid">
    <div class="d-flex flex-sm-wrap pb-3 pb-md-0 align-items-center container-fluid">
      <div class="row mt-4 align-items-center">
        <h2 class="col-lg-8 pl-0">Inventory for {{ businessName }}</h2>
        <router-link
            :to="{ name: 'BusinessInventoryCreate', params: { businessId }}"
            class="btn col-12 col-lg-4 btn-info d-flex h-100"
        >
          <span class="material-icons mr-1">add</span>
          Add Item
        </router-link>
      </div>
    </div>
    <!--  Page Content Area  -->
    <div v-if="listings.length" class="container-fluid align-items-center">
      <!--  Sort and Meta info Bar    -->
      <div class="col-12 col-lg-6 pb-0">
        <simple-sort-bar :items="items" @update="sortUpdate"/>
      </div>
      <!-- Product List   -->
      <ul class="list-unstyled pl-0">
        <li v-for="listing in listings" :key="listing.id">
          <inventory-item-card
              :businessId="businessId"
              :currency="currency"
              :item="listing"
              class="hover-white-bg hover-scale-effect slightly-transparent-white-background my-1 rounded"
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
          @next="pageUpdate"
          @previous="pageUpdate"
      />
    </div>
    <div v-else>
      No products yet
    </div>
    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.getInventory"
        :show="apiErrorMessage !== null"
        title="Error viewing inventory"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import ErrorModal from "@/components/ErrorModal";
import {Api} from "@/Api";
import InventoryItemCard from "@/components/cards/InventoryCard";
import SimpleSortBar from "@/components/SimpleSortBar";

export default {
  components: {
    SimpleSortBar,
    InventoryItemCard,
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

        {key: "Lowest Price Per Item", value: "pricePerItem", isAscending: true},
        {key: "Highest Price Per Item", value: "pricePerItem", isAscending: false},

        {key: "Lowest Total Price", value: "totalPrice", isAscending: true},
        {key: "Highest Total Price", value: "totalPrice", isAscending: false},

        {key: "Manufactured (Oldest First)", value: "manufactured", isAscending: true},
        {key: "Manufactured (Newest First)", value: "manufactured", isAscending: false},

        {key: "Sell-By Date (Oldest First)", value: "sellBy", isAscending: true},
        {key: "Sell-By Date (Newest First)", value: "sellBy", isAscending: false},

        {key: "Name (A-Z)", value: "name", isAscending: true},
        {key: "Name (Z-A)", value: "name", isAscending: false},
      ],
    };
  },

  beforeMount: async function () {
    await this.getInventory();
    return Promise.allSettled([this.updateBusinessName(), this.getCurrency(), this.pageUpdate()]);
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
      await this.getInventory();
      window.scrollTo(0, 0);
    },
    getInventory: async function () {
      try {
        const response = (await Api.getBusinessInventory(this.businessId, this.searchParams)).data;
        this.listings = response.results;
        this.totalResults = response.totalCount;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return false;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    /**
     * Gets currency object.
     * @returns {Promise<void>} Currency object, null when the currency doesn't exist or API request error.
     */
    getCurrency: async function () {
      this.currency = await this.$helper.tryGetCurrencyForBusiness(this.businessId,
          this.$stateStore);
    },

    updateBusinessName: async function () {
      this.businessName = await this.$helper.tryGetBusinessName(this.businessId);
    },
  },

  watch: {
    businessName() {
      if (this.businessName != null) {
        document.title = `Inventory for ${this.businessName}`
      }
    }
  }
}
</script>
