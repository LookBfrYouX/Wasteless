<template>
  <div>
    Hey! You're on the new Transactions Page 🧾
    <report-date-selector  @newDates="event => this.filterDates = event"/>
  </div>
</template>
<script>
import ReportDateSelector from "@/components/ReportDateSelector";

export default {
  name: "Transactions",
  components: {
    ReportDateSelector
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
