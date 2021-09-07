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
    imgSrc() {
      let src;
      switch (this.nutriScore) {
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