<template>
  <div class="overflow-auto">
    <div
        v-for="card in cards"
        :key="card.id"
    >
      <marketplace-card :card="card"/>
    </div>
    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.getCardsFromAPI"
        :show="apiErrorMessage !== null"
        title="Error viewing inventory"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import MarketplaceCard from "@/components/cards/MarketplaceCard";
import {Api} from "@/Api";
import ErrorModal from "./ErrorModal";

export default {
  components: {MarketplaceCard, ErrorModal},
  props: ['section'],
  data() {
    return {
      apiErrorMessage: null,
      cards: [],
    };
  },
  mounted: async function () {
    await this.getCardsFromAPI(this.section)
  },
  watch: {
    section: async function () {
      await this.getCardsFromAPI(this.section)
    }
  },
  methods: {
    /**
     * Get the marketplace cards from the API for the specified section
     * @returns {Promise<T>} Promise containing a list of the cards
     */
    async getCardsFromAPI(section) {
      try {
        const data = (await Api.getMarketplaceCards(section)).data;
        this.cards = data;
        this.cards.reverse();
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return false;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },
  }
}
</script>
