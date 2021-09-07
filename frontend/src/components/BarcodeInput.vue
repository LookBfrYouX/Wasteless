<template>
<v-form>
  <div class="d-flex">
    <v-text-field
        v-model="barcode"
        :rules="[() => barcode.length <= 13 || 'Barcode must be 13 numbers']"
        class="mr-2"
        counter="13"
        dense
        label="Barcode Number"
        outlined
        prefix="EAN-13"
        type="number"
    />
    <v-btn>Fill</v-btn>
  </div>
  <div v-if="errorMessage != null" class="row mt-2">
    <div class="col">
      <p class="alert alert-warning">{{ errorMessage }}</p>
    </div>
  </div>
</v-form>
</template>

<script>
export default {
  name: "BarcodeInput",
  data() {
    return {
      barcode: "",
      errorMessage: null,
    }
  }
}
</script>

<style scoped>

</style>