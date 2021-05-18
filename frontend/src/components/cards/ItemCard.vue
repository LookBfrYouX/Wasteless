<template>
  <!-- this component produces the item card component from the given props -->
  <div class="container card item-card" data-toggle="collapse" v-bind:data-target="'#' + idPrefix + item.id" @click="console.log('clicked')"> <!-- TODO add onclick handler and method when components and routes are ready -->
    <div class="row card-body">
      <div class="col-2 pr-0">
        <img class="img-fluid item-image" src="./../../../assets/images/login.jpg" alt="Image" />
      </div>
      <div class="col-10">
        <h1 class="card-title text-truncate">{{ item.product.name }}</h1>
        <div class="d-flex flex-wrap justify-content-between">
          <h4 class="pr-2">{{ item.quantity }} <small>units in stock</small></h4>
          <h4 class="pr-2">
            {{ item.pricePerItem }} <small>per item</small>
          </h4>
          <h4 class="pr-2">
            {{ item.totalPrice }} <small>for whole</small>
          </h4>
        </div>
        <h4 class="text-nowrap">
          <small>{{ metaValues[0].key }}:</small> <span class="text-nowrap">{{ metaValues[0].value}}</span>
        </h4>

        <div class="collapse" v-bind:id="idPrefix + item.id">
          <div v-for="(meta, index) in metaValues"
               v-bind:key="index">
            <h4 v-if="index !== 0"
                >
              <small>{{ meta.key }}:</small> <span class="text-nowrap">{{ meta.value }}</span></h4>
          </div>
          <div class="card-text">
            About: {{ item.description }}
          </div>
        </div>
      </div>
    </div>

<!--    <div class="row collapse" >-->
<!--      <div class="col-10 offset-2">-->
<!--        <div class="d-flex flex-wrap align-items-baseline w-100">-->
<!--          <h3 class="mb-0 mr-3">{{ item.totalPrice }}</h3>-->
<!--          <p class="mb-0">{{ item.quantity }} units @ ${{ item.pricePerItem }} each</p>-->
<!--        </div>-->
<!--        <div class="item-description w-100">-->
<!--          <div class="py-2">{{ item.product.description }}</div>-->
<!--        </div>-->
<!--        <div v-for="(meta, index) in metaValues"-->
<!--             v-if="index !== 0"-->
<!--             v-bind:key="index"-->
<!--             class="col-sm-6 col-md-3 item-meta">-->
<!--          <h4>{{ meta.key }}:</h4> {{ meta.value }}-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
  </div>
</template>

<script>
export default {
  name: "ItemCard",
  props: {
    // The item information to be displayed. (See API spec for details)
    item: Object,
    idPrefix: {
      type: String,
      default: "inventory-item-"
    }
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
  max-height: 48px;
}

</style>