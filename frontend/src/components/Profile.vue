<template>
  <div class="profile-card card container">
    <div>
      <h1 class="title">Profile Information</h1>
      <img v-if="userInfo.role && userInfo.role == 'ROLE_ADMIN'" class="admin-icon" src="id-card.svg" alt="Admin role icon">
      <ul class="profile-info list-unstyled">
        <li class="row">
          <dt class="col-md label">Full Name:</dt>
          <dd class="col-md value"> {{ userInfo.firstName }} {{userInfo.middleName}} {{ userInfo.lastName }} </dd>
        </li>
        <li class="row">
          <dt class="col-md label">Nickname:</dt>
          <dd class="col-md value"> {{ userInfo.nickname }} </dd>
        </li>
        <li class="row">
          <dt class="col-md label">Role:</dt>
          <dd class="col-md value"> {{ userInfo.role && userInfo.role == 'ROLE_ADMIN' ? 'Admin' : 'User' }}</dd>
        </li>
        <li class="row">
          <dt class="col-md label">Member since:</dt>
          <dd class="col-md value"> {{ memberSinceText }}</dd>
        </li>
        <li class="row">
          <dt class="col-md label">Bio:</dt>
          <dd class="col-md value"> {{ userInfo.bio }} </dd>
        </li>
        <li class="row">
          <dt class="col-md label">Date of Birth:</dt>
          <dd class="col-md value">{{ dateOfBirthText }}</dd>
        </li>
        <li class="row">
          <dt class="col-md label">Email Address:</dt>
          <dd class="col-md value"> {{ userInfo.email }} </dd>
        </li>
        <li class="row">
          <dt class="col-md label">Phone Number:</dt>
          <dd class="col-md value"> {{ userInfo.phoneNumber }} </dd>
        </li>
        <li class="row">
          <dt class="col-md label">Home Address:</dt>
          <dd class="col-md value"> {{ userInfo.homeAddress}} </dd>
        </li>
        <li>
        <button
          v-if="checkAdmin()"
          class="btn btn-white-bg-primary"
          id="makeAdmin"
          type="button"
          v-on:click="makeAdmin(userId)"
        >
        Make Admin
        </button>
        </li>
        <div class="row mt-2" v-if="errorMessage.length > 0">
          <div class="col">
            <p class="alert alert-warning">{{ errorMessage }}</p>
          </div>
        </div>
      </ul>
    </div>
  </div>
</template>


<script>
const Api = require("./../Api").default;

const MONTH_NAMES = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
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
      },
      errorMessage: "",
      isAdmin: false
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
    checkAdmin: function() {
      let user = JSON.parse(localStorage.authUser)
      if (user.role == "ROLE_ADMIN") {
        return true;
      } else {
        return false;
      }

    },

    makeAdmin: async function(userId) {
      try {
        await Api.makeAdmin(userId);
        console.log("happy days");
        this.errorMessage = "Success";
      } catch (err) {
        this.errorMessage = err.userFacingErrorMessage;
        return;
      }
      this.errorMessage = "";
    },

    /**
     * Calls the API to get profile information with the given user ID
     * Returns the promise, not the response
     */
    callApi: function(userId) {
      if (typeof userId != "number" || isNaN(userId)) {
        const err = new Error("Cannot load profile page (no profile given). You may need to log in");
        err.userFacingErrorMessage = err.message;
        return Promise.reject(err);
      }
      return Api.profile(userId);
    },

    /**
     * Parses the API response given a promise to the request
     */
    parseApiResponse: async function(apiCall) {
      try {
        const response = await apiCall;
        this.userInfo = response.data;
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
.main {
  line-height: 1.1;
}

.profile-info li {
  margin-bottom: 0.5em;
}

.profile-info {
  font-size: 1.1em;
}

.profile-card {
  margin: 30px;
  border: none;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  padding: 30px;
}

</style>
