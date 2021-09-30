<template>
  <v-expansion-panel>
    <v-expansion-panel-header>Nutrition filtering</v-expansion-panel-header>
    <v-expansion-panel-content>
      <v-row align="center">
        <v-col cols="12" lg="6" class="diets">
          <v-subheader>Diets</v-subheader>
          <v-select
              v-model="diets"
              :items="dietOptions"
              :return-object="true"
              item-text="text"
              item-value="key"
              label="Select"
              multiple
              solo
              dense
              clearable
          >
            <template v-slot:selection="{ item, index }">
              <v-chip v-if="index < shownChips" small>
                <span>{{ item.text }}</span>
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

        <v-col cols="12" lg="3" class="my-0 py-0 min-nova-group">
          <v-subheader>Min NovaGroup</v-subheader>
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

        <v-col cols="12" lg="3" class="my-0 py-0 max-nova-group">
          <v-subheader>Max NovaGroup</v-subheader>
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

        <v-col cols="12" lg="3" class="my-0 py-0 min-nutri-score">
          <v-subheader>Min Nutri-Score</v-subheader>
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

        <v-col cols="12" lg="3" class="my-0 py-0 max-nutri-score">
          <v-subheader>Max Nutri-Score</v-subheader>
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

        <v-col cols="12" lg="3" class="my-0 py-0 fat-levels">
          <v-subheader>Fat levels</v-subheader>
          <v-select
              v-model="fat"
              :items=nutrientLevelOptions
              label="Select"
              multiple
              solo
              dense
              clearable
              item-text="text"
              item-value="key"
          >
            <template v-slot:selection="{ item, index }">
              <v-chip v-if="index < shownChips" small>
                <span>{{ item.text }}</span>
              </v-chip>
              <span
                  v-if="index === shownChips"
                  class="grey--text text-caption"
              >
              (+{{ fat.length - shownChips }} others)
            </span>
            </template>
          </v-select>
        </v-col>

        <v-col cols="12" lg="3" class="my-0 py-0 saturated-fat-levels">
          <v-subheader>Saturated fat levels</v-subheader>
          <v-select
              v-model="saturatedFat"
              :items=nutrientLevelOptions
              label="Select"
              multiple
              solo
              dense
              clearable
              item-text="text"
              item-value="key"
          >
            <template v-slot:selection="{ item, index }">
              <v-chip v-if="index < shownChips" small>
                <span>{{ item.text }}</span>
              </v-chip>
              <span
                  v-if="index === shownChips"
                  class="grey--text text-caption"
              >
              (+{{ saturatedFat.length - shownChips }} others)
            </span>
            </template>
          </v-select>
        </v-col>

        <v-col cols="12" lg="3" class="my-0 py-0 sugar-levels">
          <v-subheader>Sugar levels</v-subheader>
          <v-select
              v-model="sugars"
              :items=nutrientLevelOptions
              label="Select"
              multiple
              solo
              dense
              clearable
              item-text="text"
              item-value="key"
          >
            <template v-slot:selection="{ item, index }">
              <v-chip v-if="index < shownChips" small>
                <span>{{ item.text }}</span>
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

        <v-col cols="12" lg="3" class="my-0 py-0 salt-levels">
          <v-subheader>Salt levels</v-subheader>
          <v-select
              v-model="salts"
              :items=nutrientLevelOptions
              label="Select"
              multiple
              solo
              dense
              clearable
              item-text="text"
              item-value="key"
          >
            <template v-slot:selection="{ item, index }">
              <v-chip v-if="index < shownChips" small>
                <span>{{ item.text }}</span>
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
      nutrientLevelOptions: [
        {
          key: "LOW",
          text: "Low"
        }, {
        key: "MODERATE",
          text: "Moderate"
        }, {
          key: "HIGH",
          text: "High"
        }
      ],
      dietOptions: [
        {
          key: "isGlutenFree",
          text: "Gluten Free"
        },
        {
          key: "isDairyFree",
          text: "Dairy Free"
        },
        {
          key: "isVegetarian",
          text: "Vegetarian"
        },
        {
          key: "isVegan",
          text: "Vegan"
        },
        {
          key: "isPalmOilFree",
          text: "Palm Oil Free"
        }
      ],

      //Selected elements
      minNovaGroup: null,
      maxNovaGroup: null,
      minNutriScore: null,
      maxNutriScore: null,
      diets: [],
      fat: [],
      saturatedFat: [],
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
      const returnObj = {};
      this.dietOptions.forEach(({ key }) => {
        // Initialize all diet options to null
        returnObj[key] = null;
      });

      this.diets.forEach(({ key }) => {
        returnObj[key] = true;
      });

      // Sending false means must not include diet. Null means don't care about 
      // the value. Hoping that sending a value of null and not sending the param
      // at all are the same
      this.$emit('newDiets', returnObj);
    },
    fat() {
      this.$emit('newFat', this.fat);
    },
    saturatedFat() {
      this.$emit('newSaturatedFat', this.saturatedFat);
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
<style lang="scss" scoped>
@import "~/src/styles/grid-breakpoints.scss";

// CSS using bootstrap breakpoints but using order-md-x vuetify properties
// uses vuetify breakpoints
@media (min-width: map-get($grid-breakpoints, "lg")) {
  .diets {
    order: 0;
  }
  .min-nova-group {
    order: 1;
  }
  .max-nova-group {
    order: 2;
  }
  .fat-levels {
    order: 3;
  }
  .saturated-fat-levels {
    order: 4;
  }
  .min-nutri-score {
    order: 5;
  }
  .max-nutri-score {
    order: 6;
  }
  .sugar-levels {
    order: 7;
  }
  .salt-levels {
    order: 8;
  }
}
</style>