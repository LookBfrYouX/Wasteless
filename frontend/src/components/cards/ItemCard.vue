<template>
  <!-- this component produces the item card component from the given props -->
  <div class="container card item-card p-2" data-toggle="collapse" data-target="#hidden" @click="console.log('clicked')"> <!-- TODO add onclick handler and method when components and routes are ready -->
    <div class="row">
      <div class="col-2 pr-0">
        <img class="img-fluid item-image" src="./../../../assets/images/login.jpg" alt="Image" />
      </div>
      <div class="col-10">
        <div class="item-title">
          <h1 class="text-truncate mb-0">{{ item.product.name }}</h1>
        </div>
      </div>
    </div>

    <div class="row collapse" id="hidden">
      <div class="col-10 offset-2">
        <div class="d-flex flex-wrap align-items-baseline w-100">
          <h3 class="mb-0 mr-3">{{ item.totalPrice }}</h3>
          <p class="mb-0">{{ item.quantity }} units @ ${{ item.pricePerItem }} each</p>
        </div>
        <div class="item-description w-100">
          <div class="py-2">{{ item.product.description }}</div>
        </div>
        <div class="row">
          <div v-for="(meta, index) in metaValues" v-bind:key="index" class="col-sm-6 col-md-3 item-meta">
            <i>{{ meta.key }}:</i> {{ meta.value }}
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
    }
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
.item-meta {
  font-size: 0.7em;
}
.item-image {
  max-height: 48px;
}

</style>