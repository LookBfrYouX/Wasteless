<template>
  <div
      :class="{ 'py-1': !xButton }"
      class="border border-primary d-flex rounded justify-content-center align-items-center slightly-transparent-white-background hover-white-bg bg-color-transition px-1"
  >
    <!-- if there is no x button, tag gets a bit smaller. Heights are still different when it has and does not have the button but is pretty enough -->
    <slot/>
    <button
        v-if="xButton"
        class="btn btn-outline-primary d-flex justify-content-center align-items-center p-0 my-1 ml-1 rounded-circle"
        type="button"
        @click="event => $emit('xClick', event)"
    >
      <span class="material-icons">close</span>
    </button>
  </div>
</template>
<script>
export default {
  props: {
    /**
     * If true, an 'X' button is shown to the right of the content
     * An `xClick` event is emitted on click
     */
    xButton: {
      type: Boolean,
      default: false,
    }
  }
};
</script>