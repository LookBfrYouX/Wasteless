<template>
  <v-container>
    <div>
      <h1>Transactions mofo</h1>
    </div>
    <div class="pt-5">
      <sales-report :business-id="businessId" />
    </div>
  </v-container>
</template>
<script>
import SalesReport from "@/views/business/SalesReport";

export default {
  name: "Transactions",
  components: {
    SalesReport,
  },
  props: {
    businessId: {
      required: true,
      type: Number
    },
  },
  data() {
    return {
      filterDates: [],
    }
  }
};
</script>
