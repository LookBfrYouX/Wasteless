<template>
  <v-container class="w-100">
    <v-row>
      <v-col class="d-flex align-center justify-center">
        <v-subheader>Date Range</v-subheader>
        <v-btn label="Date Range">Select Date Range</v-btn>
      </v-col>
      <v-col align="center" class="d-flex align-center justify-center">
        <v-subheader>Granularity</v-subheader>
        <v-select
          class="granularitySelect"
          v-model="granularity"
          dense
          :items="items"
          label="Group By"
          solo
        />
      </v-col>
      <v-col align="center" class="d-flex align-center justify-center">
        <v-btn v-on:click="setResultsWithMocks">Go</v-btn>
      </v-col>
    </v-row>
    <v-divider></v-divider>
    <v-row class="mb-2">
      <v-col>
        <h4>Total Transactions</h4>
        {{ numberOfTransactions }}
      </v-col>
      <v-col>
        <h4>Total Sales</h4>
        {{ $helper.makeCurrencyString(totalValue, currency) }}
      </v-col>
      <v-col>
        <h4>Period</h4>
        {{ this.$helper.isoToDateString(minDate) }} to
        {{ this.$helper.isoToDateString(maxDate) }}
      </v-col>
      <v-col>
        <h4>Business</h4>
        <span v-if="business.name">{{business.name}}</span>
      </v-col>
    </v-row>
    <SalesReportTable v-bind:granularity="granularity" v-bind:transactionInformation="transformedTransactionData"/>

  </v-container>
</template>

<script>
import { Api } from "@/Api";
import SalesReportTable from "@/components/SalesReportTable.vue";
export default {
  components:{SalesReportTable},

  props: {
    businessId: {
      required: true,
      type: Number,
    },
  },

  data() {
    // minDate inclusive
    const minDate = new Date(2020, 0, 1);
    this.normalizeDateToStartOfWeek(minDate);
    // maxDate exclusive
    const maxDate = new Date(2022, 0, 1)
    this.normalizeDateToStartOfWeek(maxDate);

    return {
      minDate,
      maxDate,
      businessName: "",
      granularity: "Week",
      totalValue: 0,
      numberOfTransactions: 0,
      transactionData: {},
      items: ["Day", "Week", "Month", "Year"],
      business: {},
      currency: null
      // mockTransactionResponse:{
      // "transactions": [
      //     {
      //         "date": "2020-01-02T15:34:20+13:00",
      //         "transactionCount": 1,
      //         "amount": 5.25
      //     },
      //     {
      //         "date": "2021-01-22T15:34:20+13:00",
      //         "transactionCount": 1,
      //         "amount": 6.6
      //     },
      //     {
      //         "date": "2021-02-21T15:34:20+13:00",
      //         "transactionCount": 5,
      //         "amount": 63.5
      //     },
      //     {
      //         "date": "2021-03-20T15:34:20+13:00",
      //         "transactionCount": 3,
      //         "amount": 26.25
      //     }
      // ],
      // "totalAmount": 101.6,
      // "totalTransactionCount": 10
      // }
    };
  },
  mounted() {
    this.getTransactions();
    this.getBusinessInformation();
  },
  methods: {
    /**
     * Gets information for the business (currency, name etc.)
     * @return true if successful
     */
    async getBusinessInformation() {
      try {
        this.business = (await Api.businessProfile(this.businessId)).data;
        this.currency = await this.$helper.getCurrencyForBusinessByCountry(this.business.address.country);
        return true;
      } catch (err) {
        // If can't get currency not that big of a deal
        if (await Api.handle401.call(this, err)) {
          return;
        }
        return false;
      }
    },

    /**
     * Sends API request and sets totalvalue, numberOfTransactions and transactionData variables
     */
    getTransactions: async function () {
      /* makes a query to the api to retrieve the transactions with the props*/
      try {
        const response = (
          await Api.getTransactions(this.businessId, {
            transactionGranularity: this.granularity.toUpperCase(),
            startDate: this.minDate.toISOString().slice(0, 10),
            endDate: this.maxDate.toISOString().slice(0, 10),
          })
        ).data;
        this.totalValue = response.totalAmount;
        this.numberOfTransactions = response.totalTransactionCount;
        this.transactionData = response.transactions;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },
    /**
     * sets some mock data for testing sets totalvalue, numberOfTransactions and transactionData variables
     */
    setResultsWithMocks: function () {
      /* makes a query to the api to retrieve the transactions with the props*/
      this.totalValue = this.mockTransactionResponse.totalAmount;
      this.numberOfTransactions =
        this.mockTransactionResponse.totalTransactionCount;
      this.transactionData = this.mockTransactionResponse.transactions;
    },

    /**
     * Parses the date depending on granularity
     */
    generateUserFacingDateText(date) {
      const weekText = date => `${date.getUTCDate().toString().padStart(2, "0")}-${(date.getUTCMonth() + 1).toString().padStart(2, "0")}-${date.getUTCFullYear().toString()}`;
      // can't define variables inside switch variables

      if (this.granularity == "Day") {
        return date.toDateString();
      }
      if (this.granularity == "Week") {
        const startDate = new Date(date.getTime());
        this.normalizeDateToStartOfWeek(startDate);
        const endDate = new Date(startDate.getTime());
        endDate.setUTCDate(endDate.getUTCDate() + 6);
        return `${weekText(startDate)} to ${weekText(endDate)}`;
      }
      if (this.granularity == "Month") {
        return `${this.$constants.MONTH_NAMES[date.getUTCMonth()]} ${date.getUTCFullYear()}`
      }
      if (this.granularity == "Year") {
        return date.getUTCFullYear().toString();
      }

      throw new Error("Yo what you doing here");
    },

    normalizeDateToStartOfDay(date) {
      date.setUTCHours(0);
      date.setUTCMinutes(0);
      date.setUTCSeconds(0);
      date.setUTCMilliseconds(0);
    },

    normalizeDateToStartOfWeek(date) {
      date.setUTCDate(date.getUTCDate() - date.getUTCDay());
      this.normalizeDateToStartOfDay(date);
    },

    normalizeDateToStartOfMonth(date) {
      date.setUTCDate(1);
      this.normalizeDateToStartOfDay(date);
    },

    normalizeDateToStartOfYear(date) {
      date.setUTCMonth(0);
      this.normalizeDateToStartOfMonth(date);
    },
  },

  computed: {
    transformedTransactionData() {
      let i = 0;
      let dataArray = [];
      let date = new Date(this.minDate.getTime()); // date is the start of the next period
      while (date.getTime() <= this.maxDate.getTime()) {
        // console.log(date.toUTCString(), this.maxDate.toUTCString());
        const startOfPeriod = new Date(date.getTime());
        switch (this.granularity) {
          case "Day":
            date.setUTCDate(date.getUTCDate() + 1);
            break;

          case "Week":
            date.setUTCDate(date.getUTCDate() + 7);
            break;

          case "Month":
            date.setUTCMonth(date.getUTCMonth() + 1);
            break;

          case "Year":
            date.setUTCFullYear(date.getUTCFullYear() + 1);
            break;
        }
        if (i < this.transactionData.length - 1) {
          let transaction = this.transactionData[i];
          transaction.date = new Date(transaction.date);

          if (transaction.date.getTime() < date.getTime()) {
            console.log(
              transaction.date.toUTCString(),
              startOfPeriod.toUTCString()
            );
            dataArray.push({
              ...transaction,
              dateRangeText: this.generateUserFacingDateText(transaction.date),
              amountText: this.$helper.makeCurrencyString(transaction.amount, this.currency),

            });
            i++;
            continue;
          }
        } else {
          // console.log(
          //   "empty", startOfPeriod.toUTCString()
          // );
          dataArray.push({
            date: startOfPeriod,
            dateRangeText: this.generateUserFacingDateText(startOfPeriod),
            amountText: this.$helper.makeCurrencyString(0, this.currency),
            amount: 0,
            transactionCount: 0
          });
        }
      }

      return dataArray;
    },
  },

  watch: {
    business() {
      if (this.business.name) {
        document.title = `${this.business.name} | Business Sales Report | Wasteless`;
      }
    }
  }
};
</script>

<style>
.v-text-field__details {
  display: none;
}
.granularitySelect {
  max-width: 204.467px;
}
</style>
