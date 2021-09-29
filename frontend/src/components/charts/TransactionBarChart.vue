<template>
  <bar-chart class="p-3" :chartdata="chartdata" :options="options" />
</template>

<script>
import BarChart from "@/components/charts/BarChart";
export default {
  name: "TransactionBarChart",
  components: {BarChart},
  data() {
    return {
      chartdata: {
        labels: ['January', 'February'],
        datasets: [
          {
            label: 'Data One',
            backgroundColor: '#f87979',
            data: [40, 21]
          },
          {
            label: 'Data two',
            backgroundColor: '#008899',
            data: [44, 26]
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    }
  }
}
</script>


<style scoped>

</style>