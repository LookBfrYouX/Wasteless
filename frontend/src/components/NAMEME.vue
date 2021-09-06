<template>
  <v-row>
    <div
      v-for="{key, name} in nutrientTypes"
      class="form-group col-6 bg-white rounded"
      :key="key"
    >
      <!-- Each of the four nutrients have the same logic and HTML so it is in a for-loop
      the reduce repetition. The only things that need to be modified are the names to display,
      the model for the sliders and ensuring the updated value is emitted on a change
       -->
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
        <!--
          Vuetify uses indexes for v-slider so levels (LOW, MODERATE etc.) needs to be
          converted to an index.

          On change, gets index, maps this to a nutrient level value (LOW, MODERATE etc.), 
          creates a new object containing the existing values, adds the new value
          for the nutrient, and emits the event.
          This new value is after the `...value` so it overrides the existing property

          e.g. if key is 'fat' and the slider value is 3, it becomes {...value, fat: 'HIGH' }.
          The [] makes it use the value of the variable, not the name of the variable
        -->
        
      </div>
  </v-row>
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

/*
Use using <this-component :model="myModel"/> where
myModel = {
  fat: null,
  saturatedFat: "LOW",
  sugars: "MODERATE",
  sodium: "HIGH"
}

The values are grouped together in an object as it means there only needs to be 
a single prop and event handler. By calling it `value` instead of something more
descriptive like `nutrientLevels`, `v-model` can be used.
*/
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
