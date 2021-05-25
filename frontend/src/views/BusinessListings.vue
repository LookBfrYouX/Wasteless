<template>
  <div class="w-100">
    <sorted-paginated-item-list
      v-bind:items="listings"
      v-bind:sortOptions="sortOptions"
      v-bind:currentSortOption.sync="currentSortOption"
    >
      <template v-slot:title>
        <h2>Listings for {{businessName? businessName: "business"}}</h2>
      </template>
      <template v-slot:item="slotProps">
        <listing-item-card
            class="hover-white-bg hover-scale-effect slightly-transparent-white-background my-1 rounded" v-on:click="viewListing(slotProps.item.id)"
            v-bind:item="slotProps.item"
            :businessId="businessId"
            :currency="currency"
        />
        <!--<business-listing v-bind:listing="slotProps.item"/> -->
      </template>
      <template v-slot:right-button>
        <router-link :to="{ name: 'createListing', params: { businessId }}"
                     class="btn btn-info d-flex">
          <span class="material-icons mr-1">add</span>
          Create Listing
        </router-link>
      </template>
    </sorted-paginated-item-list>
    <error-modal
      title="Error viewing listings"
      v-bind:goBack="false"
      v-bind:hideCallback="() => apiErrorMessage = null"
      v-bind:refresh="true"
      v-bind:retry="this.getListingsPipeline"
      v-bind:show="apiErrorMessage !== null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import SortedPaginatedItemList from '../components/SortedPaginatedItemList.vue';
import ListingItemCard from "./../components/cards/ListingItemCard";
import ErrorModal from "../components/Errors/ErrorModal";
import { Api } from "../Api";

import { helper } from "../helper";

const sortOptions = [
  {
    name: "Price",
    sortMethod: helper.sensibleSorter("price") 
  }, {
    name: "Name",
    sortMethod: helper.sensibleSorter(el => el.inventoryItem.product.name)
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

    /**
     * Go to listing detail page by passing the listing id
     */
    viewListing(listingId) {
      this.$router.push({
        name: "salesListingDetail",
        params: {
          businessId: this.businessId,
          listingId
        }
      });
    }
  },

  watch: {
    businessName() {
      if (this.businessName != null) document.title = `Listings for ${this.businessName}`
    }
  }
}
</script>
