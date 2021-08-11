<template>
<!--  Test div search and filter container - remove this when integrating  -->
  <v-container fluid>
    <v-row>
      <!--    Main COL starts here  -->
      <v-col cols="12" lg="6" class="p-2">
        <v-row class="px-2">
          <!--  search 6col  -->
          <v-col cols="12" class="p-2">
            <v-subheader>Search businesses</v-subheader>
            <v-text-field
                class="col-12"
                v-model="searchParams.searchString"
                label="Search"
                prepend-inner-icon="search"
                solo
                dense
                @keydown.enter="doSearch"
            ></v-text-field>
          </v-col>
        </v-row>
        <v-row class="px-2">
          <!--  keys 6col  -->
          <v-col cols="12" lg="6" class="p-2">
            <v-subheader>Search By</v-subheader>
            <v-select
                :items="sortKeys"
                v-model="searchParams.selectedKeys"
                chips
                dense
                label="Search By"
                prepend-inner-icon="check"
                multiple
                solo
                @change="doUpdate"
            >
                <template v-slot:selection="{ item, index }">
                  <v-chip v-if="index === 0">
                    <span>{{ item }}</span>
                  </v-chip>
                  <span
                      v-if="index === 1"
                      class="grey--text text-caption"
                  >
                  (+{{ searchParams.selectedKeys.length - 1 }} others)
                </span>
              </template>
            </v-select>
          </v-col>
          <!--  sort 6col  -->
          <v-col cols="12" lg="6" class="p-2">
            <v-subheader>Sort</v-subheader>
            <simple-sort-bar
                :items="sortItems"
                @update="sortUpdate"
            />
          </v-col>
        </v-row>
      </v-col>
      <!--    Main COL finishes here  -->
    </v-row>
  </v-container>
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
    /**
     * This method is for calling event emitter on keystrokes or button press etc.
     */
    doSearch: function () {
      this.doUpdate();
    },
    /**
     * This method is called when sort update is caught.
     * Updates the sortBy and isAscending attributes before calling main update function.
     */
    sortUpdate: function (sortBy, isAscending) {
      this.searchParams.sortBy = sortBy;
      this.searchParams.isAscending = isAscending;
      this.doUpdate();
    },
    /**
     * This method emits the update to the parent component.
     * All data is stored in searchParams.
     */
    doUpdate: function () {
      this.$emit('multi-search-bar-update', this.searchParams);
    }
  },
}
</script>

<style scoped>

</style>