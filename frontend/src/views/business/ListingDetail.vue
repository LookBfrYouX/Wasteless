<template>
  <div class="container my-4">
    <div class="p-3 bg-white rounded">
      <div class="row">
        <div class="col-12">
          <div class="d-flex flex-wrap mb-2">
            <h2 class="card-title mb-0">
              {{ name }} (ID:
              <code class="text-dark">{{ listingId }}</code>)
            </h2>
          </div>

          <div class="btn-group" role="group" aria-label="Basic example" style="flex-wrap: wrap;">
            <button class="btn btn-white-bg-primary d-flex align-items-end" type="button"
                    @click="$router.go(-1)">
              <span class="material-icons mr-1">arrow_back</span>
              Back
            </button>
            <button class="btn btn-white-bg-primary d-flex align-items-end" type="button">
              <span class="material-icons mr-1">business</span>
              <router-link
                  :to="{ name: 'BusinessDetail', params: { businessId, showBackButton: true}}"
                  class="text-reset text-decoration-none"
              >
                View Business
              </router-link>
            </button>
          </div>

          <image-carousel :images="productImages"/>
          <div class="mt-2 d-inline">{{ description }}</div>

          <v-tooltip bottom
                     :disabled="!$stateStore.getters.isActingAsBusiness() && !listingWasPurchased">
            <template v-slot:activator="{ on }">
              <!-- https://stackoverflow.com/a/56370288 -->
              <div v-on="on" class="float-right">
                <button
                    class="btn btn-primary d-flex float-right"
                    type="button"
                    :disabled="!listingLoaded || listingWasPurchased || $stateStore.getters.isActingAsBusiness()"
                    v-on="on"
                    @click="openConfirmationDialog"
                >
                  <span class="material-icons mr-1">shopping_bag</span>
                  Buy now
                </button>
              </div>
            </template>
            <span class="text-grey" v-if="$stateStore.getters.isActingAsBusiness()">
              You cannot buy an item while acting as a business
            </span>
            <span class="text-grey" v-else-if="listingWasPurchased">
              This listing has been purchased
            </span>
          </v-tooltip>

          <div class="mt-2">RRP (each): {{
              $helper.makeCurrencyString(recommendedRetailPrice, currency)
            }}
          </div>
          <div class="mt-2">Quantity: {{ quantity }}</div>
          <div class="mt-2">Price: {{ $helper.makeCurrencyString(price, currency) }}</div>
          <div v-if="moreInfo" class="mt-2 mb-5">More Information: {{ moreInfo }}</div>
          <div class="d-flex flex-wrap justify-content-between mb-2">
            <div class="date mt-2">Opened: {{ $helper.isoToDateString(listingCreated, true) }}</div>
            <div class="date mt-2">Closes: {{ $helper.isoToDateString(listingCloses, true) }}</div>
          </div>
        </div>
      </div>
      <hr>
      <div class="d-flex flex-wrap justify-content-between mb-2">
        <div class="date mt-2">Manufactured: {{ $helper.isoToDateString(manufactured) }}</div>
        <div class="date mt-2">Sell By: {{ $helper.isoToDateString(sellBy) }}</div>
        <div class="date mt-2">Best Before: {{ $helper.isoToDateString(bestBefore) }}</div>
        <div class="date mt-2">Expires: {{ $helper.isoToDateString(expires) }}</div>
      </div>
    </div>

    <v-dialog v-model="buyConfirmationDialogOpen" max-width="500px" @click:outside="onDialogClose">
      <v-card v-if="listingLoaded" class="buy-confirmation-dialog">
        <!-- Business information needs to be loaded; otherwise business name and address can't be accessed -->
        <v-card-title class="text-h5">Buy this listing?
        </v-card-title>
        <v-card-text>
          Buy <strong>{{ quantity }} </strong><strong>'{{ name }}'</strong> from <strong>'{{ business.name }}'</strong> for
          <strong>{{ $helper.makeCurrencyString(price, currency) }}?</strong>
        </v-card-text>
        <v-spacer></v-spacer>
        <v-card-text>
          Pickup will be at <strong>{{ $helper.addressToString(business.address) }}</strong>
        </v-card-text>
        <v-spacer></v-spacer>
        <v-alert type="warning" v-if="buyApiErrorMessage !== null" class="mx-4">
          {{ buyApiErrorMessage }}
        </v-alert>
        <v-alert type="success" v-if="listingWasPurchased" class="mx-4">
          You have successfully purchased the listing!
        </v-alert>
        <v-card-actions class="justify-content-around">
          <v-btn
              class="cancel-button"
              color="grey darken-4"
              text
              @click="onDialogClose"
          >
            {{ listingWasPurchased ? "Go back" : "Cancel" }}
          </v-btn>
          <v-btn
              class="ma-2 buy-button"
              :loading="buyApiCallOngoing"
              :disabled="buyApiCallOngoing"
              @click.stop="buyButtonClicked"
              v-if="!listingWasPurchased"
          >
            Buy
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.apiPipeline"
        :show="apiErrorMessage !== null"
        title="Error fetching listing information"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<script>
import ErrorModal from "@/components/ErrorModal.vue";

import {ApiRequestError} from "@/ApiRequestError";
import {Api} from "@/Api";
import ImageCarousel from '../../components/ImageCarousel.vue';

export default {
  name: "ListingDetail",
  components: {
    ImageCarousel,
    ErrorModal,
  },

  data() {
    return {
      name: "",
      description: "",
      business: null,
      productImages: [],
      quantity: null,
      price: null,
      moreInfo: "",
      listingCreated: "",
      listingCloses: "",
      manufactured: "",
      sellBy: "",
      bestBefore: "",
      expires: "",
      recommendedRetailPrice: "",

      currency: null,

      listingLoaded: false,
      buyConfirmationDialogOpen: false,
      apiErrorMessage: null,
      buyApiCallOngoing: false,
      buyApiErrorMessage: null,
      listingWasPurchased: false
    }
  },
  props: {
    businessId: {
      required: true,
      type: Number
    },
    listingId: {
      required: true,
      type: Number,
    }
  },

  /**
   * Make API request to get listing information when component is initialized
   */
  beforeMount: async function () {
    await this.apiPipeline();
  },

  methods: {
    /**
     * This method is called when the user clicks cancel or go back, functionality depends on
     * whether the user has purchased the item or not
     */
    onDialogClose() {
      if (this.listingWasPurchased) {
        this.$router.go(-1);
      } else {
        this.buyConfirmationDialogOpen = false;
      }
    },

    /**
     * Calls the API and updates the component's data with the result
     * Does not run pipeline if user should not be able to edit business
     */
    apiPipeline: async function () {
      if (!this.$stateStore.getters.isSignedIn()) {
        return false;
      }

      try {
        await this.parseApiResponse(this.callApi());
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
        return false;
      }

      return true;
    },

    /**
     * Calls the API to get all listings for the business
     * Returns the promise, not the response
     */
    callApi: async function () {
      return Api.getBusinessListings(this.businessId);
    },

    /**
     * Parses the API response given a promise to the listings request
     */
    parseApiResponse: async function (apiCall) {
      // No endpoint to fetch just one listing, so fetch all listings and find the right one
      const listings = (await apiCall).data.results;
      const listing = listings.find(({id}) => id === this.listingId);
      if (listing === undefined) {
        throw new ApiRequestError(
            `Couldn't find listing with the ID '${this.listingId}'. It may have been purchased by another user`);
      }
      this.name = listing.inventoryItem.product.name;

      if (listing.inventoryItem.business) {
        this.business = listing.inventoryItem.business; // NOT IN BACKEND AS OF TIME OF WRITING
        this.currency = this.$helper.getCurrencyForBusinessByCountry(this.business.address.country);
      } else {
        this.business = {
          name: "Unknown business",
          address: {
            country: "Unknown location"
          }
        }
      }

      this.listingLoaded = true;
      this.description = listing.inventoryItem.product.description;
      this.productImages = listing.inventoryItem.product.images;
      this.quantity = listing.quantity;
      this.price = listing.price;
      this.moreInfo = listing.moreInfo;
      this.listingCreated = listing.created;
      this.listingCloses = listing.closes;
      this.manufactured = listing.inventoryItem.manufactured;
      this.sellBy = listing.inventoryItem.sellBy;
      this.bestBefore = listing.inventoryItem.bestBefore;
      this.expires = listing.inventoryItem.expires;
      this.recommendedRetailPrice = listing.inventoryItem.product.recommendedRetailPrice;
    },

    /**
     * Handles API call to buy product and its error handling
     */
    buyButtonClicked: async function () {
      this.buyApiCallOngoing = true;

      try {
        await Api.buyListing(this.businessId, this.listingId);
      } catch (err) {
        this.buyApiCallOngoing = false;
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.buyApiErrorMessage = err.userFacingErrorMessage;
        return;
      }

      this.buyApiCallOngoing = false;
      this.buyApiErrorMessage = null;
      this.listingWasPurchased = true;
    },

    /**
     * Open buy listing confirmation dialog and reset its state
     */
    openConfirmationDialog() {
      this.buyApiCallOngoing = false;
      this.buyApiErrorMessage = null;
      this.buyConfirmationDialogOpen = true;
    }
  },

  watch: {
    /**
     * Update page title with product and business name
     */
    name() {
      if (typeof this.name === "string" && this.name.trim().length) {
        document.title = `Buy '${this.name}' from ${this.business.name}`;
      }
    }
  }
}
</script>
<style scoped>
.date {
  font-size: smaller;
  display: inline-block
}

.buy-confirmation-dialog .v-card__text {
  color: black;
}

.buy-confirmation-dialog .buy-button {
  background-color: var(--primary);
  color: white;
}
</style>
