<template>
  <v-expansion-panel>
    <v-expansion-panel-header>Nutrition filtering</v-expansion-panel-header>
    <v-expansion-panel-content>
      <v-row align="center">
        <v-col cols="12" lg="6">
          <v-subheader>Diets</v-subheader>
          <v-select
              v-model="selected"
              :items=diets
              label="Select"
              multiple
              solo
              dense
              item-text="short"
              item-value="long"
          >
            <template v-slot:selection="{ item, index }">
              <v-chip v-if="index < shownChips" small>
                <span>{{ item }}</span>
              </v-chip>
              <span
                  v-if="index === shownChips"
                  class="grey--text text-caption"
              >
              (+{{ selected.length - shownChips }} others)
            </span>
            </template>
          </v-select>
        </v-col>

        <v-col cols="6" lg="3" class="my-0 py-0">
          <v-subheader>Min NovaGroup</v-subheader>
          <v-select
              :items=novaGroups
              label="Select"
              multiple
              solo
              dense
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>

        <v-col cols="6" lg="3" class="my-0 py-0">
          <v-subheader>Max NovaGroup</v-subheader>
          <v-select
              :items=novaGroups
              label="Select"
              multiple
              solo
              dense
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>

        <v-col cols="6" lg="3" class="my-0 py-0">
          <v-subheader>Fat</v-subheader>
          <v-select
              :items=nutritionOptions
              label="Select"
              multiple
              solo
              dense
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>

        <v-col cols="6" lg="3" class="my-0 py-0">
          <v-subheader>Saturated fat</v-subheader>
          <v-select
              :items=nutritionOptions
              label="Select"
              multiple
              solo
              dense
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>

        <v-col cols="6" lg="3" class="my-0 py-0">
          <v-subheader>Min NutriScore</v-subheader>
          <v-select
              :items=nutriScoreOptions
              label="Select"
              multiple
              solo
              dense
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>

        <v-col cols="6" lg="3" class="my-0 py-0">
          <v-subheader>Max NutriScore</v-subheader>
          <v-select
              :items=nutriScoreOptions
              label="Select"
              multiple
              solo
              dense
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>

        <v-col cols="6" lg="3" class="my-0 py-0">
          <v-subheader>Sugars</v-subheader>
          <v-select
              :items=nutritionOptions
              label="Select"
              multiple
              solo
              dense
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>

        <v-col cols="6" lg="3" class="my-0 py-0">
          <v-subheader>Salt</v-subheader>
          <v-select
              :items=nutritionOptions
              label="Select"
              multiple
              solo
              dense
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>
      </v-row>
    </v-expansion-panel-content>
  </v-expansion-panel>
</template>

<style scoped>
.v-expansion-panel-content__wrap {
  padding: 0 !important;
}
</style>

<script>
export default {
  name: "ListingSearchNutritionFilter",
  data() {
    return {
      selected: [],
      shownChips: 1,
      novaGroups: [1, 2, 3, 4],
      nutriScoreOptions: ['A', 'B', 'C', 'D', 'E'],
      nutritionOptions: ['Unknown', 'Low', 'Moderate', 'High'],
      diets: ['Gluten Free', 'Dairy Free', 'Vegetarian', 'Vegan', 'Palm Oil Free'],
    }
  },
}
</script>