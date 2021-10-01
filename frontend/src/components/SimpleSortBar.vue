<template>
  <div class="mx-0">
    <v-select
        :items="items.map(a => a.key)"
        dense
        prepend-inner-icon="sort"
        label="Sort By"
        solo @change="sortChange"
    ></v-select>
  </div>
</template>

<script>
export default {
  name: "SimpleSortBar",
  props: {
    /**
     * A mapping of key, value, and isAscending.
     * e.g. [{ key: 'Name A-Z', value: 'name', isAscending: true }]
     */
    items: Array,
  },
  methods: {
    sortChange: function (event) {
      const item = this.items.filter(obj => {
        return obj.key === event;
      })[0];
      if (item) {
        this.$emit('update', item.value, item.isAscending);
      }
    }
  }
}
</script>