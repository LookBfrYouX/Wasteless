<style lang="scss">
@import "./styles/custom.scss";
</style>

<template>
  <div id="app">
    <navbar
        v-bind:query="query"
        v-on:input="event => this.query = event.target.value"
    />
    <div class="w-100 d-flex justify-content-center gradient-background pb-4">
      <router-view
          v-on:searchresultscreated="updateInput"
          v-bind:setDocumentTitle="setDocumentTitle"
      ></router-view>
    </div>
  </div>
</template>

<script>
import Navbar from './components/Navbar.vue';

export default {
  name: 'app',
  data: function () {
    return {
      query: '',
    };
  },

  components: {
    Navbar
  },

  methods: {
    updateInput: function (query) {
      // When page is loaded and if router-view is search results page,
      // the search results page will send an event with the current value of the text box
      // to App, which will then input Navbar props
      this.query = query;
    }
  },
  watch: {
    $route: {
      immediate: true,
      handler(to) {
        document.title = to.meta.title + " - Wasteless" || 'Wasteless';
      }
    },

    /**
     * Set document title
     * Title is set on router component load (see router.js), but if the page
     * needs to update title, this callback should be used
     */
    setDocumentTitle(title) {
      document.title = title;
    }
  }
}
</script>

<style>
[v-cloak] {
  display: none;
}
</style>
