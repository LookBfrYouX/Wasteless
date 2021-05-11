<template>
  <!-- This component adds a container in which a list of item cards can be displayed -->
  <!-- This component stores a list of data that can be displayed by child components -->
  <!-- This component can sort the list according to the event emitted by the sort component -->
<div class="container">
  <div class="results-wrapper p-3 col-12 mt-5">
    <!-- Add the sorting and filtering component - update event returns the sorted data -->
    <item-sorter :headings="sortList" :items="itemList" @update="itemList = $event"></item-sorter>
    <!-- Create a list of card components from the list of items -->
    <ul class="list-unstyled list-group">
      <li class="list-group-item card" v-for="(item, index) in itemList" v-bind:key="index">
        <item-card v-bind:item="itemList[index]" />
      </li>
    </ul>
  </div>
</div>
</template>

<script>
import ItemCard from "../cards/ItemCard";
import ItemSorter from "../sorters/itemSorter";
export default {
  name: "ItemCardContainer",
  components: {ItemSorter, ItemCard},
  data: function () {
    return {
      // TODO retrieve itemList from api and replace hardcoded one
      itemList: [{
        title: "Test title 1",
        imageUrl: "/someimage.png",
        description: "A description for the test title 1",
        totalPrice: 20.00,
        quantity: 5,
        pricePerUnit: 4.00,
        expiryDate: "05/06/2021",
        manufacturedDate: "18/05/2021",
        sellByDate: "03/06/2021",
        bestBeforeDate: "04/06/2021",
      },
        {
          title: "Test title 2",
          imageUrl: "/someimage.png",
          description: "A description for the test title 1",
          totalPrice: 22.00,
          quantity: 2,
          pricePerUnit: 11.00,
          expiryDate: "05/06/2021",
          manufacturedDate: "18/05/2021",
          sellByDate: "03/06/2021",
          bestBeforeDate: "04/06/2021",
        }],
      sortList: ["title", "totalPrice", "expiryDate"],
    }
  },
  methods: {

  }
}
</script>

<style scoped>
.results-wrapper {
  background: #fff;
}
</style>