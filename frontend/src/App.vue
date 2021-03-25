<style lang="scss">
  @import "./styles/custom.scss";
</style>

<template>
  <div id="app">
    <navbar v-bind:userId="userId" v-on:logOut="logOut()" :key="key"/>
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
      key: 0
    };
  },

  components: {
    Navbar
  },

  props: ["userId"],
  
  methods: {
    search() {
      const searchName = "search";
      let newQuery = this.$route.params.query;

      if (this.$route.name == searchName && newQuery == this.query) {
        this.key = (this.key + 1) % 2;
        // Reloads the search component by updating the key, but doesn't add it to history
        return;
      }
      this.$router.push({
        name: searchName,
        params: {
          query: this.query
        }
      });
    },

    checkLogin() {
      // returns true if value (user id) is assigned to login in localStorage
      return Boolean(localStorage.getItem("userId"));
    },

    login() {
      if (this.$route.name != "login") this.$router.push({ name: "login" });
    },

    logOut() {
      window.localStorage.removeItem("userId");
      this.$router.push("/");
      // Reload window on logout
      this.key += 1;
    },

    profile() {
      if (this.$route.name != "profile") this.$router.push({ name: "profile" });
    },

    home() {
      if (this.$route.path != "/") this.$router.push("/");
    },

    signUp() {
      if (this.$route.path != "/signUp") this.$router.push("/signUp");
    },

    updateInput: function(query) {
      // When page is loaded and if router-view is search results page,
      // it will send an event with the current value of the text box
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