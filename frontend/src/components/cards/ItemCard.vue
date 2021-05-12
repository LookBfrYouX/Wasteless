<template>
  <!-- this component produces the item card component from the given props -->
  <div class="container card item-card">
    <div class="row item-title">
      <div class="col-12 p-2">
        <h1>{{ item.title }}</h1>
      </div>
    </div>
    <div class="row">
      <div class="col-lg-3 col-sm-12 p-2">
        <h3 class="w100">{{ item.totalPrice }}</h3>
        <p>{{ item.quantity }} units @ ${{ item.pricePerUnit }} each</p>
      </div>
      <div class="col-9 p-2 item-description">
        <p>{{ item.description }}</p>
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
    metaValues() {
      let results = [];
      [{ key: 'Expires On', value: this.item.expiryDate },
        { key: 'Best Before', value: this.item.bestBeforeDate },
        { key: 'Manufactured On', value: this.item.manufacturedDate },
        { key: 'Sell By', value: this.item.sellByDate }].forEach((meta) => {
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
  bottom:0px;
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