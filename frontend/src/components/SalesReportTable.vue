<template>
  <div>
    <div v-if="transactionInformation.length != 0">
      <v-data-table
        :headers="headers"
        :items="transactionInformation"
        :items-per-page="5">
      </v-data-table>
    </div>
    <div v-else>
      No Transactions for this time period
    </div>
  </div>
</template>

<script>
export default {
  props: {
    granularity: {
      default: "",
      type: String
    },
    transactionInformation: {}
  },
  computed: {
    headers() {
      return [
        {
          text: this.granularity == "Week"? "Week (starting Sunday)": this.granularity,
          value: "dateRangeText"
        },
        {
          text: "Transactions",
          value: "transactionCount"
        },
        {
          text: "Sales",
          value: "amountText"
        }]
    }
  }
}
</script>

<style>

</style>
