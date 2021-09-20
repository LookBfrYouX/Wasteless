<template>
  <div>
    <div v-if="transactionInformation.length != 0">
      <v-data-table
        :headers="headers"
        :items="transactionInformation"
        :items-per-page="5">
      </v-data-table>
    </div>
    <div>
      No Transactions for this business
    </div>
  </div>
</template>

<script>
export default {
  props: {
  granularity: "",
  transactionInformation: {}
  },
  data() {
    return {
      headers: [
      {
        text: granularity,
        value: "date"
      },
      {
        text: "Transactions",
        value: "numOfTransactions"
      },
      {
        text: "Sales",
        value: "transactionValue"
      }]
    }
  }
}
</script>

<style>

</style>
