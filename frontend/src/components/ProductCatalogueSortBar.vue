<template>
  <div>
    <div>
      Sort bar:
    </div>
    <button @click="sortByName">
      Name
    </button>
    <button @click="sortByPrice">
      Price
    </button>
  </div>
</template>

<script>
export default {
  name: "ProductCatalogueSortBar",
  data() {
    return {
      sortBy: "",
    }
  },
  methods: {
    sortByName: function () {
      this.sortBy = (this.sortBy === "name-acs") ? "name-desc" : "name-acs";
      this.sendEvent();
    },
    sortByPrice: function () {
      this.sortBy = (this.sortBy === "recommendedRetailPrice-acs") ? "recommendedRetailPrice-desc" : "recommendedRetailPrice-acs";
      this.sendEvent();
    },
    sendEvent: function () {
      this.$emit('update', this.sortBy);
    }
  }
}
</script>

<style scoped>

</style>