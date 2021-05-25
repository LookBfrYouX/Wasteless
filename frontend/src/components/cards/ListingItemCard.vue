<template>
  <!-- this component produces the item card component from the given props -->
  <div class="container card item-card collapsed">
    <div class="row card-body">
      <div class="col-sm-4 p-0">
        <img
            v-if="item.inventoryItem.product.images.length"
            alt="Product Image"
            class="image-fluid w-100 rounded-circle mb-2"
            v-bind:src="item.inventoryItem.product.images[0].thumbnailFilename"
        >
        <img
            v-else
            alt="Product Image"
            class="image-fluid w-100 rounded-circle mb-2"
            src="./../../../assets/images/default-product-thumbnail.svg"
        >
      </div>
      <div class="col-sm-8">
        <h2 class="card-title">{{ item.inventoryItem.product.name }}</h2>
        <h4>
          {{ $helper.makeCurrencyString(item.price, currency, false) }} <small> for </small> {{ item.quantity }}
        </h4>
        <h6 v-if="item.moreInfo"> About this price: {{ item.moreInfo }}</h6>
        <div class="d-flex flex-wrap">
          <div class="mr-3 text-nowrap"> Listed on: {{ $helper.isoToDateString(item.created) }}</div>
          <div class="text-nowrap"> Closes on: {{ $helper.isoToDateString(item.closes) }}</div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "ListingItemCard",
  props: {
    // Listing item object (See API spec for details)
    item: Object,
    businessId: {
      type: Number,
      required: true
    },
    currency: Object,
  },
}
</script>

<style scoped>

</style>