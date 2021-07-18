<template>
  <div>
    <v-select
        class="col-6 col-lg-4"
        :items="items.map(a => a.key)"
        @change="sortChange"
        label="Sort By"
        solo dense
    ></v-select>
  </div>
</template>

<script>
export default {
  name: "ProductCatalogueSortBar",
  props: {
    items: Array,
  },
  data() {
    return {
      sortBy: "",
    }
  },
  methods: {
    sortChange: function (event) {
      const item = this.items.filter(obj => { return obj.key === event; })[0];
      if (item) {
        this.$emit('update', item.value);
      }
    }
  }
}
</script>