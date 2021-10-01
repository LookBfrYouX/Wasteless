/**
 * Module containing constants
 */
export const constants = {
  USER: {
    ADMIN_ROLE: "ROLE_ADMIN"
  },

  SIGN_UP: {
    MIN_AGE: 13,
    MAX_AGE: 110,
  },

  ADDRESS_FORM: {
    EDIT_DISTANCE: {
      /**
       * Edit distance divided by (modified) string length
       * Use ratio instead of raw measure to not favour longer strings
       */
      WORST_RATIO: 2,

      /**
       * Delete/substitute costs are very high since suggestions are likely to be much longer than the input string
       * Typos are probably fairly rare so most of the time character should be inserted, not modified - that probably means
       * the Photon suggestion does not match input at all (e.g. 'name' property matches but we don't care about the name)
       */
      INSERT_COST: 1,
      DELETE_COST: 50,
      SUBSTITUTE_COST: 50,
    },

    /**
     * API should be called only this number of milliseconds after the last input event
     */
    API_CALL_DEBOUNCE_TIME: 100,

    /**
     * API should not be called the query string is less than this length
     */
    API_MIN_QUERY_LENGTH: 3,
  },

  MONTH_NAMES: [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
  ],

  SORTED_PAGINATED_ITEM_LIST: {
    RESULTS_PER_PAGE: 5,
    RESULTS_PER_LISTINGS_PAGE: 12
  },

  BUSINESSES: {
    TYPES: [
      "Accommodation and Food Services",
      "Retail Trade",
      "Charitable organisation",
      "Non-profit organisation"
    ],
    SHORT_LONG_TYPES: [
      {short: "Hospitality", long: "ACCOMMODATION_AND_FOOD"},
      {short: "Retail", long: "RETAIL"},
      {short: "Charity", long: "CHARITY"},
      {short: "Non-profit", long: "NON_PROFIT"}
    ]
  },

  CURRENCY: {
    DEFAULT_CURRENCY: {
      code: "NZD",
      name: "New Zealand Dollar",
      symbol: "$"
    }
  },

  PRODUCT: {
    ALLERGY_CHIP_CONFIG: [
      {
        key: "isDairyFree",
        shortText: "DF",
        longText: "Dairy Free",
        backgroundColor: "#ffdd50",
        foregroundColor: "black",
      }, {
        key: "isGlutenFree",
        shortText: "GF",
        longText: "Gluten Free",
        backgroundColor: "#93641c",
        foregroundColor: "white",
      }, {
        key: "isVegetarian",
        shortText: "V",
        longText: "Vegetarian",
        backgroundColor: "#40826d",
        foregroundColor: "white",
      }, {
        key: "isVegan",
        shortText: "VE",
        longText: "Vegan",
        backgroundColor: "#74B74E",
        foregroundColor: "white",
      }, {
        key: "isPalmOilFree",
        shortText: "POF",
        longText: "Palm Oil Free",
        backgroundColor: "#ffc5c5",
        foregroundColor: "black",
      }
    ],

    NUTRIENT_LEVELS_MAP: [
      {
        value: null,
        name: "Unknown",
        color: "grey--text",
        icon: "help"
      }, {
        value: "LOW",
        name: "Low",
        color: "green--text",
        icon: "circle"
      }, {
        value: "MODERATE",
        name: "Moderate",
        color: "amber--text",
        icon: "circle"
      }, {
        value: "HIGH",
        name: "High",
        color: "red--text text--darken-3",
        icon: "circle"
      }
    ],

    NOVA_GROUP: [
      {
        value: 1,
        description: "Group 1 - Unprocessed or minimally processed foods"
      },
      {
        value: 2,
        description: "Group 2 - Processed culinary ingredients"
      },
      {
        value: 3,
        description: "Group 3 - Processed foods"
      },
      {
        value: 4,
        description: "Group 4 - Ultra-processed food and drink products"
      }
    ]
  },

  API: {
    TIMEOUT_SHORT: 4000,
    TIMEOUT_MEDIUM: 5000,
    TIMEOUT_LONG: 10000
  },

  MARKETPLACE: {
    /**
     * Key is name of section that should be used internally, value is name that should be used externally
     */
    SECTIONS: {
      ForSale: "For Sale",
      Wanted: "Wanted",
      Exchange: "Exchange"
    },

    CREATE_CARD: {
      TAG_SUGGESTIONS: {
        NUM_SUGGESTIONS: 5,
        WORST_RATIO: 2,
        INSERT_COST: 1,
        DELETE_COST: 50,
        SUBSTITUTE_COST: 50,
      },
    }
  },

  OPEN_FOOD_FACTS_URL: "https://world.openfoodfacts.org/api/v0/product/"
};
