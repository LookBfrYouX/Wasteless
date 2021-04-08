import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

/** This module implements the vuex state store to manage global state.
 * Persistence is still done through local storage so remember to save things there that need to persisted through
 * closing the page or refreshing the page.
 */
export default new Vuex.Store({
    // The state of the application has it's data stored here. Please do not get/edit it directly as
    // this makes debugging and predictability much harder.
    state () {
        return {
            authUser: JSON.parse(localStorage.getItem("authUser")),
            welcomeMessage: "HERE WE GOOO",
        };
    },
    // Mutations are for manipulating state data. This ensures predictable state changes.
    // Mutations are not async and should be triggered by actions which can be async.
    mutations: {
        setWelcomeMessage(state, newMessage) {
            state.welcomeMessage = newMessage;
        },
        setAuthUser(state, authUser) {
            state.authUser = authUser;
            localStorage.setItem("authUser", JSON.stringify(authUser));
        },
        deleteAuthUser(state) {
            state.authUser = null;
            localStorage.removeItem("authUser");
        },
        makeAdmin(state) {
            state.authUser.role = "ROLE_ADMIN";
            localStorage.setItem("authUser", JSON.stringify(state.authUser));
        },
        revokeAdmin(state) {
            state.authUser.role = "ROLE_USER";
            localStorage.setItem("authUser", JSON.stringify(state.authUser));
        }
    },
    // Actions are functions that can be async and trigger the mutation of state data.
    // State data should never be changed directly, instead use actions and mutations.
    actions: {
        setWelcomeMessage(context, message) {
            context.commit('setWelcomeMessage', message);
        },
        authUserLogin(context, authUser) {
            context.commit('setAuthUser', authUser);
        },
        authUserLogout(context) {
            context.commit('deleteAuthUser');
        },
        revokeAdmin(context) {
            context.commit('revokeAdmin');
        }
    },
    // Getters are used to retrieve the data in the store. It can be manipulated to provide correct formatting etc
    // and is the recommended way to retrieve data.
    getters: {
        getWelcomeMessage(state) {
            return state.welcomeMessage;
        },
        getAuthUser(state) {
            return state.authUser;
        }
    }
});