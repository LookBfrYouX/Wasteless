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
   * @param {Boolean} publicOnly if true, only public components are in the output string
   * @returns
   */
  addressToString(address, publicOnly = false) {
    const numberUndef = address.streetNumber == undefined
        || address.streetNumber.trim().length == 0;
    const nameUndef = address.streetName == undefined
        || address.streetName.trim().length == 0;

    let street = undefined;
    if (numberUndef && !nameUndef) {
      // if street number defined but street name not, don't show either
      street = address.streetName;
    }
    else if (!numberUndef && !nameUndef) {
      street = `${address.streetNumber} ${address.streetName}`;
    }

    const components = publicOnly? [
        address.suburb,
        address.city,
        address.region,
        address.country
      ]: [
      street,
      address.suburb,
      address.city,
      address.region,
      address.postcode,
      address.country
      ];
      
    return components.filter(component => 
        typeof component == "string" && component.trim().length > 0
      ).join(', ');
  },

  /**
   * Given ISO string, returns human-readable date string
   * @param {} dateString YYYY-MM-DDTHH:MM:SS.MSMSZ
   * @param {boolean} includeTime if true, also returns time in format `, hh:mm p`
   * @returns date string in `dd MMM YYYY` format
   */
  isoToDateString(dateString, includeTime = false) {
    const timestamp = Date.parse(dateString);
    if (isNaN(timestamp)) {
      return null;
    }
    const date = new Date(timestamp);
    const day = date.getDate() < 10 ? `0${date.getDate()}`
        : date.getDate().toString();
    let result = `${day} ${constants.MONTH_NAMES[date.getMonth()]} ${date.getFullYear()}`;
    if (includeTime) {
      result += ", " + date.toLocaleTimeString("en-NZ",
          {hour: "numeric", minute: "2-digit", seconds: undefined});
    }
    return result;
  },

  /**
   * Given year month and day, returns `yyyy-mm-dd` string
   * @param {Number} year
   * @param {Number} month
   * @param {Number} day
   */
  toYyyyMmDdString(year, month, day) {
    if (month < 10) {
      month = "0" + month;
    }
    if (day < 10) {
      day = "0" + day;
    }
    return `${year}-${month}-${day}`;
  },

  /**
   * Formats the date as a D MMMM YYYY string
   * @param date date object, or something that can be passed to the constructor
   */
  formatDate: function (date) {
    if (!(date instanceof Date)) {
      date = new Date(date);
    }
    return `${date.getDate()} ${constants.MONTH_NAMES[date.getMonth()]}, ${date.getFullYear()}`;
  },

  /**
   * Calculates the time since registration and returns it as a string
   * @return string in format 'y years, m months'
   */
  generateTimeSinceRegistrationText: function (registrationDate, currentDate) {
    const yearDiff = currentDate.getFullYear() - registrationDate.getFullYear();
    const monthDiff = currentDate.getMonth() - registrationDate.getMonth();

    const timeDiffInMonth = yearDiff * 12 + monthDiff;

    const years = Math.floor(timeDiffInMonth / 12);
    let months = timeDiffInMonth % 12;

    const yearsText = `${years} year${years == 1 ? "" : "s"}`;

    const monthsText = `${months} month${months == 1 ? "" : "s"}`;

    if (years == 0) {
      return monthsText;
    }

    return `${yearsText}, ${monthsText}`;
  },

  /**
   * Formatted text for member since text
   * @param createdDate Date to format
   */
  memberSinceText: function (registrationDate) {
    if (isNaN(Date.parse(registrationDate))) {
      return "Unknown";
    }
    const created = new Date(registrationDate);
    const dateOfRegistration = this.formatDate(created);
    const monthsSinceRegistration = this.generateTimeSinceRegistrationText(
        created,
        new Date()
    );

    return `${dateOfRegistration} (${monthsSinceRegistration})`;
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
  goToProfile: async function (actingAsBusiness = undefined,
      $router = undefined, $route = undefined) {
    if ($route == undefined && this.$route === undefined) {
      console.warn(
          "[helper.js, goToProfile]: this needs to be passed using `.call(this)` or passed as argument");
      return false;
    }

    if (actingAsBusiness
        === undefined) {
      actingAsBusiness = this.$stateStore.getters.getActingAs();
    }
    if ($router === undefined) {
      $router = this.$router;
    }
    if ($route === undefined) {
      $route = this.$route;
    }

    let reload = false;
    let args;

    if (actingAsBusiness == null) {
      args = {
        name: "UserDetail"
      }

      if ($route.name === args.name && $route.params.userId === undefined) {
        reload = true;
      }
    } else {
      const businessId = actingAsBusiness.id;
      args = {
        name: "BusinessDetail",
        params: {
          businessId
        }
      }

      if ($route.name === "BusinessDetail" &&
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
    if (isNaN(businessId)) {
      console.warn(
          "Business ID not given; getBusinessCountry");
    }
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
    const country = countryData.find(
        countryEl => countryEl.name == countryName);
    if (country) {
      return country.currency;
    }
    return null;
  },

  /**
   * getCurrencyForBusiness with error handling
   * @param {*} businessId
   * @param {*} stateStore
   * @returns currency, returns null when currency is not found or API request error
   */
  tryGetCurrencyForBusiness: async function (businessId, stateStore) {
    try {
      return await this.getCurrencyForBusiness(businessId, stateStore);
    } catch (err) {
      return null;
    }
  },

  /**
   * Given price and currency, return price with currency
   * @param {*} price
   * @param {*} currency currency the price is given in
   * @param {Boolean} showCurrencyCode if false, currency code not given
   * @returns string in form SYMBOL PRICE (CODE), or undefined if price is not a number
   */
  makeCurrencyString(price, currency, showCurrencyCode = true) {
    if (typeof price != "number") {
      return;
    }
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

  /**
   * Method which returns sort method for a string or numeric property of an object
   * @param{Function|String} key object key to sort by, or lambda which extracts key from an object
   */
  sensibleSorter: (key) => {
    /**
     * Gets property from an object given key or lambda
     * @param {Object} obj object to extract property from
     * @param {Function|String} key Key is either a key to an object or a lambda which takes in an object and returns a key
     */
    const getProp = (obj, key) => {
      if (key instanceof Function) {
        return key(obj);
      }
      return obj[key];
    }

    return (a, b) => {
      a = getProp(a, key);
      b = getProp(b, key);
      // For number can use a - b, but this works with both strings and numbers
      if (a === b) {
        return 0;
      }
      return a > b ? 1 : -1;
    }
  },

  /**
   * Attempts to get business name.
   *
   * @param {Number} id business id
   * @return null if could not get it, string otherwise
   */
  tryGetBusinessName: async function (id) {
    try {
      const {data} = await Api.businessProfile(id);
      return data.name;
    } catch (err) {
      return null;
    }
  },


  /**
   * Formats object with name components, removing null or empty strings
   * @param {{firstName: String, middleName: String, lastName: String, nickname: String}} object with name components
   * @return string in format {firstName} {middleName} {lastName} ({nickname})
   */
  formatFullName(user) {
    let name = [user.firstName, user.middleName, user.lastName]
        .filter(el => typeof el == "string" && el.trim().length)
        .map(el => el.trim())
        .join(" ");
    
    if (typeof user.nickname == "string" && user.nickname.trim().length) {
      name += ` (${user.nickname})`
    }
    return name;
  },
}
