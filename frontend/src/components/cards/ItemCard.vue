<template>
  <!-- this component produces the item card component from the given props -->
  <div class="container card item-card" @click="console.log('clicked')"> <!-- TODO add onclick handler and method when components and routes are ready -->
    <div class="row">
      <div class="col-md-2 col-sm-12 p-2 w-100">
        <img class="img-fluid item-image" src="./../../../assets/images/default-product-thumbnail.svg" alt="Image" />
      </div>
      <div class="col-10 w-100">
        <div class="col-12 p-2 item-title">
          <h1>{{ item.product.name }}</h1>
        </div>
        <div class="col-12 p-2 item-description w-100">
          <p>{{ item.product.description }}</p>
        </div>
      </div>
      <div class="col-lg-3 col-sm-12 p-2 w-100">
        <h3 class="w100">{{ item.totalPrice }}</h3>
        <p>{{ item.quantity }} units @ ${{ item.pricePerItem }} each</p>
      </div>
    </div>
    <div class="row item-meta">
      <div v-for="(meta, index) in metaValues" v-bind:key="index" class="col-xl-3 col-lg-3 col-sm-6 p-2">
        <i>{{ meta.key }}:</i> {{ meta.value }}
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
.item-image {
  max-width: 120px;
}
.item-meta {
  font-size: 0.7em;
}
.item-description {
  max-height: 120px;
  overflow: hidden;
}
.item-description:after {
  content:"";
  position:absolute;
  bottom:0;
  left:0;
  height:60px;
  width:100%;
  background: linear-gradient(rgba(0,0,0,0), #FFF);
}
.item-title {
  max-height: 80px;
  overflow: hidden;
}
.item-title:after {
  content:"";
  position:absolute;
  top:40px;
  left:0;
  height:40px;
  width:100%;
  background: linear-gradient(rgba(0,0,0,0), #FFF);
}
</style>