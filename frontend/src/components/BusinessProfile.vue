<template>
  <div class="business-profile-card card container">
    <div>
      <h1 class="title">Business Information</h1>
      <img v-if="businessInfo.role && businessInfo.role == 'ROLE_ADMIN'" class="admin-icon" src="id-card.svg" alt="Admin role icon">
      <ul class="profile-info list-unstyled">
        <li class="row">
          <dt class="col-md label">Business Title:</dt>
          <dd class="col-md value"> {{ businessInfo.name }} </dd>
        </li>
        <li class="row">
          <dt class="col-md label">Description:</dt>
          <dd class="col-md value"> {{ businessInfo.description }} </dd>
        </li>
        <li class="row">
          <dt class="col-md label">Address:</dt>
          <dd class="col-md value"> {{ businessInfo.address }} </dd>
        </li>
        <li class="row">
          <dt class="col-md label">Business Type:</dt>
          <dd class="col-md value"> {{ businessInfo.type}} </dd>
        </li>
      </ul>
      <div class="row mt-2" v-if="errorMessage.length > 0">
          <div class="col">
            <p class="alert alert-warning">{{ errorMessage }}</p>
          </div>
      </div>
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
      businessInfo: {
        name: "",
        description: "",
        address: "",
        type: "",
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
  methods: {
    /**
     * Calls the API to get profile information with the given business ID
     * Returns the promise, not the response
     */
    callApi: function(businessId) {
      if (typeof businessId != "number" || isNaN(businessId)) {
        const err = new Error("Cannot load profile page (no profile given). You may need to log in");
        err.userFacingErrorMessage = err.message;
        return Promise.reject(err);
      }
      return Api.businessProfile(businessId);
    },

    /**
     * Parses the API response given a promise to the request
     */
    parseApiResponse: async function(apiCall) {
      try {
        const response = await apiCall;
        this.businessInfo = response.data;
      } catch(err) {
        alert(err.userFacingErrorMessage == undefined? err.toString(): err.userFacingErrorMessage);
      }
    },
  },

  computed: {

  },

  watch: {
    businessId: function() {
      this.parseApiResponse(this.callApi(this.businessId))
    }
  },
}
</script>
