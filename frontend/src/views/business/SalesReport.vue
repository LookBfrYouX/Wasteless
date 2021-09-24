<template>
  <v-container>
    <v-row align="center">
      <v-col cols="12" lg="8" class="my-0 py-0 align-center">
        <v-subheader>Date range</v-subheader>
        <v-dialog
            ref="menu"
            v-model="menu"
            :return-value.sync="filterDates"
            max-width="700px"
        >
          <template v-slot:activator="{ on, attrs }">
            <v-text-field
                v-model="dateText"
                label="Anytime"
                prepend-inner-icon="date_range"
                readonly
                v-bind="attrs"
                v-on="on"
                clearable
                solo
                dense
            />
          </template>
          <v-card>
            <v-card-text class="px-0 py-0">
              <v-container fluid>
                <v-row>
                  <v-col>
                    <v-subheader>Date range</v-subheader>
                    <v-select
                        :items=dateOptions
                        label="Select"
                        solo
                        dense
                        clearable
                        item-text="short"
                        item-value="long"
                        @change="dateRangeSelected"
                    />
                  </v-col>
                </v-row>
                <v-row>
                  <v-col cols="12" class="col-md-6" align="center">
                    <v-subheader>Custom date range</v-subheader>
                    <v-date-picker
                        v-model="filterDates"
                        range
                        :title-date-format="getDateText"
                    />
                  </v-col>
                  <v-col cols="12" class="col-md-6" align="center">
                    <v-row>
                      <v-col>
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
                  </v-col>
                </v-row>
                <v-row align="center">
                  <v-col>
                    <v-btn
                        text
                        color="primary"
                        @click="menu = false"
                        outlined
                    >
                      CANCEL
                    </v-btn>
                  </v-col>
                  <v-col>
                    <v-btn
                        text
                        color="primary"
                        @click="$refs.menu.save(filterDates)"
                        outlined
                    >
                      APPLY
                    </v-btn>
                  </v-col>
                  <v-col>
                    <v-btn
                        text
                        color="primary"
                        @click="$refs.menu.save([])"
                        outlined
                    >
                      CLEAR
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
      dateOptions: ["Today", "Last week", "Last month", "Last year"]
    }
  },
  watch: {
    filterDates() {
      this.$emit('newDates', this.filterDates);
      console.log(this.filterDates);
    }
  },
  methods: {
    /**
     * Returns the dateText computed variable
     */
    getDateText() {
      return this.dateText;
    },
    dateRangeSelected(event) {
      console.log(event)

      let today = new Date();
      let todayFormatted = today.toISOString().split('T')[0];

      switch (event) {
        case null:
          this.$refs.menu.save([]);
          break;
        case "Today":
          this.filterDates = [todayFormatted, todayFormatted];
          break;
        case "Last week":
          this.filterDates = [todayFormatted, todayFormatted];
          break;
      }
      console.log(this.filterDates)
    }
  },
  computed: {
    /**
     * Returns formatted text for dates that will fit in a small text box
     * Doesnt display year if year is this year
     * If one date, queries dates before that date
     * If multiple dates, queries that range of dates
     */
    dateText: {
      get() {
        let options = {year: undefined, month: 'numeric', day: 'numeric'};
        if ((this.filterDates[1] && new Date(this.filterDates[1]).getFullYear()
                !== new Date().getFullYear()) ||
            (this.filterDates[0] && new Date(this.filterDates[0]).getFullYear()
                !== new Date().getFullYear())) {
          options.year = 'numeric';
        }

        if (this.filterDates.length === 2) {
          return new Date(this.filterDates[0]).toLocaleDateString('en-NZ', options) + " to " +
              new Date(this.filterDates[1]).toLocaleDateString('en-NZ', options);
        }
        return null;
      },
      /**
       * Run when "x" clicked on the date range text box
       */
      set(newName) {
        this.filterDates = [];
        return newName;
      }
    }
  }
}
</script>
