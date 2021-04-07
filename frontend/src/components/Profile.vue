<template>
  <div class="container">
    <div class="row mt-2">
      <!--User profile image card-->
      <div class="col-md-4 m-2 card">
        <!--Upload image button overlay-->
        <div
            style="position: absolute; bottom: 10px; right: 20px; border-radius: 50%; background: lightgray; width: 70px; height: 70px; padding: 10px">
          <font-awesome-icon style="height: 100%; width: 100%;" icon="images"/>
        </div>
        <!--User profile image-->
        <img class="my-3 rounded-circle" src="placeholder-profile.png" alt="Users profile image">

      </div>
      <div class="col-md-7 m-2 card">
        <div class="m-3">
          <div class="d-flex align-items-center">
            <!--Users name-->
            <h2 style="float: left" class="mb-0">{{ userInfo.firstName }} {{ userInfo.middleName }}
              {{ userInfo.lastName }}</h2>
            <!--Location data-->
            <div v-if="userInfo.homeAddress.city || userInfo.homeAddress.country"
                 class="d-flex align-items-center">
              <font-awesome-icon class="m-2" icon="map-marker-alt"/>
              <span class="text-muted">{{
                  [userInfo.homeAddress.city, userInfo.homeAddress.country].filter(Boolean).join(
                      ', ')
                }}</span>
            </div>

          </div>

          <span class="text-muted">{{
              userInfo.role && userInfo.role === 'ROLE_ADMIN' ? 'Admin' : ''
            }}</span>
          <p>{{ userInfo.bio }}</p>

          <button class="btn btn-white-bg-primary mt-3">Send Message</button>
          <br>
          <div class="profile-buttons pt-3">

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

        </div>
      </div>
    </div>
    <div class="row">
      <div class="col-md-4 order-2 order-md-1 m-2 card">
        <h5 class="text-muted mt-3">Interested in</h5>
        <div class="card my-2">
          <div class="card-body">
            <h5 class="card-title">Card title</h5>
            <h6 class="card-subtitle mb-2 text-muted">Card subtitle</h6>
            <p class="card-text">Some quick example text to build on the card title and make up the
              bulk of the card's content.</p>
            <a href="#" class="card-link">Card link</a>
            <a href="#" class="card-link">Another link</a>
          </div>
        </div>
        <div class="card my-2">
          <div class="card-body">
            <h5 class="card-title">Card title</h5>
            <h6 class="card-subtitle mb-2 text-muted">Card subtitle</h6>
            <p class="card-text">Some quick example text to build on the card title and make up the
              bulk of the card's content.</p>
            <a href="#" class="card-link">Card link</a>
            <a href="#" class="card-link">Another link</a>
          </div>
        </div>

      </div>
      <div class="col-md-7 order-1 order-md-2 m-2 card">
        <ul class="nav nav-tabs mt-2">
          <li class="nav-item">
            <a class="nav-link active" aria-current="page" href="#">Details</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#">Previously Bought</a>
          </li>
        </ul>
        <div class="m-4">
          <h5 class="text-muted">User Details</h5>
          <li class="row">
            <dt class="label">Nickname:</dt>
            <dd class="col-md value"><p>{{ userInfo.nickname }}</p></dd>
          </li>
          <li class="row">
            <dt class="label">Member since:</dt>
            <dd class="col-md value"><p>{{ memberSinceText }}</p></dd>
          </li>
          <li class="row">
            <dt class="label">Date of Birth:</dt>
            <dd class="col-md value"><p>{{ dateOfBirthText }}</p></dd>
          </li>
          <h5 class="text-muted">Contact Information</h5>
          <li class="row">
            <dt class="label">Email Address:</dt>
            <dd class="col-md value"><p>{{ userInfo.email }}</p></dd>
          </li>
          <li class="row">
            <dt class="label">Phone Number:</dt>
            <dd class="col-md value"><p>{{ userInfo.phoneNumber }}</p></dd>
          </li>
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
      userInfo: {
        firstName: "",
        lastName: "",
        nickname: "",
        created: "",
        bio: "",
        dateOfBirth: "",
        emailAddress: "",
        phoneNumber: "",
        homeAddress: "",
        role: "ROLE_USER"
      },
      errorMessage: "",
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
      let user = JSON.parse(localStorage.authUser);
      if (user.role == "ROLE_ADMIN") {
        return true;
      } else {
        return false;
      }
    },

    /**
     * Calls to the API to from the profile view with a given user ID to make requested user an Administrator
     * Returns a message to user to indicate whether or not the user has been updated to the Administrator role.
     */
    makeAdmin: async function (userId) {
      try {
        await Api.makeAdmin(userId);
        this.userInfo.role = "ROLE_ADMIN";
        this.errorMessage = `Successfully made ${this.userInfo.firstName} ${this.userInfo.lastName} an admininstrator`;
        return;
      } catch (err) {
        this.errorMessage = err.userFacingErrorMessage;
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
        let user = JSON.parse(localStorage.authUser);
        if (userId == user.id) {
          user.role = "ROLE_USER";
          localStorage.setItem('authUser', JSON.stringify(user));
          this.errorMessage = `Successfully revoked ${this.userInfo.firstName} ${this.userInfo.lastName}'s administrator privileges`;
        } else {
          this.userInfo.role = "ROLE_USER";
          this.errorMessage = `Successfully revoked ${this.userInfo.firstName} ${this.userInfo.lastName}'s administrator privileges`;
        }
        return;
      } catch (err) {
        this.errorMessage = err.userFacingErrorMessage;
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
