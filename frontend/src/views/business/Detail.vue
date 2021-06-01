<template>
  <div class="bprofile-card card container">
    <div>
      <h1 class="title">Business Information</h1>
      <button v-if="showBackButton"
              class="btn btn-white-bg-primary mr-1 d-flex align-items-end mb-3"
              type="button" @click="$router.go(-1)">
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
          <dd
            v-for="admin in businessInfo.administrators"
            :key="admin.id"
            class="col-md value"
          >
              <router-link
                class="admin-link hover-cursor-pointer text-decoration-none"
                :to="{ name: 'UserDetail', params: { userId: admin.id }}"
              >
                {{ admin.firstName }} {{ admin.lastName }}
              </router-link>
          </dd>
        </dl>
      </ul>
      <div class="d-flex flex-wrap justify-content-space">
        <router-link
            class="btn btn-white-bg-primary m-1 d-flex"
            v-if="canEditBusiness"
            :to="{ name: 'BusinessProductCreate', params: { businessId }}"
        >
          <span class="material-icons mr-1">add</span>
          Add Product To Catalogue
        </router-link>
        <router-link
            class="btn btn-white-bg-primary m-1 d-flex"
            v-if="canEditBusiness"
            :to="{ name: 'BusinessProducts', params: { businessId }}"
        >
          <span class="material-icons mr-1">list</span>
          View Catalogue
        </router-link>
        <router-link
            class="btn btn-white-bg-primary m-1 d-flex"
            v-if="canEditBusiness"
            :to="{ name: 'BusinessInventory', params: { businessId }}"
        >
          <span class="material-icons mr-1">inventory</span>
          View Inventory 
        </router-link>
        <router-link
            class="btn btn-white-bg-primary m-1 d-flex"
            :to="{ name: 'BusinessListings', params: { businessId }}"
        >
          <span class="material-icons mr-1">storefront</span>
          View Listings
        </router-link>
        
      </div>
      <error-modal
          title="Error fetching business details"
          :goBack="true"
          :hideCallback="() => (apiErrorMessage = null)"
          :refresh="false"
          :retry="false"
          :show="apiErrorMessage !== null"
      >
        <p>{{ apiErrorMessage }}</p>
      </error-modal>
    </div>
  </div>
</template>

<script>
import ErrorModal from "@/components/ErrorModal.vue";
import {ApiRequestError} from "@/ApiRequestError";

import { Api } from "@/Api";

export default {
  name: 'businessProfile',
  components: {ErrorModal},

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
        if (await Api.handle401.call(this, err)) {
          return;
        }
        failed = true;
        this.apiErrorMessage = err.userFacingErrorMessage;
      }

      const currentUser = this.$stateStore.getters.getAuthUser();
      if (failed || this.businessInfo === null || currentUser === null) {
        return;
      }
      if (this.businessInfo.primaryAdministratorId == currentUser.id ||
          this.businessInfo.administrators.find(admin => admin.id == currentUser.id) !== undefined
      ) {
        // Update the user with new info
        // Deep copy user/business
        const userCopy = JSON.parse(JSON.stringify(currentUser));
        let businessCopy = JSON.parse(JSON.stringify(this.businessInfo));
        delete businessCopy.administrators; // userInfo's businesses don't contain administrators list (otherwise recursion)
        const index = userCopy.businessesAdministered.findIndex(
            business => business.id == businessCopy.id);
        if (index === -1) {
          userCopy.businessesAdministered.push(businessCopy);
        }// Business was just created
        else {
          userCopy.businessesAdministered[index] = businessCopy;
        }

        this.$stateStore.actions.setAuthUser(userCopy);
      }
    },
  },

  computed: {
    /**
     * Checks if the user can edit the business
     */
    canEditBusiness() {
      return this.$stateStore.getters.canEditBusiness(this.businessId);
    }
  },

  watch: {
    businessId: function () {
      this.parseApiResponse(this.callApi(this.businessId))
    },

    businessInfo() {
      if (this.businessInfo !== null) {
        document.title = `${this.businessInfo.name} | Business`;
      }
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

a.admin-link {
  color: inherit;
}

a.admin-link:hover {
  color: #1ec996;
}

</style>
