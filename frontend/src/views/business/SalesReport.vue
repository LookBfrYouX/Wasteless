<template>
  <v-container>
    <v-row>
      <v-col cols="12" lg="8" class="date-range my-0 py-0">
        <v-subheader>Closing date range</v-subheader>
        <v-dialog
            ref="menu"
            v-model="menu"
            :return-value.sync="filterDates"
            width="290px"
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
            ></v-text-field>
          </template>
          <v-date-picker
              v-model="filterDates"
              range
              @change="$refs.menu.save(filterDates)"
              :title-date-format="getDateText"
          >
            <v-btn
                text
                color="primary"
                @click="menu = false"
            >
              Cancel
            </v-btn>
            <v-btn
                text
                color="primary"
                @click="$refs.menu.save(filterDates)"
            >
              OK
            </v-btn>
          </v-date-picker>
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
    }
  },
  watch: {
    filterDates() {
      this.$emit('newDates', this.filterDates);
    }
  },
  methods: {
    /**
     * Returns the dateText computed variable
     */
    getDateText() {
      return this.dateText;
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

        if (this.filterDates.length === 1) {
          return "Before: " + new Date(this.filterDates[0]).toLocaleDateString('en-NZ', options);
        } else if (this.filterDates.length === 2) {
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
