<template>
  <div>
    <ul class="nav nav-tabs">
      <li class="nav-item">
        <a class="nav-link">Test</a>
      </li>
  </ul>
  <component :is="dynamic"></component>
  </div>
</template>
<script>
  export default {
    data() {
      return {
        dynamic: {
          
        }
      }
    },

  }
</script>
