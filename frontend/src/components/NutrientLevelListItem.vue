<template>
  <v-list-item>
    <v-tooltip top>
      <template v-slot:activator="{ on }">
        <v-list-item-icon>
          <span v-on="on"
                class="material-icons"
                :class="config.color">
            {{ config.icon }}
          </span>
        </v-list-item-icon>
      </template>
      <span> {{ config.name }}</span>
    </v-tooltip>
    <v-list-item-content>
      <v-list-item-title> {{ name }}</v-list-item-title>
    </v-list-item-content>
  </v-list-item>
</template>

<script>
import {constants} from "@/constants";

export default {
  name: "NutrientLevelListItem",
  props: {
    level: {
      required: true,
      validator: level => constants.PRODUCT.NUTRIENT_LEVELS_MAP.map(el => el.value).includes(level)
    },
    name,
  },

  computed: {
    /**
     * Finds the right level of the nutrients from constants.js
     * @returns {{color: string, name: string, icon: string, value: null} | {color: string, name: string, icon: string, value: string} | {color: string, name: string, icon: string, value: string} | {color: string, name: string, icon: string, value: string}}
     */
    config() {
      return constants.PRODUCT.NUTRIENT_LEVELS_MAP.find(el => el.value == this.level);
    }
  }
}
</script>
