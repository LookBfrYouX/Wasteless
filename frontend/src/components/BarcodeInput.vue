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
          v-on:keydown.enter="() => barcode.length === 13 && !apiIsLoading && setProductInformation()"
      />
      <v-btn
          :disabled="barcode.length !== 13"
          :loading="apiIsLoading"
          v-on:click="setProductInformation"
      >
        Fill
      </v-btn>
    </div>
    <v-container class="pa-0">
      <v-dialog
          v-model="dialog"
          overlay-opacity="0.7"
          width="500"
      >

        <template v-slot:activator="{ on, attrs }">
          <v-btn
              v-if="barcodeScanShown"
              v-on:click="clickedScanButton()"
              v-bind="attrs"
              v-on="on"
              block
          >
            <v-icon left>
              photo_camera
            </v-icon>
            Scan barcode
          </v-btn>
        </template>
        <v-card>
          <v-skeleton-loader
              v-if="!scannerLoaded"
              class="skeleton"
              height="400px"
              min-height="400px"
              type="image"
          ></v-skeleton-loader>
          <StreamBarcodeReader
              v-if="dialog"
              @decode="decodeScannerResult"
              @loaded="onScannerLoad"
          />
          <v-progress-linear :value="currentMax / threshold * 100"/>
          <v-card-actions class="justify-center">
            <v-btn @click="dialog = false">Close</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

    </v-container>
    <div v-if="errorMessage != null" class="row mt-2">
      <div class="col">
        <p class="alert alert-warning">{{ errorMessage }}</p>
      </div>
    </div>
  </v-form>
</template>

<script>
import {Api} from "@/Api";
import StreamBarcodeReader from "vue-barcode-reader/src/components/StreamBarcodeReader";

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
      apiRetryLimit: 3,
      apiRetryCount: 0,
      dialog: false,
      scannerLoaded: false,
      threshold: 5,
      currentMax: 0,
      barcodeScanCounts: new Map(),
      barcodeScanShown: true,
      stream: null,
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
  watch: {
    dialog: function (newValue) {
      if (!newValue) {
        this.stream.getTracks().forEach(track => track.stop());
      }
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
        if (response.data.status === 0) {
          throw new Error();
        }
        return response.data;
      } catch (err) {
        if (++this.apiRetryCount >= this.apiRetryLimit) {
          this.errorMessage = err.userFacingErrorMessage;
        } else {
          await this.getNutritionalInformationWithBarcode(barcode);
        }
      } finally {
        this.apiIsLoading = false;
      }

    },

    /**
     * Sets data variable with Data from open food facts API, Includes calling methods that parse information into required format
     */
    setProductInformation: async function () {
      const data = await this.getNutritionalInformationWithBarcode(this.barcode);

      if (data == null || data.status === 0) {
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
    setNutritionalLevelInformation: function (nutrient_levels) {
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
    setIngredientAnalysisInformation: function (ingredients_analysis_tags) {
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

    /**
     * Called by the barcode scanner component when a EAN-13 number is read. Passes the Barcode value
     * as a string.
     * The method keeps track of the passed in Barcode numbers until a number has been read more times
     * than the threshold. This is to protect from false readings.
     */
    decodeScannerResult(barcode) {
      // For some reason the `decode` event keeps being triggered from the scanner component even
      // after it is closed. This stops processing barcode reads once the dialog is closed.
      if (this.dialog) {
        if (this.barcodeScanCounts.has(barcode)) {
          const currentCount = this.barcodeScanCounts.get(barcode);
          this.barcodeScanCounts.set(barcode, currentCount + 1);
        } else {
          this.barcodeScanCounts.set(barcode, 1);
        }

        this.currentMax = Math.max(...this.barcodeScanCounts.values());

        // When the count surpasses the threshold we are confident in the reading.
        if (this.barcodeScanCounts.get(barcode) >= this.threshold) {
          this.barcode = barcode;
          this.setProductInformation();
          this.dialog = false;
          this.barcodeScanCounts = new Map();
          this.currentMax = 0;
          this.apiRetryCount = 0;
        }
      }
    },

    /**
     * Called when the barcode scanner component is initially mounted.
     */
    onScannerLoad() {
      this.scannerLoaded = true;
    },
    /**
     * Checks if the device has a camera by asking permission. If they do not have a camera (or do
     * not allow access, the dialog closes and the button disappears.
     */
    clickedScanButton() {
      navigator.mediaDevices.getUserMedia({video: true, audio: false})
      .then(stream => {
        this.stream = stream
      })
      .catch(() => {
        this.barcodeScanShown = false;
        this.dialog = false;
        this.errorMessage = "Unable to access camera"
      })
    }
  }
}
</script>
<style lang="scss" scoped>
// Existing bug with skeleton height, see https://github.com/vuetifyjs/vuetify/issues/11771
::v-deep .v-skeleton-loader.v-skeleton-loader--is-loading {
  .v-skeleton-loader__image {
    height: 100%;
  }
}
</style>