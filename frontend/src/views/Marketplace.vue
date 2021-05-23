<template>
  <div class="container">
    <div class="row">
      <h2 class="col-12">Community Marketplace</h2>
    </div>
    <div class="row">
      <div class="col-12">
        <router-link
          class="btn btn-primary"
          v-bind:to="{ name: 'createCard' }"
        >
          Create a Card
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  components: {},
  props: {},
  data() {
    return {};
  },
  beforeMount: async function () {
  },
  methods: {},
  watch: {}
}
</script>
