<template>
  <div class="container">
    <div class="row mt-2">
      <!--User profile image card-->
      <div class="col-md-4 m-2 card">
        <!--Upload image button overlay-->
        <button type="button" class="image-upload-button btn btn-lg btn-primary" disabled>
          <span class="material-icons md-48">file_upload</span>
        </button>
        <!--User profile image-->
        <img class="my-3 rounded-circle" src="placeholder-profile.png" alt="Users profile image">

      </div>
      <div class="col-md-7 m-2 card">
        <div class="m-3">
          <div class="d-flex align-items-center">
            <!--Users name-->
            <h2 class="mb-0 float-left">{{ userInfo.firstName }} {{ userInfo.middleName }}
              {{ userInfo.lastName }}</h2>
            <!--Location data-->
            <div
                v-if="userInfo.homeAddress"
                class="d-flex align-items-center">
              <span class="material-icons md-dark md-inactive ml-2">location_on</span>
              <span class="text-muted">
                {{ userInfo.homeAddress }}
              </span>
            </div>
<!--            <div-->
<!--                v-if="userInfo.homeAddress.city || userInfo.homeAddress.country"-->
<!--                class="d-flex align-items-center">-->
<!--              <span class="material-icons md-dark md-inactive ml-2">location_on</span>-->
<!--              <span class="text-muted">-->
<!--                {{-->
<!--                  [userInfo.homeAddress.city, userInfo.homeAddress.country].filter(Boolean).join(-->
<!--                      ', ')-->
<!--                }}-->
<!--              </span>-->
<!--            </div>-->

          </div>

          <span class="text-muted">{{
              userInfo.role && userInfo.role === 'ROLE_ADMIN' ? 'Admin' : ''
            }}</span>
          <p>{{ userInfo.bio }}</p>

          <br>
          <div class="profile-buttons d-flex">
            <button class="btn btn-white-bg-primary mx-1 d-flex" disabled><span class="material-icons mr-1">send</span>Send Message</button>
            <button
                v-if="checkAdmin() &&  userInfo.role != 'ROLE_ADMIN'"
                class="btn btn-white-bg-primary mx-1 d-flex"
                id="makeAdmin"
                type="button"
                v-on:click="makeAdmin(userId)"
            >
              <span class="material-icons mr-1">person</span>
              Make Admin
            </button>
            <button
                v-if="checkAdmin() && userInfo.role == 'ROLE_ADMIN'"
                class="btn btn-white-bg-primary mx-1 d-flex"
                id="revokeAdmin"
                type="button"
                v-on:click="revokeAdmin(userId)"
            >
              <span class="material-icons mr-1">person</span>
              Revoke Admin
            </button>
          </div>
          <div class="row mt-2" v-if="statusMessage.length > 0">
            <div class="col">
              <p class="alert alert-warning">{{ statusMessage }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col-md-4 order-2 order-md-1 m-2 card">
        <h5 class="text-muted mt-3">Businesses</h5>
        <div class="card my-2">
          <div class="card-body">
            <h5 class="card-title">Card title</h5>
            <h6 class="card-subtitle mb-2 text-muted">Card subtitle</h6>
            <p class="card-text">Some quick example text to build on the card title and make up the
              bulk of the card's content.</p>
            <a href="/profile" class="card-link">Card link</a>
            <a href="/profile" class="card-link">Another link</a>
          </div>
        </div>
        <div class="card my-2">
          <div class="card-body">
            <h5 class="card-title">Card title</h5>
            <h6 class="card-subtitle mb-2 text-muted">Card subtitle</h6>
            <p class="card-text">Some quick example text to build on the card title and make up the
              bulk of the card's content.</p>
            <a href="/profile" class="card-link">Card link</a>
            <a href="/profile" class="card-link">Another link</a>
          </div>
        </div>

      </div>
      <div class="col-md-7 order-1 order-md-2 m-2 card">
        <ul class="nav nav-tabs mt-2">
          <li class="nav-item">
            <a class="nav-link active" aria-current="page">Details</a>
          </li>
          <li class="nav-item">
            <a class="nav-link disabled">Future Tab</a>
          </li>
        </ul>
        <div class="m-4">
          <table class="table table-hover">
            <tbody>
            <tr>
              <td colspan="2"><h5 class="text-muted">User Details</h5></td>
            </tr>
            <tr v-if="userInfo.nickname" scope="row">
              <th style="white-space: nowrap;">Nickname:</th>
              <td class="col-md value"><p>{{ userInfo.nickname }}</p></td>
            </tr>
            <tr v-if="memberSinceText" scope="row">
              <th style="white-space: nowrap;">Member since:</th>
              <td class="col-md value"><p>{{ memberSinceText }}</p></td>
            </tr>
            <tr v-if="dateOfBirthText" scope="row">
              <th style="white-space: nowrap;">Date of Birth:</th>
              <td class="col-md value"><p>{{ dateOfBirthText }}</p></td>
            </tr>
            <tr>
              <td colspan="2"><h5 class="text-muted">Contact Information</h5></td>
            </tr>
            <tr v-if="userInfo.email" scope="row">
              <th style="white-space: nowrap;">Email Address:</th>
              <td class="col-md value"><p>{{ userInfo.email }}</p></td>
            </tr>
            <tr v-if="userInfo.phoneNumber" scope="row">
              <th style="white-space: nowrap;">Phone Number:</th>
              <td class="col-md value"><p>{{ userInfo.phoneNumber }}</p></td>
            </tr>
            <tr v-if="true" scope="row">
              <th style="white-space: nowrap;">Address:</th>
              <td class="col-md value">
                <p>
                  {{userInfo.homeAddress}}
                </p>
<!--                <p>{{-->
<!--                    [userInfo.homeAddress.streetNumber + ' ' + userInfo.homeAddress.streetName ,-->
<!--                      userInfo.homeAddress.city,-->
<!--                      userInfo.homeAddress.region,-->
<!--                      userInfo.homeAddress.postcode,-->
<!--                      userInfo.homeAddress.country-->
<!--                    ].filter(Boolean).join(', ')-->
<!--                  }}-->
<!--                </p>-->
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
const Api = require("./../Api").default;

const MONTH_NAMES = ["January", "February", "March", "April", "May", "June", "July", "August",
  "September", "October", "November", "December"];
Object.freeze(MONTH_NAMES);

export default {
  name: 'profilePage',
  components: {},

  data() {
    return {
      userInfo: {},
      statusMessage: "",
    }
  },
  props: {
    userId: {
      type: Number, // may be NaN
      required: true
      // default: 10
    }
  },

  beforeMount: function () {
    // gets user information from api
    this.parseApiResponse(this.callApi(this.userId));
  },

  methods: {
    // TODO: make admin buttons auto update without refresh

    /**
     * Checks to see if logged in user is an admin using cookie storing session.
     */
    checkAdmin: function () {
      return (this.$stateStore.getters.getAuthUser().role === "ROLE_ADMIN");
    },

    /**
     * Calls to the API to from the profile view with a given user ID to make requested user an Administrator
     * Returns a message to user to indicate whether or not the user has been updated to the Administrator role.
     */
    makeAdmin: async function (userId) {
      try {
        await Api.makeAdmin(userId);
        if (userId === this.$stateStore.getters.getAuthUser().id) {
          this.$stateStore.actions.makeAdmin();
        }
        this.userInfo.role = "ROLE_ADMIN";
        this.statusMessage = `Successfully made ${this.userInfo.firstName} ${this.userInfo.lastName} an admininstrator`;
        return;
      } catch (err) {
        this.statusMessage = err.userFacingErrorMessage;
        return;
      }
    },

    /**
     * Calls to the API to from the profile view with a given user ID to revoke requested user from an Administrator
     * Returns a message to user to indicate whether selected user has been premoted to Admin or request failed.
     */
    revokeAdmin: async function (userId) {
      try {
        await Api.revokeAdmin(userId);
        this.userInfo.role = "ROLE_USER";
        if (userId.toString() === this.$stateStore.getters.getAuthUser().id) {
          this.$stateStore.actions.revokeAdmin();
          this.statusMessage = `Successfully revoked ${this.userInfo.firstName} ${this.userInfo.lastName}'s administrator privileges`;
        } else {
          this.statusMessage = `Successfully revoked ${this.userInfo.firstName} ${this.userInfo.lastName}'s administrator privileges`;
        }
        return;
      } catch (err) {
        this.statusMessage = err.userFacingErrorMessage;
        return;
      }
    },

    /**
     * Calls the API to get profile information with the given user ID
     * Returns the promise, not the response
     */
    callApi: function (userId) {
      if (typeof userId != "number" || isNaN(userId)) {
        const err = new Error(
            "Cannot load profile page (no profile given). You may need to log in");
        err.userFacingErrorMessage = err.message;
        return Promise.reject(err);
      }
      return Api.profile(userId);
    },

    /**
     * Parses the API response given a promise to the request
     */
    parseApiResponse: async function (apiCall) {
      try {
        const response = await apiCall;
        this.userInfo = response.data;
      } catch (err) {
        alert(
            err.userFacingErrorMessage == undefined ? err.toString() : err.userFacingErrorMessage);
      }
    },

    /**
     * Formats the date as a D MMMM YYYY string
     * @param date date object, or something that can be passed to the constructor
     */
    formatDate: function (date) {
      if (!(date instanceof Date)) {
        date = new Date(date);
      }
      return `${date.getDate()} ${MONTH_NAMES[date.getMonth()]}, ${date.getFullYear()}`;
    },

    /**
     * Calculates the time since registration and returns it as a string
     * @return string in format 'y years, m months'
     */
    generateTimeSinceRegistrationText: function (registrationDate, currentDate) {
      const yearDiff = currentDate.getFullYear() - registrationDate.getFullYear();
      const monthDiff = currentDate.getMonth() - registrationDate.getMonth();

      const timeDiffInMonth = yearDiff * 12 + monthDiff;

      const years = Math.floor(timeDiffInMonth / 12);
      let months = timeDiffInMonth % 12;

      const yearsText = `${years} year${years == 1 ? "" : "s"}`;

      if (timeDiffInMonth == 0) {
        months = 1;
      }
      // If it was created this month, don't want to show '0 months' but '1' month instead
      const monthsText = `${months} month${months == 1 ? "" : "s"}`;

      if (years == 0) {
        return monthsText;
      }

      return `${yearsText}, ${monthsText}`;
    },
  },

  computed: {
    authUser() {
      return this.$stateStore.getters.getAuthUser();
    },
    dateOfBirthText: function () {
      if (isNaN(Date.parse(this.userInfo.dateOfBirth))) {
        return "Unknown";
      }
      return this.formatDate(this.userInfo.dateOfBirth);
    },

    memberSinceText: function () {
      if (isNaN(Date.parse(this.userInfo.created))) {
        return "Unknown";
      }
      const created = new Date(this.userInfo.created);
      const dateOfRegistration = this.formatDate(created);
      const monthsSinceRegistration = this.generateTimeSinceRegistrationText(
          created,
          new Date()
      );

      return `${dateOfRegistration} (${monthsSinceRegistration})`;
    }
  },

  watch: {
    // if userid changes updates text fields
    userId: function () {
      this.parseApiResponse(this.callApi(this.userId))
    }
  },
}
</script>
