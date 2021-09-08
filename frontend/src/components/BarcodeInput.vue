<template>
<v-form>
  <label>Barcode</label>
  <div class="d-flex">
    <v-text-field class="mr-2" label="Barcode No." placeholder="EAN-13 Barcode" maxLength="13" v-model="barcode" solo dense />
    <v-btn v-on:click="setProductInformation" :disabled="barcode.length!=13" :loading="apiIsLoading">Find</v-btn>
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
      isPalmOilFree: false,
      isVegan: false,
      isVegetarian: false,
      isGlutenFree: false,
      isDairyFree: false,
      errorMessage: null,
      apiIsLoading:false
    }
  },
  methods: {
    /**
     * Calls Open Food Facts API with User Barcode
     */
    getNutritionalInformationWithBarcode: async function (barcode) {
      this.apiIsLoading = true;
      try {
        const response = await Api.getOpenFoodFacts(barcode);
        return response.data;
      } catch (err) {
        this.errorMessage = err.userFacingErrorMessage;
      } finally {
        this.apiIsLoading = false;
      }

    },

    /**
     * Sets data variable with Data from open food facts API, Includes calling methods that parse information into required format
     */
    setProductInformation: async function() {
      const data = await this.getNutritionalInformationWithBarcode(this.barcode);
      if (data.status == 0) {
        this.errorMessage = "Product Not Found: Please enter details manually";
      } else {
        this.errorMessage = null;
        this.name = data.product.product_name;
        this.manufacturer = data.product.brands;
        this.nutriScore = data.product.nutriscore_grade.toUpperCase();
        this.novaGroup = data.product.nova_group;
        if (data.product.nutrient_levels) {
          const nutrient_levels = data.product.nutrient_levels;
          this.setNutritionalLevelInformation(nutrient_levels);
        }
        if (data.product.ingredients_analysis_tags) {
          const ingredients_analysis_tags = data.product.ingredients_analysis_tags;
          this.setIngredientAnalysisInformation(ingredients_analysis_tags);
        }
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
            this.isPalmOilFree = true;
            break;

          case 'en:vegan':
            this.isVegan = true;
            break;

          case 'en:vegetarian':
            this.isVegetarian = true;
            break;

          case 'en:gluten-free':
            this.isGlutenFree = true;
            break;

          case 'en:dairy-free':
            this.isDairyFree = true;
            break;
        }
      }
    },
  }
}
</script>

<style scoped>

</style>
