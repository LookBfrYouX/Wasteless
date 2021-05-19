<template>
  <!-- this component produces the item card component from the given props -->
  <div class="container card item-card collapsed"
       data-toggle="collapse"
       v-bind:data-target="'#' + idPrefix + item.id"
       >
    <div class="row card-body">
      <div class="col-2 pr-0">
        <img class="img-fluid item-image" src="./../../../assets/images/login.jpg" alt="Image" />
      </div>
      <div class="col-10">
        <h2 class="card-title"
            >{{ item.product.name }}</h2>
        <div class="d-flex flex-wrap justify-content-between">
          <h4 class="pr-2">{{ item.quantity }} <small>units in stock</small></h4>
          <h4 class="pr-2">
            {{ $helper.makeCurrencyString(item.pricePerItem, currency, false) }}
            <small>per item</small>
          </h4>
          <h4 class="pr-2">
            {{ $helper.makeCurrencyString(item.totalPrice, currency, false) }}
            <small>for whole</small>
          </h4>
        </div>
        <h4 v-if="metaValues.length !== 0"
            class="text-nowrap">
          <small>{{ metaValues[0].key }}:</small> <span class="text-nowrap">{{ metaValues[0].value}}</span>
        </h4>

        <div class="collapse"
             v-bind:id="idPrefix + item.id">
          <div v-for="(meta, index) in metaValues"
               v-bind:key="index">
            <h4 v-if="index !== 0"
                >
              <small>{{ meta.key }}:</small> <span class="text-nowrap">{{ meta.value }}</span></h4>
          </div>
          <div class="card-text">
            About: {{ item.description }}
          </div>
          <div class="d-flex justify-content-end">
            <router-link :to="{ name: 'productDetail', params: {businessId: businessId, productId: item.product.id }}"
                         class="btn btn-success">
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
  name: "ItemCard",
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
      [{ key: 'Expires On', value: this.item.expires },
        { key: 'Best Before', value: this.item.bestBefore },
        { key: 'Manufactured On', value: this.item.manufactured },
        { key: 'Sell By', value: this.item.sellBy }].forEach((meta) => {
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
.item-card:hover {
  cursor: pointer;
  transition-timing-function: ease-in-out;
  transition: 0.25s;
  transform: translateX(10px);
}

.item-image {
  max-height: 48px;
}

.collapsed .card-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>