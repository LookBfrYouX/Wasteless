const {Api} = require("./Api.js");
const countryData = require("./assets/countryData.json");
const {constants} = require("./constants");

/**
 * Helper methods that may be used by multiple pages. It is added to the vue prototype so
 * the component does not need to explicitly import this class
 */
export const helper = {
  /**
   * Given address object, convert it to string, stripping out undefined components
   * @param {object} address
   * @returns
   */
  addressToString(address) {
    const numberUndef = address.streetNumber == undefined
        || address.streetNumber.trim().length == 0;
    const nameUndef = address.streetName == undefined
        || address.streetName.trim().length == 0;

    let street = undefined;
    if (numberUndef && !nameUndef) {
      street = address.streetNumber;
    }// if street number defined but street name not, don't show either
    else if (!numberUndef
        && !nameUndef) {
      street = `${address.streetNumber} ${address.streetName}`;
    }

    return [
      street,
      address.city,
      address.region,
      address.postcode,
      address.country
    ].filter(
        component => typeof component == "string" && component.trim().length
            > 0).join(', ');
  },

  /**
   * Given ISO string, returns human-readable date string
   * @param {} dateString YYYY-MM-DDTHH:MM:SS.MSMSZ
   * @returns date string in `dd MMM YYYY` format
   */
  isoToDateString(dateString) {
    const timestamp = Date.parse(dateString);
    if (isNaN(timestamp)) {
      return null;
    }
    const date = new Date(timestamp);
    const day = date.getDate() < 10 ? `0${date.getDate()}`
        : date.getDate().toString();
    return `${day} ${constants.MONTH_NAMES[date.getMonth()]} ${date.getFullYear()}`;
  },

  /**
   * Navigates to profile page. Call using `goToProfile.bind(this)()` or `goToProfile(actingAsBusiness, router, route)`
   * If acting as business, goes to business profile. Otherwise, user profile.
   * If already on own profile page, reloads the page
   * @param {*} actingAsBusiness business you want to act as. Use null if acting as self
   * @param {*} $router router
   * @param {*} $route  route
   * @returns 
   */
  goToProfile: async function (actingAsBusiness = undefined, $router = undefined, $route = undefined) {
    if ($route == undefined && this.$route === undefined) {
      console.warn(
          "[helper.js, goToProfile]: this needs to be passed using `.call(this)` or passed as argument");
      return false;
    }

    if (actingAsBusiness === undefined) actingAsBusiness = this.$stateStore.getters.getActingAs();
    if ($router === undefined) $router = this.$router;
    if ($route  === undefined) $route  = this.$route;
    
    let reload = false;
    let args;

    if (actingAsBusiness == null) {
      args = {
        name: "profile"
      }

      if ($route.name === args.name && $route.params.userId
          === undefined) {
        reload = true;
      }
    } else {
      const businessId = actingAsBusiness.id;
      args = {
        name: "businessProfile",
        params: {
          businessId
        }
      }

      if ($route.name === "businessProfile" &&
          $route.params.businessId == businessId) {
        reload = true;
      }
    }

    if (reload) {
      await $router.go();
    } else {
      await $router.push(args);
    }
  },

  /**
   * Gets country given business ID and state store.
   * Does a API request if necessary. Caller must catch
   * @param {*} businessId
   * @param {*} stateStore
   * @returns
   */
  getBusinessCountry: async function (businessId, stateStore) {
    if (isNaN(businessId)) console.warn("Business ID not given; getBusinessCountry");
    const actingAsBusiness = stateStore.getters.getActingAs();
    if (actingAsBusiness == null) {
      // const user = stateStore.getters.getAuthUser();
      // if (user.businessesAdmistered)
      // If acting as admin, can view page and don't know country information
      const {data} = await Api.businessProfile(businessId);
      return data.address.country;
    }
    
    return actingAsBusiness.address.country;
  },

  /**
   * Given business Id and state store, returns currency
   * May throw error
   * @param {*} businessId
   * @param {*} stateStore
   * @returns currency, or null is not found
   */
  getCurrencyForBusiness: async function (businessId, stateStore) {
    const countryName = await this.getBusinessCountry(businessId, stateStore);
    const country = countryData.find(countryEl => countryEl.name == countryName);
    if (country) {
      return country.currency;
    }
    return null;
  },

  
  /**
   * Given price and currency, return price with currency
   * @param {*} price
   * @param {*} currency currency the price is given in
   * @param {Boolean} showCurrencyCode if false, currency code not given
   * @returns string in form SYMBOL PRICE (CODE), or undefined if price is not a number
   */
  makeCurrencyString(price, currency, showCurrencyCode = true) {
    if (typeof price != "number") return;
    price = price.toFixed(2);
    if (currency == null || currency == undefined) {
      return `${price} (unknown currency)`;
    }
    let str = `${currency.symbol}${price}`;
    if (currency.code != null && showCurrencyCode) {
      str += " " + currency.code;
    }
    return str;
  },
}
