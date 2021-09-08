<template>
  <v-form>
    <label>Dietary Certifications</label>
    <v-select
        v-model="certifications"
        :items=options
        @change="onChange"
        chips
        multiple
        outlined
    ></v-select>
  </v-form>
</template>

<script>
export default {
  name: "DietaryCertificationsInput",
  data() {
    return {
      queryParams: {
        isGlutenFree: false,
        isDairyFree: false,
        isVegetarian: false,
        isVegan: false,
        isPalmOilFree: false
      },
      certifications: [],
      options: ['Gluten Free', 'Dairy Free', 'Vegetarian', 'Vegan', 'Palm Oil Free']
    }
  },
  methods: {
    /**
     * This method updates the boolean values and emits the search queries to the parent component.
     */
    onChange() {
      this.queryParams.isGlutenFree = this.certifications.includes('Gluten Free');
      this.queryParams.isDairyFree = this.certifications.includes('Dairy Free');
      this.queryParams.isVegetarian = this.certifications.includes('Vegetarian');
      this.queryParams.isVegan = this.certifications.includes('Vegan');
      this.queryParams.isPalmOilFree = this.certifications.includes('Palm Oil Free');

      this.$emit('input', this.queryParams);
    }
  }
}
</script>