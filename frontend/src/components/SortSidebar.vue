<template>
  <div class="sort-results bg-light">
    <div class="p-3">
      <h3 class="d-inline">Sort By:</h3>
      <button class="float-right btn btn-light d-flex" type="button" v-on:click="closeClicked()">
        <span class="material-icons">arrow_back</span>
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
     * Array of sort options to display.
     * Must have at least the 'name' property (text to display) and the name must be unique
     * [{name: String}]
     */
    sortOptions: {
      type: Array,
      required: true
    },

    /**
     * Callback when the user closes the sidebar
     */
    closeClicked: {
      type: Function,
      required: true
    },

    /**
     * Current sort option.
     * Must contain at least the `name` property and a `reversed` boolean.
     * An update event, `update:currentSortOption` is emitted when it gets updated
     * The name is used as a key to determine if the `reversed` boolean should be toggled
     * { name: String, reversed: Boolean}
     */
    currentSortOption: {
      type: Object,
      required: true
    },
  },

  methods: {
    /**
     * Callback when the user clicks on one of the sort options.
     * If the user clicks the current sort option (determined by comparing names) the `currentSortOption` `reversed
     * If it is a different option, the `reversed` property of the current sort option is unmodified.
     */
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