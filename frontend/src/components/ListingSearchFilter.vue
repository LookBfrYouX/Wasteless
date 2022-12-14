<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12" lg="8" class="filter-business my-0 py-0">
        <v-subheader>Filter businesses</v-subheader>
        <v-select
            v-model="businessTypes"
            :items="businesses"
            label="Select"
            multiple
            solo
            dense
            prepend-inner-icon="business"
            item-text="short"
            item-value="long"
        >
          <template v-slot:selection="{ item, index }">
            <v-chip v-if="index < shownChips" small>
              <span>{{ item.short }}</span>
            </v-chip>
            <span
                v-if="index === shownChips"
                class="grey--text text-caption"
            >
              (+{{ businessTypes.length - shownChips }} others)
            </span>
          </template>
        </v-select>
      </v-col>

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
              :min="todayDate"
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

      <v-col cols="12" lg="4" class="min-price my-0 py-0">
        <v-subheader>Min price</v-subheader>
        <v-text-field label="Min" type="number" min="0.01"
                      max="10000000" step="0.01" solo dense :value="minPrice"
                      @change="event => $emit('newMin', event)"></v-text-field>
      </v-col>

      <v-col cols="12" lg="4" class="max-price my-0 py-0">
        <v-subheader>Max price</v-subheader>
        <v-text-field label="Max" type="number" min="0.01"
                      max="10000000" step="0.01" solo dense :value="maxPrice"
                      @change="event => $emit('newMax', event)"></v-text-field>
      </v-col>
    </v-row>
  </v-container>
</template>

<style lang="scss" scoped>
@import "~/src/styles/grid-breakpoints.scss";

// Have to use this as using order-md-x causes conflicts between vuetify and bootstrap breakpoints
@media (min-width: map-get($grid-breakpoints, "lg")) {
  .filter-business {
    order: 1;
  }
  .min-price {
    order: 2;
  }
  .date-range {
    order: 3;
  }
  .max-price {
    order: 4;
  }
}
</style>

<script>
import {constants} from '@/constants';

export default {
  name: "listingSearchFilter",
  data() {
    return {
      businesses: constants.BUSINESSES.SHORT_LONG_TYPES,
      todayDate: null,
      menu: false,
      shownChips: 1,

      filterDates: [],
      businessTypes: []
    }
  },
  watch: {
    filterDates() {
      this.$emit('newDates', this.filterDates);
    },
    businessTypes() {
      this.$emit('newTypes', this.businessTypes);
    }
  },
  props: {
    minPrice: {
      type: Number,
      required: false
    },
    maxPrice: {
      type: Number,
      required: false
    },
  },
  /**
   * Sets sets the todayDate variable to be used when restricting date ranges
   */
  mounted() {
    let [d, m, y] = new Date().toLocaleDateString("en-NZ").split("/");
    if (d.length < 2) {
      d = "0" + d
    }
    this.todayDate = y + "-" + m + "-" + d;
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

        if (this.filterDates.length == 1) {
          return "Before: " + new Date(this.filterDates[0]).toLocaleDateString('en-NZ', options);
        } else if (this.filterDates.length == 2) {
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