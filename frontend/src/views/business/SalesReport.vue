<template>
  <report-date-selector  @newDates="event => this.filterDates = event"/>
</template>

<script>
import ReportDateSelector from "@/components/ReportDateSelector";
export default {
  components: {
    ReportDateSelector
  },
  data() {
    return {
      filterDates: [],
    }
  }
}
</script>
