<template>
  <div>
    <h2>Community Marketplace</h2>
    <div class="w-70">
      <UCMTabularDisplay/>
    </div>
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
