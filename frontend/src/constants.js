/**
 * Module containing constants
 */
export const constants = {
  SIGN_UP: {
    MIN_AGE: 13
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
    RESULTS_PER_PAGE: 10
  },

  SEARCH_RESULTS: {
    RESULTS_PER_PAGE: 10
  },

  PRODUCT_CATALOG: {
    RESULTS_PER_PAGE: 10
  },

  BUSINESSES: {
    TYPES: [
      "Accommodation and Food Services",
      "Retail Trade",
      "Charitable organisation",
      "Non-profit organisation"
    ]
  },

  CURRENCY: {
    DEFAULT_CURRENCY: {
      code: "NZD",
      name: "New Zealand Dollar",
      symbol: "$"
    }
  },

  API: {
    TIMEOUT_SHORT: 2500,
    TIMEOUT_LONG: 10000
  },

  PRODUCTS: {
    MAX_PRICE: 10000
  }
};