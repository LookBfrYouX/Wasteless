import Vue from 'vue';

// This is the state object. It is reactive and can not be accessed directly by components.
// Use getters and actions to modify this object.

let actingAsId = null; // ID of business that an user is currently acting as
try {
  actingAsId = parseInt(localStorage.getItem("actingAsId"), 10);
  /* eslint no-empty: ["error", { "allowEmptyCatch": true }] */
} catch {}

let authUser = null;
try {
  authUser = JSON.parse(localStorage.getItem("authUser"));
  /* eslint no-empty: ["error", { "allowEmptyCatch": true }] */
} catch {}

const state = new Vue.observable({
  authUser,
  actingAsId
});

// This is the globally accessible store. It allows the state to be read and modified.
// Currently, vue has been set up to give access to this object using this.$stateStore within components.
export const store = {
  getters: {
    /**
     * Get the authenticated user
     * @returns {any}
     */
    getAuthUser: () => {
      return state.authUser;
    },
    /**
     * Get businessId for a select business
     * @returns {null}
     */
    getBusinessId: () => {
      return state.businessId;
    },
    
    /**
     * Get the business the user is acting as
     * @returns null or the business the user is acting as. Returns the same object as those found in authUser
     */
    getActingAs: () => {
      if (state.authUser == null) return null;
      if (!Number.isInteger(state.actingAsId)) return null;
    
      const business = state.authUser.businessesAdministered.find(business => business.id == state.actingAsId);
      if (business != undefined) return business;
      return null;
    },

    /**
     * Checks if a user is logged in or not
     */
    isLoggedIn() {
      return state.authUser !== null && state.authUser !== undefined;
    },

    /**
     * True if admin, false if not OR IF NOT LOGGED IN
     */
    isAdmin() {
      return state.authUser && state.authUser.role === "ROLE_ADMIN";
    },

    isActingAsBusiness() {
      return state.actingAsId !== null;
    },


  },
  actions: {
    // Set the currently logged in user and sync with persistent storage
    setAuthUser: (authUser) => {
      state.authUser = authUser;
      localStorage.setItem("authUser", JSON.stringify(authUser));
      store.actions.setActingAs(null);
    },

    /**
     * Delete authenticated user (and actingAs) from state and local storage
     */
    deleteAuthUser: () => {
      state.authUser = null;
      localStorage.removeItem("authUser");
      store.actions.setActingAs(null);
    },
    // Make the currently logged in user admin
    makeAdmin: () => {
      state.authUser.role = "ROLE_ADMIN";
      localStorage.setItem("authUser", JSON.stringify(state.authUser));
    },
    // Remove admin status from currently logged in user
    revokeAdmin: () => {
      state.authUser.role = "ROLE_USER";
      localStorage.setItem("authUser", JSON.stringify(state.authUser));
    },
    
    /**
     * Sets the business the user is acting as.
     * When `getActingAs` is called, it returns the business in `authUser` with the same ID. Hence, if the business with the given ID does not exist in `authUser`'s businesses array, `getActingAs` will return null.
     * This ensures that there is will not be multiple copies of the business object that may go out of sync
     * @param {*} business ID of business, the business the user is acting as (or object with `id`), or null to remove
     * @throws error if business ID not in the list of user businesses ()
     */
    setActingAs: (business) => {
      if (business == null) {
        state.actingAsId = null;
        localStorage.removeItem("actingAsId");
      } else {
        const id = Number.isInteger(business)? business: business.id;
        if (
          state.authUser == null || 
          state.authUser.businessesAdministered.find(business => business.id == id) == undefined
        ) throw new Error("Tried to set acting as business, but business ID not found in authUser's businesses");
        state.actingAsId = id;
        localStorage.setItem("actingAsId", id);
      }
    },
    // Delete business of user acting as when user switch back to individual
    deleteActingAs: () => {
      store.actions.setActingAs(null);
    }
  },
};
