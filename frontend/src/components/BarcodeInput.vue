<template>
<v-form>
  <div class="d-flex">
    <v-text-field
        v-model="barcode"
        :rules="[() => barcode.length <= 13 || 'Barcode must be 13 numbers']"
        class="mr-2"
        counter="13"
        dense
        label="Barcode Number"
        outlined
        prefix="EAN-13"
        type="number"
    />
    <v-btn v-on:click="setProductInformation">Fill</v-btn>
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
      errorMessage: null,
      info: {
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
      }
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

      if (data == null) return;
      if (data.status == 0) {
        this.errorMessage = data.status_verbose;
      } else {
        this.errorMessage = null;
        this.info.name = data.product.product_name;
        this.info.manufacturer = data.product.brands;
        this.info.nutriScore = data.product.nutriscore_grade.toUpperCase();
        this.info.novaGroup = data.product.nova_group;
        const nutrient_levels = data.product.nutrient_levels;
        const ingredients_analysis_tags = data.product.ingredients_analysis_tags;
        this.setNutritionalLevelInformation(nutrient_levels);
        this.setIngredientAnalysisInformation(ingredients_analysis_tags);
        console.log("HERE");
        this.$emit("info", this.info);
      }
    },

    /**
     * Parses and sets nutritional level tags in required format
     */
    setNutritionalLevelInformation: function(nutrient_levels) {
      this.info.fat = nutrient_levels.fat.toUpperCase();
      this.info.saturatedFat = nutrient_levels["saturated-fat"].toUpperCase();
      this.info.sugars = nutrient_levels.sugars.toUpperCase();
      this.info.salt = nutrient_levels.salt.toUpperCase();
    },

    /**
     * Parses and sets Analysis level tags (e.g. gluten free, dairy free etc) in required format
     */
    setIngredientAnalysisInformation: function(ingredients_analysis_tags) {
      for (const tag of ingredients_analysis_tags) {
        switch (tag) {
          case 'en:palm-oil-free':
            this.info.isPalmOilFree = true;
            break;

          case 'en:vegan':
            this.info.isVegan = true;
            break;

          case 'en:vegetarian':
            this.info.isVegetarian = true;
            break;

          case 'en:gluten-free':
            this.info.isGlutenFree = true;
            break;

          case 'en:dairy-free':
            this.info.isDairyFree = true;
            break;
        }
      }
    },
  }
}
</script>
