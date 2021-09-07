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
              :value="novaGroup"
              :items="options"
              item-text="score"
              @change="novaGroup => $emit('input', novaGroup)"
              solo
              clearable
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
import novaGroup1Image from "@/../assets/images/nova-group-1.svg";
import novaGroup2Image from "@/../assets/images/nova-group-2.svg";
import novaGroup3Image from "@/../assets/images/nova-group-3.svg";
import novaGroup4Image from "@/../assets/images/nova-group-4.svg";

export default {
  name: "NovaGroupInput",

  data() {
    return {
      options: ['1', '2', '3', '4'],
    };
  },

  props: {
    novaGroup: null
  },

  computed: {
    /**
     * Loads a Nova group image of a selected score by users.
     * @returns Path to the Nova group image.
     */
    imgSrc() {
      let src;
      switch (this.novaGroup) {
        case '1':
          src = novaGroup1Image;
          break;
        case '2':
          src = novaGroup2Image;
          break;
        case '3':
          src = novaGroup3Image;
          break;
        case '4':
          src = novaGroup4Image;
          break;
        default:
          src= null;
      }
      return src;
    }
  }
}
</script>