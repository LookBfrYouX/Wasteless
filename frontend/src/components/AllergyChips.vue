<template>
  <div>
    <!-- Can't use v-for and v-if together so filtering out false elements
               from the array instead -->
    <v-chip
        v-bind="$attrs"
        v-for="{ key, longText, backgroundColor, foregroundColor }
                in $constants.PRODUCT.ALLERGY_CHIP_CONFIG
                  .filter(({ key }) => product[key])"
        :key="key"

        class="mr-1 mb-1"
        :color="backgroundColor"
        :text-color="foregroundColor"
    >
    <span>
      {{ longText }}
    </span>
    </v-chip>
  </div>
</template>

<script>
export default {
  name: "AllergyChips",

  props: {
    product: {
      required: true
    }
  }
}
</script>