<template>
  <v-card class="w-100">
    <img
        v-if="item.inventoryItem.product.images.length"
        :src="item.inventoryItem.product.images[0].thumbnailFilename"
        alt="Product Image"
        class="image-fluid w-100 m-0"
    >
    <img
        v-else
        alt="Product Image"
        class="image-fluid w-100 m-0"
        src="@/../assets/images/default-product-thumbnail.svg"
    >
    <v-card-title class="truncate-overflow ">
      {{ item.inventoryItem.product.name }}
    </v-card-title>
    <v-card-text>
      <div class="row">
        <div class="col-5">
          {{ $helper.makeCurrencyString(item.price, currency, false) }} <small> for </small>
          {{ item.quantity }}
        </div>
        <div class="col-7">
          Closes
          {{ $helper.isoToDateString(item.closes) }}
        </div>
      </div>

    </v-card-text>
  </v-card>
</template>

<script>
export default {
  name: "ListingItemCard",
  data() {
    return {
      currency: null
    }
  },
  props: {
    item: Object
  },
  beforeMount: async function () {
    return Promise.allSettled(
        [this.getCurrency()]);
  },
  methods: {
    /**
     * Gets currency object.
     * @returns {Promise<void>} Currency object, null when the currency doesn't exist or API request error.
     */
    getCurrency: async function () {
      this.currency = await this.$helper.tryGetCurrencyForBusiness(
          this.item.inventoryItem.businessId,
          this.$stateStore);
      console.log(this.currency);
    }
  }
}
</script>

<style scoped>
</style>