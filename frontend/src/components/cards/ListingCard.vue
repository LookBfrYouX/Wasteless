<template>
  <router-link
    :to="{ name: 'BusinessListingDetail',
      params: {
        businessId: item.inventoryItem.business.id,
        listingId: item.id,
      },
    }"
    class="text-decoration-none text-reset"
  >
    <v-card class="w-100 hover-scale-effect p-relative">
      <div
        v-if="item.inventoryItem.product"
        class="allergy-info-container d-flex flex-wrap justify-content-between p-2"
      >
      <!-- using justify-content: space between as the least important 
           information is probably around the edges -->
        <allergy-chips :product="item.inventoryItem.product" small/>
      </div>
      <img
          v-if="item.inventoryItem.product.images.length"
          :src="item.inventoryItem.product.images[0].thumbnailFilename"
          alt="Product Image"
          class="image-fluid w-100 m-0"
      />
      <img
          v-else
          alt="Product Image"
          class="image-fluid w-100 m-0"
          src="@/../assets/images/default-product-thumbnail.svg"
      />
      <v-card-text class="pt-2 pb-0">
        <div class="row">
          <div class="col-6 text-truncate">
            {{ item.inventoryItem.business.name }}
          </div>
          <v-tooltip top>
            <template v-slot:activator="{ on }">
              <div class="col-6">
                <div v-on="on" class="text-end text-truncate">
                  {{ item.inventoryItem.business.address.city }}
                </div>
              </div>
            </template>
            <span>{{ $helper.addressToString(item.inventoryItem.business.address, true) }}</span>
          </v-tooltip>
        </div>
      </v-card-text>
      <v-card-title class="py-0">
        {{ item.inventoryItem.product.name }}
      </v-card-title>
      <v-card-text>
        <div class="row">
          <div class="col-5">
            {{ $helper.makeCurrencyString(item.price, currency, false) }}
            <small> for </small>
            {{ item.quantity }}
          </div>
          <div class="col-7 text-end">
            Closes
            {{ $helper.isoToDateString(item.closes) }}
          </div>
        </div>
      </v-card-text>
    </v-card>
  </router-link>
</template>

<script>
export default {
  name: "ListingItemCard",
  data() {
    return {
      currency: null
    };
  },
  props: {
    item: Object
  },

  beforeMount: function () {
    this.getCurrency();
  },

  methods: {
    /**
     * Gets currency object.
     * @returns {Promise<void>} Currency object, null when the currency doesn't exist or API request error.
     */
    getCurrency: async function () {
      this.currency = this.$helper.getCurrencyForBusinessByCountry(
        this.item.inventoryItem.business.address.country
      );
    },
  },
};
</script>

<style lang="scss" scoped>
@import "~/src/styles/grid-breakpoints.scss";

.v-card {
  height: 500px;
  overflow: hidden;
}

@media (max-width: map-get($grid-breakpoints, "md")) {
  .v-card {
    height: inherit;
  }
}

.listing-container img {
  max-height: 30vh;
}

.allergy-info-container {
  position: absolute;
}
</style>