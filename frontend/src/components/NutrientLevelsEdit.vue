<template>
  <v-row>
    <div
      v-for="{key, name} in nutrientTypes"
      class="col-12 col-md-6"
      :key="key"
    >
      <!-- Each of the four nutrients have the same logic and HTML so it is in a for-loop
      the reduce repetition. The only things that need to be modified are the names to display,
      the model for the sliders and ensuring the updated value is emitted on a change
       -->
    <label :for="key">{{name}}</label>
    <div class="bg-white rounded px-2">
      <!-- If slider is given background color, the labels clip. Surrounding it in a div
      and giving background color to the container fixes this -->
      <v-slider
          :id="key"
          :name="key"

          :value="nutrientLevelsMap.findIndex(el => el.value == value[key])"
          @change="index => $emit('input', {...value, [key]: nutrientLevelsMap[index].value })"

          :tick-labels="nutrientLevelsMap.map(el => el.name)"
          :max="nutrientLevelsMap.length - 1"
          step="1"
          ticks="always"
          tick-size="4"
          thumb-color="#1ec996"
          track-color="#1cd9a1"
          track-fill-color="#0e6e51"
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
      </div>
  </v-row>
</template>
<script>
/*
Use this component using <this-component :model="myModel"/> where
myModel = {
  fat: null,
  saturatedFat: "LOW",
  sugars: "MODERATE",
  sodium: "HIGH"
}
or something similar

The values are grouped together in an object as it means there only needs to be 
a single prop and event handler. By calling it `value` instead of something more
descriptive like `nutrientLevels`, `v-model` (replacing :value="" and @input) can
be used, which makes it simpler for the parent
*/
export default {
  props: {
    value: {
      required: true,
      type: Object
    }
  },

  computed: {
    nutrientLevelsMap() {
      return this.$constants.PRODUCT.NUTRIENT_LEVELS_MAP;
    },

    nutrientTypes() {
      return [
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
