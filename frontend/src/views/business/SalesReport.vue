<template>

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

  computed: {
    calculateTotalValue: function () {
      for (transaction in transactionData) {
        totalValue += transaction.amount;
      }
    },
    calculateNumberOfTransactions: function () {
      numberOfTransactions = transactionData.length;
    }
  },

  methods: {

  }
}
</script>

<style>

</style>
