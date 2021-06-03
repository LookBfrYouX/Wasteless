<template>
  <ul class="d-flex pagination">
    <li
        v-for="({pageNum, key, text}) in pages"
        :key="key"
        :class="{disabled: pageNum == current}"
        class="page-item"
    >
      <a
          class="page-link hover-cursor-pointer"
          @click="setPage(pageNum)"
      >
        {{ text }}
      </a>
    </li>
  </ul>
</template>
<script>

export default {
  props: {
    /**
     * First page number. Default 1
     */
    start: {
      type: Number,
      required: false,
      default: 1
    },

    /**
     * Last page number
     */
    end: {
      type: Number,
      required: true
    },

    /**
     * Current page number
     */
    current: {
      type: Number,
      required: true
    },

    /**
     * Will show this many pages prior to the current
     */
    numPreviousPagesToShow: {
      type: Number,
      required: false,
      default: 2
    },

    /**
     * Will show this many pages past the current
     */
    numNextPagesToShow: {
      type: Number,
      required: false,
      default: 3
    },

    /**
     * Callback event when page is changed via user. Could be done via events, I guess
     */
    setPage: {
      type: Function,
      required: true
    }
  },
  computed: {
    /**
     * Computes the array of pages. The start and end are `<` and `>` for prev/next page if the current page is not the first or last respectively
     * @return{*} array of pages with `text`; text to show on page link; `pageNum`: page number to navigate to; `key`: key to use in list
     */
    pages() {
      const pages = [];
      if (this.current - 1 >= this.start) {
        pages.push({
          text: "<",
          pageNum: this.current - 1,
          key: "prev"
        });
      }

      const min = Math.max(this.start, this.current - this.numPreviousPagesToShow);
      const max = Math.min(this.end, this.current + this.numNextPagesToShow);
      for (let i = min; i <= max; i++) {
        pages.push({
          text: i,
          pageNum: i,
          key: i
        });
      }

      if (this.current + 1 <= this.end) {
        pages.push({
          text: ">",
          pageNum: this.current + 1,
          key: "next"
        });
      }

      return pages;
    }
  }
}
</script>