<template>
  <div class="row">
    <div class="col-3 form-group">
      <label for="nova-score">
        Nova Score
      </label>
      <select
        id="nova-score"
        name="nova-score"
        class="form-control"
        :value="value.novaScore"
        @change="event => $emit('input', {...value, novaScore: event.target.value })"
      >
        <option
          v-for="value in [1, 2, 3, 4]"
          :key="value"
          :value="value"
        >
          {{ value }}
        </option>
      </select>
    </div>
    <div class="col-3 form-group">
      <label for="nutri-score">
        NutriScore
      </label>
      <select
        id="nutri-score"
        name="nutri-score"
        class="form-control"
        :value="value.nutriScore"
        @change="event => $emit('input', {...value, nutriScore: event.target.value })"
      >
        <option
          v-for="value in NUTRI_SCORE_VALUES"
          :key="value"
          :value="value"
        >
          {{ value }}
        </option>
      </select>
    </div>
  </div>

</template>
<script>
const NUTRI_SCORE_VALUES = ['A','B','C','D','E'];
// const NUTRIENT_LEVELS_MAP = {
//   null: "Unknown",
//   "LOW": "Low",
//   "MODERATE": "Moderate",
//   "HIGH": "High"
// }
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
      NUTRI_SCORE_VALUES
    }
  }
}
</script>