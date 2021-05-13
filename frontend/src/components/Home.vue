<template>
  <div class="container pt-4">
    <h1>Home Page</h1>
    <p>Beautiful, ain't it?</p>
    <item-card v-bind:item="inventoryItem"></item-card>
  </div>
</template>
<script>
import ItemCard from "./cards/ItemCard";

export default {
  name: "home",
  components: {ItemCard},
  data() {
    return {
      inventoryItem: {
        id: 1,
        product: {
          id: 2,
          name: 'beans',
          description: "Yummy beans",
        },
        quantity: 5,
        pricePerItem: 4,
        totalPrice: '$20 NZD',
        manufactured: '2021-04-24',
        sellBy: null,
        bestBefore: null,
        expires: '2021-05-29',
      }
    }
  }
}
</script>
