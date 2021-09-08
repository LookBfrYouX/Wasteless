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
              :value="nutriScore"
              :items="options"
              item-text="score"
              @change="nutriScore => $emit('input', nutriScore)"
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
import nutriScoreNoneImage from "@/../assets/images/nutriscore_none.png";

export default {
  name: "NutriScoreInput",

  data() {
    return {
      options: ['A', 'B', 'C', 'D', 'E'],
    };
  },

  props: {
    nutriScore: null
  },

  computed: {
    /**
     * Loads a Nutri-Score image of a selected score by users.
     * @returns Path to the Nuti-Score image.
     */
    imgSrc() {
      let src;
      const imgSrcDict = {
        'A': nutriScoreAImage, 'B': nutriScoreBImage, 'C': nutriScoreCImage, 'D': nutriScoreDImage,
        'E': nutriScoreEImage, null: nutriScoreNoneImage
      };
      src = imgSrcDict[this.nutriScore];
      return src;
    }
  }
}
</script>