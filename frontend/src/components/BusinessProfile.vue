<template>
  <div class="bprofile-card card container">
    <div>
      <h1 class="title">Business Information</h1>
      <button class="btn btn-white-bg-primary mx-1 d-flex align-items-end mb-3" type="button"
              v-if="showBackButton" v-on:click="$router.go(-1)">
        <span class="material-icons mr-1">arrow_back</span>
        Back to Profile
      </button>
      <ul class="bprofile-info list-unstyled">
        <li class="row">
          <dt class="col-md label">Business Title:</dt>
          <dd class="col-md value"> {{ businessInfo.name }}</dd>
        </li>
        <li class="row">
          <dt class="col-md label">Description:</dt>
          <dd class="col-md value"> {{ businessInfo.description }}</dd>
        </li>
        <li class="row">
          <dt class="col-md label">Address:</dt>
          <dd class="col-md value"> {{
              [businessInfo.address.streetNumber + " " +
              businessInfo.address.streetName, businessInfo.address.city,
                businessInfo.address.region, businessInfo.address.country,
                businessInfo.address.postcode].join(", ")
            }}
          </dd>
        </li>
        <li class="row">
          <dt class="col-md label">Business Type:</dt>
          <dd class="col-md value"> {{ businessInfo.businessType }}</dd>
        </li>
      </ul>
      <button
          v-if="this.$stateStore.getters.isAdmin() || (this.$stateStore.getters.getActingAs() !== null && this.$stateStore.getters.getActingAs().id === businessId)"
          class="btn btn-white-bg-primary mx-1 d-flex"
          type="button"
          v-on:click="createProduct()"
      >
        <span class="material-icons mr-1">person</span>
        Add Product To Catalogue
      </button>
      <error-modal
        title="Error fetching business details"
        v-bind:hideCallback="() => (apiErrorMessage = null)"
        v-bind:refresh="false"
        v-bind:retry="false"
        v-bind:goBack="true"
        v-bind:show="apiErrorMessage !== null"
      >
        <p>{{ apiErrorMessage }}</p>
    </error-modal>
    </div>
  </div>
</template>

<script>
import ErrorModal from "./Errors/ErrorModal.vue";
import { ApiRequestError } from "./../ApiRequestError";
const { Api } = require("./../Api.js");

export default {
  name: 'businessProfile',
  components: { ErrorModal },

  data() {
    return {
      businessInfo: {
        name: "",
        description: "",
        address: {},
        businessType: "",
      },
      apiErrorMessage: null,
    };
  },
  props: {
    businessId: {
      type: Number,
      required: true
    },
    showBackButton: {
      type: Boolean,
      required: false,
      default: false
    },
  },

  beforeMount: function () {
    // gets user information from api
    this.parseApiResponse(this.callApi(this.businessId))
  },

  methods: {
    /**
     * Calls the API to get profile information with the given business ID
     * Returns the promise, not the response
     */
    callApi: function (businessId) {
       if (!Number.isInteger(businessId)) {
        const err = new ApiRequestError(
          "Cannot load business profile page - business ID not given"
        );
        return Promise.reject(err);
      }

      return Api.businessProfile(businessId);
    },

    createProduct: function() {
      this.$router.push({name: "createProduct"});
    },

    /**
     * Parses the API response given a promise to the request
     */
    parseApiResponse: async function (apiCall) {
      try {
        const response = await apiCall;
        this.businessInfo = response.data;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },
  },

  computed: {},

  watch: {
    businessId: function () {
      this.parseApiResponse(this.callApi(this.businessId))
    },

    businessInfo() {
      if (this.businessInfo !== null) document.title = `${this.businessInfo.name} | Business`;
    }
  },
}
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
