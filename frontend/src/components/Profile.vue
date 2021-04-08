<template>
  <div class="profile-card card container">
    <div>
      <h1 class="title">Profile Information</h1>
        <div class="profile-content p-3">
        <ul class="profile-info list-unstyled">
          <li class="row">
            <dt class="col-md label"><h3>Full Name:</h3></dt>
            <dd class="col-md value"><p>{{ userInfo.firstName }} {{userInfo.middleName}} {{ userInfo.lastName }}</p></dd>
          </li>
          <li class="row">
            <dt class="col-md label"><h3>Nickname:</h3></dt>
            <dd class="col-md value"><p>{{ userInfo.nickname }}</p></dd>
          </li>
          <li class="row">
            <dt class="col-md label"><h3>Role:</h3></dt>
            <dd class="col-md value"><p>{{ userInfo.role && userInfo.role === 'ROLE_ADMIN' ? 'Admin' : 'User' }}</p></dd>
          </li>
          <li class="row">
            <dt class="col-md label"><h3>Member since:</h3></dt>
            <dd class="col-md value"><p>{{ memberSinceText }}</p></dd>
          </li>
          <li class="row">
            <dt class="col-md label"><h3>Bio:</h3></dt>
            <dd class="col-md value"><p>{{ userInfo.bio }}</p></dd>
          </li>
          <li class="row">
            <dt class="col-md label"><h3>Date of Birth:</h3></dt>
            <dd class="col-md value"><p>{{ dateOfBirthText }}</p></dd>
          </li>
          <li class="row">
            <dt class="col-md label"><h3>Email Address:</h3></dt>
            <dd class="col-md value"><p>{{ userInfo.email }}</p></dd>
          </li>
          <li class="row">
            <dt class="col-md label"><h3>Phone Number:</h3></dt>
            <dd class="col-md value"><p>{{ userInfo.phoneNumber }}</p></dd>
          </li>
          <li class="row">
            <dt class="col-md label"><h3>Home Address:</h3></dt>
            <dd class="col-md value"><p>{{ userInfo.homeAddress}}</p></dd>
          </li>
        </ul>
        </div>
        <div class="profile-buttons p-3">
          <button
            v-if="checkAdmin() &&  userInfo.role != 'ROLE_ADMIN'"
            class="btn btn-white-bg-primary"
            id="makeAdmin"
            type="button"
            v-on:click="makeAdmin(userId)"
          >
            Make Admin
          </button>
          <button
            v-if="checkAdmin() && userInfo.role == 'ROLE_ADMIN'"
            class="btn btn-white-bg-primary"
            id="revokeAdmin"
            type="button"
            v-on:click="revokeAdmin(userId)"
          >
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
</template>


<script>
import { ApiRequestError } from '../ApiRequestError';
const Api = require("./../Api").default;

const MONTH_NAMES = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
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

  beforeMount: function() {
    // gets user information from api
    this.parseApiResponse(this.callApi(this.userId));
  },

  methods: {

    /**
     * Checks to see if logged in user is an admin using cookie storing session.
     */
    checkAdmin: function() {
      return (this.$store.state.authUser.role === "ROLE_ADMIN");
    },

    /**
     * Calls to the API to from the profile view with a given user ID to make requested user an Administrator
     * Returns a message to user to indicate whether or not the user has been updated to the Administrator role.
     */
    makeAdmin: async function(userId) {
      try {
        await Api.makeAdmin(userId);
        await this.$store.dispatch('makeAdmin');
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
    revokeAdmin: async function(userId) {
      try {
        await Api.revokeAdmin(userId);
        // Check if user to revoke is the logged in user
        if (userId.toString() === this.$store.getters.getAuthUser.id) {
          await this.$store.dispatch('revokeAdmin');
        }
        this.userInfo.role = "ROLE_USER";
        this.statusMessage = `Successfully revoked admin privileges of ${this.userInfo.firstName} ${this.userInfo.lastName} `;
      } catch (err) {
        this.statusMessage = err.userFacingErrorMessage;
      }
    },

    /**
     * Calls the API to get profile information with the given user ID
     * Returns the promise, not the response
     */
    callApi: function(userId) {
      if (typeof userId != "number" || isNaN(userId)) {
        return Promise.reject(new ApiRequestError("Cannot load profile page (no profile given). You may need to log in"));
      }
      return Api.profile(userId);
    },

    /**
     * Parses the API response given a promise to the request
     * @param {Promise<User, ApiRequestError>} apiCall
     */
    parseApiResponse: async function(apiCall) {
      try {
        const response = await apiCall;
        this.userInfo = response.data;
        this.statusMessage = "";
      } catch(err) {
        alert(err.userFacingErrorMessage == undefined? err.toString(): err.userFacingErrorMessage);
      }
    },

    /**
     * Formats the date as a D MMMM YYYY string
     * @param date date object, or something that can be passed to the constructor
     */
    formatDate: function(date) {
      if (!(date instanceof Date)) date = new Date(date);
      return `${date.getDate()} ${MONTH_NAMES[date.getMonth()]}, ${date.getFullYear()}`;
    },

    /**
     * Calculates the time since registration and returns it as a string
     * @return string in format 'y years, m months'
     */
    generateTimeSinceRegistrationText: function(registrationDate, currentDate) {
      const yearDiff = currentDate.getFullYear() - registrationDate.getFullYear();
      const monthDiff = currentDate.getMonth() - registrationDate.getMonth();

      const timeDiffInMonth = yearDiff * 12 + monthDiff;

      const years = Math.floor(timeDiffInMonth / 12);
      let months = timeDiffInMonth % 12;

      const yearsText  = `${years} year${years == 1? "": "s"}`;

      if (timeDiffInMonth == 0) months = 1;
      // If it was created this month, don't want to show '0 months' but '1' month instead
      const monthsText = `${months} month${months == 1? "": "s"}`;

      if (years == 0) {
        return monthsText;
      }

      return`${yearsText}, ${monthsText}`;
    },
  },

  computed: {
    dateOfBirthText: function() {
      if (isNaN(Date.parse(this.userInfo.dateOfBirth))) return "Unknown";
      return this.formatDate(this.userInfo.dateOfBirth);
    },


    memberSinceText: function() {
      if (isNaN(Date.parse(this.userInfo.created))) return "Unknown";
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
    userId: function() {
      this.parseApiResponse(this.callApi(this.userId))
    }
  },
}
</script>

<style scoped>

dt {
  text-align: right;
}

.profile-card {
  margin: 30px;
  border-radius: 15px;
  display: flex;
  justify-content: center;
  padding: 30px;
}

.profile-content {
  border-top: 1px solid #eaeee7;
  border-bottom: 1px solid #eaeee7;
}

</style>
