<template>
  <!-- this component produces the item card component from the given props -->
<div>
  <h1>{{ item.title }}</h1>
  <h3>${{ item.totalPrice }} NZD</h3>
  <p>{{ item.description }}</p>
</div>
</template>

<script>
export default {
  name: "ItemCard",
  props: {
    item: Object,
  }
}
</script>

<style scoped>

</style>