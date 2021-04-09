<template>
  <div class="modal fade" ref="modal">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h4 class="modal-title">{{ title }}</h4>
          <button type="button" class="close" v-on:click="hideCallback">
          </button>
        </div>
        <div class="modal-body">
          <slot></slot>
        </div>
        <div class="modal-footer">
          <button v-if="retry" type="button" class="btn btn-primary" v-on:click="retryClicked">
            Retry
          </button>
          <button v-if="refresh" type="button" class="btn btn-primary" v-on:click="refreshClicked">
            Refresh Page
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
// When there's an unrecoverable error, but not a 404

import { Modal } from "bootstrap";
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
      type: Function
    }
  },
  watch: {
    show: function() {
      this.updateModalVisibility();
    }
  },

  methods: {
    updateModalVisibility: function() {
      this.show? this.modal.show(): this.modal.hide();
    },

    refreshClicked: function(event) {
      this.hideCallback();
      if (this.refresh === true) {
        // boolean
        this.$router.go();
      } else {
        this.refresh(event);
      }
    },

    retryClicked: function(event) {
      this.hideCallback();
      if (this.retry) this.retry(event);
    }
  },

  mounted() {
    this.modal = new Modal(this.$refs.modal);
    // Not in data so Vue won't track updates to it that we don't care about
    this.updateModalVisibility();
    this.$refs.modal.addEventListener("hidden.bs.modal", event => {
      if (this.hideCallback) this.hideCallback(event);
      console.log("!!!!!!!!!!")
    });

  }
}
</script>
