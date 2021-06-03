<template>
  <div class="container">
    <div class="row">
      <div class="col-12 pt-2 d-flex flex-wrap justify-content-between align-items-baseline">
        <div class="btn fake-button py-0">
          Create Card
          <!-- Want to center the marketplace text while having a create card button on the right. Having this div allows us to have something on the left that is the same width and hence keep the text centered -->
        </div>
        <h2>
          Community Marketplace
        </h2>
        <router-link
            :to="{ name: 'MarketplaceCardCreate' }"
            class="btn btn-primary mb-2"
        >
          Create a Card
        </router-link>
      </div>
    </div>
    <div class="row">
      <div class="col-12">
        <div
            :class="{
            }"
            class="btn-group w-100 d-flex flex-wrap"
        >
          <button
              v-for="([sectionKey, sectionName]) in Object.entries($constants.MARKETPLACE.SECTIONS)"
              :key="sectionKey"
              :class="{ active: section == sectionKey }"
              class="btn btn-primary mb-0"
              type="button"
              @click="section = sectionKey"
          >
            <h4 class="text-center mb-0">{{ sectionName }}</h4>
          </button>
        </div>
        <marketplace-card-container :section="section"/>
      </div>
    </div>
  </div>
</template>
<style scoped>
.fake-button {
  visibility: hidden;
  display: none;
  height: 0;
}

@media (min-width: 768px) {
  .fake-button {
    display: inline;
  }
}

</style>
<script>
import MarketplaceCardContainer from "@/components/MarketplaceSection.vue";

export default {
  components: {MarketplaceCardContainer},
  data() {
    return {
      section: "ForSale"
    };
  }
}
</script>
