import {store} from "@/store";

export default [{
  name: "BusinessCreate",
  path: "/business/create",
  meta: {
    title: "Create Business | Wasteless",
    requiresNotBusinessAdmin: true
  },
  component: () => import("@/views/business/Create.vue"),
  props: () => {
    const user = store.getters.getAuthUser();
    const userId = user ? user.id : NaN; // If not logged in, should be redirected to a different page, so user id should never be NaN anyway
    return {userId};
  }
},
  {
    name: "BusinessCreateAdmin",
    // GAA can create card acting as any user
    path: "/business/admin/:userId(\\d+)/create",
    meta: {
      title: "Create Card | Wasteless",
      /* Only accessible if GAA */
      adminOnly: true,
    },
    component: () => import("@/views/business/Create.vue"),
    props: route => ({userId: parseInt(route.params.userId, 10)})
  },
  {
    name: "BusinessDetail",
    path: "/business/:businessId(\\d+)",
    meta: {
      title: "Business Profile | Wasteless",
    },
    component: () => import("@/views/business/Detail.vue"),
    props: route => {
      // If business ID is optional, user can switch back to acting as user in navbar, causing page to fail
      let businessId = parseInt(route.params.businessId, 10);
      const showBackButton = route.params.showBackButton; // Optional
      return {businessId, showBackButton};
    },
  },
  {
    name: "BusinessAdminEdit",
    path: "/business/:businessId(\\d+)/admin",
    component: () => import("@/views/business/AdminEdit.vue"),
    props: route => ({businessId: parseInt(route.params.businessId, 10)}),
    meta: {
      title: "Edit Administrators | Wasteless",
      requiresBusinessAdmin: true
    }
  },
  {
    name: "BusinessProductCreate",
    path: "/business/:businessId(\\d+)/product/create",
    component: () => import("@/views/business/ProductCreate.vue"),
    props: route => ({businessId: parseInt(route.params.businessId, 10)}),
    meta: {
      title: "Create Product | Wasteless",
      requiresBusinessAdmin: true
    }
  },
  {
    name: "BusinessProducts",
    path: "/business/:businessId(\\d+)/product",
    component: () => import("@/views/business/Products.vue"),
    props: route => ({businessId: parseInt(route.params.businessId, 10)}),
    meta: {
      title: "Product Catalogue | Wasteless",
      requiresBusinessAdmin: true
    }
  },
  {
    name: "BusinessProductDetail",
    path: "/business/:businessId(\\d+)/product/:productId(\\d+)",
    component: () => import("@/views/business/ProductDetail.vue"),
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
    component: () => import("@/views/business/ProductImagesEdit.vue"),
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
    component: () => import("@/views/business/InventoryCreate.vue"),
    props: route => ({businessId: parseInt(route.params.businessId, 10)})
  },
  {
    name: "BusinessInventory",
    path: "/business/:businessId(\\d+)/inventory",
    meta: {
      title: "Business Inventory | Wasteless",
      requiresBusinessAdmin: true
    },
    component: () => import("@/views/business/Inventory"),
    props: route => ({businessId: parseInt(route.params.businessId, 10)})
  },

  {
    name: "BusinessListingCreate",
    path: "/business/:businessId(\\d+)/listing/create",
    meta: {
      title: "Create New Listing | Wasteless",
      requiresBusinessAdmin: true
    },
    component: () => import("@/views/business/ListingCreate"),
    props: route => ({businessId: parseInt(route.params.businessId, 10)})
  },
  {
    name: "BusinessListings",
    path: "/business/:businessId(\\d+)/listing",
    meta: {
      title: "Business Listings | Wasteless",
    },
    component: () => import("@/views/business/BusinessListings.vue"),
    props: route => ({businessId: parseInt(route.params.businessId, 10)})
  },
  {
    name: "Listings",
    path: "/listings",
    meta: {
      title: "Listings | Wasteless",
      requiresNotBusinessAdmin: true,
    },
    component: () => import("@/views/business/Listings")
  },
  {
    name: "BusinessListingDetail",
    path: "/business/:businessId(\\d+)/listings/:listingId(\\d+)",
    component: () => import("@/views/business/ListingDetail.vue"),
    props: route => ({
      listingId: parseInt(route.params.listingId, 10),
      businessId: parseInt(route.params.businessId, 10)
    }),
    meta: {
      title: "Business Listing | Wasteless"
    }
  }
];