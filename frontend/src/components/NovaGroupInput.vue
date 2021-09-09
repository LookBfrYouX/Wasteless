<template>
  <div>
    <v-container>
      <v-row align="center"
             justify="center">
        <v-col cols="6">
          <label>
            Nova Group
          </label>
          <v-select
              :value="value"
              :items="options"
              item-text="text"
              item-value="value"
              :return-object="true"
              @input="option => $emit('input', option == null? null: option.value)"
              solo
              :clearable="value != null"
          ></v-select>
        </v-col>
        <v-col cols="6">
          <v-img
              contain
              max-height="110"
              :src="imgSrc"
          ></v-img>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>

// Importing image allows Vue to copy the file to /public on build
import novaGroupNullImage from "@/../assets/images/nova-group-null.svg";
import novaGroup1Image from "@/../assets/images/nova-group-1.svg";
import novaGroup2Image from "@/../assets/images/nova-group-2.svg";
import novaGroup3Image from "@/../assets/images/nova-group-3.svg";
import novaGroup4Image from "@/../assets/images/nova-group-4.svg";

export default {
  name: "NovaGroupInput",

  data() {
    return {
      options: [
        {
          text: 'N/A',
          value: null
        }, {
          text: '1',
          value: 1,
        }, {
          text: '2',
          value: 2,
        }, {
          text: '3',
          value: 3
        }, {
          text: '4',
          value: 4
        }
      ]
    };
  },

  props: {
    value: null
  },

  computed: {
    /**
     * Loads a Nova group image of a selected score by users.
     * @returns Path to the Nova group image.
     */
    imgSrc() {
      const imgSrcDict = {
        1: novaGroup1Image,
        2: novaGroup2Image,
        3: novaGroup3Image,
        4: novaGroup4Image,
        null: novaGroupNullImage 
      };
      return imgSrcDict[this.value];
    }
  }
}
</script>