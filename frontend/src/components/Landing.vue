<!-- home page will include inventory / product catalogue later. -->
<template>
  <div class="container pt-4">
    <h1>Landing Page</h1>
    <p v-if="$stateStore.getters.isLoggedIn()">Welcome!</p>
    <p v-else>Why aren't you signed in?</p>
  </div>
</template>
<script>
export default {
  name: "landingPage"
}
</script>
