<template>
  <v-dialog
    @click:outside="hideCallbackEvent"
    :value="show"
    @change="hideCallback"
    max-width="500px"
  >
      <v-card class="error-modal-card">
        <v-card-title class="text-h5">
          {{title}}
        </v-card-title>
        <v-spacer></v-spacer>
        <div class="p-4">
          <slot></slot>
        </div>
        <v-spacer></v-spacer>
        <v-card-actions class="justify-content-around">
          <v-btn
            class="ma-2 retry-button"
            v-if="retry"
            text
            @click.stop="retryClicked"
          >
            Retry
          </v-btn>
          <v-btn
            class="ma-2 refresh-button"
            v-if="refresh"
            text
            @click.stop="refreshClicked"
          >
            Refresh Page
          </v-btn>
          <v-btn
            class="ma-2 go-back-button"
            v-if="goBack"
            text
            @click.stop="goBackClicked"
          >
            Go back
          </v-btn>
        </v-card-actions>
      </v-card>
  </v-dialog>
</template>
<style scoped lang="scss">
.error-modal-card {
  .retry-button, .refresh-button, .go-back-button {
    background-color: var(--primary);
    color: white;
  }
}
</style>
<script>
// When there's an unrecoverable error, but not a 404

export default {
  name: "ErrorModal",
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
  
  methods: {
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
      if (typeof this.goBack === "boolean") {
        if (this.goBack === true) {
          this.$router.go(-1);
        }
      } else {
        this.goBack(event);
      }
    },

    hideCallbackEvent: function (event) {
      if (this.hideCallback) {
        this.hideCallback(event);
      }
    }
  }
}
</script>
