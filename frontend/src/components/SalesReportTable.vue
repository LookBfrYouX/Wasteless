<template>
  <v-data-table>

  </v-data-table>
</template>

<script>
export default {
props:{
  granularity:"day",
  dateRange:{
    minDate: "",
    maxDate: ""
  }
},
data:{
  totalTransactions:0,
  totalSales:0,

}

}
</script>

<style>

</style>
