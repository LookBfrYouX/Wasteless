<template>
  <div class="container mt-4">
    <div class="row">
      <div class="col-12">
        <h2>Edit Administrators{{businessName? ` for ${businessName}`: ""}}</h2>
      </div>
    </div>

    <v-autocomplete
        v-model="adminIdToAdd"
        :items="userSearchResults"
        :loading="userSearchLoading"
        :search-input.sync="userSearchQuery"
        item-text="name"
        item-value="id"

        color="white"
        hide-no-data
        hide-selected
        label="Public APIs"
        placeholder="Start typing to Search"
        prepend-icon="md-database-search"
      ></v-autocomplete>
      <button
        class="btn btn-primary d-flex align-items-center"
        type="button"
        :disabled="adminIdToAdd == null"
        :click="addAdmin"
      >
        <span class="material-icons">add</span>
        Add as administrator
      </button>

    <v-data-table
       :headers="[{
         text: 'Name',
         sortable: true,
         value: 'name'
       }, {
         text: 'Email',
         sortable: true,
         value: 'email'
       }, {
         text: 'Remove',
         sortable: false,
         value: 'actions'
       }]"
       :items="admins == null? []: admins"

       :loading="admins == null"
        loading-text="Loading business information"


        :items-per-page="10"
        :footer-props="{
          showFirstLastPage: true,
          firstIcon: 'md-arrow-collapse-left',
          lastIcon: 'md-arrow-collapse-right',
          prevIcon: 'md-minus',
          nextIcon: 'md-plus'
        }"
    >
      <template v-slot:top>
        <!-- Not sure if dialog needs to go inside this slot -->
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
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <button
          class="btn btn-outline-danger btn-sm d-flex align-content-center"
          :disabled="business.primaryAdministratorId == item.id"
          @click="removeAdminButtonClicked(item.id)"
        >
          <span class="material-icons">delete</span>
        </button>
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
      adminIdToRemove: null,
      removeAdminDialogOpen: false,

      todoModel: null,
      userSearchQuery: "",
      userSearchLoading: false,
      userSearchResults: [],
      adminIdToAdd: null,
    }
  },

  async mounted() {
    await Promise.all([this.fetchBusiness()])
  },

  methods: {
    /**
     * Fetches businesses, setting `business`
     */
    async fetchBusiness() {
      try {
        this.business = (await Api.businessProfile(this.businessId)).data;
      } catch(err) {
        if (await Api.handle401(err)) return;
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    searchEvent(query) {
      console.log(query);
    },


    /**
     * Formats object with name components, removing null or empty strings
     * @param {{firstName: String, middleName: String, lastName: String, nickname: String}} object with name components
     * @return string in format {firstName} {middleName} {lastName} ({nickname})
     */
    formatName(user) {
      let name = [user.firstName, user.middleName, user.lastName]
          .filter(el => typeof el == "string" && el.trim().length)
          .map(el => el.trim())
          .join(" ");
      
      if (typeof user.nickname == "string" && user.nickname.trim().length) {
        name += ` (${user.nickname})`
      }
      return name;
    },

    removeAdminButtonClicked(id) {
      this.adminIdToRemove = id;
      this.removeAdminDialogOpen = true;
    },

    removeAdmin() {
      console.log("REMOVE");
    },

    addAdmin() {
      console.log("ADD", this.adminIdToAdd);
      this.adminIdToAdd = null;
      this.userSearchQuery = "";
      this.userSearchResults = [];
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
            name: this.formatName(admin),
            email: admin.email,
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

    async userSearchQuery(query) {
      this.userSearchLoading = true;

      // // If user is selected from dropdown and they later type some other stuff, remove the selection
      // if (this.adminIdToAdd != null) {
      //   const selectedUser = this.userSearchResults.find(user => user.id == this.adminIdToAdd);
      //   if (!selectedUser || selectedUser.name != query) {
      //     this.adminIdToAdd = null;
      //   }
      // }


      console.log(query);
      try {
        const results = (await Api.search(query)).data.results;
        this.userSearchResults = results.map(user => ({
          ...user,
          name: this.formatName(user),
          disabled: this.existingAdminIds.has(user.id)
        }));
      } catch(err) {
        if (await Api.handle401(err)) return;
        console.error(err);
        console.log("TODO");
        this.userSearchLoading = false;
        return;
      }
      this.userSearchLoading = false;
    }
  }
};
</script>
<style>
</style>