import {store} from "@/store";

export default [
  {
    name: "SignIn",
    path: "/signin",
    component: () => import("@/views/user/SignIn.vue"),
    meta: {
      title: "Sign In | Nutrisave",
      noAuthOnly: true
    }
  },
  {
    name: "UserCreate",
    path: "/signup",
    component: () => import("@/views/user/Create.vue"),
    meta: {
      title: "Sign Up | Nutrisave",
      noAuthOnly: true
    }
  },
  {
    name: "UserDetail",
    path: "/profile/:userId(\\d+)?",
    component: () => import("@/views/user/Detail.vue"),
    props: route => {
      let userId = route.params.userId ? parseInt(route.params.userId, 10)
          : NaN;
      // Using \d so parseInt should never fail
      if (isNaN(userId) && store.getters.isSignedIn()) {
        userId = store.getters.getAuthUser().id;
      }
      return {userId};
    },
    meta: {
      title: "Profile | Nutrisave"
    }
  },
  {
    name: "Search",
    path: "/search/:query(.*)",
    component: () => import("@/views/user/Search.vue"),
    meta: {
      title: route => `'${route.params.query}' | Search`,
    },
    props: route => ({search: route.params.query}),
  }
];
