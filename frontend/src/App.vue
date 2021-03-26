<style lang="scss">
  @import "./styles/custom.scss";
</style>

<template>
  <div id="app">
    <navbar
        v-bind:userId="userId"
        v-bind:force-search-update="() => key++"
        v-bind:query="query"
        v-on:input="event => this.query = event.target.value"
        v-bind:on-log-out="logOut"
        :key="navKey"
    />
    <div class="w-100 d-flex justify-content-center login-container gradient-background pb-4">
      <router-view
        v-bind:key="key"
        v-on:searchresultscreated="updateInput"
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
  data: function() {
    return {
      query: '',
      key: 0,
      // used to update the router view component in cases where refresh is needed but the router URL
      // is the same (e.g. user clicks search when they haven't updated the input field)
      navKey: 0
      // used to update the navbar component on logout as we couldn't figure out how to make
      // v-if re-trigger when local storage changes
    };
  },

  components: {
    Navbar
  },

  props: ["userId"],
  
  methods: {
    logOut() {
      window.localStorage.removeItem("userId");
      this.$router.push("/");
      // Reload window on logout
      this.navKey += 1;
    },

    updateInput: function(query) {
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
        document.title = to.name || 'Wasteless';
      }
    },
  }
}
</script>

<style>
[v-cloak] {
  display: none;
}
</style>