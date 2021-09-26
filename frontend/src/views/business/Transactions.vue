<template>
  <v-container>
    <div>
      <h2 class="col-lg-8 pl-0">Transaction Report</h2>
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
