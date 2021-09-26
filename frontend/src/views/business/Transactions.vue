<template>
  <div>
    Hey! You're on the new Transactions Page 🧾
  </div>
</template>
<script>

export default {
  name: "Transactions",
  props: {
    businessId: {
      required: true,
      type: Number
    },
  }
};
</script>
