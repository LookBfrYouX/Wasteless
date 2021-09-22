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
  },
  computed: {
    /**
     * Computes the total number of pages for the pagination component.
     */
    totalPages: function () {
      return Math.floor((this.totalResults - 1) / this.itemsPerPage) + 1;
    }
  },
};
</script>
