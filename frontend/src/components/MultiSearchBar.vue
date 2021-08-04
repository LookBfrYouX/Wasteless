<template>
<!--Test div - page width  -->
  <div class="col-lg-8 d-flex flex-wrap">
<!--  Test div search and filter container  -->
    <div class="col-12 col-lg-6">
      <div class="col-12 d-flex flex-wrap container-fluid p-0">
    <!--  search 6col  -->
        <v-text-field
            class="col-12 pl-3"
            v-model="searchParams.searchString"
            label="Search"
            solo
            dense
            @keydown.enter="doSearch"
        ></v-text-field>
    <!--  keys 6col  -->
        <v-select
            class="col-lg-6 pr-1 pl-3"
            :items="sortKeys"
            v-model="searchParams.selectedKeys"
            chips
            dense
            clearable
            label="Chips"
            multiple
            solo
            @change="doUpdate"
        ></v-select>
    <!--  sort 6col  -->
        <simple-sort-bar
            class="col-lg-6 p-0"
            :items="sortItems"
            @update="sortUpdate"
        />
      </div>
    </div>
    <div class="col-12 col-lg-6 bg-dark">
      Filter area
    </div>
  </div>
</template>

<script>
import SimpleSortBar from "@/components/SimpleSortBar";
export default {
  name: "MultiSearchBar",
  components: {SimpleSortBar},
  props: {
    sortItems: Array,
    sortKeys: Array,
  },
  data() {
    return {
      searchParams: {
        searchString: "",
        selectedKeys: [],
        sortBy: "",
        isAscending: true,
      }
    }
  },
  methods: {
    doSearch: function () {
      this.doUpdate();
    },
    sortUpdate: function (sortBy, isAscending) {
      this.searchParams.sortBy = sortBy;
      this.searchParams.isAscending = isAscending;
      this.doUpdate();
    },
    doUpdate: function () {
      console.log(this.searchParams);
      this.$emit('multi-search-bar-update', this.searchParams);
    }
  },
}
</script>

<style scoped>

</style>