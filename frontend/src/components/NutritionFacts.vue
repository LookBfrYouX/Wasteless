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
              <div v-if="product.novaGroup">
                <img :src="loadNovaGroupImg"
                     class="nova"
                     :alt="`Nova Group is ${product.novaGroup}`"/>
              </div>
              <div v-else>
                Nova Group unknown
              </div>
              <div v-if="product.nutriScore">
                <img :src="loadNutriScoreImg"
                     class="nutri"
                     :alt="`Nutri-Score is ${product.nutriScore}`"/>
              </div>
              <div v-else>
                Nutri-Score unknown
              </div>
            </div>
            <div class="mb-2">
              Dietary Requirements
            </div>
            <allergy-chips :product="product"/>

          </v-col>
          <v-col cols="12" md="6">
            <div>
              Nutrients Levels
            </div>
            <div>
              <v-list>
                <v-list-item-group>
                  <nutrient-level-list-item name="Fat" :level="product.fat"/>
                  <nutrient-level-list-item name="Saturated Fat" :level="product.saturatedFat"/>
                  <nutrient-level-list-item name="Sugars" :level="product.sugars"/>
                  <nutrient-level-list-item name="Salt" :level="product.salt"/>
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
import AllergyChips from "@/components/AllergyChips";
import NutrientLevelListItem from "@/components/NutrientLevelListItem";
import novaGroup1Image from "@/../assets/images/nova-group-1.svg";
import novaGroup2Image from "@/../assets/images/nova-group-2.svg";
import novaGroup3Image from "@/../assets/images/nova-group-3.svg";
import novaGroup4Image from "@/../assets/images/nova-group-4.svg";
import nutriScoreAImage from "@/../assets/images/nutriscore_A.png";
import nutriScoreBImage from "../../assets/images/nutriscore_B.png";
import nutriScoreCImage from "../../assets/images/nutriscore_C.png";
import nutriScoreDImage from "../../assets/images/nutriscore_D.png";
import nutriScoreEImage from "../../assets/images/nutriscore_E.png";

export default {
  name: "NutritionFacts",

  components: {
    AllergyChips,
    NutrientLevelListItem
  },

  data() {
    return {
      nutrientsLevels: null,
    }
  },

  props: {
    product: {
      default: function() {
        return {
          nutriScore: null,
          novaGroup: null,
          fat: null,
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
  },

  computed: {
    /**
     * Load Nova Group image. Called only when novaScore is not null.
     * @returns {*}
     */
    loadNovaGroupImg() {
      const imgSrcDict = {
        1: novaGroup1Image,
        2: novaGroup2Image,
        3: novaGroup3Image,
        4: novaGroup4Image,
      };
      return imgSrcDict[this.product.novaGroup];
    },

    /**
     * Load Nutri-Score image. Called only when nutriScore is not null.
     * @returns {*}
     */
    loadNutriScoreImg() {
      const imgSrcDict = {
        'A': nutriScoreAImage,
        'B': nutriScoreBImage,
        'C': nutriScoreCImage,
        'D': nutriScoreDImage,
        'E': nutriScoreEImage,
      };
      return imgSrcDict[this.product.nutriScore];
    },
  }
}

</script>
<style scoped>
img.nova {
  max-height: 5em;
  max-width: 100%;
  min-width: 0;
  object-fit: contain;
}
img.nutri {
  padding-left: 0.5em;
  max-width: min(100%, 10em);
  min-width: 0;
  object-fit: contain;
}
</style>