<template>
  <v-container>
    <v-row align="end">
      <v-col cols="12" md="4">
        <h4>Business type</h4>
        <v-select
            v-model="filteredBusinesses"
            :items="businesses"
            label="Select"
            multiple
            solo
            prepend-inner-icon="business"
        >
          <template v-slot:selection="{ item, index }">
            <v-chip v-if="index === 0">
              <span>{{ item }}</span>
            </v-chip>
            <span
                v-if="index === 1"
                class="grey--text text-caption"
            >
              (+{{ filteredBusinesses.length - 1 }} others)
            </span>
          </template>
        </v-select>
      </v-col>

      <v-col cols="12" md="4">
        <h4>Price range</h4>
        <v-item-group multiple>
          <v-row align="end">
            <v-col>
              <v-text-field label="Min" type="number" min="0.01"
                            max="10000000" step="0.01" solo></v-text-field>
            </v-col>
            <v-col>
              <v-text-field label="Max" type="number" min="0.01"
                            max="10000000" step="0.01" solo></v-text-field>
            </v-col>
          </v-row>
        </v-item-group>
      </v-col>

      <v-col cols="12" md="4">
        <v-item-group multiple>
          <v-row align="center">
            <v-col cols="10">
              <h4>Listing closes</h4>
            </v-col>
            <v-col cols="2" align="end">
              <v-tooltip bottom>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon
                      v-bind="attrs"
                      v-on="on"
                  >
                    help
                  </v-icon>
                </template>
                <span>One date = earliest, two dates = range</span>
              </v-tooltip>
            </v-col>
          </v-row>
        </v-item-group>
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
            ></v-text-field>
          </template>
          <v-date-picker
              v-model="dates"
              multiple
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
  </v-container>
</template>

<script>
export default {
  name: "listingSearchFilter",
  data() {
    return {
      businesses: ["Accommodation and Food Services", "Retail Trade", "Charitable organisation",
        "Non-profit organisation"],
      filteredBusinesses: [],
      dates: [],
      min: null,
      max: null,
      menu: false,
    }
  },
  watch: {
    dates() {
      if (this.dates.length > 2) {
        this.dates = this.dates.slice(1).slice(-2)
      }
    }
  }
}
</script>