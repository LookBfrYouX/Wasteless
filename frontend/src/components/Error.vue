<template>
  <div class="container mt-4">
    <h1>{{title}}</h1>
    <h3>{{subheading}}</h3>
    <p>{{text}}</p>
    <div class="pigeon">
      <img src="navbara-pigeon.svg">
    </div>
  </div>
</template>
<style scoped>
.pigeon {
  position: absolute;
  bottom: 0;
  right: 0;
}

.pigeon img {
  height: max(200px, 20vh);
}
</style>
<script>
export default {
  name: "error",
  props: {
    title: {
      type: String,
      default: "An unknown error occurred"
    },
    subheading: {
      type: String,
      default: "Something went wrong and we had to stop what we were doing"
    },
    text: {
      type: String,
      default: "Try going back; sometimes these are one-off issues"
    },
  }
}
</script>