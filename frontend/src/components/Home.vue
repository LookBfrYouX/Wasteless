<template>
  <div class="container pt-4">
    <h1>Home Page</h1>
    <p>Beautiful, ain't it?</p>
    <inventory-item-entry business-id="2"/>
  </div>
</template>
<script>
import InventoryItemEntry from "@/components/InventoryItemEntry";
export default {
  name: "home",
  components: {InventoryItemEntry}
}
</script>
