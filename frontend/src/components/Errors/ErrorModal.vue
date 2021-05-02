<template>
  <div ref="modal" class="modal fade">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h4 class="modal-title">{{ title }}</h4>
          <button class="close" type="button" v-on:click="hideCallback">
          </button>
        </div>
        <div class="modal-body">
          <slot></slot>
        </div>
        <div class="modal-footer">
          <button v-if="retry" class="btn btn-primary" type="button" v-on:click="retryClicked">
            Retry
          </button>
          <button v-if="refresh" class="btn btn-primary" type="button" v-on:click="refreshClicked">
            Refresh Page
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
// When there's an unrecoverable error, but not a 404

import {Modal} from "bootstrap";

export default {
  name: "errorModal",
  props: {
    title: {
      type: String
    },
    show: {
      type: Boolean,
      required: true
    },
    hideCallback: {
      // Called when hidden, or any of the buttons clicked
      type: Function,
      required: true
    },
    refresh: {
      // Boolean or function
      // if false will not be shown
      // If true will show refresh button and refresh
      // if function it will simply call this method
    },
    retry: {
      required: true
    }
  },
  watch: {
    show: function () {
      this.updateModalVisibility();
    }
  },

  methods: {
    updateModalVisibility: function () {
      this.show ? this.modal.show() : this.modal.hide();
    },

    refreshClicked: function (event) {
      this.hideCallback();
      if (this.refresh === true) {
        // boolean
        this.$router.go();
      } else {
        this.refresh(event);
      }
    },

    retryClicked: function (event) {
      this.hideCallback();
      if (this.retry) {
        this.retry(event);
      }
    },

    hideCallbackEvent: function(event) {
      if (this.hideCallback) {
        this.hideCallback(event);
      }
    }
  },

  mounted() {
    this.modal = new Modal(this.$refs.modal);
    // Not in data so Vue won't track updates to it that we don't care about
    this.updateModalVisibility();
    this.$refs.modal.addEventListener("hidden.bs.modal", this.hideCallbackEvent);
  },

  /**
   * Hide the modal if it is open
   */
  beforeDestroy() {
    if (this.modal) this.modal.hide(); // If navigation occurs while modal open, modal disapears but body has
    // the modal open class
    this.$refs.modal.removeEventListener("hidden.bs.modal", this.hideCallbackEvent);
  }


}
</script>
