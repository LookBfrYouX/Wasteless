<template>
  <div>
    <div>
      <v-button>Select Date Range</v-button>
      <v-button/>
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
      transactionData:{}
    }
  },
  methods: {

  }
}
</script>

<style>

</style>
