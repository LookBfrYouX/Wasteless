<template>
  <div class="container m-3">
    <div class="row">
      <div class="col-md-3 card m-1">
        <h5 class="text-muted mt-3">Quick Links</h5>
        <div class="profile-buttons d-flex flex-wrap">
          <button class="btn btn-white-bg-primary m-1 d-flex" v-on:click="goToMarketplace">
            <span class="material-icons mr-1">store</span>
            Marketplace
          </button>
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
  methods: {
    goToMarketplace() {
      this.$router.push({name: 'marketplace'});
    }
  }
}
</script>
