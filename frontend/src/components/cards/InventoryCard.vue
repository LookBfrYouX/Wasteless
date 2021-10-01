<template>
  <!-- this component produces the item card component from the given props -->
  <div :data-target="'#' + idPrefix + item.id"
       class="container card item-card collapsed"
       data-toggle="collapse"
  >
    <div class="row card-body">
      <div class="col-2 p-0">
        <img
            v-if="item.product.images.length"
            :src="item.product.images[0].thumbnailFilename"
            alt="Product Image"
            class="image-fluid w-100 rounded-circle"
        >
        <img
            v-else
            alt="Product Image"
            class="image-fluid w-100 rounded-circle"
            src="@/../assets/images/default-product-thumbnail.svg"
        >
      </div>
      <div class="col-10">
        <h2 class="card-title"
        >{{ item.product.name }}</h2>
        <div class="d-flex flex-wrap justify-content-between">
          <h4 class="pr-2">{{ item.quantity }} <small>units in stock</small></h4>
          <h4 class="pr-2">
            {{ $helper.makeCurrencyString(item.pricePerItem, currency) }}
            <small>per item</small>
          </h4>
          <h4 class="pr-2">
            {{ $helper.makeCurrencyString(item.totalPrice, currency) }}
            <small>for whole</small>
          </h4>
        </div>
        <h4 v-if="metaValues.length !== 0"
            class="text-nowrap">
          <small>{{ metaValues[0].key }}:</small> <span
            class="text-nowrap">{{ metaValues[0].value }}</span>
        </h4>

        <div :id="idPrefix + item.id"
             class="collapse">
          <div v-for="(meta, index) in metaValues"
               :key="index">
            <h4 v-if="index !== 0"
            >
              <small>{{ meta.key }}:</small> <span class="text-nowrap">{{ meta.value }}</span></h4>
          </div>
          <div class="d-flex justify-content-end">
            <router-link
                :to="{ name: 'BusinessProductDetail', params: { businessId: businessId, productId: item.product.id }}"
                class="btn btn-success"
            >
              View Product
            </router-link>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "InventoryItemCard",
  props: {
    // The item information to be displayed. (See API spec for details)
    item: Object,
    // idPrefix for giving unique html id value for each item card. Id is required for Bootstrap expanding/collapsing functionality.
    idPrefix: {
      type: String,
      default: "inventory-item-"
    },
    businessId: {
      type: Number,
      required: true
    },
    currency: Object,
  },
  computed: {
    // A list of dates and their description strings used for the date data.
    // Only dates that were supplied and not empty are displayed.
    metaValues() {
      let results = [];
      [
        {key: 'Expires On', value: this.item.expires},
        {key: 'Sell By', value: this.item.sellBy},
        {key: 'Best Before', value: this.item.bestBefore},
        {key: 'Manufactured On', value: this.item.manufactured}
      ].forEach((meta) => {
        if (meta.value !== null && meta.value !== undefined && meta.value !== "") {
          results.push(meta)
        }
      });
      return results;
    },
  }
}
</script>

<style scoped>
.card-body {
  cursor: pointer;
}

.collapsed .card-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>