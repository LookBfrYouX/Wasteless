<template>
  <div>
    <v-container>
      <v-row align="center"
             justify="center">
        <v-col cols="6">
          <label>
            Nutri-Score
          </label>
          <!-- return-object required for test to work; event not emitted in jest test otherwise -->
          <v-select
              :value="value"
              @input="option => $emit('input', option == null? null: option.value)"
              :return-object="true"
              :items="options"
              item-text="text"
              item-value="value"
              solo
              :clearable="value != null"
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
      options:[
        {
          text: 'N/A',
          value: null
        }, {
          text: 'A',
          value: 'A'
        }, {
          text: 'B',
          value: 'B',
        }, {
          text: 'C',
          value: 'C'
        }, {
          text: 'D',
          value: 'D'
        }, {
          text: 'E',
          value: 'E'
        }
      ]
    };
  },

  props: {
    value: null
  },

  computed: {
    /**
     * Loads a Nutri-Score image of a selected score by users.
     * @returns Path to the Nuti-Score image.
     */
    imgSrc() {
      const imgSrcDict = {
        'A': nutriScoreAImage,
        'B': nutriScoreBImage,
        'C': nutriScoreCImage,
        'D': nutriScoreDImage,
        'E': nutriScoreEImage,
        null: nutriScoreNoneImage
      };
      return imgSrcDict[this.value];
    }
  }
}
</script>