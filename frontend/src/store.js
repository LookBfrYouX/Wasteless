import Vue from 'vue';

// This is the state object. It is reactive and can not be accessed directly by components.
// Use getters and actions to modify this object.

let actingAsId = null; // ID of business that an user is currently acting as
try {
  actingAsId = parseInt(localStorage.getItem("actingAsId"), 10);
  /* eslint no-empty: ["error", { "allowEmptyCatch": true }] */
} catch {}

const state = new Vue.observable({
  authUser: JSON.parse(localStorage.getItem("authUser")),
  welcomeMessage: "HERE WE GOOO",
  actingAsId
});

// This is the globally accessible store. It allows the state to be read and modified.
// Currently, vue has been set up to give access to this object using this.$stateStore within components.
export const store = {
  getters: {
    // Get the authenticated user
    getAuthUser: () => {
      return state.authUser;
    },
    // Get the current welcome message (example)
    getWelcomeMessage: () => {
      return state.welcomeMessage;
    },
    
    /**
     * Get the business the user is acting as
     * @returns null or the business the user is acting as. Returns the same object as those found in authUser
     */
    getActingAs: () => {
      if (state.authUser == null) return null;
      if (!Number.isInteger(state.actingAsId)) return null;
    
      const business = state.authUser.businesses.find(business =>  business.id == state.actingAsId);
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
  },
  actions: {
    // Set the current welcome message (example)
    setWelcomeMessage: (message) => {
      state.welcomeMessage = message;
    },
    // Set the currently logged in user and sync with persistent storage
    setAuthUser: (authUser) => {
      state.authUser = authUser;
      localStorage.setItem("authUser", JSON.stringify(authUser));
      localStorage.setItem("userId", state.authUser.id);
      store.actions.setActingAs(null);
    },

    /**
     * Delete authenticated user (and actingAs) from state and local storage
     */
    deleteAuthUser: () => {
      state.authUser = null;
      localStorage.removeItem("authUser");
      localStorage.removeItem("userId");
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
     * Sets the business the user is acting as. Only uses the ID of the business;
     * when `getActingAs` is called, it returns the business in `authUser` with the same ID. Hence, if the business with the given ID does not exist in `authUser`'s businesses array, `getActingAs` will return null.
     * This ensures that there is will not be multiple copies of the business object that may go out of sync
     * @param {*} business business the user is acting as (or object with `id`), or null
     */
    setActingAs: (business) => {
      if (business == null) {
        state.actingAsId = null;
        localStorage.removeItem("actingAsId");
      } else {
        state.actingAsId = business.id;
        localStorage.setItem("actingAsId", business.id);
      }
    },
    // Delete business of user acting as when user switch back to individual
    deleteActingAs: () => {
      store.actions.setActingAs(null);
    }
  },
};
