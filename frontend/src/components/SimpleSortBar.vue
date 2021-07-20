<template>
  <div>
    <v-select
        :items="items.map(a => a.key)"
        @change="sortChange"
        label="Sort By"
        solo dense
    ></v-select>
  </div>
</template>

<script>
export default {
  name: "SimpleSortBar",
  props: {
    items: Array,
  },
  methods: {
    sortChange: function (event) {
      const item = this.items.filter(obj => { return obj.key === event; })[0];
      if (item) {
        this.$emit('update', item.value); // TODO deal with other constraints
      }
    }
  }
}
</script>