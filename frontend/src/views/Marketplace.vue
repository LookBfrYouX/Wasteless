<template>
  <div class="w-100">
    <h2>Community Marketplace</h2>
    <UCMTabularDisplay/>
  </div>
</template>

<script>
import UCMTabularDisplay from '../components/UCMTabularDisplay.vue'

export default {
  components: {
    UCMTabularDisplay
  },
  props: {},
  data() {
    return {};
  },
  beforeMount: async function () {
  },
  methods: {},
  watch: {}
}
</script>
