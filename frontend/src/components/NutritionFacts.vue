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
                  :src="novaGroupImgSrc">
              </v-img>
              <v-img
                  contain
                  max-width="60%"
                  :src="nutriScoreImgSrc">
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
                      <span class="material-icons red--text">
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
import nutriScoreAImage from "@/../assets/images/nutriscore_A.png";

export default {
  name: "NutritionFacts",

  data() {
    return {
      novaGroupImgSrc: novaGroup1Image,
      nutriScoreImgSrc: nutriScoreAImage
    }
  }
}

</script>
<style>
div.v-list-item {
  pointer-events: none;
}
</style>