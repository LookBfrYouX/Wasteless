<template>
  <div ref="modal" class="modal fade">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <button class="btn btn-danger d-flex p-0 justify-content-center align-items-center rounded-circle close-button" type="button" v-on:click="hideCallback">
        <span class="material-icons">close</span>
      </button>
        <div class="modal-header">
          <h4 class="modal-title">{{ title }}</h4>
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
          <button v-if="goBack" class="btn btn-primary" type="button" v-on:click="goBackClicked">
            Go back
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.close-button {
  position: absolute;
  top: -0.5em;
  right: -0.5em;
  height: 1.5em;
  width: 1.5em;
}
</style>
<script>
// When there's an unrecoverable error, but not a 404

// import {Modal} from "bootstrap";
const $ = require("jquery");

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
      // Called when hidden, or any of the buttons clicked
      type: Function,
      required: true
    },
    refresh: {
      required: true
    },

    /**
     * If false or not given, retry button will not be shown
     * If boolean, will determine if shown or not and use default refresh handler
     */
    retry: {
      required: true
    },

    /**
     * Boolean or function
     * If boolean, will determine if shown or not and use default back handler
     * Otherwise, the function will be called with the event passed as an argument
     */
    goBack: {
      required: true
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
      this.show ? this.modal.modal("show"): this.modal.modal("hide");
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
    },

    hideCallbackEvent: function (event) {
      if (this.hideCallback) {
        this.hideCallback(event);
      }
    }
  },

  mounted() {
    this.modal = $(this.$refs.modal).modal({
      show: this.show
    });
    // Not in data so Vue won't track updates to it that we don't care about
    this.updateModalVisibility();
    this.modal.on("hidden.bs.modal", this.hideCallbackEvent);
  },

  /**
   * Hide the modal if it is open
   */
  beforeDestroy() {
    try {
      this.modal.modal("hide"); // If navigation occurs while modal open, modal disapears but body has the modal open class
      this.modal.modal("dispose"); // dipose of the modal
    } catch(err) {
      console.warn("Modal destroy failed. Removing it manually...")
      document.body.classList.remove("modal-open");
      document.body.style.paddingRight = "inherit";
      const backdrops = document.body.querySelectorAll(".modal-backdrop");
      backdrops.forEach( backdrop => backdrop.remove());
    }
  }

}
</script>
