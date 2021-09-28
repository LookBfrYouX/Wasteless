<template>
  <v-container>
    <v-row align="center">
      <v-col cols="12" lg="8" class="my-0 py-0 align-center">
        <v-dialog
            ref="menu"
            v-model="menu"
            :return-value.sync="filterDates"
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
                        v-model="filterDates"
                        range
                        @input="setDropdownText('Custom')"
                    />
                  </v-col>
                  <v-col cols="12" class="col-md-6 px-8" align="center">
                    <v-subheader>Starting</v-subheader>
                    <v-text-field
                        readonly
                        :value="filterDates[0]"
                    />
                    <v-subheader>Ending</v-subheader>
                    <v-text-field
                        readonly
                        :value="filterDates[1]"
                    />
                  </v-col>
                </v-row>
                <v-row align="center">
                  <v-col align="center">
                    <v-btn
                        text
                        color="success"
                        @click="$refs.menu.save(filterDates)"
                        outlined
                    >
                      APPLY
                    </v-btn>
                  </v-col>
                  <v-col align="center">
                    <v-btn
                        text
                        color="primary"
                        @click="filterDates=[]"
                        outlined
                    >
                      CLEAR
                    </v-btn>
                  </v-col>
                  <v-col align="center">
                    <v-btn
                        text
                        color="error"
                        @click="menu=false"
                        outlined
                    >
                      CANCEL
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
      dates: {},
    }
  },
  watch: {
    /**
     * Emits the changed date range for a parent component to catch
     */
    filterDates() {
      this.dates.startDate = new Date(this.filterDates[0]);
      if (this.filterDates[1]) {
        this.dates.endDate = new Date(this.filterDates[1]);
      } else {
        this.dates.endDate = new Date(this.filterDates[0]);
      }
      this.$emit('newDates', this.dates);
      if (this.filterDates.length === 0) {
        this.selectedDropdown = null;
      } else if (this.filterDates.length === 2 && this.filterDates[0] > this.filterDates[1]) {
        this.filterDates = this.filterDates.sort()
      }
    }
  },
  computed: {
    getLabel() {
      if (this.filterDates.length > 0 && this.selectedDropdown != null) {
        return `${this.selectedDropdown}`
      } else if (this.filterDates.length > 0) {
        return "Custom"
      } else {
        return "select";
      }
    }
  },
  methods: {
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
    formatDate(date) {
      return date.toISOString().split('T')[0];
    },
    /**
     * Sets the date ranged based on dropdown input
     * @param event selected dropdown text
     */
    dateRangeSelected(event) {
      let today = new Date();
      let weekAgo = new Date(new Date().setDate(new Date().getDate() - 6));
      let monthAgo = new Date(new Date().setMonth(new Date().getMonth() - 1));
      let yearAgo = new Date(new Date().setFullYear(new Date().getFullYear() - 1));

      switch (event) {
        case null:
          this.filterDates = [];
          break;
        case "Today":
          this.filterDates = [this.formatDate(today), this.formatDate(today)];
          break;
        case "Last week":
          this.filterDates = [this.formatDate(weekAgo), this.formatDate(today)];
          break;
        case "Last month":
          this.filterDates = [this.formatDate(monthAgo), this.formatDate(today)];
          break;
        case "Last year":
          this.filterDates = [this.formatDate(yearAgo), this.formatDate(today)];
          break;
      }
    }
  },
}
</script>
