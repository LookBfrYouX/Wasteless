<template>
  <div id="navbar">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <router-link exact to="/">
        <a class="navbar-brand">App Logo</a>
      </router-link>

      <button
          aria-controls="navbarSupportedContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
          class="navbar-toggler"
          data-target="#navbarSupportedContent"
          data-toggle="collapse"
          type="button"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <div id="navbarSupportedContent" class="collapse navbar-collapse">

        <ul class="navbar-nav mr-auto">
          <router-link v-if="isLoggedIn" active-class="active" exact to="/profile">
            <li class="nav-item">
              <a class="nav-link">
                Profile
              </a>
            </li>
          </router-link>
        </ul>
        <!--if logged in shows this section-->
        <div v-if="isLoggedIn" class="d-flex">
          <form class="input-group mt-2 navbar-center form-inline" v-on:submit.prevent="search">
            <div class="input-group mb-3 navbar-search">
              <div class="input-group-prepend h-100">
                <span class="input-group-text"><span class="material-icons">search</span></span>
              </div>
              <input
                  class=" form-control mr-sm-2 h-100"
                  placeholder="Search"
                  type="text"
                  v-bind:value="query"
                  v-on:input="event => $emit('input', event)"
              />
            </div>

          </form>
          <div id="navbar-list-4" class="collapse navbar-collapse">
            <ul class="navbar-nav">
              <li class="nav-item dropdown">
                <a id="navbarDropdownMenuLink" aria-expanded="false"
                   aria-haspopup="true" class="nav-link dropdown-toggle d-flex align-items-center" data-toggle="dropdown"
                   href="#" role="button">
                  <img class="nav-picture rounded-circle" src="placeholder-profile.png">
                  <div class="d-flex flex-column mx-1">
                    <span class="m-0 p-0 text-dark">{{ authUser.firstName }} {{
                        authUser.lastName
                      }}</span>
                    <span v-if="isAdmin" class="admin-text p-0 text-faded">ADMIN</span>
                  </div>
                </a>
                <div aria-labelledby="dropdownMenuButton" class="dropdown-menu dropdown-menu-right">
                  <button v-for="business in actingAsEntities" :key="business.id"
                          v-on:click="switchActingAs(business)">
                    {{business.name}}
                  </button>

                  <button class="dropdown-item" v-on:click="logOut">Log out</button>
                </div>
              </li>
            </ul>
          </div>
        </div>
        <!-- otherwise shows login button-->
        <span v-else>
          <a
              v-if="this.$route.name != 'login'"
              class="btn btn-outline-success my-2 my-sm-0 mr-sm-2"
              type="button"
              v-on:click="login"
          >
            Login
          </a>
          <a
              v-if="this.$route.name != 'signUp'"
              class="btn btn-outline-success my-2 my-sm-0 mr-sm-2"
              type="button"
              v-on:click="signUp"
          >
            Sign Up
          </a>
        </span>
      </div>
    </nav>
    <error-modal
        title="Couldn't log you out"
        v-bind:hideCallback="() => logOutErrorMessage = null"
        v-bind:refresh="false"
        v-bind:retry="this.logOut"
        v-bind:show="logOutErrorMessage !== null"
    >
      <p>{{ logOutErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<script>
const Api = require("./../Api").default;
import ErrorModal from "./Errors/ErrorModal";

export default {
  name: "navbar",
  props: ["query"],
  components: {ErrorModal},
  data() {
    return {
      logOutErrorMessage: null
    }
  },
  computed: {
    /**
     * Wrapper around state store auth user
     */
    authUser: {
      get: function () {
        return this.$stateStore.getters.getAuthUser();
      },
      set: function (val) {
        this.$stateStore.actions.setAuthUser(val);
      }
    },

    /**
     * Checks if a user is logged in or not
     */
    isLoggedIn() {
      return this.$stateStore.getters.isLoggedIn();
    },

    /**
     * True if admin, false if not OR IF NOT LOGGED IN
     */
    isAdmin() {
      return this.$stateStore.getters.isAdmin();
    },

    actingAsEntities() {
      let actingAsEntities = this.authUser.businesses;
      console.log(actingAsEntities);
      let currentBusiness = this.$stateStore.getters.getActingAs()
      if (currentBusiness != null) {
        let index = actingAsEntities.indexOf(currentBusiness);
        if (index !== -1) {
          actingAsEntities.splice(index, 1);
        }
      }
      return actingAsEntities;
    }
  },
  methods: {
    /**
     * Go (refresh page) if already on the same page, or pushes if it is a different page. Does not support params
     */
    pushOrGo: async function (routeName) {
      this.$route.name === routeName ?
          this.$router.go() :
          this.$router.push({name: routeName});
    },

    /**
     * Navigate to log in page
     */
    login: async function () {
      console.log(localStorage.getItem("authUser"));
      return this.pushOrGo("login");
    },

    /**
     * Navigate to sign up page
     */
    signUp: async function () {
      return this.pushOrGo("signUp");
    },

    /**
     * Removes authorized user and redirects to home,
     * or error page if log out fails
     */
    logOut: async function () {
      try {
        await Api.logOut();
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.logOutErrorMessage = err.userFacingErrorMessage;
        return;
      }

      await this.$stateStore.actions.deleteAuthUser();
      await this.pushOrGo("home");
    },

    /**
     * Handler when search button clicked
     * Navigates to search page with current query, or reloads page if on search page and query has not changed
     */
    search: async function () {
      const searchName = "search";
      let newQuery = this.$route.params.query;

      if (this.$route.name == searchName && newQuery == this.query) {
        await this.$router.go();
        return;
      }

      await this.$router.push({
        name: searchName,
        params: {
          query: this.query,
        },
      });
    },

    switchActingAs: function (business) {
      this.$stateStore.actions.setActingAs(business);
    },
  },
};
</script>

<style scoped>

.admin-text {
  margin: -0.5em 0 0;
  font-size: 0.8em;
}

.navbar-search {
  width: 100% !important;
}

.navbar-center {
  position: absolute;
  left: 50%;
  transform: translatex(-50%);
  height: 100%;
  width: 30%;
}

.navbar {
  height: 50px;
}

</style>