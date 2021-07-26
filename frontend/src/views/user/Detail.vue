<template>
  <div class="container my-4">
    <div class="w-100 grid-container">
      <div class="profile-image-container card">
        <img alt="User's profile image" class="my-3 rounded-circle"
              src="@/../assets/images/default-user-thumbnail.svg">
      </div>
      <div class="user-info-container card p-3">
        <div class="">
          <div class="d-flex align-items-center flex-column flex-sm-row">
            <!--Users name-->
            <h2 class="mb-0 float-left">{{ userInfo.firstName }} {{ userInfo.middleName }}
              {{ userInfo.lastName }}</h2>
            <!--Location data-->
            <div
                v-if="userInfo.homeAddress && (userInfo.homeAddress.suburb || userInfo.homeAddress.city)"
                class="d-flex align-items-center">
              <span class="material-icons md-dark md-inactive ml-2">location_on</span>
              <span class="text-muted">
                {{ $helper.addressToString(userInfo.homeAddress, true) }}
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
                @click="makeAdmin(userId)"
            >
              <span class="material-icons mr-1">person</span>
              Make Admin
            </button>
            <button
                v-if="isAdmin && userInfo.role == 'ROLE_ADMIN'"
                id="revokeAdmin"
                class="btn btn-white-bg-primary m-1 d-flex"
                type="button"
                @click="revokeAdmin(userId)"
            >
              <span class="material-icons mr-1">person</span>
              Revoke Admin
            </button>
            <router-link
                v-if="isAdmin"
                :to="{ name: 'MarketplaceCardCreateAdmin', params: { userId: userInfo.id }}"
                class="btn btn-white-bg-primary m-1 d-flex"
            >
              Create Marketplace Card
            </router-link>
            <router-link
                v-if="isSignedIn && authUser.id === userInfo.id"
                :to="{ name: 'BusinessCreate' }"
                class="btn btn-white-bg-primary m-1 d-flex"
            >
              <span class="material-icons mr-1">business</span>
              Register Business
            </router-link>
            <router-link
                v-else-if="isSignedIn && isAdmin"
                :to="{name: 'BusinessCreateAdmin', params: {userId}}"
                class="btn btn-white-bg-primary m-1 d-flex"
            >
              <span class="material-icons mr-1">business</span>
              Register Business as {{userInfo.firstName}}
            </router-link>
          </div>
          <div v-if="statusMessage.length > 0" class="row mt-2">
            <div class="col">
              <p class="alert alert-warning">{{ statusMessage }}</p>
            </div>
          </div>
        </div>
      </div>
          
      <div class="card businesses-container p-3 overflow-auto">
        <h5 class="text-muted">Businesses</h5>
        <div
            v-if="Array.isArray(userInfo.businessesAdministered) && userInfo.businessesAdministered.length !== 0">
          <ul class="profile-business-info list-unstyled pl-0">
            <li
                v-for="(business, index) in userInfo.businessesAdministered"
                :key="index"
                class="list-group-item card text-wrap mb-2 border"
            >
              <router-link
                  :to="{ name: 'BusinessDetail', params: { businessId: business.id, showBackButton: true}}"
                  class="text-reset text-decoration-none"
              >
                <h5 class="business-name card-title card-link green-text-hover">
                  {{ business.name }}
                </h5>
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
      <div class="card user-details-container p-3 pb-0 overflow-auto">
        <ul class="nav nav-tabs">
          <li class="nav-item">
            <a aria-current="page" class="nav-link active">Details</a>
          </li>
          <li class="nav-item">
            <a class="nav-link disabled">Future Tab</a>
          </li>
        </ul>
        <div class="m-md-4 mb-md-0">
          <div class="overflow-auto w-100">
            <table class="table table-hover mb-0">
              <tbody>
                <tr>
                  <td class="pl-0 pl-md-2" colspan="2"><h5 class="text-muted">User Details</h5></td>
                </tr>
                <tr v-if="userInfo.nickname" scope="row">
                  <th class="pl-0 pl-md-2">Nickname:</th>
                  <td class="pr-0 pr-md-2 col-md value"><p>{{ userInfo.nickname }}</p></td>
                </tr>
                <tr scope="row">
                  <th class="pl-0 pl-md-2">Member since:</th>
                  <td class="pr-0 pr-md-2 col-md value"><p>{{ this.$helper.memberSinceText(userInfo.created) }}</p></td>
                </tr>
                <tr v-if="dateOfBirthText" scope="row">
                  <th class="pl-0 pl-md-2">Date of Birth:</th>
                  <td class="pr-0 pr-md-2 col-md value"><p>{{ dateOfBirthText }}</p></td>
                </tr>
                <tr>
                  <td class="pl-0 pl-md-2" colspan="2"><h5 class="text-muted">Contact Information</h5>
                  </td>
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
    </div>
    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.apiPipeline"
        :show="apiErrorMessage !== null"
        title="Error fetching user details"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<style>
</style>
<style lang="scss" scoped>
@import "~/src/styles/grid-breakpoints.scss";

/* Using CSS Grid instead of standard bootstrap as we need to 
limit the height of the businesses administered list. Height of the 
businesses and user details container need to be equal, so thought this
would be the easiest option
*/
.grid-container {
  display: grid;
  // Auto height for first row, don't know what the second row is doing but
  // it seems to be working
  grid-template-rows: auto min-content;
  // First column takes up a third, second takes up two thirds 
  grid-template-columns: 4fr 8fr;
  grid-gap: 1rem;
}

.businesses-container, .user-details-container {
  // Sets max height for the entire row
  // Tried using minmax(min-content, 35em) but min-content ensures the 
  // content doesn't overflow, which we do want in this case
  // This is a difficult problem as we want the max height of the business container
  // to be the below value, **unless the user details container is taller**
  max-height: 35em;
}

.user-details-container {
  min-height: 0;
}

@media (max-width: map-get($grid-breakpoints, "md")) {
  .grid-container {
    grid-template-columns: auto;
    grid-template-rows: repeat(4, auto);
  }
  
  .user-details-container {
    // Switch order: user details is the third row, before businesses list
    grid-row: 3/4;
  }

  .businesses-container, .user-details-container {
    max-height: initial;
  }
}

th {
  white-space: nowrap;
}
</style>
<script>
import ErrorModal from '../../components/ErrorModal.vue';
import {ApiRequestError} from "@/ApiRequestError";

import {Api} from "@/Api";

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
  beforeMount: async function () {
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
      return this.$helper.formatDate(this.userInfo.dateOfBirth);
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
      if (this.userInfo !== null) {
        document.title = `${this.userInfo.firstName} ${this.userInfo.lastName} | Profile`;
      }
      // If switch user profile and request fails will be stuck with old title
    }
  }
}
</script>
