<template>
  <div>
    <v-switch
      v-model="hideZeroes"
      label="Hide periods with no transactions"
    />
    <v-data-table
        :headers="headers"
        :items="tableData"
        :loading="transactionInformation == null"
        :items-per-page="5"
    >
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
    transactionInformation: null
  },

  data() {
    return {
      hideZeroes: false
    };
  },

  computed: {
    tableData() {
      if (this.transactionInformation == null) return [];
      if (!this.hideZeroes) return this.transactionInformation;
      return this.transactionInformation.filter(el => el.transactionCount > 0);
    },

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
