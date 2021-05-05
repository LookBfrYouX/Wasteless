<template>
  <div class="bprofile-card card container">
    <div>
      <h1 class="title">Business Information</h1>
      <button class="btn btn-white-bg-primary mx-1 d-flex align-items-end mb-3" type="button"
              v-if="showBackButton" v-on:click="$router.go(-1)">
        <span class="material-icons mr-1">arrow_back</span>
        Go back
      </button>
      <ul class="bprofile-info list-unstyled">
        <dl class="row">
          <dt class="col-md label">Business Title:</dt>
          <dd class="col-md value"> {{ businessInfo.name }}</dd>
        </dl>
        <dl class="row">
          <dt class="col-md label">Description:</dt>
          <dd class="col-md value"> {{ businessInfo.description }}</dd>
        </dl>
        <dl class="row">
          <dt class="col-md label">Address:</dt>
          <dd class="col-md value"> {{ $helper.addressToString(businessInfo.address) }}
          </dd>
        </dl>
        <dl class="row">
          <dt class="col-md label">Business Type:</dt>
          <dd class="col-md value"> {{ businessInfo.businessType }}</dd>
        </dl>
        <dl class="row">
          <dt class="col-md label">Administrator:</dt>
          <dd v-for="admin in businessInfo.administrators" v-bind:key="admin.id"
              class="col-md value admin-link"
              v-on:click="viewAdmin(admin.id)"> {{ admin.firstName }} {{ admin.lastName }}</dd>
        </dl>
      </ul>
      <div
        class="d-flex flex-wrap justify-content-space"
        v-if="$stateStore.getters.canEditBusiness(businessId)"
      >
        <button
            class="btn btn-white-bg-primary m-1 d-flex"
            type="button"
            v-on:click="createProduct()"
        >
          <span class="material-icons mr-1">person</span>
          Add Product To Catalogue
        </button>
        <button
            v-if="$stateStore.getters.canEditBusiness(businessId)"
            class="btn btn-white-bg-primary m-1 d-flex"
            type="button"
            v-on:click="viewCatalogue()"
        >
          <span class="material-icons mr-1">person</span>
          View Catalog
        </button>
      </div>
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
        administrators: [],
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
      default: true
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
      this.$router.push({
        name: "createProduct",
        params: {
          businessId: this.businessId
        }
      });
    },

    viewCatalogue: function() {
      this.$router.push({
        name: "productCatalogue",
        params: {
          businessId: this.businessId
        }
      });
    },

    /**
     * Parses the API response given a promise to the request,
     * setting the businessInfo object, handling errors if necessary,
     *  updating statestore's authUser if they are an admin of the business
     */
    parseApiResponse: async function (apiCall) {
      let failed = false; // don't bother updating state if api call fails
      try {
        const response = await apiCall;
        this.businessInfo = response.data;
      } catch (err) {
        if (await Api.handle401.call(this, err)) return;
        failed = true;
        this.apiErrorMessage = err.userFacingErrorMessage;
      }

      const currentUser = this.$stateStore.getters.getAuthUser();
      if (failed || this.businessInfo === null || currentUser === null) return;
      if (this.businessInfo.primaryAdministratorId == currentUser.id ||
          this.businessInfo.administrators.find(admin => admin.id == currentUser.id) !== undefined
      ) {
        // Update the user with new info
        // Deep copy user/business
        const userCopy = JSON.parse(JSON.stringify(currentUser));
        let businessCopy = JSON.parse(JSON.stringify(this.businessInfo));
        delete businessCopy.administrators; // userInfo's businesses don't contain administrators list (otherwise recursion)
        const index = userCopy.businessesAdministered.findIndex(business => business.id == businessCopy.id);
        if (index === -1) userCopy.businessesAdministered.push(businessCopy); // Business was just created
        else userCopy.businessesAdministered[index] = businessCopy;

        this.$stateStore.actions.setAuthUser(userCopy);
      }
    },

    /**
     * View profile of the business administrator
     * @param userId Passed as admin id but it is the same as user id.
     */
    viewAdmin(userId) {
      this.$router.push({
        name: "profile",
        params: {
          userId
        }
      });
    }
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

.admin-link:hover {
  cursor: pointer;
  color: #1ec996;
}

</style>
