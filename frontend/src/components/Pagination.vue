<template>
  <ul class="d-flex pagination">
    <li
      v-for="({pageNum, text}) in pages"
      v-bind:key="pageNum"
      v-bind:class="{disabled: pageNum == current}"
      class="page-item"
    >
      <a
        v-on:click="setPage"
        class="page-link"
      >
        {{text}}
      </a>
    </li>
  </ul>
</template>
<script>

export default {
  props: {
    start: {
      type: Number,
      required: false,
      default: 1
    },
    end: {
      type: Number,
      required: true
    },
    current: {
      type: Number,
      required: true
    },
    numPreviousPagesToShow: {
      type: Number,
      required: false,
      default: 2
    },
    numNextPagesToShow: {
      type: Number,
      required: false,
      default: 3
    },
    setPage: {
      type: Function,
      required: true
    }
  },
  computed: {
    pages() {
      const pages = [];
      if (this.current - 1 >= this.start) pages.push({
        text: "<",
        pageNum: this.current - 1
      });

      const min = Math.max(this.start, this.current - this.numPreviousPagesToShow);
      const max = Math.min(this.end  , this.current + this.numNextPagesToShow);
      for(let i = min; i <= max; i++) {
        pages.push({
          text: i,
          pageNum: i
        });
      }

      if (this.current + 1 <= this.end) pages.push({
        text: ">",
        pageNum: this.current + 1
      });
      
      return pages;
    }
  }
}
</script>