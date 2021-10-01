<template>
  <v-container>
    <v-row align="center">
      <v-col cols="12" lg="8" class="my-0 py-0 align-center">
        <v-dialog
            ref="menu"
            v-model="menu"
            max-width="700px"
            persistent
        >
          <template v-slot:activator="{ on, attrs }">
            <v-btn v-on="on" v-bind="attrs" >{{ getLabel }}</v-btn>
          </template>
          <v-card>
            <v-card-text class="px-0 py-0">
              <v-container fluid>
                <v-row>
                  <v-col>
                    <v-subheader class="mb-2">Date range</v-subheader>
                    <v-select
                        :items=dateOptions
                        :label="dateDropdownText"
                        v-model="selectedDropdown"
                        solo
                        dense
                        @change="dateRangeSelected"
                    >
                    </v-select>
                  </v-col>
                </v-row>
                <v-row>
                  <v-col cols="12" class="col-md-6" align="center">
                    <v-date-picker
                      :value="filterDates"
                      @input="datePickerChange"
                      range
                    />
                  </v-col>
                  <v-col cols="12" class="col-md-6 px-8" align="center">
                    <v-subheader>Starting</v-subheader>
                    <v-text-field
                        readonly
                        :value="filterDatesStartDateString"
                    />
                    <v-subheader>Ending</v-subheader>
                    <v-text-field
                        readonly
                        :value="filterDatesEndDateString"
                    />
                  </v-col>
                </v-row>
                <v-row align="center">
                  <v-col align="center">
                    <v-btn
                        text
                        color="success"
                        @click="applyClicked"
                        outlined
                    >
                      Apply 
                    </v-btn>
                  </v-col>
                  <v-col align="center">
                    <v-btn
                        text
                        color="primary"
                        @click="resetClicked"
                        outlined
                    >
                      Reset 
                    </v-btn>
                  </v-col>
                  <v-col align="center">
                    <v-btn
                        text
                        color="error"
                        @click="cancelClicked"
                        outlined
                    >
                      Cancel
                    </v-btn>
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>
          </v-card>
        </v-dialog>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      menu: false,
      filterDates: [],
      selectedDropdown: null,
      dateDropdownText: "Select",
      dateOptions: ["Today", "Last week", "Last month", "Last year"],
    }
  },

  props: {
    startDate: {
      required: true
    },
    endDate: {
      required: true
    },
    defaultDates: {
      required: true
    }
  },

  watch: {
    startDate() {
      this.updateFilterDatesWithProps();
    },
    endDate() {
      this.updateFilterDatesWithProps();
    },
  },

  computed: {
    filterDatesStartDateString() {
      if (this.filterDates.length == 0) return null;
      else return this.dateStringToDate(this.filterDates[0]).toDateString();
    },

    filterDatesEndDateString() {
      if (this.filterDates.length < 2) {
        return this.filterDatesStartDateString;
      }
      
      return this.dateStringToDate(this.filterDates[1]).toDateString();
    },

    getLabel() {
      if (this.filterDates.length > 0 && this.selectedDropdown != null) {
        return `${this.selectedDropdown}`
      } else if (this.filterDates.length > 0) {
        return "Custom"
      } else {
        return "Select";
      }
    },
  },

  beforeMount() {
    this.updateFilterDatesWithProps();
  },

  methods: {
    /**
     * Replaces filterDates value with that derived from props
     */
    updateFilterDatesWithProps() {
      this.filterDates = [
        this.dateToISODateString(this.startDate),
        this.dateToISODateString(this.endDate),
      ]
    },

    resetClicked() {
      this.filterDates = [
        this.dateToISODateString(this.defaultDates.startDate),
        this.dateToISODateString(this.defaultDates.endDate)
      ];
    },

    /**
     * Handler when apply button clicked - emits events with filterDates values
     */
    applyClicked() {
      this.menu = false;
      const dates = this.defaultDates;
      
      if (this.filterDates.length > 0) {
        dates.startDate = this.dateStringToDate(this.filterDates[0]);
        dates.endDate = this.dateStringToDate(this.filterDates[0]);
      }
      if (this.filterDates.length > 1) {
        dates.endDate = this.dateStringToDate(this.filterDates[1]);
      }

      this.$emit("newDates", dates);
    },

    /**
     * When cancel clicked, reset filterDates value for the next time it is opened
     */
    cancelClicked() {
      this.updateFilterDatesWithProps();
      this.menu = false;
    },

    /**
     * Converts date string to date
     */
    dateStringToDate(dateStr) {
      return new Date(dateStr + "T00:00:00.000Z");
    },

    /**
     * Handler on date picker change. Sorts the dates before setting 
     * it to the value of filter dates
     * 
     */
    datePickerChange(dates) {
      dates.sort();
      this.filterDates = dates;
      this.setDropdownText("Custom");
    },

    /**
     * Sets the date dropdown text
     * @param text text to set the dropdown to
     */
    setDropdownText(text) {
      this.selectedDropdown = null;
      this.dateDropdownText = text;
    },

    /**
     * Formats a date to follow v-date-pickers dates
     * @param date a date to format
     * @returns {string} formatted date
     */
    dateToISODateString(date) {
      return date.toISOString().split('T')[0];
    },
    /**
     * Sets the date ranged based on dropdown input
     * @param event selected dropdown text
     */
    dateRangeSelected(event) {
      let today = new Date();
      let weekAgo = new Date(new Date().setUTCDate(new Date().getUTCDate() - 6));
      let monthAgo = new Date(new Date().setUTCMonth(new Date().getUTCMonth() - 1));
      let yearAgo = new Date(new Date().setUTCFullYear(new Date().getUTCFullYear() - 1));

      switch (event) {
        case null:
          this.filterDates = [];
          break;
        case "Today":
          this.filterDates = [this.dateToISODateString(today), this.dateToISODateString(today)];
          break;
        case "Last week":
          this.filterDates = [this.dateToISODateString(weekAgo), this.dateToISODateString(today)];
          break;
        case "Last month":
          this.filterDates = [this.dateToISODateString(monthAgo), this.dateToISODateString(today)];
          break;
        case "Last year":
          this.filterDates = [this.dateToISODateString(yearAgo), this.dateToISODateString(today)];
          break;
      }
    }
  },
}
</script>
