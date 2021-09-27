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
      type: String,
    },
    transactionInformation: {}
  },

  computed: {
    headers() {
      return [
        {
          text: this.granularity == "Week"? "Week (starting Sunday)": this.granularity,
          value: "dateRangeText",
          sort: (a, b) => this.dateMap[a] - this.dateMap[b]
        },
        {
          text: "Transactions",
          value: "transactionCount"
        },
        {
          text: "Sales",
          value: "amountText",
          sort: (a, b) => this.amountMap[a] - this.amountMap[b]
        }]
    },

    dateMap() {
      const dateMap = {};
      this.transactionInformation.forEach(el => {
        dateMap[el.dateRangeText] = el.date;
      });
      return dateMap;
    },

    amountMap() {
      const amountMap = {};
      this.transactionInformation.forEach(el => {
        amountMap[el.amountText] = el.amount;
      });
      return amountMap;
    }
  }
}
</script>

<style>

</style>
