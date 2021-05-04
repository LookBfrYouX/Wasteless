<template>
  <div class="p-absolute">
    <error-modal
        title="Not acting as a business"
        v-bind:hideCallback="() => message = null"
        v-bind:refresh="false"
        v-bind:retry="false"
        v-bind:show="message != null"
        v-bind:goBack="true"
    >
    <p>{{message}}</p>
    </error-modal>
  </div>
</template>

<script>
import ErrorModal from "./ErrorModal";

export default {
  name: "notActingAsBusiness",

  components: {
    ErrorModal
  },

  data() {
    return {
      message: null
    }
  },

  computed: {
    authUser() {
      return this.$stateStore.getters.getAuthUser();
    },

    actingAs() {
      return this.$stateStore.getters.getActingAs();
    }
  },
  
  methods: {
    updateMessage() {
      if (this.authUser == null) this.message = "You must be logged in and acting as a business before viewing this page";
      else if (this.actingAs == null) this.message = "You must be acting as a business before viewing this page";
      else this.message = null;
    }
  },

  watch: {
    authUser() {
      this.updateMessage();
    },

    actingAs() {
      this.updateMessage();
    }
  },

  beforeMount() {
    this.updateMessage();
  }
  
}

</script>