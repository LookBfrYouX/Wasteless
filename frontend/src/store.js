import Vue from 'vue';

const state = new Vue.observable({
    authUser: JSON.parse(localStorage.getItem("authUser")),
    welcomeMessage: "HERE WE GOOO",
});

export const store = {
    getters: {
        getAuthUser: () => {
            return state.authUser;
        },
        getWelcomeMessage: () => {
            return state.welcomeMessage;
        },
    },
    actions: {
        setWelcomeMessage: (message) => {
            state.welcomeMessage = message;
        },
        setAuthUser: (authUser) => {
            state.authUser = authUser;
            localStorage.setItem("authUser", JSON.stringify(authUser));
            localStorage.setItem("userId", state.authUser.id);
        },
        deleteAuthUser: () => {
            state.authUser = null;
            localStorage.removeItem("authUser");
            localStorage.removeItem("userId");
        },
        makeAdmin: () => {
            state.authUser.role = "ROLE_ADMIN";
            localStorage.setItem("authUser", JSON.stringify(state.authUser));
        },
        revokeAdmin: ()=> {
            state.authUser.role = "ROLE_USER";
            localStorage.setItem("authUser", JSON.stringify(state.authUser));
        }
    },
};
