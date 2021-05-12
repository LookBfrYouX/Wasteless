<template>
  <div class="sort-results bg-light">
    <div class="p-3">
      <h3 class="d-inline">Sort By:</h3>
      <button class="float-right btn btn-light d-flex" type="button" v-on:click="closeClicked()">
        <span class="material-icons">clear</span>
      </button>
      <ul
        id="search-headers"
        class="list-unstyled w-100 mt-2"
        v-bind:class="{'table-reversed': currentSortOption.reversed}"
      >
        <li
            v-for="(sortOption, i) in sortOptions"
            v-bind:key="sortOption.name"
            class="w-100"
            v-bind:class='{"current-sort": currentSortOption.name == sortOption.name}'
            v-on:click="sortByClicked(sortOption)"
        > 
          <div class="dropdown-divider" v-if="i"></div>
          <div class="w-100 sort-text rounded p-1 d-flex flex-row justify-content-between align-items-center">
            <span>{{ sortOption.name }}</span>
            <span class="arrow material-icons user-select-none">arrow_upward</span>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>
<style scoped>
#search-headers li .sort-text:hover {
  /* background-color: white; */
  box-shadow: 0 0 10px 3px rgba(0,0,0, 0.05);
}
.arrow {
  visibility: hidden;
  transition: transform 0.1s;
}

.current-sort .arrow {
  visibility: visible;
}

.table-reversed .current-sort .arrow {
  transform: rotate(180deg);
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