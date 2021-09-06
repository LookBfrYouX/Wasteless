<template>
  <div class="row">
    <div
      v-for="{key, name} in nutrientTypes"
      class="form-group col-6 bg-white rounded"
      :key="key"
    >
      <label :for="key">{{name}}</label>
      <v-slider
          :id="key"
          :name="key"

          :value="NUTRIENT_LEVELS.findIndex(el => el.value == value[key])"
          @change="index => $emit('input', {...value, [key]: NUTRIENT_LEVELS[index].value })"

          :tick-labels="NUTRIENT_LEVELS.map(el => el.name)"
          :max="NUTRIENT_LEVELS.length - 1"
          step="1"
          ticks="always"
          tick-size="4"
        ></v-slider>
      </div>
  </div>
</template>
<script>
// const NUTRI_SCORE_VALUES = ['A','B','C','D','E'];
const NUTRIENT_LEVELS = [
  {
    value: null,
    name: "Unknown",
  }, {
    value: "LOW",
    name: "Low"
  }, {
    value: "MODERATE",
    name: "Moderate"
  }, {
    value: "HIGH",
    name: "High"
  }
]
export default {
  props: {
    value: {
      required: true,
      type: Object
    }
    // nutriScore: {
    //   required: true,
    //   type: String,
    //   validator: val => NUTRI_SCORE_VALUES.includes(val)
    // },
    // novaScore: {
    //   required: true,
    //   type: Number,
    //   validator: val => val >= 1 && val <= 4
    // },
    // fat: {
    //   required: false,
    //   type: String,
    //   default: null,
    //   validator: val => Object.keys(NUTRIENT_LEVELS_MAP).includes(val)
    // },
    // saturatedFat: {
    //   required: true,
    //   type: String,
    //   validator: val => Object.keys(NUTRIENT_LEVELS_MAP).includes(val)
    // },
    // sugars: {
    //   required: true,
    //   type: String,
    //   validator: val => Object.keys(NUTRIENT_LEVELS_MAP).includes(val)
    // },
    // sodium: {
    //   required: true,
    //   type: String,
    //   validator: val => Object.keys(NUTRIENT_LEVELS_MAP).includes(val)
    // }
  },

  data() {
    return {
      // NUTRI_SCORE_VALUES
      NUTRIENT_LEVELS,
      nutrientTypes: [
        {
          name: "Fat",
          key: "fat"
        }, {
          name: "Saturated Fat",
          key: "saturatedFat"
        }, {
          name: "Sugars",
          key: "sugars"
        }, {
          name: "Sodium",
          key: "sodium"
        }
      ]
    }
  }
}
</script>
