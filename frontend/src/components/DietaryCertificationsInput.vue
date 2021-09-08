<template>
  <v-form>
    <label>Dietary Certifications</label>
    <v-select
        :value="certifications"
        @input="certificationSetter"
        :items=options
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
      options: ['Gluten Free', 'Dairy Free', 'Vegetarian', 'Vegan', 'Palm Oil Free']
    }
  },
  props: {
    value: {
      isGlutenFree: false,
      isDairyFree: false,
      isVegetarian: false,
      isVegan: false,
      isPalmOilFree: false
    }
  },
  methods: {
    certificationSetter(newList) {
      let queryParams = {
        ...this.value,
        isGlutenFree: newList.includes('Gluten Free'),
        isDairyFree: newList.includes('Dairy Free'),
        isVegetarian: newList.includes('Vegetarian'),
        isVegan: newList.includes('Vegan'),
        isPalmOilFree: newList.includes('Palm Oil Free')
      };

      this.$emit('input', queryParams);
    }
  },
  computed: {
    certifications() {
      const arr = [];
      if (this.value.isGlutenFree) {
        arr.push("Gluten Free");
      }
      if (this.value.isDairyFree) {
        arr.push("Dairy Free");
      }
      if (this.value.isVegetarian) {
        arr.push("Vegetarian");
      }
      if (this.value.isVegan) {
        arr.push("Vegan");
      }
      if (this.value.isPalmOilFree) {
        arr.push("Palm Oil Free");
      }
      return arr;
    }
  }
}
</script>