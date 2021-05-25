<template>
  <div class="container">
    <div class="row">
      <div class="col-12">
        <h2 class="text-center pt-4">Community Marketplace</h2>
        <div
            class="btn-group w-100"
            v-bind:class="{
            }"
          >
            <button
              v-for="([sectionKey, sectionName]) in Object.entries($constants.MARKETPLACE.SECTIONS)"
              v-bind:key="sectionKey"
              type="button"
              class="btn btn-primary mb-0"
              v-bind:class="{ active: section == sectionKey }"
              v-on:click="section = sectionKey"
            >
              <h4 class="text-center mb-0">{{ sectionName }}</h4>
            </button>
          </div>
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
