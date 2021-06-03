<template>
  <div>
    <div class="d-flex flex-wrap justify-content-between">
      <h4 class="card-title mb-0">{{ userNameText }}</h4>
      <span v-if="roleText" class="text-muted">
        {{ roleText }}
      </span>
    </div>
    <div class="text-muted">{{ user.email }}</div>
    <div v-if="user.homeAddress" class="text-muted">
      {{ $helper.addressToString(user.homeAddress) }}
    </div>
    <div v-else class="text-muted">Address unknown</div>
  </div>
</template>
<script>
export default {
  props: {
    user: {
      required: true,
      type: Object
    }
  },

  computed: {
    userNameText() {
      let name = [this.user.firstName, this.user.middleName, this.user.lastName].join(" ");
      if (this.user.nickname) {
        name += ` (${this.user.nickname})`;
      }
      return name;
    },

    roleText() {
      if (this.user.role && this.user.role == this.$constants.USER.ADMIN_ROLE) {
        return "Admin";
      }
      return false;
    }

  }
}
</script>
