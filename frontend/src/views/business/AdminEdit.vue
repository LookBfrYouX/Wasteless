<template>
  <div class="container mt-4">
    <div class="row">
      <div class="col-12">
        <h2>Edit Administrators{{businessName? ` for ${businessName}`: ""}}</h2>
      </div>
      <div class="col-12">
        <router-link
          :to="{name: 'BusinessDetail', params: { businessId }}"
          class="btn btn-primary"
        >
          Back to Business
        </router-link>
      </div>
    </div>

    <div class="row mb-2 mb-md-4">
      <div class="col-12 col-md-8 d-flex align-items-center">
        <v-autocomplete
          v-model="adminIdToAdd"
          :items="userSearchResults"
          :loading="userSearchLoading"
          :search-input.sync="userSearchQuery"
          item-text="name"
          item-value="id"

          solo
          clearable
          color="white"

          class="remove-v-autocomplete-bottom-padding"

          hide-selected
          label="Search Users"
          placeholder="Start typing to Search"
          prepend-icon="search"


          :hide-no-data="userSearchErrorMessage == null"
          :no-data-text="userSearchErrorMessage == null? 'No results': userSearchErrorMessage"
        >
        <!-- Stick error message inline in the no data text -->
        </v-autocomplete>
      </div>
      <div class="col-12 col-md-4 d-flex align-items-center justify-content-end">
        <button
          class="btn btn-primary d-flex align-items-center"
          type="button"
          :disabled="adminIdToAdd == null"
          @click="addAdmin"
        >
          <span class="material-icons">add</span>
          Add as administrator
        </button>
      </div>
      <div v-if="addAdminErrorMessage" class="col-12">
        <div class="alert alert-warning mb-0">
          {{addAdminErrorMessage}}
        </div>
      </div>
    </div>
    <v-data-table
       :headers="[{
         text: 'Name',
         sortable: true,
         value: 'name'
       }, {
         text: 'Remove',
         sortable: false,
         align: 'end',
         value: 'actions'
       }]"
       :items="admins == null? []: admins"

       :loading="admins == null"
        loading-text="Loading business information"

        :items-per-page="10"
        :footer-props="{
          showFirstLastPage: false,
          prevIcon: 'chevron_left',
          nextIcon: 'chevron_right'
        }"
    >
      <template v-slot:top>
        <!-- Once delete button clicked, confirmation through this dialog -->
        <v-dialog v-model="removeAdminDialogOpen" max-width="500px">
          <v-card>
            <v-card-title class="text-h5">Remove this user as an administrator of this business?</v-card-title>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="removeAdminDialogOpen = false">Cancel</v-btn>
              <v-btn color="blue darken-1" text @click="removeAdmin">OK</v-btn>
              <v-spacer></v-spacer>
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- If remove admin API call fails, this dialog appears -->
        <v-dialog
            :value="removeAdminErrorMessage != null"
            @input="() => removeAdminErrorMessage = null"
            max-width="500px"
        >
          <v-card>
            <v-card-title class="text-h5">Admin Removal Failed</v-card-title>
            <v-card-text>{{ removeAdminErrorMessage }}</v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="removeAdmin">Retry</v-btn>
              <v-btn color="blue darken-1" text @click="() => removeAdminErrorMessage = null">Ok</v-btn>
              <v-spacer></v-spacer>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </template>
      <template v-slot:[`item.name`]="{ item }">
        <div
          class="d-flex flex-column"
        >
          <span>{{ item.name }}</span>
          <span
            class="primary-admin-text text-faded"
            v-if="business.primaryAdministratorId == item.id"
          >
            Primary Business Administrator
          </span>
        </div>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <div
          class="d-flex justify-content-end"
          v-if="business.primaryAdministratorId != item.id"
        >
          <button
            class="btn btn-outline-danger btn-sm d-flex align-content-center"
            @click="removeAdminButtonClicked(item.id)"
          >
            <span class="material-icons">delete</span>
          </button>
        </div>
      </template>
    </v-data-table>

    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="fetchBusiness"
        :show="apiErrorMessage !== null"
        title="Error fetching business information"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
 </div>	
</template>
<script>
import {Api} from "@/Api";

import ErrorModal from "@/components/ErrorModal.vue"

export default {
  components: {
    ErrorModal
  },

  props: {
    businessId: {
      required: true,
      type: Number,
    },
  },

  data() {
    return {
      business: null,
      apiErrorMessage: null,

      userSearchQuery: "",
      userSearchLoading: false,
      userSearchErrorMessage: null,
      userSearchResultsRaw: [],
      adminIdToAdd: null,
      addAdminErrorMessage: null,

      adminIdToRemove: null,
      removeAdminDialogOpen: false,
      removeAdminErrorMessage: null
    }
  },

  async mounted() {
    await Promise.all([this.fetchBusiness()]);
  },

  methods: {
    /**
     * Fetches businesses, setting `business`
     */
    async fetchBusiness() {
      this.business = null; // Show loading screen
      try {
        this.business = (await Api.businessProfile(this.businessId)).data;
      } catch(err) {
        if (await Api.handle401.call(this, err)) return;
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },


    /**
     * Callback when a remove admin button is clicked. Pass in the relevant admin ID
     */
    removeAdminButtonClicked(id) {
      this.adminIdToRemove = id;
      this.removeAdminDialogOpen = true;
    },

    /**
     * Pipeline for removing admin, given by `adminIdToRemove`, from the buisness
     * Handles API call, errors etc.
     */
    async removeAdmin() {
      const id = this.adminIdToRemove;
      this.removeAdminDialogOpen = false;
      this.adminIdToRemove = null;
      this.removeAdminErrorMessage = null;

      try {
        await Api.removeBusinessAdmin(this.businessId, id);
      } catch(err) {
        if (await Api.handle401.call(this, err)) return;
        this.removeAdminErrorMessage = err.userFacingErrorMessage;
        return;
      }

      await this.fetchBusiness();
    },

    /**
     * Pipeline for adding admin, given by `adminIdToAdd`, to the buisness
     * Handles API call, errors etc.
     */
    async addAdmin() {
      const id = this.adminIdToAdd;
      this.adminIdToAdd = null;
      this.userSearchQuery = "";
      this.userSearchResultsRaw = [];
      this.addAdminErrorMessage = null;

      try {
        await Api.addBusinessAdmin(this.businessId, id);
      } catch(err) {
        if (await Api.handle401.call(this, err)) return;
        this.addAdminErrorMessage = err.userFacingErrorMessage;
        return;
      }

      await this.fetchBusiness();
    }
  },

  computed: {
    /**
     * Business name, or null if businesses have not been fetched yet
     */
    businessName() {
      if (this.business) return this.business.name;
      return null;
    },
   
    /**
     * Returns array of business admins with only information necesssary for the table
     * If businesses have not been fetched, null is returned
     */
    admins() {
      if (this.business) {
        return this.business.administrators.map(admin => {
          return {
            name: this.$helper.formatFullName(admin),
            id: admin.id
          }
        });
      }
      return null; 
    },

    /**
     * Gets set of existing admin IDs. Empty set if businesses not loaded
     */
    existingAdminIds() {
      if (this.business) return new Set(this.business.administrators.map(admin => admin.id));
      return new Set();
    },

    /**
     * Checks if the user has permission to edit the business; either a GAA or the primary administrator
     */
    userCanModifyBusiness() {
      const {getters} = this.$stateStore;

      return getters.isAdmin() || getters.isSignedIn() &&
             this.business && getters.getAuthUser().id == this.business.primaryAdministratorId;
    },

    /**
     * Calculates full user name and disabled state
     */
    userSearchResults() {
      return this.userSearchResultsRaw.map(user => ({
        id: user.id,
        name: this.$helper.formatFullName(user),
        disabled: typeof user.disabled == "boolean"? user.disabled: this.existingAdminIds.has(user.id)
        // If this is not computed, when admin is added and you click back on the search field,
        // the person you just added can still be selected
      }));
    }
  },

  watch: {
    /**
     * Fetches businesses when ID changes
     */
    async businessId() {
      await this.fetchBusiness();
    },

    /**
     * Sets document title when business name updated
     */
    businessName() {
      if (this.businessName != null) {
        document.title = `Edit Admins for ${this.businessName}`;
      }
    },

    /**
     * Callback when search query changes. Responsible for API request to search for users
     */
    async userSearchQuery(query) {
      this.userSearchLoading = true;
      this.userSearchErrorMessage = null;

      // If user is selected from dropdown and they later type some other stuff, remove the selection
      if (this.adminIdToAdd != null) {
        const selectedUser = this.userSearchResults.find(user => user.id == this.adminIdToAdd);
        if (!selectedUser || selectedUser.name != query) {
          this.adminIdToAdd = null;
        }
      }

      try {
        const params = { searchQuery: query, pagEndIndex: 10 };
        this.userSearchResultsRaw = (await Api.search(params)).data.results;
      } catch(err) {
        if (await Api.handle401.call(this, err)) return;
        // Show that api request failed inline in the search results
        this.userSearchErrorMessage = `Search failed; ${err.userFacingErrorMessage}`;
      }

      this.userSearchLoading = false;
    }
  }
};
</script>
<style scoped>
.primary-admin-text {
  font-size: 0.8em;
  font-variant-caps: all-small-caps;
}

</style>