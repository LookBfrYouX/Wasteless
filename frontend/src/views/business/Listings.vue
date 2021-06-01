<template>
  <div class="w-100">
    <sorted-paginated-item-list
      :items="listings"
      :sortOptions="sortOptions"
      :currentSortOption.sync="currentSortOption"
    >
      <template v-slot:title>
        <h2>Listings for {{businessName? businessName: "business"}}</h2>
      </template>
      <template v-slot:item="slotProps">
        <router-link
          :to="{ name: 'BusinessListingDetail', params: { businessId, listingId: slotProps.item.id }}"
          class="text-decoration-none text-reset"
        >
          <listing-item-card
              class="hover-white-bg hover-scale-effect slightly-transparent-white-background my-1 rounded"
              :item="slotProps.item"
              :businessId="businessId"
              :currency="currency"
          />
        </router-link>
      </template>
      <template v-slot:right-button>
        <router-link
            :to="{ name: 'BusinessListingCreate', params: { businessId }}"
            class="btn btn-info d-flex"
            v-if="$stateStore.getters.canEditBusiness(businessId)">
          <span class="material-icons mr-1">add</span>
          Create Listing
        </router-link>
      </template>
    </sorted-paginated-item-list>
    <error-modal
      title="Error viewing listings"
      :goBack="false"
      :hideCallback="() => apiErrorMessage = null"
      :refresh="true"
      :retry="this.getListingsPipeline"
      :show="apiErrorMessage !== null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import SortedPaginatedItemList from '../../components/SortedPaginatedItemList.vue';
import ListingItemCard from "@/components/cards/ListingCard";
import ErrorModal from "@/components/ErrorModal";
import { Api } from "@/Api";

import { helper } from "@/helper";

const sortOptions = [
  {
    name: "Name",
    sortMethod: helper.sensibleSorter(el => el.inventoryItem.product.name)
  },
  {
    name: "Price",
    sortMethod: helper.sensibleSorter("price") 
  }, {
    name: "Quantity",
    sortMethod: helper.sensibleSorter("quantity")
  }, {
    name: "Listing Created",
    // Yes, you can sort dates as a string in this format
    // Add a reversed param to sorter? Should the 'natural' sort for created/closes be oldest first? 
    sortMethod: helper.sensibleSorter("created")
  }, {
    name: "Listing Closes",
    sortMethod: helper.sensibleSorter("closes")
  }
];

export default {
  components: {
    ListingItemCard,
    SortedPaginatedItemList,
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
      listings: [],
      apiErrorMessage: null,
      sortOptions: sortOptions,
      currentSortOption: { ...sortOptions[0], reversed: false},
      businessName: null,
      currency: null
    };
  },

  beforeMount: async function() {
    return Promise.allSettled([this.loadBusinessName(), this.getListingsPipeline(), this.getCurrency()]);
  },
  
  methods: {
    getListingsPipeline: async function() {
      try {
        this.listings = (await Api.getBusinessListings(this.businessId)).data;
      } catch(err) {
        if (await Api.handle401.call(this, err)) return false;
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    loadBusinessName: async function() {
      this.businessName = await this.$helper.tryGetBusinessName(this.businessId);
    },

    /**
     * Gets currency object.
     * @returns {Promise<void>} Currency object, null when the currency doesn't exist or API request error.
     */
    getCurrency: async function () {
      this.currency = await this.$helper.tryGetCurrencyForBusiness(this.businessId, this.$stateStore);
    },
  },

  watch: {
    businessName() {
      if (this.businessName != null) document.title = `Listings for ${this.businessName}`
    }
  }
}
</script>
