<template>
  <div class="container">
    <ul class="nav nav-tabs">
      <li class="nav-item">
        <a class="nav-link" v-on:click="changeTab('ForSaleTab')">For Sale</a>
      </li>

      <li class="nav-item">
        <a class="nav-link" v-on:click="changeTab('WantedTab')">Wanted</a>
      </li>

      <li class="nav-item">
        <a class="nav-link" v-on:click="changeTab('ExchangeTab')">Exchange</a>
      </li>
  </ul>
    <component :is="dynamic.component"></component>
  </div>
</template>
<script>
  import WantedTab from "./WantedTab.vue"
  import ForSaleTab from "./ForSaleTab.vue"
  import ExchangeTab from "./ExchangeTab.vue"

  export default {
    components: {
      WantedTab,
      ForSaleTab,
      ExchangeTab
    },
    data() {
      return {
        dynamic: {
          component: 'ForSaleTab'
        }
      }
    },
    methods: {
      changeTab(name) {
        console.log(name)
        this.dynamic.component = name;
        console.log(this.dynamic.component)
      }
    }

  }
</script>
