import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from "./views/Home.vue"
import Landing from "./views/Landing.vue"
import {store} from "./store"

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
 */

export const router = new VueRouter({
  hashbang: false,
  mode: "history",
  base: process.env.VUE_APP_BASE_URL,
  routes: [
    {
      name: "Landing",
      path: "/",
      component: Landing,
      meta: {
        title: "Welcome to Wasteless",
        noAuthOnly: true
      }
    },
    {
      name: "Home",
      path: "/home",
      component: Home,
      meta: {
        title: "Home | Wasteless"
      }
    },
    {
      name: "SignIn",
      path: "/signin",
      component: () => import("./views/SignIn.vue"),
      meta: {
        title: "Sign In | Wasteless",
        noAuthOnly: true
      }
    },
    {
      name: "UserCreate",
      path: "/signup",
      component: () => import("./views/UserCreate.vue"),
      meta: {
        title: "Sign Up | Wasteless",
        noAuthOnly: true
      }
    },
    {
      name: "UserDetail",
      path: "/profile/:userId(\\d+)?",
      component: () => import("./views/UserDetail.vue"),
      props: route => {
        let userId = route.params.userId ? parseInt(route.params.userId, 10): NaN;
        // Using \d so parseInt should never fail
        if (isNaN(userId) && store.getters.isSignedIn()) {
          userId = store.getters.getAuthUser().id;
        }
        return { userId };
      },
      meta: {
        title: "Profile | Wasteless"
      }
    },
    {
      name: "Search",
      path: "/search/:query(.*)",
      component: () => import('./views/Search.vue'),
      meta: {
        title: route => `'${route.params.query}' | Search`,
      },
      props: route => ({search: route.params.query}),
    },



    {
      name: "BusinessCreate",
      path: "/business/create",
      meta: {
        title: "Create Business | Wasteless",
        requiresNotBusinessAdmin: true
      },
      component: () => import("./views/business/Create.vue"),
    },
    {
      name: "BusinessDetail",
      path: "/business/:businessId(\\d+)",
      meta: {
        title: "Business Profile | Wasteless",
      },
      component: () => import("./views/business/Detail.vue"),
      props: route => {
        // If business ID is optional, user can switch back to acting as user in navbar, causing page to fail
        let businessId = parseInt(route.params.businessId, 10);
        const showBackButton = route.params.showBackButton; // Optional
        return { businessId, showBackButton };
      },
    },



    {
      name: "BusinessProductCreate",
      path: "/business/:businessId(\\d+)/product/create",
      component: () => import("./views/business/ProductCreate.vue"),
      props: route => ({ businessId: parseInt(route.params.businessId, 10) }),
      meta: {
        title: "Create Product | Wasteless",
        requiresBusinessAdmin: true
      }
    },
    {
      name: "BusinessProducts",
      path: "/business/:businessId(\\d+)/product",
      component: () => import("./views/business/Products.vue"),
      props: route => ({ businessId: parseInt(route.params.businessId, 10) }),
      meta: {
        title: "Product Catalogue | Wasteless",
        requiresBusinessAdmin: true
      }
    },
    {
      name: "BusinessProductDetail",
      path: "/business/:businessId(\\d+)/product/:productId(\\d+)",
      component: () => import("./views/business/ProductDetail.vue"),
      props: route => ({
        productId: parseInt(route.params.productId, 10),
        businessId: parseInt(route.params.businessId, 10)
      }),
      meta: {
        title: "Product Detail | Wasteless",
        requiresBusinessAdmin: true
      }
    },
    {
      name: "BusinessProductImagesEdit",
      path: "/business/:businessId(\\d+)/product/:productId(\\d+)/images",
      component: () => import("./views/business/ProductImagesEdit.vue"),
      props: route => ({
        productId: parseInt(route.params.productId, 10),
        businessId: parseInt(route.params.businessId, 10)
      }),
      meta: {
        title: "Edit Product Images | Wasteless",
        requiresBusinessAdmin: true
      }
    },



    {
      name: "BusinessInventoryCreate",
      path: "/business/:businessId(\\d+)/inventory/create",
      meta: {
        title: "Add to Inventory | Wasteless",
        requiresBusinessAdmin: true
      },
      component: () => import("./views/business/InventoryCreate.vue"),
      props: route => ({businessId: parseInt(route.params.businessId, 10) })
    },
    {
      name: "BusinessInventory",
      path: "/business/:businessId(\\d+)/inventory",
      meta: {
        title: "Business Inventory | Wasteless",
        requiresBusinessAdmin: true
      },
      component: () => import("./views/business/Inventory"),
      props: route => ({ businessId: parseInt(route.params.businessId, 10) })
    },



    {
      name: "BusinessListingCreate",
      path: "/business/:businessId(\\d+)/listing/create",
      meta: {
        title: "Create New Listing | Wasteless",
        requiresBusinessAdmin: true
      },
      component: () => import("./views/business/ListingCreate"),
      props: route => ({ businessId: parseInt(route.params.businessId, 10) })
    },
    {
      name: "BusinessListings",
      path: "/business/:businessId(\\d+)/listing",
      meta: {
        title: "Business Listings | Wasteless",
      },
      component: () => import("./views/business/Listings.vue"),
      props: route => ({ businessId: parseInt(route.params.businessId, 10) })
    },
    {
      name: "BusinessListingDetail",
      path: "/business/:businessId(\\d+)/listings/:listingId(\\d+)",
      component: () => import("./views/business/ListingDetail.vue"),
      props: route => ({
        listingId: parseInt(route.params.listingId, 10),
        businessId: parseInt(route.params.businessId, 10)
      }),
      meta: {
        title: "Business Listing | Wasteless"
      }
    },




    {
      name: "Marketplace",
      path: "/marketplace",
      meta: {
        title: "Marketplace | Wasteless",
        requiresNotBusinessAdmin: true,
      },
      component: () => import("./views/marketplace/Marketplace")
    },
    {
      name: "MarketplaceCardCreate",
      path: "/marketplace/create",
      meta: {
        title: "Create Card | Wasteless",
        requiresNotBusinessAdmin: true,
      },
      component: () => import("./views/marketplace/CardCreate"),
      props: () => {
        const user = store.getters.getAuthUser();
        const userId = user? user.id: NaN; // If not logged in, should be redirected to a different page, so user id should never be NaN anyway
        return { userId };
      }
    },
    {
      name: "MarketplaceCardCreateAdmin",
      // GAA can create card acting as any user
      path: "/marketplace/admin/:userId(\\d+)/create",
      meta: {
        title: "Create Card | Wasteless",
        /* Only accessible if GAA */
        adminOnly: true,
      },
      component: () => import("./views/marketplace/CardCreate"),
      props: route => ({ userId: parseInt(route.params.userId, 10) })
    },



    {
      name: "Error",
      path: "/error",
      meta: {
        title: "Error 😢 | Wasteless",
        anyone: true
      },
      component: () => import("./views/errors/Error.vue")
    },
    {
      name: "Error401",
      path: "/error401",
      meta: {
        title: "401 Not Authorized | Wasteless",
        anyone: true
      },
      component: () => import("./views/errors/Error401.vue")
    },
    {
      name: "Error403",
      path: "/error403",
      meta: {
        title: "403 Forbidden | Wasteless",
        anyone: true
      },
      component: () => import("./views/errors/Error403.vue")
    },
    {
      name: "Error404",
      path: "/*",
      meta: {
        title: "Not Found 😢 | Wasteless",
        anyone: true
      },
      component: () => import("./views/errors/Error404.vue"),
      props: route => ({ path: route.fullPath })
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

  const business = await store.getters.getActingAs();
  const businessId = parseInt(to.params.businessId, 10);

  const to401 = () => next({ name: "Error401" });
  const to403 = () => next({ name: "Error403" });

  if (to.meta.anyone) next();

  if (to.meta.noAuthOnly) {
    if (store.getters.isSignedIn()) next();
    else next({ name: "Home" });
  }

  // Admins can do everything
  else if (store.getters.isAdmin()) next();

  // If page is admin only and we get to this point, user is not admin
  else if (to.meta.adminOnly) to403();

  // If route requires user to be business admin
  else if (to.meta.requiresBusinessAdmin) {
    // Yes, it needs to be this explicit. You try it if you think your so smart
    if (business && businessId && business.id === businessId) next();
    else to403();
  }

  // Preventing businesses from accessing Create.vue
  else if (to.meta.requiresNotBusinessAdmin && business) to403();
  
  // If user passes all other checks
  else next();
});
