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
          <v-tooltip bottom :disabled="!$stateStore.getters.isActingAsBusiness()">
            <template class="float-right" v-slot:activator="{ on }">
              <!-- https://stackoverflow.com/a/56370288 -->
              <div v-on="on" class="d-inline">
                <button
                  class="btn btn-primary d-flex float-right"
                  type="button"
                  :disabled="!listingLoaded || $stateStore.getters.isActingAsBusiness()"
                  v-on="on"
                  @click="buyConfirmationDialogOpen = true"
                >
                  <span class="material-icons mr-1">shopping_bag</span>
                  Buy now
                </button>
              </div>
            </template>
            <span class="text-grey">
              You cannot purchase an item while acting as a business
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
    
    <v-dialog v-model="buyConfirmationDialogOpen" max-width="500px" >
      <v-card v-if="listingLoaded" class="buy-confirmation-dialog">
        <!-- Business information needs to be loaded; otherwise business name and address can't be accessed -->
        <v-card-title class="text-h5">Buy this listing?
        </v-card-title>
        <v-card-text>
          Buy <b>{{ quantity }} </b><b>'{{name}}'</b> from <b>'{{ business.name }}'</b> for <b>{{ $helper.makeCurrencyString(price, currency) }}?</b>
        </v-card-text>
        <v-spacer> </v-spacer>
        <v-card-text>
          Pickup will be at {{ $helper.addressToString(business.address) }}
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="() => buyConfirmationDialogOpen = false">Cancel
          </v-btn>
          <v-btn color="blue darken-1" text @click="buyButtonClicked">Yes</v-btn>
          <v-spacer></v-spacer>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.apiPipeline"
        :show="apiErrorMessage !== null"
        title="Error fetching product information"
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
      listingLoaded: false,
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
      buyConfirmationDialogOpen: false,
      apiErrorMessage: null,
      currency: null
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

  beforeMount: async function () {
    await this.apiPipeline();
  },

  methods: {
    // /**
    //  * Loads currency info
    //  * @return true on success
    //  */
    // loadCurrencies: async function () {
    //   if (!this.$stateStore.getters.isSignedIn()) {
    //     return false;
    //   }

    //   try {
    //     this.currency = await this.$helper.getCurrencyForBusiness(this.businessId,
    //         this.$stateStore);
    //   } catch (err) {
    //     // If can't get currency not that big of a deal
    //     if (await Api.handle401.call(this, err)) {
    //       return;
    //     }
    //     return false;
    //   }
    //   return true;
    // },

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
     * Calls the API to get profile information with the given user ID
     * Returns the promise, not the response
     */
    callApi: async function () {
      return Api.getBusinessListings(this.businessId);
    },

    /**
     * Parses the API response given a promise to the request.
     */
    parseApiResponse: async function (apiCall) {
      const listings = (await apiCall).data.results;
      const listing = listings.find(({id}) => id === this.listingId);
      if (listing === undefined) {
        throw new ApiRequestError(
            `Couldn't find listing with the ID ${this.listingId}.`);
      }
      this.name = listing.inventoryItem.product.name;
      if (listing.business) {
        this.business = listing.business; // NOT IN BACKEND AS OF TIME OF WRITING
        this.currency = this.$helper.getCurrencyForBusinessByCountry(this.business.address.country);
      } else {
        this.business = {
          name: "TODO BUSINESS NAME",
          address: {
            country: "TODO COUNTRY"
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

    buyButtonClicked: async function() {
      console.log("Buy button clicked");
      try {
        await this.$api.purchaseListing(this.businessId, this.listingId);
      } catch(err) {
        if (await this.$api.handle401.call(this, err)) {
          return;
        }

        console.log(err);
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

</style>