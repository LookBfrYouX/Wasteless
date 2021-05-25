<template>
  <div class="overflow-auto">
    <div
      v-for="card in cards"
      v-bind:key="card.id"
      >
      <marketplace-card :card="card"/>
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
  import MarketplaceCard from "./../components/cards/MarketplaceCard";
  import {Api} from "./../Api";
  import ErrorModal from "../components/Errors/ErrorModal";
  export default {
    components: {MarketplaceCard, ErrorModal},
    props: ['section'],
    data() {
      return {
        apiErrorMessage: null,
        cards: [],
      };
    },
    mounted() {
        this.getCards()
    },
    watch: {
      section() {
        this.getCards()
      }
    },
    methods: {
      /**
       * Get the cards from the API and then sort into the 3 sections
       * @returns {Promise<void>}
       */
      async getCards() {
        this.cards = await this.getCardsFromAPI(this.section);
        this.sortResultsByCreated();
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
      },
      sortResultsByCreated: function () {
        this.cards.sort((a,b)=> a.created-b.created)
      }
    }
  }
</script>
