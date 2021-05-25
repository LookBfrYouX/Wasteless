<template>
  <div class="container">
    <div class="row">
      <div class="col-12">
        <h2>Community Marketplace</h2>
        <ul class="nav">
          <li class="nav-item"  v-for="([sectionKey, sectionName]) in Object.entries($constants.MARKETPLACE.SECTIONS)"
              v-bind:key="sectionKey"
              v-bind:class="{ active: section == sectionKey }"
              >
            <a class="nav-link active" v-on:click="section = sectionKey">{{sectionName}}</a>
          </li>
        </ul>
        <marketplace-tab v-bind:section="section"/>
      </div>
    </div>
  </div>
</template>

<script>
import MarketplaceTab from "@/components/MarketplaceTab.vue";

export default {
  components: {MarketplaceTab},
  data() {
    return {
      section:"ForSale"
    };
  }
}
</script>
