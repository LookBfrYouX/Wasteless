<template>
  <div class="p-absolute">
    <error-modal
        title="Not acting as a business"
        v-bind:goBack="true"
        v-bind:hideCallback="() => message = null"
        v-bind:refresh="false"
        v-bind:retry="false"
        v-bind:show="message != null"
    >
      <p>{{ message }}</p>
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

  props: {
    businessId: {
      required: true,
      type: Number
    }
  },

  data() {
    return {
      message: null
    }
  },

  computed: {
    canEditBusiness() {
      return this.$stateStore.getters.canEditBusiness(this.businessId);
    }
  },

  methods: {
    updateMessage() {
      if (this.canEditBusiness) {
        this.message = null;
      } else {
        this.message = "You must be acting as the business or be acting as an admin as your self";
      }
    }
  },

  watch: {
    canEditBusiness() {
      this.updateMessage();
    },
  },

  beforeMount() {
    this.updateMessage();
  }

}

</script>