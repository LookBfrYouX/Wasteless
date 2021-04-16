import Vue from 'vue'
import VueRouter from 'vue-router'

import Home from "./components/Home.vue"

export default [];

Vue.use(VueRouter);

export const router = new VueRouter({
  hashbang: false,
  mode: "history",
  base: process.env.VUE_APP_BASE_URL,
  routes: [
    {
      meta: { title: 'Home' },
      name: "home",
      path: "/",
      component: Home,
      meta: {title: "Home | Wasteless"}
    },
    {
      meta: { title: 'Login' },
      name: "login",
      path: "/login",
      component: () => import("./components/Login.vue"),
      meta: {title: "Login | Wasteless"}
    },
    {
      meta: { title: 'Sign Up' },
      name: "signUp",
      path: "/signUp",
      component: () => import("./components/SignUp.vue"),
      meta: {title: "Sign Up | Wasteless"}
    },
    {
      meta: { title: 'Dashboard' },
      name: "profile",
      path: "/profile/:userId(\\d+)?",
      component: () => import("./components/Profile.vue"),
      meta: {title: "Profile | Wasteless"},
      props: route => {
        let userId = route.params.userId;
        if (userId == null) {
          const loggedInId = parseInt(window.localStorage.getItem("userId"));
          userId = loggedInId; // may be NaN
        } else {
          userId = parseInt(userId, 10); // Using \d so parseInt should always work
        }

        return {userId};
      }
    },
    {
      meta: { title: 'Search' },
      name: "search",
      path: "/searchresults/:query(.*)",
      component: () => import('./components/SearchResults.vue'),
      meta: {title: route => `'${route.params.query}' | Search`},
      props: route => ({search: route.params.query}),
    },
    {
      meta: { title: 'Register Business' },
      name: "registerBusiness",
      path: "/registerbusiness",
      meta: {title: "Create Business | Wasteless"},
      component: () => import("./components/RegisterBusiness.vue"),
    },
    {
      meta: { title: 'Business Dashboard' },
      name: "businessProfile",
      path: "/businessprofile/:businessId(\\d+)",
      meta: {title: "Business Profile | Wasteless"},
      component: () => import("./components/BusinessProfile.vue"),
      props: route => {
        let businessId = parseInt(route.params.businessId)
        return {businessId: businessId}
      }
    },
    {
      name: "error",
      path: "/error",
      meta: {title: "Error 😢 | Wasteless"},
      component: () => import("./components/Errors/Error.vue")
    },
    {
      name: "error401",
      path: "/error401",
      meta: {title: "401 Not Authorized | Wasteless"},
      component: () => import("./components/Errors/Error401.vue")
    },
    {
      name: "error404",
      path: "/*",
      meta: {title: "Not Found 😢 | Wasteless"},
      component: () => import("./components/Errors/Error404.vue"),
      props: route => ({path: route.fullPath})
    }
  ],
});

/**
 * Sets page title on navigate
 * Uses `route.meta.title`; either a string or function that, given the route, returns a string
 * See https://github.com/vuejs/vue-router/issues/914#issuecomment-384477609
 * To set title after navigate, use the `setDocumentTitle` prop passed to the router component in `App.vue`
 */
router.afterEach((to) => {
  Vue.nextTick(() => {
    let title = "Wasteless";
    if (typeof to.meta.title == "string") title = to.meta.title;
    else if (typeof to.meta.title == "function") title = to.meta.title(to);
    document.title = title;
  });
})
