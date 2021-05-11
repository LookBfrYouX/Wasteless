<template>
  <div class="container pt-4">
    <h1>Home Page</h1>
    <p>Beautiful, ain't it?</p>
    <item-card-container />
  </div>
</template>
<script>
import ItemCardContainer from "./containers/ItemCardContainer";
export default {
  name: "home",
  components: {ItemCardContainer}
}
</script>
