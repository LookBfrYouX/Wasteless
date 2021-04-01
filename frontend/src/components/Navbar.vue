<template>
  <div id="navbar">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <a class="navbar-brand" v-on:click="home">Navbar</a>
      <button
        class="navbar-toggler"
        type="button"
        data-toggle="collapse"
        data-target="#navbarSupportedContent"
        aria-controls="navbarSupportedContent"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
          <li class="nav-item">
            <a class="nav-link" v-on:click="home"
              >Home <span class="sr-only">(current)</span></a
            >
          </li>
        </ul>
        <!--if logged in shows this section-->
        <div v-if="checkLogin() == true" class="d-flex">
          <div>
            <!-- User role status icon (shows icon if user is admin) -->
            <p>{{ userRole }}</p>
          </div>
          <form class="form-inline my-2 my-lg-0" v-on:submit.prevent="search">
            <input
              class="form-control mr-sm-2"
              type="text"
              v-bind:value="query"
              v-on:input="event => $emit('input', event)"
              placeholder="Search"
            />
            <button
              class="btn btn-outline-success my-2 my-sm-0 mr-sm-2"
              type="submit"
            >
              Go
            </button>
          </form>
          <div class="dropdown">
            <button
              class="btn btn-secondary dropdown-toggle"
              type="button"
              id="dropdownMenuButton"
              data-toggle="dropdown"
              aria-haspopup="true"
              aria-expanded="false"
            >
              Menu
            </button>
            <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
              <button class="dropdown-item" v-on:click="profile">
                Profile Page
              </button>
              <button class="dropdown-item" v-on:click="logOut">Log out</button>
            </div>
          </div>
        </div>
        <!-- otherwise shows login button-->
        <span v-else>
          <button
            class="btn btn-outline-success my-2 my-sm-0 mr-sm-2"
            v-on:click="login"
            type="button"
            v-if="this.$route.path != '/login'"
          >
            Login
          </button>
          <button
            class="btn btn-outline-success my-2 my-sm-0 mr-sm-2"
            v-on:click="signUp"
            type="button"
            v-if="this.$route.path != '/signUp'"
          >
            Sign Up
          </button>
        </span>
      </div>
    </nav>
  </div>
</template>

<script>
export default {
  name: "navbar",
  data() {
    return {
      userRole: null,
    };
  },

  props: ["query", "onLogOut", "forceSearchUpdate"],
  // onLogOut: callback that should be passed when the user clicks log out button on NavBar
  // forceSearchUpdate: callback that is called if user clicks search when the value of the search field
  // is the same as what is currently in the URL (Vue router will block this if we try to push)

  mounted() {
    window.addEventListener('user-role-changed', (event) => {
      this.userRole = event.detail.storage;
    });
  },
  methods: {
    checkLogin() {
      // returns true if value (user id) is assigned to login in localStorage
      return Boolean(localStorage.getItem("userId"));
    },
    home() {
      if (this.$route.path != "/") this.$router.push("/");
    },
    profile() {
      if (this.$route.name != "profile") this.$router.push({ name: "profile" });
    },
    login() {
      if (this.$route.name != "login") this.$router.push({ name: "login" });
    },
    logOut() {
      this.onLogOut();
    },
    search() {
      const searchName = "search";
      let newQuery = this.$route.params.query;

      if (this.$route.name == searchName && newQuery == this.query) {
        this.forceSearchUpdate();

        // Reloads the search component by updating the key, but doesn't add it to history
        return;
      }
      this.$router.push({
        name: searchName,
        params: {
          query: this.query,
        },
      });
    },
    signUp() {
      if (this.$route.path != "/signUp") this.$router.push("/signUp");
    },
  },
};
</script>