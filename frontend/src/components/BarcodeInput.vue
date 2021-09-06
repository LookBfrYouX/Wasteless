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
      saturatedFat: "",
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
    getNutritionalInformationWithBarcode: async function (barcode) {
      try {
        const response = await Api.getOpenFoodFacts(barcode);
        return response.data;
      } catch (err) {
        this.errorMessage = err.userFacingErrorMessage;
        return null;
      }

    },

    /**
     * Sets data variable with Data from open food facts API, Includes calling methods that parse information into required format
     */
    setProductInformation: async function() {
      const data = await this.getNutritionalInformationWithBarcode(this.barcode);
      if (data.status == 0) {
        this.errorMessage = data.status_verbose;
      } else {
        this.errorMessage = null;
        this.name = data.product.product_name;
        console.log(this.name)
        this.manufacturer = data.product.brands;
        this.nutriScore = data.product.nutriscore_grade.toUpperCase();
        this.novaGroup = data.product.nova_group;
        const nutrient_levels = data.product.nutrient_levels;
        const ingredients_analysis_tags = data.product.ingredients_analysis_tags;
        this.setNutritionalLevelInformation(nutrient_levels);
        this.setIngredientAnalysisInformation(ingredients_analysis_tags);
      }
    },

    /**
     * Parses and sets nutritional level tags in required format
     */
    setNutritionalLevelInformation: function(nutrient_levels) {
      this.fat = nutrient_levels.fat.toUpperCase();
      this.saturatedFat = nutrient_levels["saturated-fat"].toUpperCase();
      this.sugars = nutrient_levels.sugars.toUpperCase();
      this.salt = nutrient_levels.salt.toUpperCase();
    },

    /**
     * Parses and sets Analysis level tags (e.g. gluten free, dairy free etc) in required format
     */
    setIngredientAnalysisInformation: function(ingredients_analysis_tags) {
      for (const tag of ingredients_analysis_tags) {
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
