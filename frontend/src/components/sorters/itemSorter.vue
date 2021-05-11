<template>
  <div>
    <button v-for="(heading, index) in headings" v-bind:key="index" @click="reverseList()">{{ heading }}</button>
  </div>

</template>

<script>
export default {
  name: "itemSorter",
  props: {
    headings: Array,
    items: Array,
  },
  data: function () {
    return {
    }
  },
  methods: {
    reverseList: function () {
      let resultList = this.items;
      this.$emit('update', resultList.reverse());
    }
  }
}
</script>

<style scoped>

</style>