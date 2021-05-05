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
   * Navigates to profile page. Call using `goToProfile.bind(this)()`
   * If acting as business, goes to business profile. Otherwise, user profile.
   * If already on own profile page, reloads the page
   */
  goToProfile: async function () {
    let reload = false;
    let args;

    if (this.$stateStore.getters.getActingAs() == null) {
      args = {
        name: "profile"
      }

      if (this.$route.name === args.name && this.$route.params.userId
          === undefined) {
        reload = true;
      }
    } else {
      const businessId = this.$stateStore.getters.getActingAs().id;
      args = {
        name: "businessProfile",
        params: {
          businessId
        }
      }

      if (this.$route.name === "businessProfile" &&
          this.$route.params.businessId === businessId.toString()) {
        reload = true;
      }
    }

    if (reload) {
      await this.$router.go();
    } else {
      await this.$router.push(args);
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
   * @returns string in form SYMBOL PRICE (CODE)
   */
  makeCurrencyString(price, currency) {
    if (currency == null || currency == undefined) {
      return `${price} (unknown currency)`;
    }
    let str = `${currency.symbol}${price}`;
    if (currency.code != null) {
      str += " " + currency.code;
    }
    return str;
  },
}
