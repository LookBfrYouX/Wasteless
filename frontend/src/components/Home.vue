<template>
  <div class="container pt-4">
    <h1>Home Page</h1>
    <p>Beautiful, ain't it?</p>
    <item-card
        v-bind:item="inventoryItem"
        :id-prefix="'item1-'"></item-card>
    <item-card
        v-bind:item="inventoryItem"
        :id-prefix="'item2-'"></item-card>
    <item-card
        v-bind:item="inventoryItem"
        :id-prefix="'item3-'"></item-card>
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
          name: 'beans long data long data long data long data long data long data long data long data long data long data long data long data ',
          description: "Yummy beans long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data ",
        },
        quantity: 5,
        pricePerItem: 4,
        totalPrice: '$20 NZD',
        manufactured: '2021-04-24',
        sellBy: '2021-05-26',
        bestBefore: '2021-05-27',
        expires: '2021-05-29',
        description: "This bean is leftover from International Bean Festival"
      }
    }
  }
}
</script>
