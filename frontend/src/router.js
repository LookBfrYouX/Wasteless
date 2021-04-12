import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from './components/Login.vue'
import SignUp from './components/SignUp.vue'
import Profile from './components/Profile.vue'
import BusinessProfile from './components/BusinessProfile'
import RegisterBusiness from './components/RegisterBusiness'
import Home from "./components/Home.vue"

export default []

Vue.use(VueRouter)

export const router = new VueRouter({
  hashbang: false,
  mode: "history",
  base: process.env.VUE_APP_BASE_URL,
  routes: [
    {
      name: "home",
      path: "/",
      component: Home
    },
    {
      name: "login",
      path: "/login",
      component: Login
    },
    {
      name: "signUp",
      path: "/signUp",
      component: SignUp
    },
    {
      name: "profile",
      path: "/profile/:userId(\\d+)?",
      component: Profile,
      props: route => ({userId: route.params.userId? parseInt(route.params.userId, 10): NaN })
    },
    {
      name: "search",
      path: "/searchresults/:query(.*)",
      component: () => import('./components/SearchResults.vue'),
      props: route => ({search: route.params.query})
    },
    {
      name: "registerBusiness",
      path: "/registerbusiness",
      component: RegisterBusiness
    },
    {
      name: "businessProfile",
      path: "/businessprofile/:businessId(\\d+)",
      component: BusinessProfile,
      props: route => {
        let businessId = parseInt(route.params.businessId)
        return {businessId: businessId}
      }
    },
    {
      name: "error",
      path: "/error",
      component: () => import("./components/Errors/Error.vue")
    },
    {
      name: "error401",
      path: "/error401",
      component: () => import("./components/Errors/Error401.vue")
    },
    {
      name: "error404",
      path: "/*",
      component: () => import("./components/Errors/Error404.vue"),
      props: route => ({path: route.fullPath})
    }
  ],
})
