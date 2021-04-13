<template>
  <div class="bprofile-card card container">
    <div>
      <h1 class="title">Business Information</h1>
      <button class="btn btn-white-bg-primary mx-1 d-flex align-items-end mb-3" type="button"
              v-if="this.userId" v-on:click="backToProfile">
        <span class="material-icons mr-1">arrow_back</span>
        Back to Profile
      </button>
      <ul class="bprofile-info list-unstyled">
        <li class="row">
          <dt class="col-md label">Business Title:</dt>
          <dd class="col-md value">{{ businessInfo.name }}</dd>
        </li>
        <li class="row">
          <dt class="col-md label">Description:</dt>
          <dd class="col-md value">{{ businessInfo.description }}</dd>
        </li>
        <li class="row">
          <dt class="col-md label">Address:</dt>
          <dd class="col-md value">
            {{
              [
                businessInfo.address.streetNumber +
                " " +
                businessInfo.address.streetName,
                businessInfo.address.city,
                businessInfo.address.region,
                businessInfo.address.postcode,
                businessInfo.address.country,
              ]
              .filter(Boolean)
              .join(", ")
            }}
          </dd>
        </li>
        <li class="row">
          <dt class="col-md label">Business Type:</dt>
          <dd class="col-md value">{{ businessInfo.businessType }}</dd>
        </li>
      </ul>
      <div v-if="errorMessage.length > 0" class="row mt-2">
        <div class="col">
          <p class="alert alert-warning">{{ errorMessage }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const Api = require("./../Api").default;

export default {
  name: "businessProfile",
  components: {},

  data() {
    return {
      businessInfo: {
        name: "",
        description: "",
        address: "",
        businessType: "",
      },
      errorMessage: "",
    };
  },
  beforeRouteEnter(to, from, next) {
    next(vm => {
      vm.prevRoute = from
    })
  },

  props: {
    businessId: {
      type: Number,
      required: true,
    },
    userId: {
      type: Number,
      required: false,
    }
  },

  beforeMount: function () {
    // gets user information from api
    this.parseApiResponse(this.callApi(this.businessId));
  },

  methods: {
    /**
     * Calls the API to get profile information with the given business ID
     * Returns the promise, not the response
     */
    callApi: function (businessId) {
      return Api.businessProfile(businessId);
    },

    showBackButton: function () {
      console.log(this.userId);
      return this.userId != undefined;
    },

    backToProfile: function () {
      this.$router.push({
        path: "/profile/" + this.userId
      });
    },

    /**
     * Parses the API response given a promise to the request
     */
    parseApiResponse: async function (apiCall) {
      try {
        const response = await apiCall;
        this.businessInfo = response.data;
      } catch (err) {
        alert(
            err.userFacingErrorMessage == undefined
                ? err.toString()
                : err.userFacingErrorMessage
        );
      }
    },
  },

  computed: {},

  watch: {
    businessId: function () {
      this.parseApiResponse(this.callApi(this.businessId));
    },
  },
};
</script>

<style scoped>
.main {
  line-height: 1.1;
}

.bprofile-info li {
  margin-bottom: 0.5em;
}

.bprofile-info {
  font-size: 1.1em;
}

.bprofile-card {
  margin: 30px;
  border: none;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  padding: 30px;
}
</style>
