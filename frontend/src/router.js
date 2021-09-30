import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from "@/views/Home.vue"
import Landing from "@/views/Landing.vue"

import Business from "@/routes/Business"
import User from "@/routes/User"
import Marketplace from "@/routes/Marketplace"

import {store} from "@/store"

export default [];

Vue.use(VueRouter);

/**
 * Meta properties:
 *   title: Page title; this is set on navigation
 *   anyone: accessible to everyone
 *   noAuthOnly: if true, only accessible users that are not signed in
 *   adminOnly: only administrators can access this page
 *   requiresBusinessAdmin: route has a `businessId` param and user is acting as that business
 *   requiresNotBusinessAdmin: user must not be acting as a business
 *
 * No auth properties means must be logged in
 */

export const router = new VueRouter({
  hashbang: false,
  mode: "history",
  base: process.env.VUE_APP_BASE_URL,
  routes: [
    ...Business,
    ...User,
    ...Marketplace,
    {
      name: "Landing",
      path: "/",
      component: Landing,
      meta: {
        title: "Welcome to Nutrisave",
        anyone: true
      }
    },
    {
      name: "Home",
      path: "/home",
      component: Home,
      meta: {
        title: "Home | Nutrisave"
      }
    },
    {
      name: "Error",
      path: "/error",
      meta: {
        title: "Error 😢 | Nutrisave",
        anyone: true
      },
      component: () => import("@/views/error/Error.vue")
    },
    {
      name: "Error401",
      path: "/error401",
      meta: {
        title: "401 Not Authorized | Nutrisave",
        anyone: true
      },
      component: () => import("@/views/error/Error401.vue")
    },
    {
      name: "Error403",
      path: "/error403",
      meta: {
        title: "403 Forbidden | Nutrisave",
        anyone: true
      },
      component: () => import("@/views/error/Error403.vue")
    },
    {
      name: "Error404",
      path: "/*",
      meta: {
        title: "Not Found 😢 | Nutrisave",
        anyone: true
      },
      component: () => import("@/views/error/Error404.vue"),
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
  // Navbar is static now so when the user clicks a link, close the navbar so it doesn't cover the new content
  if (document) {
    const hamburger = document.getElementById("hamburger-button");
    const supportedContent = document.getElementById("navbarSupportedContent");
    // Sometimes hamburger has no 'collapsed' class but the navbar is still collapsed, so need to check if 'supportedContent' has the 'show' class too
    if (hamburger && supportedContent &&
        !hamburger.classList.contains("collapsed") &&
        supportedContent.classList.contains("show")
    ) {
      hamburger.click();
    }
  }

  Vue.nextTick(() => {
    let title = "Nutrisave";
    if (typeof to.meta.title == "string") {
      title = to.meta.title;
    } else if (typeof to.meta.title == "function") {
      title = to.meta.title(to);
    }
    document.title = title;
  });
})

/**
 * Sets restrictions for accessing certain pages
 * Redirects to error pages when triggered
 */
router.beforeEach(async (to, from, next) => {
  // Only load page if user passes check

  const business = await store.getters.getActingAs();
  const businessId = parseInt(to.params.businessId, 10);

  const to401 = () => next({name: "Error401"});
  const to403 = () => next({name: "Error403"});

  if (to.meta.anyone) {
    next();
  } else if (to.meta.noAuthOnly) {
    if (store.getters.isSignedIn()) {
      next({name: "Home"});
    } else {
      next();
    }
  }

  // Signed out users: only `anyone` or `noAuthOnly` can allow them through
  else if (!store.getters.isSignedIn()) {
    to401();
  }// Admins can do everything
  else if (store.getters.isAdmin()) {
    next();
  }// If page is admin only and we get to this point, user is not admin
  else if (to.meta.adminOnly) {
    to403();
  }// If route requires user to be business admin
  else if (to.meta.requiresBusinessAdmin) {
    // Yes, it needs to be this explicit. You try it if you think your so smart
    if (business && businessId && business.id === businessId) {
      next();
    } else {
      to403();
    }
  }

  // Preventing businesses from accessing Create.vue
  else if (to.meta.requiresNotBusinessAdmin && business) {
    to403();
  }// If user passes all other checks
  else {
    next();
  }
});
