<template>
<v-form>
  <label>Barcode</label>
  <div class="d-flex">
    <v-text-field class="mr-2" label="Barcode No." placeholder="EAN-13 Barcode" v-model="barcode" solo dense />
    <v-btn>Find</v-btn>
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