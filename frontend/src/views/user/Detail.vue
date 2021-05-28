<template>
  <div class="container">
    <div class="row mt-2">
      <!--User profile image card-->
      <div class="col-md-4 m-2 card">

        <img alt="Users profile image" class="my-3 rounded-circle"
             src="@/../assets/images/default-user-thumbnail.svg">
      </div>
      <div class="col-md-7 m-2 card">
        <div class="m-3">
          <div class="d-flex align-items-center">
            <!--Users name-->
            <h2 class="mb-0 float-left">{{ userInfo.firstName }} {{ userInfo.middleName }}
              {{ userInfo.lastName }}</h2>
            <!--Location data-->
            <div
                v-if="userInfo.homeAddress && (userInfo.homeAddress.suburb || userInfo.homeAddress.city)"
                class="d-flex align-items-center">
              <span class="material-icons md-dark md-inactive ml-2">location_on</span>
              <span class="text-muted">
                {{
                  [userInfo.homeAddress.suburb, userInfo.homeAddress.city].filter(Boolean).join(
                      ', ')
                }}
              </span>
            </div>

          </div>

          <span class="text-muted">{{
              userInfo.role && userInfo.role === 'ROLE_ADMIN' ? 'Admin' : ''
            }}</span>
          <p>{{ userInfo.bio }}</p>

          <br>
          <div class="profile-buttons d-flex flex-wrap justify-content-center">
            <button class="btn btn-white-bg-primary m-1 d-flex" disabled><span
                class="material-icons mr-1">send</span>Send Message
            </button>
            <button
                v-if="isAdmin && userInfo.role != 'ROLE_ADMIN'"
                id="makeAdmin"
                class="btn btn-white-bg-primary m-1 d-flex"
                type="button"
                v-on:click="makeAdmin(userId)"
            >
              <span class="material-icons mr-1">person</span>
              Make Admin
            </button>
            <button
                v-if="isAdmin && userInfo.role == 'ROLE_ADMIN'"
                id="revokeAdmin"
                class="btn btn-white-bg-primary m-1 d-flex"
                type="button"
                v-on:click="revokeAdmin(userId)"
            >
              <span class="material-icons mr-1">person</span>
              Revoke Admin
            </button>
            <router-link
              v-if="isAdmin"
              v-bind:to="{ name: 'MarketplaceCardCreateAdmin', params: { userId: userInfo.id }}"
              class="btn btn-white-bg-primary m-1 d-flex"
            >
              Create Marketplace Card
            </router-link>
            <router-link
                v-if="isSignedIn && authUser.id === userInfo.id"
                class="btn btn-white-bg-primary m-1 d-flex"
                type="button"
                v-bind:to="{ name: 'BusinessCreate' }"
            >
              <span class="material-icons mr-1">business</span>
              Register Business
            </router-link>
          </div>
          <div v-if="statusMessage.length > 0" class="row mt-2">
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
        <div
            v-if="Array.isArray(userInfo.businessesAdministered) && userInfo.businessesAdministered.length !== 0">
          <ul class="profile-business-info list-unstyled">
            <li
                v-for="(business, index) in userInfo.businessesAdministered"
                v-bind:key="index"
                class="list-group-item card text-wrap mb-2 border"
            >
              <router-link
                v-bind:to="{ name: 'BusinessDetail', params: { businessId: business.id, showBackButton: true}}"
                class="text-reset"
              >
                <h5 class="business-name card-title card-link" >{{ business.name }}</h5>
              </router-link>
              <h6 class="card-subtitle mb-2 text-muted">
                {{ business.businessType }}
              </h6>
              <p class="card-text">{{ business.description }}</p>
            </li>
          </ul>
        </div>
        <div v-else>
          No businesses
        </div>
      </div>
      <div class="col-md-7 order-1 order-md-2 m-2 card">
        <ul class="nav nav-tabs mt-2">
          <li class="nav-item">
            <a aria-current="page" class="nav-link active">Details</a>
          </li>
          <li class="nav-item">
            <a class="nav-link disabled">Future Tab</a>
          </li>
        </ul>
        <div class="m-md-4">
          <table class="table table-hover">
            <tbody>
            <tr>
              <td class="pl-0 pl-md-2" colspan="2"><h5 class="text-muted">User Details</h5></td>
            </tr>
            <tr v-if="userInfo.nickname" scope="row">
              <th class="pl-0 pl-md-2">Nickname:</th>
              <td class="pr-0 pr-md-2 col-md value"><p>{{ userInfo.nickname }}</p></td>
            </tr>
            <tr v-if="memberSinceText" scope="row">
              <th class="pl-0 pl-md-2">Member since:</th>
              <td class="pr-0 pr-md-2 col-md value"><p>{{ memberSinceText }}</p></td>
            </tr>
            <tr v-if="dateOfBirthText" scope="row">
              <th class="pl-0 pl-md-2">Date of Birth:</th>
              <td class="pr-0 pr-md-2 col-md value"><p>{{ dateOfBirthText }}</p></td>
            </tr>
            <tr>
            <td class="pl-0 pl-md-2" colspan="2"><h5 class="text-muted">Contact Information</h5></td>
            </tr>
            <tr v-if="userInfo.email" scope="row">
              <th class="pl-0 pl-md-2">Email Address:</th>
              <td class="pr-0 pr-md-2 col-md value"><p>{{ userInfo.email }}</p></td>
            </tr>
            <tr v-if="userInfo.phoneNumber" scope="row">
              <th class="pl-0 pl-md-2">Phone Number:</th>
              <td class="pr-0 pr-md-2 col-md value"><p>{{ userInfo.phoneNumber }}</p></td>
            </tr>
            <tr v-if="userInfo.homeAddress" scope="row">
              <th class="pl-0 pl-md-2">Address:</th>
              <td class="pr-0 pr-md-2 col-md value">
                <p>{{ $helper.addressToString(userInfo.homeAddress) }}</p>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <error-modal
        title="Error fetching user details"
        v-bind:goBack="false"
        v-bind:hideCallback="() => apiErrorMessage = null"
        v-bind:refresh="true"
        v-bind:retry="this.apiPipeline"
        v-bind:show="apiErrorMessage !== null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<style>
.business-name:hover {
  cursor: pointer;
  color: #1ec996;
}

th {
  white-space: nowrap;
}
</style>
<script>
import ErrorModal from '../../components/ErrorModal.vue';
import {ApiRequestError} from "@/ApiRequestError";

import { Api } from "@/Api";

export default {
  name: 'profile',
  components: {ErrorModal},

  data() {
    return {
      userInfo: {},
      statusMessage: "",
      apiErrorMessage: null,
      profileURL: process.env.VUE_APP_SERVER_ADD + `/users/${this.userId}/images/`
    }
  },

  props: {
    userId: {
      type: Number, // may be NaN
      required: true
      // default: 10
    }
  },

  /**
   * Gets user information from the API
   */
  beforeMount: async function() {
    await this.apiPipeline();
  },

  methods: {
    /**
     * Calls to the API to from the profile view with a given user ID to make requested user an Administrator
     * Returns a message to user to indicate whether or not the user has been updated to the Administrator role.
     */
    makeAdmin: async function (userId) {
      try {
        await Api.makeAdmin(userId);
        if (userId === this.authUser.id) {
          this.$stateStore.actions.makeAdmin();
        }
        this.userInfo.role = "ROLE_ADMIN";
        this.statusMessage = `Successfully made ${this.userInfo.firstName} ${this.userInfo.lastName} an admininstrator`;

      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.statusMessage = err.userFacingErrorMessage;

      }
    },

    /**
     * Calls to the API to from the profile view with a given user ID to revoke requested user from an Administrator
     * Sets a message to user to indicate whether selected user has been premoted to Admin or request failed.
     */
    revokeAdmin: async function (userId) {
      try {
        await Api.revokeAdmin(userId);
        this.userInfo.role = "ROLE_USER";
        if (userId === this.authUser.id) {
          this.$stateStore.actions.revokeAdmin();
        }
        this.statusMessage = `Successfully revoked ${this.userInfo.firstName} ${this.userInfo.lastName}'s administrator privileges`;

      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.statusMessage = err.userFacingErrorMessage;

      }
    },

    /**
     * Calls the API and updates the component's data with the result
     */
    apiPipeline: async function () {
      await this.parseApiResponse(this.callApi(this.userId));
    },

    /**
     * Calls the API to get profile information with the given user ID
     * Returns the promise, not the response
     */
    callApi: function (userId) {
      if (typeof userId != "number" || isNaN(userId)) {
        const err = new ApiRequestError(
            "Cannot load profile page (no profile given). You may need to log in");
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
        const authUser = this.$stateStore.getters.getAuthUser();

        this.userInfo = response.data;

        if (authUser && authUser.id == this.userInfo.id) {
          this.$stateStore.actions.setAuthUser(this.userInfo);
        }

      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
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
      return `${date.getDate()} ${this.$constants.MONTH_NAMES[date.getMonth()]}, ${date.getFullYear()}`;
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

      const monthsText = `${months} month${months == 1 ? "" : "s"}`;

      if (years == 0) {
        return monthsText;
      }

      return `${yearsText}, ${monthsText}`;
    },
  },

  computed: {
    authUser() {
      return this.$stateStore.getters.getAuthUser()
    },
    isSignedIn() {
      return this.$stateStore.getters.isSignedIn()
    },
    isAdmin() {
      return this.$stateStore.getters.isAdmin()
    },

    /**
     * Formatted text for date of birth
     */
    dateOfBirthText: function () {
      if (isNaN(Date.parse(this.userInfo.dateOfBirth))) {
        return "Unknown";
      }
      return this.formatDate(this.userInfo.dateOfBirth);
    },

    /**
     * Formatted text for member since text
     */
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
    /**
     * If user id is updated, need to refresh content
     */
    userId: function () {
      this.apiPipeline();
    },
    userInfo() {
      if (this.userInfo
          !== null) {
        document.title = `${this.userInfo.firstName} ${this.userInfo.lastName} | Profile`;
      }
      // If switch user profile and request fails will be stuck with old title
    }
  }
}
</script>
