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
        <div class="modal-footer" v-if="retry || refresh || goBack">
          <button v-if="retry" class="btn btn-primary" type="button" v-on:click="retryClicked">
            Retry
          </button>
          <button v-if="refresh" class="btn btn-primary" type="button" v-on:click="refreshClicked">
            Refresh Page
          </button>
          <button v-if="goBack" class="btn btn-primary" type="button" v-on:click="goBackClicked">
            Go back
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
    /**
     * Determines if error modal is being shown or not; state is owned by callee
     */
    show: {
      type: Boolean,
      required: true
    },
    /**
     * Called when error modal hidden or any  of the buttons clicked.
     * Event passed as argument
     */
    hideCallback: {
      type: Function,
      required: true
    },
    /**
     * Boolean or function
     * If boolean, will determine if shown or not and use default refresh handler
     * Otherwise, the function will be called with the event passed as an argument
     */
    refresh: {
      required: true
    },

    /**
     * If false or not given, retry button will not be shown
     * Otherwise, the function will be called with the event passed as an argument
     */
    retry: {
      required: false
    },

    /**
     * Boolean or function
     * If boolean, will determine if shown or not and use default back handler
     * Otherwise, the function will be called with the event passed as an argument
     */
    goBack: {
      required: true,
    }
  },
  watch: {
    show: function () {
      this.updateModalVisibility();
    }
  },

  methods: {
    /**
     * Synchronizes modal visibility with state
     */
    updateModalVisibility: function () {
      this.show ? this.modal.show() : this.modal.hide();
    },

    /**
     * Refresh button clicked; send modal hidden callback and refresh
     */
    refreshClicked: function (event) {
      this.hideCallback();
      if (this.refresh === true) {
        // boolean
        this.$router.go();
      } else {
        this.refresh(event);
      }
    },

    /**
     * Retry button clicked; send modal hidden and retry callbacks
     */
    retryClicked: function (event) {
      this.hideCallback();
      if (this.retry) {
        this.retry(event);
      }
    },

    /**
     * Go back button clicked; send modal hidden and go back
     */
    goBackClicked: function (event) {
      this.hideCallback();
      if (this.goBack == true) {
        this.$router.go(-1);
      } else {
        this.goBack(event);
      }
    }
  },

  mounted() {
    this.modal = new Modal(this.$refs.modal);
    // Not in data so Vue won't track updates to it that we don't care about
    this.updateModalVisibility();
    this.$refs.modal.addEventListener("hidden.bs.modal", event => {
      if (this.hideCallback) {
        this.hideCallback(event);
      }
    });
  }
}
</script>
