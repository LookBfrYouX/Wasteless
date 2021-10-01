<template>
  <v-container class="w-100">
    <v-row class="py-5">
      <h2>Sales Report</h2>
    </v-row>
    <v-row>
      <v-col cols="12" md="9" class="d-flex align-end justify-space-between">
        <div class="d-flex align-center flex-wrap">
            <v-subheader>Date Range</v-subheader>
            <div>
              <report-date-selector
                :startDate="startDate"
                :endDate="endDate"
                :defaultDates="defaultDates"
                @newDates="(event) => {
                  this.startDate = event.startDate;
                  this.endDate   = event.endDate;
                }"
                class="pl-0"
              />
            </div>
        </div>
        <div class="granularity-picker d-flex align-center flex-wrap">
          <v-subheader>Granularity</v-subheader>
          <v-select
              class="granularitySelect"
              v-model="granularity"
              dense
              :items="granularityOptions"
              label="Group By"
              solo
          />
        </div>
      </v-col>
    </v-row>
    <v-divider></v-divider>
    <v-row class="mb-2">
      <v-col cols="12" md="6" class="d-flex flex-nowrap justify-space-between">
        <div>
          <h4>Total Transactions</h4>
          {{ totalTransactionCount }}
        </div>
        <div>
          <h4>Total Sales</h4>
          {{ $helper.makeCurrencyString(totalAmount, currency) }}
        </div>
      </v-col>
      <v-col cols="12" md="6" class="d-flex flex-nowrap justify-space-between">
        <div>
          <h4>Period</h4>
          {{ this.$helper.isoToDateString(startDate) }} to
          {{ this.$helper.isoToDateString(endDate) }}
        </div>
        <div>
          <h4>Business</h4>
          <span v-if="business.name">{{business.name}}</span>
        </div>
      </v-col>
    </v-row>
    <v-expansion-panels>
      <v-expansion-panel>
        <v-btn-toggle v-model="chartType" multiple mandatory>
          <v-btn>Sales</v-btn>
          <v-btn>Transactions</v-btn>
        </v-btn-toggle>
        <bar-chart class="p-3" :chart-data="chartdata" :options="options" />
      </v-expansion-panel>
      <v-expansion-panel>
        <v-expansion-panel-header>
          <template v-slot:default="{ open }">
            {{open? "Hide Table": "Show Table"}}
          </template>
        </v-expansion-panel-header>
        <v-expansion-panel-content :eager=true >
          <SalesReportTable
              :granularity="granularity"
              :transactionInformation="transformedTransactionData"
          />
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="getTransactions"
        :show="apiErrorMessage !== null"
        title="Error fetching Transaction information"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>

  </v-container>
</template>

<script>
import {Api} from "@/Api";
import SalesReportTable from "@/components/SalesReportTable.vue";
import ReportDateSelector from "@/components/ReportDateSelector";
import ErrorModal from "@/components/ErrorModal";
import BarChart from "@/components/charts/BarChart";

/**
 * Default dates. referenced in both data in other methods, can't use
 * `this` in data() so needs to be in an external function
 */
const defaultDates = () => {
  const startDate = new Date(new Date().toISOString().replace(/T.+/, "T00:00:00.000Z"));
  const endDate = new Date(startDate.getTime());
  startDate.setUTCDate(startDate.getUTCDate() - 6);
  // initially show a week of data;
  return {
    startDate,
    endDate
  };
};

export default {
  components:{
    BarChart,
    SalesReportTable,
    ReportDateSelector,
    ErrorModal,
  },

  props: {
    businessId: {
      required: true,
      type: Number,
    },
  },

  data() {
    const { startDate, endDate } = defaultDates();

    return {
      startDate, // inclusive (00:00) of the day
      endDate, // inclusive (23:59) of the day
      granularity: "Day",
      totalAmount: 0,
      totalTransactionCount: 0,
      transactions: null,
      granularityOptions: ["Day", "Week", "Month", "Year"],
      business: {},
      currency: null,
      apiErrorMessage: null,
      transformedTransactionData: null,
      chartType: [],
      chartdata: {
        labels: ['One', 'Two'],
        datasets: [
          {
            label: 'Data One',
            backgroundColor: '#112798',
            data: [1, 2],
          },
        ]
      },
      options: {
        scales: {
          yAxes: [
              {
                id: 'Sales',
                position: 'left',
                scaleLabel: {
                  display: true,
                  labelString: 'Sales',
                },
                gridLines: {
                  display: false,
                },
                ticks: {
                  beginAtZero: true,
                  }
              },
            {
              id: 'Transactions',
              position: 'right',
              display: true,
              scaleLabel: {
                display: true,
                labelString: 'Transactions',
              },
              gridLines: {
                display: false,
              },
              ticks: {
                beginAtZero: true,
              }
            }]
        },
        responsive: true,
        maintainAspectRatio: false
      }
    };
  },


  /**
   * Loads transaction information and business information (name, currency)
   */
  async mounted() {
    await Promise.allSettled([
      this.getTransactions(),
      this.getBusinessInformation(),
    ]);
    this.getTransformedTransactionData();
  },

  methods: {
    /**
     * Gets information for the business (currency, name etc.)
     * @return true if successful
     */
    async getBusinessInformation() {
      try {
        this.business = (await Api.businessProfile(this.businessId)).data;
        this.currency = this.$helper.getCurrencyForBusinessByCountry(this.business.address.country);
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
     * Sends API request and sets totalAmount, totalTransactionCount and transactions variables
     */
    getTransactions: async function () {
      /* makes a query to the api to retrieve the transactions with the props*/
      try {
        this.transactions = null;
        const response = (
            await Api.getTransactions(this.businessId, {
              transactionGranularity: this.granularity.toUpperCase(),
              startDate: this.startDate.toISOString().slice(0, 10),
              endDate: this.endDate.toISOString().slice(0, 10),
            })
        ).data;
        this.totalAmount = response.totalAmount;
        this.totalTransactionCount = response.totalTransactionCount;
        this.transactions = response.transactions;

        this.apiErrorMessage = null;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
      this.getTransformedTransactionData();
    },

    /**
     * Converts the date to a user-friendly string, dependent on the granularity
     */
    generateUserFacingDateText(date) {

      // can't define variables inside switch variables
      if (this.granularity == "Day") {
        return date.toDateString();
      }
      if (this.granularity == "Week") {
        let startDate = new Date(date.getTime());
        this.normalizeDateToStartOfWeek(startDate);
        let endDate = new Date(startDate.getTime());
        endDate.setUTCDate(endDate.getUTCDate() + 6);
        if (this.endDate.getTime() < endDate.getTime()) {
          endDate = this.endDate;
        }
        return `${this.formatWeekText(startDate)} to ${this.formatWeekText(endDate)}`;
      }
      if (this.granularity == "Month") {
        return `${this.$constants.MONTH_NAMES[date.getUTCMonth()]} ${date.getUTCFullYear()}`
      }
      if (this.granularity == "Year") {
        return date.getUTCFullYear().toString();
      }

      throw new Error("Yo what you doing here");
    },

    /**
     * A helper function for formatting the week granularity date in human readable form.
     */
    formatWeekText(date) {
      return `${
          date.getUTCDate().toString().padStart(2, "0")}-${
          (date.getUTCMonth() + 1).toString().padStart(2, "0")}-${
          date.getUTCFullYear().toString()}`;
    },

    /**
     * Normalizes a date to the start of the day, UTC time
     * @param{Date} date to modify
     * @return{Date} same date object
     */
    normalizeDateToStartOfDay(date) {
      date.setUTCHours(0);
      date.setUTCMinutes(0);
      date.setUTCSeconds(0);
      date.setUTCMilliseconds(0);
      return date;
    },

    /**
     * Normalizes a date to the start of the week (Sunday) and start of the day, UTC time
     * @param{Date} date to modify
     * @return{Date} same date object
     */
    normalizeDateToStartOfWeek(date) {
      date.setUTCDate(date.getUTCDate() - date.getUTCDay());
      this.normalizeDateToStartOfDay(date);
      return date;
    },

    /**
     * Normalizes a date to the start of the month and start of the day, UTC time
     * @param{Date} date to modify
     * @return{Date} same date object
     */
    normalizeDateToStartOfMonth(date) {
      date.setUTCDate(1);
      this.normalizeDateToStartOfDay(date);
      return date;
    },

    /**
     * Normalizes a date to the start of the year and start of the day, UTC time
     * @param{Date} date to modify
     * @return{Date} same date object
     */
    normalizeDateToStartOfYear(date) {
      date.setUTCMonth(0);
      this.normalizeDateToStartOfMonth(date);
      return date;
    },

    /**
     * Backend returns only periods where there is at least one transaction, with
     * the date being the date of the first transaction in the period
     * startDate   min+1 period       max-1 period  endDate
     *       |-------|---*----|---....----|--*----|
     *               @   ^ 1st entry from the backend
     *               ^ initial location for pointer
     * Algorithm begins with i = 0 and pointer at the end of the first period (i.e. start of second period)
     * It checks if the ith entry is before the pointer; in this case it is not, so it knows there are no
     * transactions in the 1st period, and adds an entry into an array with zero transactions and revenue.

     *                  i=0
     *       |-------|---*----|---....----|--*----|
     *                        @      * < @ so element found. i++
     *
     * It then increments the pointer by one period, moving it to the end of the second period. This time,
     * the ith entry (first entry) is before the pointer, so it knows there is at least one transaction in
     * the second time period, and adds an entry into an array. This time, it increments i.
     *
     *                            i=1
     *       |-------|---*----|---....----|--*----|
     *                                    @
     * This continues until the pointer gets to the last period
     * The last period is inclusive of the last day as the backend sets the end date to 11:59pm
     *
     * @return [{
     *   date, Date: date in the time period
     *   dateRangeText, String: text to show to the user indicating the time period
     *   amount, Number: total revenue in that period
     *   transactionCount, Integer: number of transactions in that period
     *   amountText, String: amount, but as a string with currency information
     * }] or null if transactionData is null
     */
    getTransformedTransactionData() {
      if (this.transactions == null) {
        this.transformedTransactionData = null;
        return
      }
      let i = 0;
      let dataArray = [];
      let date = new Date(this.startDate.getTime()); // date is the start of the next period

      while (date.getTime() <= this.endDate.getTime()) {
        const startOfPeriod = new Date(date.getTime());
        switch (this.granularity) {
          case "Day":
            date.setUTCDate(date.getUTCDate() + 1);
            this.normalizeDateToStartOfDay(date);
            break;

          case "Week":
            date.setUTCDate(date.getUTCDate() + 7);
            this.normalizeDateToStartOfWeek(date);
            break;

          case "Month":
            date.setUTCMonth(date.getUTCMonth() + 1);
            this.normalizeDateToStartOfMonth(date);
            break;

          case "Year":
            date.setUTCFullYear(date.getUTCFullYear() + 1);
            this.normalizeDateToStartOfYear(date);
            break;
        }

        if (i < this.transactions.length) {
          // There may periods after the last transaction, meaning i == transactions.length. In ths
          // case the remaining periods must be empty
          const transaction = this.transactions[i];
          const transactionDate = new Date(transaction.date);

          if (transactionDate.getTime() < date.getTime()) {
            i++;
            dataArray.push({
              ...transaction,
              date: transactionDate,
              dateRangeText: this.generateUserFacingDateText(transactionDate),
              amountText: this.$helper.makeCurrencyString(transaction.amount, this.currency),
            });
            continue;
          }
        }

        dataArray.push({
          date: startOfPeriod,
          amount: 0,
          transactionCount: 0,
          dateRangeText: this.generateUserFacingDateText(startOfPeriod),
          amountText: this.$helper.makeCurrencyString(0, this.currency)
        });
      }
      this.updateChart(dataArray);
      this.transformedTransactionData = dataArray;
    },

    /**
     * This method updates the chart depending on which buttons are selected.
     * It takes in the dataArray and updated the chartData and options.
     * @param dataArray
     */
    updateChart(dataArray) {
      let tempChartdata = {
        labels: dataArray.map((element) => this.generateUserFacingDateText(element.date)),
        datasets: []
      }
      if (this.chartType.includes(0)) {
        tempChartdata.datasets.push({
          label: 'Sales',
          backgroundColor: '#2b39a1',
          yAxisID: 'Sales',
          data: dataArray.map((element) => element.amount),
        });
      }
      if (this.chartType.includes(1)) {
        tempChartdata.datasets.push({
          label: 'Transactions',
          backgroundColor: '#009900',
          yAxisID: 'Transactions',
          data: dataArray.map((element) => element.transactionCount),
        });
      }
      let tempOptions = {
        scales: {
          yAxes: [
            {
              id: 'Sales',
              position: 'left',
              display: this.chartType.includes(0),
              scaleLabel: {
                display: true,
                labelString: 'Sales',
              },
              gridLines: {
                display: false,
              },
              ticks: {
                beginAtZero: true,
              }
            },
            {
              id: 'Transactions',
              position: 'right',
              display: this.chartType.includes(1),
              scaleLabel: {
                display: true,
                labelString: 'Transactions',
              },
              gridLines: {
                display: false,
              },
              ticks: {
                beginAtZero: true,
              }
            }]
        },
        responsive: true,
        maintainAspectRatio: false
      }
      this.chartdata = tempChartdata;
      this.options = tempOptions;
    }
  },

  computed: {
    defaultDates() {
      return defaultDates();
    },
  },

  watch: {
    /**
     * Updates the page title to include the business name
     */
    business() {
      if (this.business.name) {
        document.title = `${this.business.name} | Business Sales Report | Nutrisave`;
      }
    },

    granularity() {
      this.getTransactions();
    },

    startDate() {
      this.getTransactions();
    },

    endDate() {
      this.getTransactions();
    },

    chartType() {
      this.getTransformedTransactionData();
    }


  }
};
</script>

<style>
.v-text-field__details {
  display: none;
}
.granularitySelect {
  max-width: 130px;
}
.granularity-picker div.v-input--dense>.v-input__control>.v-input__slot {
  margin-bottom:0;
}
</style>
