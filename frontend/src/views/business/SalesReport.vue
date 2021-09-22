<template>
  <div class="w-100">
    <div class="row">
      <div class="col">
        Date Range: <v-btn>Select Date Range</v-btn>
      </div>
      <div class="col">
        Group By: <v-select v-model="granularity" :items="items" label="Group By" solo/>
      </div>
    </div>
    <v-divider></v-divider>
    <div class="row">

    </div>
  </div>

</template>

<script>
export default {
  data() {
    return {
      minDate:"",
      maxDate:"",
      granularity:"",
      totalValue:0,
      numberOfTransactions:0,
      transactionData:{},
      items: ["Day", "Month","Year"]
    }
  },
  methods: {

  }
};
</script>

<style>

</style>
