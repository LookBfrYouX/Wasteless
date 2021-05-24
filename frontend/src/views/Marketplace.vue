<template>
  <div class="w-100">
    <h2>Community Marketplace</h2>

    <div
      v-for="card in forSaleCards"
      v-bind:key="card.id.toString() + card.created"
    >
      <MarketplaceCard :card="card"/>
    </div>
  </div>
</template>

<script>
import MarketplaceCard from "@/components/cards/MarketplaceCard";

const mockCards = [
  {
    "id": 500,
    "creator": {
      "id": 100,
      "firstName": "John",
      "lastName": "Smith",
      "middleName": "Hector",
      "nickname": "Jonny",
      "bio": "Likes long walks on the beach",
      "email": "johnsmith99@gmail.com",
      "dateOfBirth": "1999-04-27",
      "phoneNumber": "+64 3 555 0129",
      "homeAddress": {
        "streetNumber": "3/24",
        "streetName": "Ilam Road",
        "suburb": "Upper Riccarton",
        "city": "Christchurch",
        "region": "Canterbury",
        "country": "New Zealand",
        "postcode": "90210"
      },
      "created": "2020-07-14T14:32:00Z",
      "role": "user",
      "businessesAdministered": [
        {
          "id": 100,
          "administrators": [
            "string"
          ],
          "primaryAdministratorId": 20,
          "name": "Lumbridge General Store",
          "description": "A one-stop shop for all your adventuring needs",
          "address": {
            "streetNumber": "3/24",
            "streetName": "Ilam Road",
            "suburb": "Upper Riccarton",
            "city": "Christchurch",
            "region": "Canterbury",
            "country": "New Zealand",
            "postcode": "90210"
          },
          "businessType": "Accommodation and Food Services",
          "created": "2020-07-14T14:52:00Z"
        }
      ]
    },
    "section": "ForSale",
    "created": "2021-07-15T05:10:00Z",
    "displayPeriodEnd": "2021-07-29T05:10:00Z",
    "title": "1982 Lada Samara",
    "description": "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
    "keywords": [
      {
        "id": 600,
        "name": "Vehicle",
        "created": "2021-07-15T05:10:00Z"
      }
    ]
  }
];

export default {
  components: {MarketplaceCard},
  props: {},
  data() {
    return {
      forSaleCards: [],
      wantedCards: [],
      exchangeCards: []
    };
  },
  mounted() {
    this.getCards();
  },
  methods: {
    getCards() {
      // This method will be used to get the cards from the API, for now using mocked cards
      const recvCards = mockCards;
      this.sortCards(recvCards);
    },
    sortCards(cards) {
      // Sort the received card into their own sections
      this.forSaleCards = cards.filter((card) => {
        return card.section === 'ForSale';
      });
      this.wantedCards = cards.filter((card) => {
        return card.section === 'Wanted';
      });
      this.exchangeCards = cards.filter((card) => {
        return card.section === 'Exchange';
      });
    }
  }
}
</script>
