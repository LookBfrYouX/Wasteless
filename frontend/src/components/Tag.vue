<template>
  <div
    class="border border-primary d-flex rounded justify-content-center align-items-center slightly-transparent-white-background mr-2 mb-2 px-1"
  >
    <slot/>
    <button
      class="btn btn-outline-primary d-flex justify-content-center align-items-center p-0 my-1 ml-1 rounded-circle"
      v-if="xButton"
      type="button"
      v-on:click="event => $emit('xClick', event)"
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