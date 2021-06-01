<template>
  <div id="navbar">
    <nav class="navbar fixed-top navbar-expand-lg navbar-light bg-light">
      <!-- Title -->
      <a class="navbar-brand" href="javascript:" @click="homeButtonClicked()">Navbara Pigeon</a>
      <!-- Hamburger button -->
      <button
          id="hamburger-button"
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
        <!-- Left group: links to profile and business -->
        <ul class="navbar-nav d-flex justify-content-between align-items-lg-center w-100 align-items-start">
          <div
              :class="{'d-lg-none': navbarLinks.length > 2}"
              class="d-flex d-xl-flex flex-wrap flex-lg-nowrap justify-content-center"
          >
            <!-- List of links in XL only hidden if lg and more than 2 links -->
            <li
                v-for="({name, click, active}, i) in navbarLinks"
                :key="i"
                :class="{ 'mx-4': i != 0, active }"
                class="nav-item d-flex align-items-center text-center mx-lg-0"
            >
              <a
                  class="nav-link"
                  href="javascript:"
                  @click="click"
              > {{ name }}
              </a>
            </li>
          </div>

          <li
              :class="{'d-lg-block': navbarLinks.length > 2}"
              class="navbar-item dropdown d-none d-xl-none p-absolute"
          >
            <!-- dropdown menu for the business/profile links only if lg AND if more than 2 links-->
            <a
                id="navbarDropdownBusinessLinks"
                aria-expanded="false"
                aria-haspopup="true"
                class="nav-link dropdown-toggle"
                data-toggle="dropdown"
                href="#"
                role="button"
            >
              Quick Links
            </a>
            <div aria-labelledby="navbarDropdownBusinessLinks" class="dropdown-menu">
              <a
                  v-for="({name, click, active}, i) in navbarLinks"
                  :key="i"
                  :class="{ active }"
                  class="dropdown-item"
                  href="javascript:"
                  @click="click"
              >
                {{ name }}
              </a>
            </div>
          </li>

          <!-- Center group: search input and button -->
          <li v-if="isSignedIn" class="navbar-item d-flex search-container w-100">
            <form class="input-group navbar-center form-inline" @submit.prevent="search">
              <div class="input-group w-100">
                <div class="input-group-prepend h-100">
                  <span class="input-group-text">
                    <span class="material-icons">search</span>
                  </span>
                </div>
                <input
                    :value="query"
                    class=" form-control mr-sm-2 h-100"
                    placeholder="Search"
                    type="text"
                    @input="event => $emit('input', event)"
                />
              </div>
            </form>
          </li>

          <!-- Right group: User and acting as -->
          <li v-if="isSignedIn" class="nav-item dropdown">
            <a id="navbarDropdownMenuLink" aria-expanded="false"
               aria-haspopup="true" class="nav-link dropdown-toggle d-flex align-items-center"
               data-toggle="dropdown"
               href="javascript:" role="button">
              <img v-if="isActingAsBusiness"
                   alt="User is acting as business"
                   class="nav-picture rounded-circle"
                   src="@/../assets/images/default-business-thumbnail.svg"
              >
              <img v-else
                   alt="User is acting as self"
                   class="nav-picture rounded-circle"
                   src="@/../assets/images/default-user-thumbnail.svg"
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
                 @click="switchActingAs(null)">
                {{ authUser.firstName }} {{ authUser.lastName }}
                <span v-if="currentActingAs === null">
                  &#10003;
                </span>
              </a>
              <div v-if="actingAsEntities.length" class="dropdown-divider"></div>
              <a v-for="business in actingAsEntities" :key="business.id"
                 class="dropdown-item"
                 href="javascript:"
                 @click="switchActingAs(business)">
                {{ business.name }}
                <span v-if="business === currentActingAs">

                  &#10003;
                  </span>
              </a>
              <div class="dropdown-divider"></div>
              <a class="dropdown-item" href="javascript:" @click="signOut">Sign out</a>
            </div>
          </li>

          <!-- Right group (if not signed in) -->
          <div v-if="!isSignedIn" class="d-lg-flex ml-lg-auto">
            <li v-if="this.$route.name != 'SignIn'" class="nav-item">
              <a
                  class="btn btn-outline-success my-1 my-sm-0 mr-sm-1"
                  @click="pushOrGo('SignIn')"
              >
                Sign In
              </a>
            </li>
            <li v-if="this.$route.name != 'UserCreate'" class="nav-item">
              <a
                  class="btn btn-outline-success my-1 my-sm-0 mr-sm-1"
                  @click="pushOrGo('UserCreate')"
              >
                Sign Up
              </a>
            </li>
          </div>
        </ul>
      </div>
    </nav>
    <error-modal
        :goBack="false"
        :hideCallback="() => signOutErrorMessage = null"
        :refresh="false"
        :retry="signOut"
        :show="signOutErrorMessage !== null"
        title="Couldn't sign you out"
    >
      <p>{{ signOutErrorMessage }}</p>
    </error-modal>
  </div>
</template>


<script>
import {Api} from "@/Api";
import ErrorModal from "./ErrorModal";

export default {
  name: "navbar",
  props: ["query"],
  components: {ErrorModal},
  data() {
    return {
      signOutErrorMessage: null
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
     * Returns a list of navbar links depending on if they are acting as a business or user
     * @return array of objects with properties:
     *   name: {String} text value of item
     *   click: {() => void`{} action to run when item clicked
     *   active: {Boolean} if true, `active` class given to that item; the link for the page the user is currently on should show this
     */
    navbarLinks() {
      const viewName = this.$route.name;

      if (!this.isSignedIn) {
        return [];
      }
      if (!this.isActingAsBusiness) {
        return [
          {
            name: "Profile",
            click: this.profileClicked,
            active: viewName == "UserProfile",
          },
          {
            name: "Marketplace",
            click: () => this.pushOrGo('Marketplace'),
            active: viewName == "Marketplace",
          }
        ];
      }

      return [
        {
          name: "Business Profile",
          click: this.profileClicked,
          active: viewName == "BusinessProducts",
        },
        {
          name: "Product Catalogue",
          click: () => this.pushOrGoToBusinessPage("BusinessProducts"),
          active: viewName == "BusinessProducts",
        }, {
          name: "Inventory",
          click: () => this.pushOrGoToBusinessPage("BusinessInventory"),
          active: viewName == "BusinessInventory",
        }, {
          name: "Listings",
          click: () => this.pushOrGoToBusinessPage("BusinessListings"),
          active: viewName == "BusinessListings"
        }
      ];
    },

    /**
     * Checks if a user is signed in or not
     */
    isSignedIn() {
      return this.$stateStore.getters.isSignedIn();
    },

    /**
     * Checks if user is acting as a business
     * @returns true if signed in and acting as business
     */
    isActingAsBusiness() {
      return this.$stateStore.getters.isActingAsBusiness();
    },

    /**
     * True if admin, false if not OR IF NOT SIGNED IN
     */
    isAdmin() {
      return this.$stateStore.getters.isAdmin();
    },

    /**
     * Returns a list of business
     * @return {Business[]} empty list if user not signed in or has no businesses
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
     * Switch acting as wrapper
     * Redirects to profile page
     */
    switchActingAs: async function (business) {
      this.$helper.goToProfile(business, this.$router, this.$route);
      this.$stateStore.actions.setActingAs(business);
      // Must set business after redirecting as some pages do not like it if a 
      // user accesses a business page they are not an admin of
    },

    /**
     * Redirects to 'home' if signed in, '/' otherwise
     */
    homeButtonClicked() {
      if (this.$stateStore.getters.isSignedIn()) {
        this.pushOrGo('Home');
      } else {
        this.pushOrGo('Landing');
      }
    },

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
     * or error page if sign out fails
     */
    signOut: async function () {
      this.signOutErrorMessage = null;
      try {
        await Api.logOut();
      } catch (err) {
        this.signOutErrorMessage = err.userFacingErrorMessage;
        return;
      }
      await this.pushOrGo("Landing");
      await this.$stateStore.actions.deleteAuthUser();
      // Some pages react badly if there is no auth user, so delete after navigating to landing page
      // For some reason error modal doesn't get disposed of properly
    },

    /**
     * Handler when search button clicked
     * Navigates to search page with current query, or reloads page if on search page and query has not changed
     */
    search: async function () {
      const searchName = "Search";
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
     * Goes to business page, given the user is acting as a business
     * @param name name of page to go to
     */
    pushOrGoToBusinessPage: async function (name) {
      const business = this.$stateStore.getters.getActingAs();
      if (business == null) {
        return;
      }
      const params = {
        name,
        params: {
          businessId: business.id
        }
      };
      if (this.$route.name == name && this.$route.params.businessId == business.id) {
        await this.$router.go();
      } else {
        await this.$router.push(params);
      }
    },
  },
};
</script>
<style scoped>
nav {
  z-index: 10;
}

.dropdown-menu {
  z-index: 900000;
}

.admin-text {
  margin: -0.5em 0 0;
  font-size: 0.8em;
}

.search-container {
  max-width: 25em;
  flex-grow: 2;
}

nav .active {
  /* For some reason, Bootstrap adds underline to the links. Doesn't show up on bootstrap example website */
  text-decoration: inherit;
}
</style>
