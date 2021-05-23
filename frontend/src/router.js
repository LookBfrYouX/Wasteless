import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from "./components/Home.vue"
import Landing from "./components/Landing.vue"
import {store} from "./store"

export default [];

Vue.use(VueRouter);

export const router = new VueRouter({
  hashbang: false,
  mode: "history",
  base: process.env.VUE_APP_BASE_URL,
  routes: [
    {
      name: "landing",
      path: "/",
      component: Landing
    },
    {
      name: "home",
      path: "/home",
      component: Home,
      meta: {
        title: "Home | Wasteless"
      }
    },
    {
      name: "login",
      path: "/login",
      component: () => import("./components/Login.vue"),
      meta: {
        title: "Login | Wasteless",
      }
    },
    {
      name: "signUp",
      path: "/signUp",
      component: () => import("./components/SignUp.vue"),
      meta: {
        title: "Sign Up | Wasteless",
      }
    },
    {
      name: "profile",
      path: "/profile/:userId(\\d+)?",
      component: () => import("./components/Profile.vue"),
      props: route => {
        let userId = route.params.userId ? parseInt(route.params.userId, 10)
            : NaN;
        // Using \d so parseInt should never fail
        if (isNaN(userId)
            && store.getters.isLoggedIn()) {
          userId = store.getters.getAuthUser().id;
        }
        return {userId};
      },
      meta: {
        requiresSignIn: true
      }
    },
    {
      name: "search",
      path: "/searchresults/:query(.*)",
      component: () => import('./components/SearchResults.vue'),
      meta: {
        title: route => `'${route.params.query}' | Search`,
        requiresSignIn: true
      },
      props: route => ({search: route.params.query}),
    },
    {
      name: "registerBusiness",
      path: "/registerbusiness",
      meta: {
        title: "Create Business | Wasteless",
        requiresSignIn: true,
        requiresNotBusinessAdmin: true
      },
      component: () => import("./components/RegisterBusiness.vue"),
    },
    {
      name: "createProduct",
      path: "/business/:businessId(\\d+)/createproduct",
      component: () => import("./components/CreateProduct.vue"),
      props: route => ({businessId: parseInt(route.params.businessId, 10)}),
      meta: {
        title: "Create Product | Wasteless",
        requiresSignIn: true,
        requiresBusinessAdmin: true
      }
    },
    {
      name: "productCatalogue",
      path: "/business/:businessId(\\d+)/catalogue",
      component: () => import("./components/ProductCatalogue.vue"),
      props: route => ({businessId: parseInt(route.params.businessId, 10)}),
      meta: {
        title: "Product Catalogue | Wasteless",
        requiresSignIn: true,
        requiresBusinessAdmin: true
      }
    },
    {
      name: "productDetail",
      path: "/business/:businessId(\\d+)/product/:productId(\\d+)",
      component: () => import("./components/ProductDetail.vue"),
      props: route => ({
        productId: parseInt(route.params.productId, 10),
        businessId: parseInt(route.params.businessId, 10)
      }),
      meta: {
        requiresSignIn: true,
        requiresBusinessAdmin: true
      }
    },
    {
      name: "editProductImages",
      path: "/business/:businessId(\\d+)/products/:productId(\\d+)/images",
      component: () => import("./components/EditProductImages.vue"),
      props: route => ({
        productId: parseInt(route.params.productId, 10),
        businessId: parseInt(route.params.businessId, 10)
      }),
      meta: {
        requiresSignIn: true,
        requiresBusinessAdmin: true
      }
    },
    {
      name: "businessProfile",
      path: "/business/:businessId(\\d+)",
      meta: {
        title: "Business Profile | Wasteless",
        requiresSignIn: true
      },
      component: () => import("./components/BusinessProfile.vue"),
      props: route => {
        // If business ID is optional, user can switch back to acting as user in navbar, causing page to fail
        let businessId = parseInt(route.params.businessId, 10);
        const showBackButton = route.params.showBackButton; // Optional
        return {businessId, showBackButton};
      },
    },
    {
      name: "inventoryItemEntry",
      path: "/business/:businessId(\\d+)/inventory/add",
      meta: {
        title: "Add to inventory | Wasteless",
        requiresSignIn: true,
        requiresBusinessAdmin: true
      },
      component: () => import("./components/InventoryItemEntry.vue"),
      props: route => ({businessId: parseInt(route.params.businessId, 10)})
    },
    {
      name: "businessListings",
      path: "/business/:businessId(\\d+)/listings",
      meta: {
        title: "Business Listings | Wasteless",
        requiresSignIn: true
      },
      component: () => import("./views/BusinessListings.vue"),
      props: route => ({ businessId: parseInt(route.params.businessId, 10)})
    },
    {
      name: "businessInventory",
      path: "/business/:businessId(\\d+)/inventory",
      meta: {
        title: "Business Inventory | Wasteless",
        requiresSignIn: true
      },
      component: () => import("./views/BusinessInventory"),
      props: route => ({businessId: parseInt(route.params.businessId, 10)})
    },
      // Router to create listing component. TODO remove later
    {
      name: "createListing",
      path: "/business/:businessId(\\d+)/listings/add",
      meta: {
        title: "Create New Listing | Wasteless",
        requiresBusinessAdmin: true
      },
      component: () => import("./components/CreateListing"),
      props: route => ({ businessId: parseInt(route.params.businessId, 10)})
    },
    {
      name: "marketplace",
      path: "/marketplace",
      meta: {
        title: "Marketplace | Wasteless",
        requiresNotBusinessAdmin: true,
        requiresSignIn: true
      },
      component: () => import("./views/Marketplace")
    },
    {
      name: "error",
      path: "/error",
      meta: {
        title: "Error 😢 | Wasteless",
      },
      component: () => import("./components/Errors/Error.vue")
    },
    {
      name: "error401",
      path: "/error401",
      meta: {
        title: "401 Not Authorized | Wasteless",
      },
      component: () => import("./components/Errors/Error401.vue")
    },
    {
      name: "error403",
      path: "/error403",
      meta: {
        title: "403 Forbidden | Wasteless",
      },
      component: () => import("./components/Errors/Error403.vue")
    },
    {
      name: "error404",
      path: "/*",
      meta: {
        title: "Not Found 😢 | Wasteless",
      },
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
  
  // Navbar is static now so when the user clicks a link, close the navbar so it doesn't cover the new content
  if (document) {
    const hamburger = document.getElementById("hamburger-button");
    if (hamburger && !hamburger.classList.contains("collapsed")) {
      hamburger.click();
    }
  }
  Vue.nextTick(() => {
    let title = "Wasteless";
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

  const business = await store.getters.getActingAs()
  const businessId = await parseInt(to.params.businessId, 10);

  // Must be logged in
  if (to.meta.requiresSignIn && !store.getters.isLoggedIn()) {
    next({name: 'error401' });
  }
  // Admins can do everything
  else if (store.getters.isAdmin()) {
    next()
  }
  // If route requires user to be business admin
  else if (to.meta.requiresBusinessAdmin) {
    // Yes, it needs to be this explicit. You try it if you think your so smart
    if (business && businessId && business.id === businessId) {
      next();
    } else {
      next({name: "error403"});
    }
  }
  // Preventing businesses from accessing RegisterBusiness.vue
  else if (to.meta.requiresNotBusinessAdmin && business) {
    next({name: "error403"});
  }
  // If user passes all other checks
  else {
    next();
  }
});