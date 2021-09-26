<template>
  <div>
    <v-data-table
      :headers="headers"
      :items="transactionInformation == null? []: transactionInformation"
      :loading="transactionInformation == null"
      :items-per-page="5">
    </v-data-table>
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
