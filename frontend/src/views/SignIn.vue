<template>
  <div class="w-100 d-flex justify-content-center sign-in-container gradient-background pb-4">
    <div class="container">
      <form class="slightly-transparent-inputs" v-on:submit.prevent="signIn">
        <div class="row">
          <div class="col">
            <h1>Sign In</h1>
          </div>
        </div>

        <div class="row">
          <div class="col-12 form-group required">
            <label for="email">Email</label>
            <input
                id="email"
                v-model="email"
                autocomplete="email"
                class="form-control"
                maxlength="50"
                name="email"
                placeholder="Email"
                required
                type="email"
            />
          </div>

          <div class="col-12 form-group required">
            <label for="password">Password</label>
            <input
                id="password"
                v-model="password"
                autocomplete="current-password"
                class="form-control"
                maxlength="50"
                name="password"
                placeholder="Password"
                required
                type="password"
            />
          </div>
        </div>

        <div class="row">
          <div class="col">
            <div class="d-flex justify-content-between">
              <input id="submit" class="btn btn-primary" type="submit" value="Sign In"/>
              <router-link
                  class="btn btn-white-bg-primary hover-cursor-pointer"
                  type="button"
                  v-bind:to="{ name: 'SignUp' }"
              >
                Sign Up
              </router-link>
            </div>
          </div>
        </div>

        <div v-if="errorMessage.length > 0" class="row mt-2">
          <div class="col">
            <p class="alert alert-warning">{{ errorMessage }}</p>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.sign-in-container > div {
  max-width: 40em;
}
</style>

<script>
// grabs mock api
const {Api} = require("../Api.js");

export default {
  /* creates vue variables to be manipulates
     initilizes object with methods and data
  */
  name: "Sign In",
  components: {},

  data() {
    return {
      errorMessage: "",
      email: "",
      password: ""
    };
  },

  methods: {
    signIn: async function () {
      // uses mock API to determine whether to allow user to login or be rejected
      let response;

      try {
        // Attempt to login and get the userId
        response = await Api.login({email: this.email, password: this.password});
        const userId = response.data.userId;
        // Get and set the current signed in user information

        response = await Api.profile(userId);
        const authUser = response.data;
        // Adding the image URL (the GET request to backend) to the User object
        // authUser.imageURL = process.env.VUE_APP_SERVER_ADD + `/users/${userId}/images/`;
        // authUser.imageURL = process.env.VUE_APP_SERVER_ADD
        //     + `/user-content/images/user/default-user-thumbnail.svg`;
        this.$stateStore.actions.setAuthUser(authUser);
      } catch (err) {
        this.errorMessage = err.userFacingErrorMessage;
        return;
      }

      this.errorMessage = "";
      await this.$router.push({ name: "Home" });
    },
  },
};
</script>
