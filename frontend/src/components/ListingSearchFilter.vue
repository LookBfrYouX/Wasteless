<template>
  <div class="container">
    <v-row align="end">
      <v-col cols="12" md="3">
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
      <v-col cols="12" md="3">
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
                v-model="dates"
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
    </v-row>

    <v-row>
      <v-col cols="12" md="3">
        <v-subheader>Min price</v-subheader>
        <v-text-field label="Min" type="number" min="0.01"
                      max="10000000" step="0.01" solo dense></v-text-field>

      </v-col>
      <v-col cols="12" md="3">
        <v-subheader>Max price</v-subheader>
        <v-text-field label="Max" type="number" min="0.01"
                      max="10000000" step="0.01" solo dense></v-text-field>
      </v-col>
    </v-row>
  </div>
</template>

<script>
export default {
  name: "listingSearchFilter",
  data() {
    return {
      businesses: ["Hospitality", "Retail", "Charity",
        "Non-profit"],
      filteredBusinesses: [],
      dates: [],
      min: null,
      max: null,
      menu: false,
      shownChips: 1,
    }
  },
}
</script>