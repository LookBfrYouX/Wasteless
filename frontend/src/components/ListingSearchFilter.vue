<template>
  <div class="container col-6">
    <v-row align="end">
      <v-col cols="12" md="8" class="filter-business">
        <v-subheader>Filter businesses</v-subheader>
        <v-select
            v-model="filteredBusinesses"
            :items="businesses"
            label="Select"
            multiple
            solo
            dense
            prepend-inner-icon="business"
        >
          <template v-slot:selection="{ item, index }">
            <v-chip v-if="index < shownChips" small>
              <span>{{ item }}</span>
            </v-chip>
            <span
                v-if="index === shownChips"
                class="grey--text text-caption"
            >
              (+{{ filteredBusinesses.length - shownChips }} others)
            </span>
          </template>
        </v-select>
      </v-col>

      <v-col cols="12" md="8" class="date-range">
        <v-subheader>Date range</v-subheader>
        <v-menu
            ref="menu"
            v-model="menu"
            :close-on-content-click="false"
            :return-value.sync="dates"
            offset-y
            min-width="auto"
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
              v-model="dates"
              range
              @change="$refs.menu.save(dates)"
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
                @click="$refs.menu.save(dates)"
            >
              OK
            </v-btn>
          </v-date-picker>
        </v-menu>
      </v-col>

      <v-col cols="12" md="4" class="min-price">
        <v-subheader>Min price</v-subheader>
        <v-text-field label="Min" type="number" min="0.01"
                      max="10000000" step="0.01" solo dense></v-text-field>
      </v-col>

      <v-col cols="12" md="4" class="max-price">
        <v-subheader>Max price</v-subheader>
        <v-text-field label="Max" type="number" min="0.01"
                      max="10000000" step="0.01" solo dense></v-text-field>
      </v-col>
    </v-row>
  </div>
</template>

<style lang="scss" scoped>
@import "~/src/styles/grid-breakpoints.scss";

@media (min-width: map-get($grid-breakpoints, "md")) {
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
      businesses: Object.keys(constants.BUSINESSES.SHORT_LONG_TYPES),
      filteredBusinesses: [],
      dates: [],
      min: null,
      max: null,
      menu: false,
      shownChips: 1,
    }
  },
  computed: {
    /**
     * Returns formatted text for dates that will fit in a small text box
     */
    dateText() {
      let options = {year: undefined, month: 'numeric', day: 'numeric'};
      if (new Date(this.dates[1]).getFullYear() !== new Date().getFullYear()) {
        options = {year: 'numeric', month: 'numeric', day: 'numeric'};
      }

      if (this.dates.length == 1) {
        return "Before: " + new Date(this.dates[0]).toLocaleDateString('nz-NZ', options);
      } else if (this.dates.length == 2) {
        return new Date(this.dates[0]).toLocaleDateString('nz-NZ', options) + " to " +
            new Date(this.dates[1]).toLocaleDateString('nz-NZ', options);
      }
      return null;
    }
  }
}
</script>