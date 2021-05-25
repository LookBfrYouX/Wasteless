<template>
  <div class="container">
    <div class="row">
      <h2 class="col-12">Community Marketplace</h2>
    </div>
    <div class="row">
      <div class="col-12">
        <router-link
          class="btn btn-primary"
          v-bind:to="{ name: 'createCard' }"
        >
          Create a Card
        </router-link>
      </div>
    </div>
    <div class="row">
      <div class="col-12">
        <h3>For Sale</h3>
        <div
            v-for="card in forSaleCards"
            v-bind:key="card.id"
        >
          <marketplace-card :card="card"/>
        </div>

        <h3>Wanted</h3>
        <div
            v-for="card in wantedCards"
            v-bind:key="card.id"
        >
          <marketplace-card :card="card"/>
        </div>

        <h3>Exchange</h3>
        <div
            v-for="card in exchangeCards"
            v-bind:key="card.id"
        >
          <marketplace-card :card="card"/>
        </div>
      </div>
    </div>
    <error-modal
        title="Error viewing inventory"
        v-bind:goBack="false"
        v-bind:hideCallback="() => apiErrorMessage = null"
        v-bind:refresh="true"
        v-bind:retry="this.getCards"
        v-bind:show="apiErrorMessage !== null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<script>
import MarketplaceCard from "@/components/cards/MarketplaceCard";
import {Api} from "@/Api";
import ErrorModal from "../components/Errors/ErrorModal";

export default {
  components: {MarketplaceCard, ErrorModal},
  props: {},
  data() {
    return {
      apiErrorMessage: null,
      forSaleCards: [],
      wantedCards: [],
      exchangeCards: []
    };
  },
  mounted() {
    this.getCards();
  },
  methods: {

    /**
     * Get the cards from the API and then sort into the 3 sections
     * @returns {Promise<void>}
     */
    async getCards() {
      this.forSaleCards = await this.getCardsFromAPI('ForSale');
      this.wantedCards = await this.getCardsFromAPI('Wanted');
      this.exchangeCards = await this.getCardsFromAPI('Exchange');
    },

    /**
     * Get the marketplace cards from the API for the specified section
     * @returns {Promise<T>} Promise containing a list of the cards
     */
    async getCardsFromAPI(section) {
      try {
        return (await Api.getMarketplaceCards(section)).data;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return false;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    }
  }
}
</script>
