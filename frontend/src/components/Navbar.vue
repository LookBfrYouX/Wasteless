<template>
  <div id="navbar">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <!-- Title -->
      <a class="navbar-brand" href="javascript:" v-on:click="pushOrGo('home')">Navbara Pigeon</a>
      <!-- Hamburger button -->
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

      <!-- Overflow content -->
      <div id="navbarSupportedContent" class="collapse navbar-collapse">
        <!-- Left group -->
        <ul class="navbar-nav d-flex justify-content-between align-items-lg-center w-100 align-items-start">
          <div class="d-lg-flex">
            <!--Profile page link -->
            <li v-if="isLoggedIn" class="nav-item mr-lg-auto d-flex align-items-center text-center">
              <a class="nav-link" href="javascript:" v-on:click="profileClicked">
                {{ currentActingAs ? "Business " : "" }} Profile
              </a>
            </li>
            <!-- Product catalog link -->
            <li v-if="isActingAsBusiness" class="navbar-item mr-lg-auto d-flex align-items-center">
              <a class="nav-link" href="javascript:" v-on:click="productCatalogClicked">
                Catalogue
              </a>
            </li>
          </div>

          <!-- Center group: search input and button -->
          <li v-if="isLoggedIn" class="navbar-item d-flex search-container w-100">
            <form class="input-group navbar-center form-inline" v-on:submit.prevent="search">
              <div class="input-group w-100">
                <div class="input-group-prepend h-100">
                  <span class="input-group-text">
                    <span class="material-icons">search</span>
                  </span>
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
          </li>

          <!-- Right group: User and acting as -->
          <li v-if="isLoggedIn" class="nav-item dropdown">
            <a id="navbarDropdownMenuLink" aria-expanded="false"
               aria-haspopup="true" class="nav-link dropdown-toggle d-flex align-items-center"
               data-toggle="dropdown"
               href="javascript:" role="button">
              <img v-if="isActingAsBusiness"
                   alt="User is acting as business"
                   class="nav-picture rounded-circle"
                   src="./../../assets/images/default-business-thumbnail.svg"
              >
              <img v-else
                   alt="User is acting as self"
                   class="nav-picture rounded-circle"
                   src="./../../assets/images/default-user-thumbnail.svg"
              >
              <div class="d-flex flex-column mx-1">
                <span class="m-0 p-0 text-dark">
                  {{ printCurrentActingAs }}
                </span>
                <span v-if="isAdmin" class="admin-text p-0 text-faded">ADMIN</span>
              </div>
            </a>

            <div aria-labelledby="dropdownMenuButton" class="dropdown-menu position-absolute">
              <div class="h4 dropdown-header">Act as</div>
              <a class="dropdown-item" href="javascript:"
                 v-on:click="$stateStore.actions.setActingAs()">
                {{ authUser.firstName }} {{ authUser.lastName }}
                <span v-if="currentActingAs === null">
                  &#10003;
                </span>
              </a>
              <a v-for="business in actingAsEntities" :key="business.id"
                 class="dropdown-item"
                 href="javascript:"
                 v-on:click="$stateStore.actions.setActingAs(business)">
                {{ business.name }}
                <span v-if="business === currentActingAs">
                    &#10003;
                  </span>
              </a>
              <div class="dropdown-divider"></div>
              <a class="dropdown-item" href="javascript:" v-on:click="logOut">Log out</a>
            </div>
          </li>

          <!-- Right group (if not logged in) -->
          <div v-if="!isLoggedIn" class="d-lg-flex ml-lg-auto">
            <li v-if="this.$route.name != 'login'" class="nav-item">
              <a
                  class="btn btn-outline-success my-1 my-sm-0 mr-sm-1"
                  type="button"
                  v-on:click="pushOrGo('login')"
              >
                Login
              </a>
            </li>
            <li v-if="this.$route.name != 'Sign Up'" class="nav-item">
              <a
                  class="btn btn-outline-success my-1 my-sm-0 mr-sm-1"
                  type="button"
                  v-on:click="pushOrGo('signUp')"
              >
                Sign Up
              </a>
            </li>
          </div>
        </ul>
      </div>
    </nav>
    <error-modal
        title="Couldn't log you out"
        v-bind:goBack="false"
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
const {Api} = require("./../Api.js");
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
      }
    },

    /**
     * Checks if a user is logged in or not
     */
    isLoggedIn() {
      return this.$stateStore.getters.isLoggedIn();
    },

    /**
     * Checks if user is acting as a business
     * @returns true if logged in and acting as business
     */
    isActingAsBusiness() {
      return this.$stateStore.getters.isActingAsBusiness();
    },

    /**
     * True if admin, false if not OR IF NOT LOGGED IN
     */
    isAdmin() {
      return this.$stateStore.getters.isAdmin();
    },

    /**
     * Returns a list of business
     * @return {Business[]} empty list if user not logged in or has no businesses
     */
    actingAsEntities() {
      return this.authUser.businessesAdministered;
    },

    currentActingAs() {
      return this.$stateStore.getters.getActingAs();
    },

    printCurrentActingAs() {
      if (this.currentActingAs != null) {
        return this.currentActingAs.name;
      } else {
        return `${this.authUser.firstName} ${this.authUser.lastName}`;
      }
    },
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
     * Removes authorized user and redirects to home,
     * or error page if log out fails
     */
    logOut: async function () {
      this.logOutErrorMessage = null;
      try {
        await Api.logOut();
      } catch (err) {
        // if (await Api.handle401.call(this, err)) return;
        this.logOutErrorMessage = err.userFacingErrorMessage;
        return;
      }
      await this.pushOrGo("landing");
      await this.$stateStore.actions.deleteAuthUser();
      // Some pages react badly if there is no auth user, so delete after navigating to landing page
      // For some reason error modal doesn't get disposed of properly
    },

    /**
     * Handler when search button clicked
     * Navigates to search page with current query, or reloads page if on search page and query has not changed
     */
    search: async function () {
      const searchName = "search";
      let newQuery = this.$route.params.query;

      // If already on search with same query, refresh instead of reloading
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

    /**
     * Profile button page clicked
     * If acting as business, goes to business profile. Otherwise, user profile.
     * If already on own profile page, reloads the page
     */
    profileClicked: async function () {
      this.$helper.goToProfile.bind(this)();
    },

    /**
     * Link to product catalog page clicked
     */
    productCatalogClicked: async function () {
      const business = this.$stateStore.getters.getActingAs();
      if (business == null) {
        return;
      }
      const params = {
        name: "productCatalogue",
        params: {
          businessId: business.id
        }
      };
      if (this.$route.name == 'productCatalogue' && this.$route.params.businessId == business.id) {
        await this.$router.go();
      } else {
        await this.$router.push(params);
      }
    }
  },
};
</script>

<style scoped>

.dropdown-menu {
  z-index: 900000;
}

.admin-text {
  margin: -0.5em 0 0;
  font-size: 0.8em;
}

.search-container {
  max-width: 30em;
  flex-grow: 2;
}
</style>
