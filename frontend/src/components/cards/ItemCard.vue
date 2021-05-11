<template>
  <!-- this component produces the item card component from the given props -->
  <div class="container card">
    <div class="row">
      <div class="col-12">
        <h1>{{ item.title }}</h1>
      </div>
    </div>
    <div class="row">
      <div class="col-3">
        <img class="img-fluid" src="./../../../assets/images/default-product-thumbnail.svg" alt="Image" />
        <h3 class="w100">{{ item.totalPrice }}</h3>
        <p>{{ item.quantity }} units @ ${{ item.pricePerUnit }} each</p>
      </div>
      <div class="col-9">
        <p>{{ item.description }}</p>
      </div>
    </div>
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