<!--
To use this component:

<sorted-paginated-item-list
  v-bind:items="myItems"
  v-slot="slotProps"
>
  <my-component v-bind:my-item="slotProps.item"/>
</sorted-paginated-item-list>

-->
<template>
  <div>
    <sort-sidebar
      v-bind:sortOptions="sortOptions"
      v-bind:currentSortOption="currentSortOption"
      v-on:update:currentSortOption="currentSortOption => $emit('update:currentSortOption', currentSortOption)"
    />
    <ul>
      <li
        v-for="item in itemsToDisplay"
        v-bind:key="getItemId(item)"
      >
        <slot v-bind:item="item">item.id</slot>
      </li>
    </ul> 

    <pagination
      v-bind:current="page"
      v-bind:end="numPages"
      v-bind:setPage="newPage => page = newPage">
    </pagination>
  </div>
</template>
<script>
import Pagination from "./Pagination"
import { constants } from "./../constants"
import SortSidebar from './SortSidebar.vue';


export default {
  components: {
    Pagination,
    SortSidebar 
  },

  props: {
    /**
     * Array of items to display. Should be the full, unsorted list of items so that client-side pagination can be done.
     * 
     * Only requirement is that each item has an `id` property
     */
    items: {
      required: true,
      type: Array // must have `id property
    },

    /**
     * Way to get unique identifier for each item. If string, uses that as the key for the item. If function, it should take in the item as an argument and return a key 
     */
    itemIdentifier: {
      required: false,
      default: "id"
    },

    resultsPerPage: {
      required: false,
      type: Number,
      default: constants.LISTS.RESULTS_PER_PAGE 
    },

    /**
     * [{
     *   name: String // name to display,
     *   sortMethod: (item1, item2) => -1, 0 or 1
     * }]
     */
    sortOptions: {
      required: true,
      type: Array,
    },

    /**
     * {
     * name: String,
     * sortMethod: Function,
     * reversed: Boolean
     * }
     */
    currentSortOption: {
      required: true,
      type: Object
    }
  },

  data() {
    return {
      page: 1 // 1-based indexing
    }
  },

  computed: {
    numPages() {
      return Math.ceil(this.items.length / this.resultsPerPage);
    },
    firstResultIndex() {
      return (this.page - 1) * this.resultsPerPage; // Page 1 has elements [0, resultsPerPage - 1]
    },
    lastResultIndex() {
      return Math.min(this.items.length, this.page * this.resultsPerPage);
    },
    itemsToDisplay() {
      // copy array to not mutate original array
      const sortedItems = [...this.items].sort(this.currentSortOption.sortMethod);
      if (this.currentSortOption.reversed) sortedItems.reverse();
      return sortedItems.slice(this.firstResultIndex, this.lastResultIndex);
    }
  },

  methods: {
    getItemId(item) {
      if (this.itemIdentifier instanceof Function) return this.itemIdentifier(item);
      return item[this.itemIdentifier];
    },
  },

  watch: {
    items() {
      if (this.numPages > this.page) this.page = this.numPages;
    },
 }
};
</script>
