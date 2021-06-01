<template>
  <div id="app">
    <navbar
        v-bind:query="query"
        v-on:input="event => this.query = event.target.value"
    />
    <div class="w-100 main-content d-flex justify-content-center gradient-background">
      <router-view
          v-on:initial-search-value="updateInput"
      ></router-view>
    </div>
    <footer class="info">

    </footer>
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
  }
}
</script>

<style>
[v-cloak] {
  display: none;
}
</style>
