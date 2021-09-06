<template>
  <v-form>
    <label>Dietary Certifications</label>
    <v-select
        v-model="certifications"
        :items=options
        chips
        item-text="fancy"
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
      certifications: [],
      options: [
        {fancy: "Gluten Free", enum: "PRODUCT_NAME"},
        {fancy: "Dairy Free", enum: "BUSINESS_NAME"},
        {fancy: "Vegetarian", enum: "PRODUCT_NAME"},
        {fancy: "Vegan", enum: "PRODUCT_NAME"},
        {fancy: "Palm Oil Free", enum: "PRODUCT_NAME"},
      ]
    }
  }
}
</script>

<style scoped>

</style>