<template>
<v-form>
  <label>Barcode</label>
  <v-text-field label="Barcode No." placeholder="EAN-13 Barcode" v-model="barcode" solo dense />
</v-form>
</template>

<script>
export default {
  name: "BarcodeInput",
  data() {
    return {
      barcode: "",
    }
  }
}
</script>

<style scoped>

</style>