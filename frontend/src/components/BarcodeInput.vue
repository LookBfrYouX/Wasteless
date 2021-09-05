<template>
<v-form>
  <label>Barcode</label>
  <div class="d-flex">
    <v-text-field class="mr-2" label="Barcode No." placeholder="EAN-13 Barcode" maxLength="13" v-model="barcode" solo dense />
    <v-btn v-on:click="setProductInformation">Find</v-btn>
  </div>
  <div v-if="errorMessage != null" class="row mt-2">
    <div class="col">
      <p class="alert alert-warning">{{ errorMessage }}</p>
    </div>
  </div>
</v-form>
</template>

<script>
import {Api} from "@/Api";

export default {
  name: "BarcodeInput",
  data() {
    return {
      barcode: "",
      name: "",
      manufacturer: "",
      fat: "",
      saturated_fat: "",
      sugars: "",
      salt: "",
      novaGroup: "",
      nutriScore: "",
      palmOilFree: false,
      vegan: false,
      vegetarian: false,
      glutenFree: false,
      dairyFree: false,
      errorMessage: null
    }
  },
  methods: {
    /**
     * Calls Open Food Facts API with User Barcode
     */
    getUsedNutritionalInformationWithBarcode: async function (barcode) {
      const response = await Api.getOpenFoodFacts(barcode);
      return response.data;
    },


    /**
     * Sets data variable with Data from open food facts API, Includes calling methods that parse information into required format
     */
    setProductInformation: async function() {
      const data = await this.getUsedNutritionalInformationWithBarcode(this.barcode);
      this.name = data.product.product_name;
      this.manufacturer = data.product.brands;
      this.nutriScore = data.product.nutriscore_grade;
      this.novaGroup = data.product.nova_group;
      const nutrient_levels_tags = data.product.nutrient_levels_tags;
      const ingredients_analysis_tags = data.product.ingredients_analysis_tags;
      this.setNutritionalLevelInformation(nutrient_levels_tags);
      this.setIngredientAnalysisInformation(ingredients_analysis_tags);
    },

    /**
     * Parses and sets nutritional level tags in required format
     */
    setNutritionalLevelInformation: function(nutrient_levels_tags) {
      if (nutrient_levels_tags.includes("en:fat-in-low-quantity")) {
        this.fat = "LOW";
      } else if (nutrient_levels_tags.includes("en:fat-in-moderate-quantity")) {
        this.fat = "MODERATE";
      } else if (nutrient_levels_tags.includes("en:fat-in-high-quantity")) {
        this.fat = "HIGH";
      }

      if (nutrient_levels_tags.includes("en:saturated-fat-in-low-quantity")) {
        this.saturated_fat = "LOW";
      } else if (nutrient_levels_tags.includes("en:saturated-fat-in-moderate-quantity")) {
        this.saturated_fat = "MODERATE";
      } else if (nutrient_levels_tags.includes("en:saturated-fat-in-high-quantity")) {
        this.saturated_fat = "HIGH";
      }

      if (nutrient_levels_tags.includes("en:sugars-in-low-quantity")) {
        this.sugars = "LOW";
      } else if (nutrient_levels_tags.includes("en:sugars-in-moderate-quantity")) {
        this.sugars = "MODERATE";
      } else if (nutrient_levels_tags.includes("en:sugars-in-high-quantity")) {
        this.sugars = "HIGH";
      }

      if (nutrient_levels_tags.includes("en:salt-in-low-quantity")) {
        this.salt = "LOW";
      } else if (nutrient_levels_tags.includes("en:salt-in-moderate-quantity")) {
        this.salt = "MODERATE";
      } else if (nutrient_levels_tags.includes("en:salt-in-high-quantity")) {
        this.salt = "HIGH";
      }
    },

    /**
     * Parses and sets Analysis level tags (e.g. gluten free, dairy free etc) in required format
     */
    setIngredientAnalysisInformation: function(ingredients_analysis_tags) {
      for (const tag in ingredients_analysis_tags) {
        switch (tag) {
          case 'en:palm-oil-free':
            this.palmOilFree = true;
            break;

          case 'en:vegan':
            this.vegan = true;
            break;

          case 'en:vegetarian':
            this.vegetarian = true;
            break;

          case 'en:gluten-free':
            this.glutenFree = true;
            break;

          case 'en:dairy-free':
            this.dairyFree = true;
            break;
        }
      }
    },
  }
}
</script>

<style scoped>

</style>
