<template>
  <v-container class="w-100">
    <v-row>
      <v-col class="d-flex align-center justify-center">
        <v-subheader>Date Range</v-subheader>
        <v-btn label="Date Range">Select Date Range</v-btn>
      </v-col>
      <v-col align="center" class="d-flex align-center justify-center">
        <v-subheader>Granularity</v-subheader>
        <v-select class="granularitySelect" v-model="granularity" dense=true :items="items" label="Group By" solo/>
      </v-col>
      <v-col align="center" class="d-flex align-center justify-center">
        <v-btn v-on:click="setResultsWithMocks">Go</v-btn>
      </v-col>
    </v-row>
    <v-divider></v-divider>
    <v-row>
      <v-col>
        <h4>Total Transactions</h4>
        {{numberOfTransactions}}
      </v-col>
      <v-col>
        <h4>Total Sales</h4>
        {{totalValue}}
      </v-col>
      <v-col>
        <h4>Period</h4>
        {{this.$helper.isoToDateString(minDate)}} To {{this.$helper.isoToDateString(maxDate)}}
      </v-col>
      <v-col>
        <h4>Business</h4>
        {{this.$stateStore.getters.getActingAs().name}}
      </v-col>
    </v-row>
    <SalesReportTable v-bind:granularity="granularity" v-bind:transactionInformation="transactionData"/>
  </v-container>
</template>

<script>
import {Api} from "@/Api"
import SalesReportTable from "@/components/SalesReportTable.vue";
export default {
  components:{SalesReportTable},
  data() {
    // minDate inclusive
    const minDate = new Date();
    minDate.setUTCHours(0);
    minDate.setUTCMinutes(0);
    minDate.setUTCSeconds(0);
    minDate.setUTCMilliseconds(0);
    minDate.setUTCDate(minDate.getUTCDate() - minDate.getUTCDay());
    // maxDate exclusive
    const maxDate = new Date(minDate.getTime());
    maxDate.setUTCDate(maxDate.getUTCDate() + 7);
    return {
      minDate,
      maxDate,
      businessName:"",
      granularity:"Day",
      totalValue:0,
      numberOfTransactions:0,
      transactionData:{},
      items: ["Day","Week", "Month","Year"],
      mockTransactionResponse:{
      "transactions": [
          {
              "date": "2020-01-02T15:34:20+13:00",
              "transactionCount": 1,
              "amount": 5.25
          },
          {
              "date": "2021-01-22T15:34:20+13:00",
              "transactionCount": 1,
              "amount": 6.6
          },
          {
              "date": "2021-02-21T15:34:20+13:00",
              "transactionCount": 5,
              "amount": 63.5
          },
          {
              "date": "2021-03-20T15:34:20+13:00",
              "transactionCount": 3,
              "amount": 26.25
          }
      ],
      "totalAmount": 101.6,
      "totalTransactionCount": 10
      }
    }
  },
  mounted() {
    this.transformersRobotsInDisguise()
  },
  methods: {
    /**
     * Sends API request and sets totalvalue, numberOfTransactions and transactionData variables
     */
    getTransactions: async function () {
      /* makes a query to the api to retrieve the transactions with the props*/
      try {
        const response = (await Api.getTransactions(this.$stateStore.getters.getBusinessId,
                          {granularity:this.granularity,
                           minDate:this.minDate,
                           maxDate:this.maxDate})).data;
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
      this.numberOfTransactions = this.mockTransactionResponse.totalTransactionCount;
      this.transactionData = this.mockTransactionResponse.transactions;
    },
    /**
     * Parses the date depending on granularity
     */
    // parseTransactionDates: function () {
    //   for (let transaction in this.transactionData) {
    //     if (this.granularity == "Day") {
    //       transaction.date=transaction.date// don't need to change the Date
    //       // have 7 entries per page?
    //     } else if (this.granularity == "Week") {
    //       // have date ranges in the date field?
    //       // have 4 entries per page?
    //     } else if (this.granularity == "Month") {
    //     //have 12 entries per page
    //     // get rid of days in date field
    //     } else if (this.granularity == "Year") {
    //     // get rid of days and months in field
    //     // have default number of entries per page
    //     }
    //   }
    // }
    transformersRobotsInDisguise: function () {
      let i = 0;
      let date = new Date(this.minDate.getTime());
      while(date.getTime() <= this.maxDate.getTime()) {
        console.log(date.toUTCString())
        console.log(this.maxDate)
        switch(this.granularity) {
          case "Day":
            date.setUTCDate(date.getUTCDate() + 1);
            break;

          case "Week":
            date.setUTCDate(date.getUTCDate() + 7);
            break;

          case "Month":
            date.setUTCMonth(date.getUTCMonth() + 1)
            break;

          case "Year":
            date.setUTCFullYear(date.getUTCFullYear() + 1)
            break;
        }
        if (i < this.mockTransactionResponse.transactions.length - 1) {
          let transaction = this.mockTransactionResponse.transactions[i];
          transaction.date = new Date(transaction.date);
          if (transaction.date.getTime() < date.getTime()) {
            console.log(transaction.date.toUTCString(), `${date.getUTCFullYear()}-${date.getUTCMonth()}`);
            i++;
          continue
        }
      } else {
        console.log("empty")
        }
      }
    }
  }
}
</script>

<style>
.v-text-field__details {
  display: none;
}
.granularitySelect {
  max-width: 204.467px;
}
</style>
