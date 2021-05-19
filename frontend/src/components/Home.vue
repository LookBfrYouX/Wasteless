<template>
  <div class="container pt-4">
    <h1>Home Page</h1>
    <p>Beautiful, ain't it?</p>
    <item-card
        v-bind:item="inventoryItem"
        :id-prefix="'item1-'"
        :business-id="1"
        :currency="currency"></item-card>
    <item-card
        v-bind:item="inventoryItem"
        :id-prefix="'item2-'"
        :business-id="1"
        :currency="currency"></item-card>
    <item-card
        v-bind:item="inventoryItem"
        :id-prefix="'item3-'"
        :business-id="1"
        :currency="currency"></item-card>
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
          id: 3,
          name: 'beans long data long data long data long data long data long data long data long data long data long data long data long data ',
          description: "Yummy beans long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data long data ",
        },
        quantity: 5,
        pricePerItem: 4,
        totalPrice: 18.89,
        manufactured: '2021-04-24',
        sellBy: '2021-05-26',
        bestBefore: '2021-05-27',
        expires: '2021-05-29',
        description: "This bean is leftover from International Bean Festival"
      },
      currency: {
        symbol: "$",
        name: "New Zealand Dollar",
        code: "NZD"
      }
    }
  }
}
</script>
