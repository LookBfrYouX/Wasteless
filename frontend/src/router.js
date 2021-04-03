import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from './components/Login.vue'
import SignUp from './components/SignUp.vue'
import Profile from './components/Profile.vue'

export default [
]

Vue.use(VueRouter)

export const router = new VueRouter({
    hashbang: false,
    mode: "history",
    base: process.env.VUE_APP_BASE_URL,
    routes: [
        {
            name: "login",
            path: "/login",
            component: Login
        },
        {
            name: "signup",
            path: "/signUp",
            component: SignUp
        },
        {
            name: "profile",
            path: "/profile/:userId(\\d+)?",
            component: Profile,
            props: route => {
                let userId = route.params.userId;
                if (userId == null) {
                    const loggedInId = parseInt(window.localStorage.getItem("userId"));
                    userId = loggedInId; // may be NaN
                } else {
                    userId = parseInt(userId, 10); // Using \d so parseInt should always work
                }

                return { userId };
            }
        },
        {
            name: "search",
            path: "/searchresults/:query(.*)",
            component: () => import('./components/SearchResults.vue'),
            props: route => ({ search: route.params.query })
        },
    ],
})
