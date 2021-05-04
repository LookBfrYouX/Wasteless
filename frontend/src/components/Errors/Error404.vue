<template>
  <div class="container mt-4 navbara-pigeon-peek">
    <h1>404 Not Found</h1>
    <h3>We couldn't find <code>{{ path }}</code></h3>
    <p>Try checking if the URL was typed correctly</p>
  </div>
</template>
<script>
export default {
  name: "error404",
  props: {
    path: {
      type: String,
      required: true
    }
  }
}
</script>