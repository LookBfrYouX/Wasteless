import {store} from "@/store";

export default [
  {
    name: "Marketplace",
    path: "/marketplace",
    meta: {
      title: "Marketplace | Wasteless",
      requiresNotBusinessAdmin: true,
    },
    component: () => import("@/views/marketplace/Marketplace")
  },
  {
    name: "MarketplaceCardCreate",
    path: "/marketplace/create",
    meta: {
      title: "Create Card | Wasteless",
      requiresNotBusinessAdmin: true,
    },
    component: () => import("@/views/marketplace/CardCreate"),
    props: () => {
      const user = store.getters.getAuthUser();
      const userId = user ? user.id : NaN; // If not logged in, should be redirected to a different page, so user id should never be NaN anyway
      return {userId};
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
    component: () => import("@/views/marketplace/CardCreate"),
    props: route => ({userId: parseInt(route.params.userId, 10)})
  }
]
