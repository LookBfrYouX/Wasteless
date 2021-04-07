import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
    state () {
        return {
            authUser: JSON.parse(localStorage.getItem("authUser")),
            welcomeMessage: "HERE WE GOOO",
        };
    }
});