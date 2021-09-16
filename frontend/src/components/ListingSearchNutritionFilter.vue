<template>
  <v-expansion-panel>
    <v-expansion-panel-header>Nutrition filtering</v-expansion-panel-header>
    <v-expansion-panel-content>
      <v-row align="center">
        <v-col cols="12" lg="6">
          <v-subheader>Diets</v-subheader>
          <v-select
              v-model="diets"
              :items=dietOptions
              label="Select"
              multiple
              solo
              dense
              clearable
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
              (+{{ diets.length - shownChips }} others)
            </span>
            </template>
          </v-select>
        </v-col>

        <v-col cols="12" lg="3" class="my-0 py-0">
          <v-subheader>Min nova group</v-subheader>
          <v-select
              v-model="minNovaGroup"
              :items=minNovaGroups
              label="Select"
              solo
              dense
              clearable
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>

        <v-col cols="12" lg="3" class="my-0 py-0">
          <v-subheader>Max nova group</v-subheader>
          <v-select
              v-model="maxNovaGroup"
              :items=maxNovaGroups
              label="Select"
              solo
              dense
              clearable
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>

        <v-col cols="12" lg="3" class="my-0 py-0">
          <v-subheader>Fat levels</v-subheader>
          <v-select
              v-model="fats"
              :items=nutritionOptions
              label="Select"
              multiple
              solo
              dense
              clearable
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
              (+{{ fats.length - shownChips }} others)
            </span>
            </template>
          </v-select>
        </v-col>

        <v-col cols="12" lg="3" class="my-0 py-0">
          <v-subheader>Saturated fat levels</v-subheader>
          <v-select
              v-model="saturatedFats"
              :items=nutritionOptions
              label="Select"
              multiple
              solo
              dense
              clearable
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
              (+{{ saturatedFats.length - shownChips }} others)
            </span>
            </template>
          </v-select>
        </v-col>

        <v-col cols="12" lg="3" class="my-0 py-0">
          <v-subheader>Min nutriscore</v-subheader>
          <v-select
              v-model="minNutriScore"
              :items=minNutriScores
              label="Select"
              solo
              dense
              clearable
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>

        <v-col cols="12" lg="3" class="my-0 py-0">
          <v-subheader>Max nutriscore</v-subheader>
          <v-select
              v-model="maxNutriScore"
              :items=maxNutriScores
              label="Select"
              solo
              dense
              clearable
              item-text="short"
              item-value="long"
          >
          </v-select>
        </v-col>

        <v-col cols="12" lg="3" class="my-0 py-0">
          <v-subheader>Sugar levels</v-subheader>
          <v-select
              v-model="sugars"
              :items=nutritionOptions
              label="Select"
              multiple
              solo
              dense
              clearable
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
              (+{{ sugars.length - shownChips }} others)
            </span>
            </template>
          </v-select>
        </v-col>

        <v-col cols="12" lg="3" class="my-0 py-0">
          <v-subheader>Salt levels</v-subheader>
          <v-select
              v-model="salts"
              :items=nutritionOptions
              label="Select"
              multiple
              solo
              dense
              clearable
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
              (+{{ salts.length - shownChips }} others)
            </span>
            </template>
          </v-select>
        </v-col>
      </v-row>
    </v-expansion-panel-content>
  </v-expansion-panel>
</template>

<script>
export default {
  name: "ListingSearchNutritionFilter",
  data() {
    return {
      shownChips: 1,
      novaGroups: [1, 2, 3, 4],
      nutriScoreOptions: ['A', 'B', 'C', 'D', 'E'],
      nutritionOptions: ['Unknown', 'Low', 'Moderate', 'High'],
      dietOptions: ['Gluten Free', 'Dairy Free', 'Vegetarian', 'Vegan', 'Palm Oil Free'],

      //Selected elements
      minNovaGroup: null,
      maxNovaGroup: null,
      minNutriScore: null,
      maxNutriScore: null,
      diets: [],
      fats: [],
      saturatedFats: [],
      sugars: [],
      salts: [],
    }
  },

  /**
   * Emitting events to be caught in parent component
   */
  watch: {
    minNovaGroup() {
      this.$emit('newMinNovaGroup', this.minNovaGroup);
    },
    maxNovaGroup() {
      this.$emit('newMaxNovaGroup', this.maxNovaGroup);
    },
    minNutriScore() {
      this.$emit('newMinNutriScore', this.minNutriScore);
    },
    maxNutriScore() {
      this.$emit('newMaxNutriScore', this.maxNutriScore);
    },
    diets() {
      this.$emit('newDiets', this.diets);
    },
    fats() {
      this.$emit('newFats', this.fats);
    },
    saturatedFats() {
      this.$emit('newSaturatedFats', this.saturatedFats);
    },
    sugars() {
      this.$emit('newSugars', this.sugars);
    },
    salts() {
      this.$emit('newSalts', this.salts);
    },
  },

  computed: {
    /**
     * Restricts input for minNovaGroup
     */
    minNovaGroups: function () {
      return this.maxNovaGroup === null ? this.novaGroups : this.novaGroups.slice(0,
          this.maxNovaGroup);
    },
    /**
     * Restricts input for maxNovaGroup
     */
    maxNovaGroups: function () {
      return this.minNovaGroup === null ? this.novaGroups : this.novaGroups.slice(
          this.minNovaGroup - 1);
    },
    /**
     * Restricts input for minNutriScore
     */
    minNutriScores: function () {
      return this.maxNutriScore === null ? this.nutriScoreOptions : this.nutriScoreOptions.slice(0,
          this.nutriScoreOptions.indexOf(this.maxNutriScore) + 1);
    },
    /**
     * Restricts input for maxNutriScore
     */
    maxNutriScores: function () {
      return this.minNutriScore === null ? this.nutriScoreOptions : this.nutriScoreOptions.slice(
          this.nutriScoreOptions.indexOf(this.minNutriScore));
    }
  }
}
</script>