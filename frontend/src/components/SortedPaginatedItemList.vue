<!--
To use this component:

<sorted-paginated-item-list
  :items="myItems"
>
  <template v-slot:title>
    <h2>My Component List</h2>
  </template>

  <template v-slot:right-button>
    <button>Optional button</button>
  </template>

  <template v-slot:no-items>
    <p>Content to show if there are no items 
    </p>
  <template>

  <template v-slot:item="slotProps"
    <my-component :my-item="slotProps.item"/>
  </template>

</sorted-paginated-item-list>
-->
<template>
  <div class="w-100">
    <sort-sidebar
        v-if="showSortSidebar"
        :closeClicked="() => showSortSidebar = !showSortSidebar"
        :currentSortOption="currentSortOption"
        :sortOptions="sortOptions"
        class="sort-sidebar bottom-padding-navbar-height overflow-auto"
        @update:currentSortOption="currentSortOption => $emit('update:currentSortOption', currentSortOption)"
    />
    <!-- If the screen is large enough, then you can interact with both the sort options and list items at the same time. Otherwise, this will act as a background where clicking anywhere causes it to close -->
    <div
        v-if="showSortSidebar"
        class="sidebar-behind-close d-block d-lg-none"
        @click="() => showSortSidebar = false"
    ></div>

    <div class="container">
      <div class="row pt-4">
        <!-- If margin top used, pushes the sidebar down too so use padding instead -->
        <slot name="title"/>
      </div>
      <div class="row justify-content-between">
        <!-- sort by only appears when there are any items but the right button will always appear, and the slot is optional. So that the buttons always stay left/right, need to wrap them in divs -->
        <div>
          <button
              v-if="items.length"
              class="btn btn-info"
              type="button"
              @click="() => showSortSidebar = !showSortSidebar"
          >
            Sort
          </button>
        </div>
        <div>
          <slot name="right-button"/>
        </div>
      </div>

      <div v-if="!items.length" class="row mt-2">
        <slot name="no-items">
          <p>No items</p>
        </slot>
      </div>
      <div v-else class="row justify-content-center mt-2">
        <!-- Shift the items right if the sidebar is open to prevent overlap when the overlay isn't visible and the screen isn't too big. Not great as it doesn't shift the page title -->
        <div
            v-if="showSortSidebar"
            class="col-3 d-xl-none"
        ></div>
        <div class="col-12 col-md-8 col-lg-6">
          <div>
            Displaying results {{ firstResultIndex + 1 }} - {{ lastResultIndex }} out of
            {{ items.length }}

            <!--
              Add 1 to `firstResultIndex` as zero-based indexing used internally (first item should be item 1, not 0).
              JS's `slice` method includes the first element but excludes the last element (like Python's arr[1:4]), so the last element included in the list is `lastResultIndex - 1`.
              Add 1 to convert to one-based indexing which simplifies to just `lastResultIndex`. -->
          </div>
          <ul class="list-unstyled">
            <li
                v-for="item in itemsToDisplay"
                :key="getItemId(item)"
            >
              <slot :item="item" name="item">item.id</slot>
            </li>
          </ul>
          <div class="w-100 mt-3 d-flex justify-content-center">
            <!-- Currently have a dummy div that shifts the content right when sidebar is open and user can iteract with items, so the pagination needs to be in the same row -->
            <pagination
                :current="page"
                :end="numPages"
                :setPage="newPage => page = newPage">
            </pagination>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Pagination from "./Pagination"
import {constants} from "@/constants"
import SortSidebar from './SortSidebar.vue';

export default {
  components: {
    Pagination,
    SortSidebar
  },

  props: {
    /**
     * Array of items to display. Should be the full, unsorted list of items so that client-side pagination can be done.
     */
    items: {
      required: true,
      type: Array // must have `id property
    },

    /**
     * Way to get unique identifier for each item instead of using the default `item.id`.
     * If string, uses that as the key for the item.
     * If function, it should take in the item as an argument and return a key
     */
    itemIdentifier: {
      required: false,
      default: "id"
    },

    /**
     * Number of results to show per page
     */
    resultsPerPage: {
      required: false,
      type: Number,
      default: constants.SORTED_PAGINATED_ITEM_LIST.RESULTS_PER_PAGE
    },

    /**
     * Sort options to display on the sidebar
     * [{
     *   name: String // name to display, used as key
     *   sortMethod: (item1, item2) => -1, 0 or 1 (JS sort function)
     * }]
     * A simple example for numerical sort: (a, b) => a.someVal - b.someVal
     * An unreadble way to use alphabetical sort (nested ternary expression): a.name == b.name? 0: (a.name > b.name? 1: -1)
     */
    sortOptions: {
      required: true,
      type: Array,
    },

    /**
     * {
     * name: String,
     * sortMethod: Function,
     * reversed: Boolean (toggles between sorting the list ascending or descending)
     * }
     */
    currentSortOption: {
      required: true,
      type: Object
    },
  },

  data() {
    return {
      page: 1, // 1-based indexing
      showSortSidebar: false

    }
  },

  computed: {
    /**
     * Number of pages
     */
    numPages() {
      return Math.ceil(this.items.length / this.resultsPerPage);
    },

    /**
     * Index of first result that should be shown with pagination
     */
    firstResultIndex() {
      return (this.page - 1) * this.resultsPerPage; // Page 1 has elements [0, resultsPerPage - 1]
    },

    /**
     * Exclusive index of last result that should be shown (i.e. given index - 1 is the last result to show)
     */
    lastResultIndex() {
      return Math.min(this.items.length, this.page * this.resultsPerPage);
    },

    /**
     * Sorted, paginated list of items to display
     */
    itemsToDisplay() {
      // copy array to not mutate original array
      const sortedItems = [...this.items].sort(this.currentSortOption.sortMethod);
      if (this.currentSortOption.reversed) {
        sortedItems.reverse();
      }
      return sortedItems.slice(this.firstResultIndex, this.lastResultIndex);
    }
  },

  methods: {
    /**
     * Gets the key for the given item - can be either a string or a function
     */
    getItemId(item) {
      if (this.itemIdentifier instanceof Function) {
        return this.itemIdentifier(item);
      }
      return item[this.itemIdentifier];
    },
  },

  watch: {
    /**
     * When items get updated, reset the page number
     */
    items() {
      this.page = 1;
    },

    /**
     * When items get updated, reset the page number
     */
    resultsPerPage() {
      this.page = 1;
    }
  }
};
</script>
<style scoped>
.sort-sidebar {
  z-index: 9;
  position: fixed;
  height: 100vh;

  /*content has padding-top that is approximately the height of the navbar, but can sometimes be a bit too much. Add negative margin so that the background of the sidebar intersects with the navbar (but goes behind it) */
  margin-top: -1em;
  padding-top: 1em;
  /* WARNING: content overflows past the bottom of the screen if it is there is too much content */
  width: min(80%, 20em);
}

.sidebar-behind-close {
  z-index: 1;
  /* Low z index so that the navbar is above it */
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.8);
}

.item-width {
  width: min(80%, 40em);
}
</style>