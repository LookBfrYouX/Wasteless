<template>
  <v-expansion-panels>
    <v-expansion-panel>
      <v-expansion-panel-header>
        <h5 class="text-muted">
          Nutrition Facts
        </h5>
      </v-expansion-panel-header>
      <v-expansion-panel-content>
        <v-row>
          <v-col cols="12" md="6">
            <div class="d-flex justify-space-around mb-4">
              <v-img
                  contain
                  max-width="20%"
                  :src="loadNovaGroupImg()">
              </v-img>
              <v-img
                  contain
                  max-width="60%"
                  :src="loadNutriScoreImg()">
              </v-img>
            </div>
            <div>
              Dietary Requirements
            </div>
            <div>
              <v-chip>
                Gluten Free
              </v-chip>
            </div>

          </v-col>
          <v-col cols="12" md="6">
            <div>
              Nutrients Levels
            </div>
            <div>
              <v-list>
                <v-list-item-group>
                  <v-list-item class="pl-0">
                    <v-list-item-icon>
                      <span class="material-icons"
                            :class="[`${color}--text`]">
                        circle
                      </span>
                    </v-list-item-icon>
                    <v-list-item-content>
                      <v-list-item-title>Fat</v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item class="pl-0">
                    <v-list-item-icon>
                      <span class="material-icons yellow--text">
                        circle
                      </span>
                    </v-list-item-icon>
                    <v-list-item-content>
                      <v-list-item-title>Saturated Fat</v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item class="pl-0">
                    <v-list-item-icon>
                      <span class="material-icons green--text">
                        circle
                      </span>
                    </v-list-item-icon>
                    <v-list-item-content>
                      <v-list-item-title>Sugars</v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item class="pl-0">
                    <v-list-item-icon>
                      <span class="material-icons yellow--text">
                        circle
                      </span>
                    </v-list-item-icon>
                    <v-list-item-content>
                      <v-list-item-title>Salt</v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                </v-list-item-group>
              </v-list>
            </div>
          </v-col>
        </v-row>
      </v-expansion-panel-content>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<script>
import novaGroup1Image from "@/../assets/images/nova-group-1.svg";
import novaGroup2Image from "@/../assets/images/nova-group-2.svg";
import novaGroup3Image from "@/../assets/images/nova-group-3.svg";
import novaGroup4Image from "@/../assets/images/nova-group-4.svg";
import novaGroupNullImage from "@/../assets/images/nova-group-null.svg";
import nutriScoreAImage from "@/../assets/images/nutriscore_A.png";
import nutriScoreBImage from "../../assets/images/nutriscore_B.png";
import nutriScoreCImage from "../../assets/images/nutriscore_C.png";
import nutriScoreDImage from "../../assets/images/nutriscore_D.png";
import nutriScoreEImage from "../../assets/images/nutriscore_E.png";
import nutriScoreNoneImage from "../../assets/images/nutriscore_none.png";

export default {
  name: "NutritionFacts",

  data() {
    return {
      color: "yellow",
    }
  },

  props: {
    nutritionFacts: {
      default: function() {
        return {
          product: {
            nutriScore: "B",
            novaGroup: 4,
            fat: "HIGH",
            saturatedFat: "MODERATE",
            sugars: "LOW",
            salt: "HIGH",
            isGlutenFree: true,
            isDairyFree: true,
            isVegetarian: false,
            isVegan: false,
            isPalmOilFree: true,
          }
        }
      }
    }
  },

  methods: {
    loadNovaGroupImg() {
      const imgSrcDict = {
        1: novaGroup1Image,
        2: novaGroup2Image,
        3: novaGroup3Image,
        4: novaGroup4Image,
        null: novaGroupNullImage
      };
      return imgSrcDict[this.nutritionFacts.product.novaGroup];
    },

    loadNutriScoreImg() {
      const imgSrcDict = {
        'A': nutriScoreAImage,
        'B': nutriScoreBImage,
        'C': nutriScoreCImage,
        'D': nutriScoreDImage,
        'E': nutriScoreEImage,
        null: nutriScoreNoneImage
      };
      return imgSrcDict[this.nutritionFacts.product.nutriScore];
    }
  }
}

</script>
<style>
div.v-list-item {
  pointer-events: none;
}
</style>