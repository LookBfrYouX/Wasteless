<template>
  <div class="overflow-auto">
    <sorted-paginated-item-list
        :currentSortOption.sync="currentSortOption"
        :items="cards"
        :sortOptions="sortOptions"
    >
      <template v-slot:title>
        <h2>Cards</h2>
      </template>
      <template v-slot:item="slotProps">
        <marketplace-card
            :card="slotProps.item"
            class="hover-white-bg hover-scale-effect slightly-transparent-white-background my-1 rounded"
        />
      </template>
    </sorted-paginated-item-list>
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
import SortedPaginatedItemList from "@/components/SortedPaginatedItemList";

const sortOptions = [
];

export default {
  components: {SortedPaginatedItemList, MarketplaceCard, ErrorModal},
  props: ['section'],
  data() {
    return {
      apiErrorMessage: null,
      sortOptions,
      currentSortOption: {...sortOptions[0], reversed: false},
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
