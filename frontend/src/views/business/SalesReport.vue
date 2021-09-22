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
  </v-container>
</template>

<script>
import {Api} from "@/Api"
export default {
  data() {
    return {
      minDate:"2020-01-02 15:34:20",
      maxDate:"2021-01-22 15:34:20",
      businessName:"",
      granularity:"",
      totalValue:0,
      numberOfTransactions:0,
      transactionData:{},
      items: ["Day", "Month","Year"],
      mockTransactionResponse:{
        totalAmount: 400,
        totalTransactionCount: 10,
        transactions: [
          {"date":"2020-01-02 15:34:20",
           "transactionCount": 5,
           "amount": 200},
          {"date":"2021-01-22 15:34:20",
           "transactionCount": 5,
           "amount": 200},
        ]
      }
    }
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
     * Sends API request and sets totalvalue, numberOfTransactions and transactionData variables
     */
    setResultsWithMocks: function () {
      /* makes a query to the api to retrieve the transactions with the props*/
      this.totalValue = this.mockTransactionResponse.totalAmount;
      this.numberOfTransactions = this.mockTransactionResponse.totalTransactionCount;
      this.transactionData = this.mockTransactionResponse.transactions;
    }
  },
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
