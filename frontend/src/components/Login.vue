<template>
  <div class="w-100 d-flex justify-content-center login-container gradient-background pb-4">
    <div class="container">
      <form v-on:submit.prevent="login" class="slightly-transparent-inputs">
        <div class="row">
          <div class="col">
            <h1>Login</h1>
          </div>
        </div>

        <div class="row">
          <div class="col-12 form-group required">
            <label for="email">Email</label>
            <input
              class="form-control"
              type="email"
              name="email"
              id="email"
              placeholder="Email"
              maxlength="50"
              v-model="email"
              autocomplete="email"
              required
            />
          </div>

          <div class="col-12 form-group required">
            <label for="password">Password</label>
            <input
              class="form-control"
              type="password"
              name="password"
              id="password"
              placeholder="Password"
              maxlength="50"
              v-model="password"
              autocomplete="current-password"
              required
            />
          </div>
        </div>

        <div class="row">
          <div class="col">
            <div class="d-flex justify-content-between">
              <input class="btn btn-primary" id="loginSubmit" type="submit" value="Login"/>
              <button
                class="btn btn-white-bg-primary"
                id="signUp"
                type="button"
                v-on:click="signUp()"
              >
                Sign Up
              </button>
            </div>
          </div>
        </div>

        <div class="row mt-2" v-if="errorMessage.length > 0">
          <div class="col">
            <p class="alert alert-warning">{{ errorMessage }}</p>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-container > div {
  max-width: 40em;
}
</style>

<script>
// grabs mock api
const Api = require("./../Api").default;

export default {
  /* creates vue variables to be manipulates
     initilizes object with methods and data
  */
  name: "loginPage",
  components: {},

  data() {
    return {
      responseSuccess: false,
      errorMessage: "",
      email: "",
      password: ""
    };
  },

  methods: {
    signUp() {
      // redirects user to to the signup page
      this.$router.push({ name: "signUp" });
    },
    login: async function() {
      // uses mock API to determine whether to allow user to login or be rejected
      let response;

      try {
        // Attempt to login and get the userId
        response = await Api.login({ email: this.email, password: this.password });
        const userId = response.data.userId;
        // Get and set the current logged in user information
        response = await Api.profile(userId);
        const authUser = response.data;
        this.$stateStore.actions.setAuthUser(authUser);
      } catch(err) {
        this.errorMessage = err.userFacingErrorMessage;
        return;
      }

      this.errorMessage = "";
      await this.$router.push({ name: "profile" });
    },
  },
};
</script>
