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
        v-on:keydown.enter="() => barcode.length == 13 && !apiIsLoading && setProductInformation()"
    />
    <v-btn
        v-on:click="setProductInformation"
        :disabled="barcode.length != 13"
        :loading="apiIsLoading"
    >
      Fill
    </v-btn>
  </div>
  <v-container class="pa-0">
    <v-btn
        block
        v-on:click="showBarcodeScanner"
    >
      <v-icon left>
        photo_camera
      </v-icon>
      Scan barcode using Camera
    </v-btn>
  </v-container>
  <StreamBarcodeReader
      v-if="showScanner"
      @decode="onDecode"
      @loaded="onLoaded"
  ></StreamBarcodeReader>
  <div v-if="errorMessage != null" class="row mt-2">
    <div class="col">
      <p class="alert alert-warning">{{ errorMessage }}</p>
    </div>
  </div>
</v-form>
</template>

<script>
import {Api} from "@/Api";
import {StreamBarcodeReader} from "vue-barcode-reader";

/**
 * No props. When autofill is triggered, the parsed information is sent through
 * an `info` event. The barcode itself is not part of this
 */
export default {
  name: "BarcodeInput",
  components: {
    StreamBarcodeReader
  },
  data() {
    return {
      barcode: "",
      errorMessage: null,
      apiIsLoading: false,
      showScanner: false,
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
    showBarcodeScanner: function () {
      this.showScanner = true;
    },

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
      
      if (data == null || data.status == 0) {
        this.errorMessage = "Product Not Found: Please enter details manually";
      } else {
        this.errorMessage = null;
        this.info.name = data.product.product_name;
        this.info.manufacturer = data.product.brands;
        if (data.product.nutriscore_grade != null) {
          this.info.nutriScore = data.product.nutriscore_grade.toUpperCase();
        }
        this.info.novaGroup = data.product.nova_group;
        if (data.product.nutrient_levels) {
          const nutrient_levels = data.product.nutrient_levels;
          this.setNutritionalLevelInformation(nutrient_levels);
        }
        if (data.product.ingredients_analysis_tags) {
          const ingredients_analysis_tags = data.product.ingredients_analysis_tags;
          this.setIngredientAnalysisInformation(ingredients_analysis_tags);
        }

        this.$emit("info", this.info);
      }
    },

    /**
     * Parses and sets nutritional level tags in required format
     */
    setNutritionalLevelInformation: function(nutrient_levels) {
      const dataMapper = {
        fat: 'fat',
        saturatedFat: 'saturated-fat',
        sugars: 'sugars',
        salt: 'salt'
      };
      Object.entries(dataMapper).forEach(([key, value]) => {
        if (typeof nutrient_levels[value] == "string") {
          this.info[key] = nutrient_levels[value].toUpperCase();
        }
      });
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
