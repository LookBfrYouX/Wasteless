<template>
        <div v-if="isOpen" class="sort-results bg-light">
          <div class="p-3">
            <h3 class="d-inline">Sort by</h3>
            <button class="float-right btn btn-light" type="button" v-on:click="toggleOpen()">
              <span>&larr;</span>
            </button>
            <ul id="search-headers" class="list-unstyled"
                v-bind:class='{"table-reversed": currentSortOption.reversed}'>
              <li
                  v-for="sortOption in sortOptions"
                  v-bind:key="sortOption.name"
                  class="mb-1"
                  v-bind:class='{"current-sort": currentSortOption.name == sortOption.name}'
                  v-on:click="sortByClicked(sortOption)"
              > {{ sortOption.name }}
              </li>
            </ul>
          </div>
        </div>
</template>
<style scoped>
.sort-results li.current-sort::after {
  content: '\2191';
}

.sort-results .table-reversed li.current-sort::after {
  content: '\2193';
}
</style>
<script>
export default {
  props: {
    /**
     * [{name: String}]
     */
    sortOptions: {
      type: Array,
      required: true
    },

    /**
     * name: String
     * reversed: Boolean
     */
    currentSortOption: {
      type: Object,
      required: true
    },

    isOpen: {
      type: Boolean,
      // required: true,
      default: true
    }
  },

  methods: {
    toggleOpen() {
      this.$emit("update:isOpen", !this.isOpen);
    },

    sortByClicked(sortOption) {
      const eventName = "update:currentSortOption";
      if (this.currentSortOption.name == sortOption.name) this.$emit(eventName, {
        ...this.currentSortOption,
        reversed: !this.currentSortOption.reversed
      });

      else this.$emit(eventName, {
        ...sortOption,
        reversed: this.currentSortOption.reversed
      });
    }
  }
}
</script>