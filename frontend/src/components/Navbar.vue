<template>
  <div id="navbar">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <router-link to="/" exact>
        <a class="navbar-brand">App Logo</a>
      </router-link>

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
          <router-link v-if="checkLogin()" active-class="active" to="/home" exact>
            <li class="nav-item">
              <a class="nav-link">
                Home
                <span class="sr-only">(current)</span>
              </a>
            </li>
          </router-link>
          <router-link v-if="checkLogin()" active-class="active" to="/profile" exact>
            <li class="nav-item">
              <a class="nav-link">
                Profile
                <span class="sr-only">(current)</span>
              </a>
            </li>
          </router-link>
        </ul>
        <!--if logged in shows this section-->
        <div v-if="checkLogin()" class="d-flex">
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
          <div class="collapse navbar-collapse" id="navbar-list-4">
            <ul class="navbar-nav">
              <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="navbarDropdownMenuLink" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  <img class="nav-picture rounded-circle" src="placeholder-profile.png">
                  <div class="d-flex flex-column mx-1">
                    <span class="m-0 p-0 text-dark">{{getAuthUser().firstName}} {{getAuthUser().lastName}}</span>
                    <span v-if="isAdmin()" class="admin-text p-0 text-faded">ADMIN</span>
                  </div>
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">
                  <button class="dropdown-item" v-on:click="logOut">Log out</button>
                </div>
              </li>
            </ul>
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
<style scoped>
.admin-text {
  margin: 0;
  margin-top: -0.5em;
  font-size: 0.8em;
}
</style>

<script>
export default {
  name: "navbar",
  data() {
    return {
      userRole: null,
      firstName: null,
      lastName: null
    };
  },

  props: ["query", "onLogOut", "forceSearchUpdate"],
  // onLogOut: callback that should be passed when the user clicks log out button on NavBar
  // forceSearchUpdate: callback that is called if user clicks search when the value of the search field
  // is the same as what is currently in the URL (Vue router will block this if we try to push)

  methods: {
    checkLogin() {
      // returns true if value (user id) is assigned to login in localStorage
      return Boolean(localStorage.getItem("userId"));
    },
    getAuthUser() {
      return JSON.parse(localStorage.getItem("authUser"));
    },
    isAdmin() {
      this.authUser = JSON.parse(localStorage.getItem("authUser"));
      if (this.authUser.role === "ROLE_ADMIN"){
        return true;
      } else {
        return false;
      }
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