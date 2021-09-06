<template>
  <div>
    <v-container>
      <v-row align="center"
             justify="center">
        <v-col cols="6">
          <label>
            Nutri-Score
          </label>
          <v-select
              v-model="nutritionScore"
              :items="options"
              item-text="score"
              @change="test()"
              solo
              clearable
          ></v-select>
        </v-col>
        <v-col cols="6">
          <v-img
              contain
              :src="imgSrc"
          ></v-img>
        </v-col>
      </v-row>
    </v-container>


  </div>

</template>

<script>

// Importing image allows Vue to copy the file to /public on build
import nutriScoreAImage from "@/../assets/images/nutriscore_A.png";
import nutriScoreBImage from "@/../assets/images/nutriscore_B.png";
import nutriScoreCImage from "@/../assets/images/nutriscore_C.png";
import nutriScoreDImage from "@/../assets/images/nutriscore_D.png";
import nutriScoreEImage from "@/../assets/images/nutriscore_E.png";

export default {
  name: "NutritionScoreInput",

  data() {
    return {
      options: ['A', 'B', 'C', 'D', 'E'],
      nutritionScore: null,
    };
  },
  methods: {
    test() {
      console.log(this.nutritionScore);
    }
  },

  computed: {
    imgSrc() {
      let src;
      switch (this.nutritionScore) {
        case 'A':
          src = nutriScoreAImage;
          break;
        case 'B':
          src = nutriScoreBImage;
          break;
        case 'C':
          src = nutriScoreCImage;
          break;
        case 'D':
          src = nutriScoreDImage;
          break;
        case 'E':
          src = nutriScoreEImage;
          break;
        default:
          src= null;
      }
      return src;
    }
  }
}
</script>