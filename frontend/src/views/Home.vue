<template>
  <div class="container m-3">
    <div class="row">
      <div class="col-md-3 card m-1">
        <h5 class="text-muted mt-3">Quick Links</h5>
        <div class="profile-buttons d-flex flex-wrap">
          <router-link
            v-if="isActingAsUser"
            :to="{ name: 'Listings' }"
            class="btn btn-white-bg-primary m-1 d-flex"
            >
            <span class="material-icons mr-1">shopping_cart</span>
            Shop now
          </router-link>
          <router-link
              v-if="isActingAsUser"
              :to="{ name: 'Marketplace' }"
              class="btn btn-white-bg-primary m-1 d-flex"
          >
            <span class="material-icons mr-1">store</span>
            Marketplace
          </router-link>
        </div>
      </div>
      <div class="col-md-8 card m-1">
        <h5 class="text-muted mt-3">Home</h5>
        <p>Beautiful, ain't it?</p>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: "home",
  computed: {
    /**
     * Checks if acting as a user
     * @returns true if signed in and acting as user
     */
    isActingAsUser() {
      return !this.$stateStore.getters.isActingAsBusiness();
    }
  }
}
</script>
