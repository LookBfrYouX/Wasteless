<template>
  <v-form>
    <label>Dietary Certifications</label>
    <v-select
        v-model="certifications"
        :items=options
        @change="onChange"
        chips
        multiple
        solo
    ></v-select>
  </v-form>
</template>

<script>
export default {
  name: "DietaryCertificationsInput",
  data() {
    return {
      isGlutenFree: false,
      isDairyFree: false,
      isVegetarian: false,
      isVegan: false,
      isPalmOilFree: false,
      certifications: [],
      options: ['Gluten Free', 'Dairy Free', 'Vegetarian', 'Vegan', 'Palm Oil Free']
    }
  },
  methods: {
    onChange() {
      this.isGlutenFree = this.certifications.includes('Gluten Free');
      this.isDairyFree = this.certifications.includes('Dairy Free');
      this.isVegetarian = this.certifications.includes('Vegetarian');
      this.isVegan = this.certifications.includes('Vegan');
      this.isPalmOilFree = this.certifications.includes('Palm Oil Free');
    }
  }
}
</script>