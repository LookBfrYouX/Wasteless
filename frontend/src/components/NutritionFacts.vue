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
            <div class="d-flex justify-space-around mb-2">
              <div class="novaWrapper">
                <div class="text-no-wrap overflow-hidden">
                  Nova Group
                  <v-tooltip top>
                    <template v-slot:activator="{ on }">
                      <v-icon
                          v-on="on"
                          color="blue"
                      >
                        information
                      </v-icon>
                    </template>
                    <span>The NOVA classification assigns a group to food products based on how much processing they have been through.</span>
                  </v-tooltip>
                </div>
                <div v-if="product.novaGroup">
                  <img :src="loadNovaGroupImg"
                       class="nova"
                       :alt="`Nova Group is ${product.novaGroup}`"/>
                  <div class="text-caption font-weight-light mb-3">
                    {{ getNovaGroupDescription }}
                  </div>
                </div>
                <div v-else
                     class="mt-2">
                  Unknown
                </div>
              </div>

              <div class="nutriWrapper">
                <div class="text-no-wrap mb-1">
                  Nutri-Score
                  <v-tooltip top>
                    <template v-slot:activator="{ on }">
                      <v-icon
                          v-on="on"
                          color="blue"
                      >
                        information
                      </v-icon>
                    </template>
                    <span>The Nutri-Score is a logo that shows the nutritional quality of food products with A to E grades.</span>
                  </v-tooltip>
                </div>
                <div v-if="product.nutriScore">
                  <img :src="loadNutriScoreImg"
                       class="nutri"
                       :alt="`Nutri-Score is ${product.nutriScore}`"/>
                </div>
                <div v-else>
                  Unknown
                </div>
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
import {constants} from "@/constants";
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
      required: true
    }
  },

  computed: {
    /**
     * Load Nova Group image. Called only when novaGroup is not null.
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
     * Load NutriScore image. Called only when nutriScore is not null.
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

    getNovaGroupDescription() {
      const novaGroupItem = constants.PRODUCT.NOVA_GROUP.filter(el => el.value === this.product.novaGroup);
      return novaGroupItem[0].description
    },
  }
}

</script>
<style scoped>
div.novaWrapper {
  max-width: 40%;
}
div.nutriWrapper {
  max-width: 40%;
}
img.nova {
  max-height: 5em;
  max-width: 100%;
  min-width: 0;
  object-fit: contain;
}
img.nutri {
  max-width: min(100%, 10em);
  min-width: 0;
  object-fit: contain;
}
</style>