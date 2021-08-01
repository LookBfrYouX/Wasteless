<template>
  <div id="app">
    <v-app>
      <navbar
          :query="query"
          @input="event => this.query = event.target.value"
      />
      <div class="w-100 main-content d-flex justify-content-center gradient-background">
        <router-view
            @initial-search-value="updateInput"
        ></router-view>
      </div>
      <footer class="info">

      </footer>
    </v-app>
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

  /**
   * Vuetify has one very annoying rule which sets link color. Overriding this is difficult as many rules
   * (especially bootstrap ones) set text color and the rule is specific: `v-application a` so it overrides
   * most other rules (e.g. bootstrap rules are often just a single class, while this is a class then an element
   * type). There is no way to 'undo' the effects of a CSS rule by adding more rules, and the `v-application`
   * class cannot be removed as it is required to for Vuetify to function properly. Modifying all bootstrap rules
   * to override this is difficult and Vuetify doesn't give us a easy way of modifying its CSS styles. Hence, the
   * best solution we found was to just delete that single rule on the client side using JS.
   */

  mounted: function () {
    const vuetifyStylesheet = document.querySelector("#vuetify-theme-stylesheet");
    vuetifyStylesheet.textContent = vuetifyStylesheet.textContent.replace(
        /\.v-application a {\s*color:\s*.+;\s*}/, "");
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
