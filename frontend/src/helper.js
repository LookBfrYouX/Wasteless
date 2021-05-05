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
  }
}
