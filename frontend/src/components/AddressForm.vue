<!--
Address Form Component

Styling of the inputs are not supported yet

Implementation:

- API_CALL_DEBOUNCE_TIME milliseconds after the last typing event, an API call is made
- Suggestions are set to the addressSuggestionsRaw property
- the addressSuggestions property is then manually updated - couldn't make it a computed property
  as it would not update, although this would be ideal
  - The `toString` method of the object is different depending on which input field the user is currently in
  - If it is in the first address field, when a suggestion is selected, it will update all address fields
  - In other cases, any address field less specific than the currently active input field will be autofilled
    - However, the API query always uses the full text
- This is sent to all autocomplete

The parent component must provide `address` prop. When the address is updated in this component, an
`addressupdate` event is emitted with the updated address as the argument. The object also has a
`toString` method which returns the address as a string, components separated by commas (if the components are of non-zero length)

-->
<template>
  <div class="row">
    <div class="form-group col-12 col-md-3">
      <label>Street number</label>
      <suggestions
          :suggestions="[]"
          :value="address.streetNumber"
          inputClasses="form-control"
          maxlength="100"
          name="streetnumber"

          placeholder="Street number"
          type="text"
          @focus="activeAddressInputName = 'streetNumber'"

          @input="onAddressInput"
          @suggestion="suggestionSelected"
      />
      <!-- No suggestions for street number -->
    </div>
    <div class="form-group col-12 col-md-9">
      <label>Street name</label>
      <suggestions
          :suggestions="addressSuggestions"
          :value="address.streetName"
          autocomplete="street-address"
          inputClasses="form-control"
          maxlength="200"
          name="streetName"
          placeholder="Street name"

          type="text"
          @focus="activeAddressInputName = 'streetName'"

          @input="onAddressInput"
          @suggestion="suggestionSelected"
      />
    </div>
    <div class="form-group col-12 col-md-9 ml-auto">
      <label>Suburb</label>
      <suggestions
          :suggestions="addressSuggestions"
          :value="address.suburb"
          autocomplete="suburb"
          inputClasses="form-control"
          maxlength="200"
          name="suburb"
          placeholder="Suburb"

          type="text"
          @focus="activeAddressInputName = 'suburb'"

          @input="onAddressInput"
          @suggestion="suggestionSelected"
      />
    </div>
    <div class="form-group col-12 col-md-6">
      <label>City</label>
      <suggestions
          :suggestions="addressSuggestions"
          :value="address.city"
          autocomplete="address-level2"
          inputClasses="form-control"
          maxlength="200"
          name="city"
          placeholder="City"

          type="text"
          @focus="activeAddressInputName = 'city'"

          @input="onAddressInput"
          @suggestion="suggestionSelected"
      />
    </div>
    <div class="form-group col-12 col-md-6">
      <label>Region</label>
      <suggestions
          :suggestions="addressSuggestions"
          :value="address.region"
          autocomplete="address-level1"
          inputClasses="form-control"
          maxlength="200"
          name="region"
          placeholder="Region"

          type="text"
          @focus="activeAddressInputName = 'region'"

          @input="onAddressInput"
          @suggestion="suggestionSelected"
      />
    </div>

    <div class="form-group col-12 col-md-6">
      <label>Post code</label>
      <suggestions
          :suggestions="addressSuggestions"
          :value="address.postcode"
          autocomplete="postal-code"
          inputClasses="form-control"
          maxlength="30"
          name="postcode"
          placeholder="Post code"

          type="text"
          @focus="activeAddressInputName = 'postcode'"

          @input="onAddressInput"
          @suggestion="suggestionSelected"
      />
    </div>
    <div class="form-group required col-12 col-md-6">
      <label>Country</label>
      <select
          :suggestions="addressSuggestions"
          :value="address.country"
          autocomplete="country-name"
          class="form-control"
          maxlength="100"
          name="country"
          placeholder="Country"
          required
          @focus="activeAddressInputName = 'country'"
          @input="event => onAddressInput(event.target.value)"
          @suggestion="suggestionSelected"
      >
        <!-- countryCodes is key-value map from code to name. Get array of codes, convert to {code, name} object array then sort by name -->
        <option
            v-for="country in countryData"
            :key="country.code"
            :value="country.name">{{ country.name }}
        </option>
      </select>
    </div>
  </div>
</template>
<script>

import {Api} from "@/Api";
import EditDistance from "@/EditDistance";
import countryData from "@/assets/countryData.json";

import Suggestions from "./Suggestions";

// Fields in order of specificity
// When updating this, ensure all address related functions and input properties are updated as well!
export const ADDRESS_SECTION_NAMES = ["streetNumber", "streetName", "suburb", "city", "region",
  "postcode",
  "country"];
Object.freeze(ADDRESS_SECTION_NAMES);
// Can't move this to the constants file as it is too linked with HTML, props etc.

export default {
  name: "address-form",

  components: {
    "suggestions": Suggestions
  },

  props: {
    address: {
      required: true,
      default: {
        streetNumber: "",
        streetName: "",
        city: "",
        suburb: "",
        region: "",
        postcode: "",
        country: "",
      }
    },
    countryData: {
      required: false,
      default: () => countryData
    }
  },

  data() {
    return {
      activeAddressInputName: null,
      addressSuggestionsRaw: [],
      addressSuggestions: [],

      /**
       * If country name is set and is different from the country used by OSM, Photon may not
       * return results. Hence, before sending the query to Photon, convert the country name
       * from the canonical name to that used by Photon if it is known - a previous query returned that country name
       */
      canonicalCountryNameToOsmCountryNameCache: {}
    }
  },

  methods: {
    /**
     * Map OSM properties object to address components used in the sign up page
     * @argument properties OSM properties object
     * @return object. Components may be undefined. Country may be undefined if OSM's
     * two character country code (`countrycode`) is not found in country codes list.
     *
     */
    mapOSMPropertiesToAddressComponents: function (properties) {
      const {
        housenumber, // streetNumber
        street,      // streetName
        district,    // suburb
        county,      // city
        state,       // region
        postcode,
        countrycode,
        country,
        type,
        name
        // osm_id,
      } = properties;

      // Convert from OSM country name to restcountries name
      const countryCanonical = this.countryCodeToNameDict[countrycode];

      if (countryCanonical != undefined && country != undefined &&
          this.canonicalCountryNameToOsmCountryNameCache[countryCanonical] == undefined) {
        this.canonicalCountryNameToOsmCountryNameCache[countryCanonical] = country;
        // Add OSM country name to cache
      }

      let streetName = street;
      if (type === "street") {
        // For some reason, sometimes we get results where housenumber or street name is undefined,
        // type is road and the name property is the name of the road. No idea why
        streetName = name;
      }

      const components = {
        streetNumber: housenumber,
        streetName: streetName,
        suburb: district,
        city: county,
        region: state,
        postcode: postcode,
        country: countryCanonical
      };

      return components;
    },

    /**
     * Generates address string from current inputs
     * If any values are undefined, it is ignored
     * @param convertCountryToOsmName if true, converts country name to country name used by OSM
     */
    generateAddressString: function (convertCountryToOsmName = false) {
      // Was getting strange errors where sometimes a component would be undefined
      // Think its to do with accessing properties via this['someString'] so this
      // is a workaround
      let {streetNumber, streetName, suburb, city, region, postcode, country} = this.address;

      if (convertCountryToOsmName && this.canonicalCountryNameToOsmCountryNameCache[country]
          !== undefined) {
        country = this.canonicalCountryNameToOsmCountryNameCache[country];
      }

      let components = {
        streetNumber,
        streetName,
        suburb,
        city,
        region,
        postcode,
        country
      };
      return this.generateAddressStringFromAddressComponents(components);
    },

    /**
     * Generates address string in display order
     * @param addressComponents object containing components of address
     * @param mostSpecificComponentName name most specific address component present in the returned string
     * If invalid, returns all components (default behaviour)
     * @param failOnUndef if true, will return undefined if any components are undefined. If false, will simply ignore. If any are present but are empty strings (or all whitespace), they will be ignored
     * @return undefined if failOnUndef is true and component is undefined, a string otherwise
     */
    generateAddressStringFromAddressComponents: function (addressComponents,
        mostSpecificComponentName = null, failOnUndef = false) {
      let address = "";
      for (let name of this.getAddressComponentNamesLessSpecificThan(mostSpecificComponentName)) {
        const component = addressComponents[name];
        if (component == undefined) {
          if (failOnUndef) {
            return;
          }
          continue;
        }
        const trimmed = component.trim();
        if (trimmed.length == 0) {
          if (name == "streetName") {
            address = "";
          }
          // If street name is blank then get rid of street number too
          continue;
        }
        if (address.length != 0) {
          if (name === "streetName") {
            address += " ";
          }// No comma between street number and street name; just a space
          else {
            address += ", ";
          }
        }
        address += trimmed
      }
      return address.trim();
    },

    /**
     * Returns an ordered list of sections that are less specific than the given component
     * e.g. if city is given, city, region, postcode, country are returned
     * If componentName is not valid, it will return all section names
     */
    getAddressComponentNamesLessSpecificThan(componentName) {
      let startIndex = ADDRESS_SECTION_NAMES.findIndex(el => el == componentName);
      if (startIndex == -1) {
        startIndex = 0;
      }

      return ADDRESS_SECTION_NAMES.slice(startIndex);
    },

    /**
     * Generates address string from OSM object for string comparison with edit distance
     * Includes properties not used in the rest of the component such as district, locality
     * @param {object} osmAddress OSM object
     * @return {string} OSM address with several relevant fields concatanated together with commas and spaces
     */
    generateComparisonString(osmAddress) {
      if (osmAddress.housenumber
          && osmAddress.street) {
        osmAddress["streetaddress"] = osmAddress.housenumber + " "
            + osmAddress.street;
      } else if (osmAddress.street) {
        osmAddress["streetaddress"] = osmAddress.street;
      }
      const components = [
        "streetaddress",
        "district",
        "locality",
        "city",
        "county",
        "state",
        "postcode",
        "country"
      ];

      // Concatenate with comma and space
      // housenumber/street could be undefined so streetaddress could be undefined as well. Hence, filter
      const result = components.filter(name => osmAddress[name]).reduce(
          (prev, curr) => prev + osmAddress[curr] + ", ", "");
      if (result.length) {
        return result.slice(0, -2);
      } // Remove comma and space from end
      return result;
    },

    /**
     * Filters raw address suggestions and formats them in a way suitable for the autofill component
     */
    generateAddressSuggestions: function () {
      const suggestionsDict = {};
      // Using dict instead of array to remove duplicates (e.g. shops in a mall will have different name but otherwise same address)

      const originalString = this.generateAddressString().toLocaleLowerCase();
      for (const {type, properties} of this.addressSuggestionsRaw) {
        if (type != "Feature") {
          continue;
        }
        const addressComponents = this.mapOSMPropertiesToAddressComponents(properties);
        if (addressComponents.country == undefined) {
          continue;
        } // Unknown country

        const addressString = this.generateAddressStringFromAddressComponents(addressComponents,
            this.activeAddressInputName, true);

        if (addressString == undefined) {
          // Ignore any suggestions where the necessary components are not present
          continue;
        }

        const resultString = this.generateComparisonString(properties);
        const distance = EditDistance.calculate(
            originalString,
            resultString.toLocaleLowerCase(),
            this.$constants.ADDRESS_FORM.EDIT_DISTANCE.INSERT_COST,
            this.$constants.ADDRESS_FORM.EDIT_DISTANCE.DELETE_COST,
            this.$constants.ADDRESS_FORM.EDIT_DISTANCE.SUBSTITUTE_COST
        );

        const ratio = distance / Math.abs(resultString.length);

        // Use ratio to not favour longer suggestions
        if (ratio > this.$constants.ADDRESS_FORM.EDIT_DISTANCE_WORST_RATIO) {
          // Suggestion too bad
          continue;
        }

        suggestionsDict[addressString] = {
          ...addressComponents,
          score: ratio,
          toString: () => addressString
        }
      }

      const sorted = Array.from(Object.values(suggestionsDict));
      sorted.sort((a, b) => a.score - b.score);
      // Highest score at the top
      return sorted.map(el => {
        // eslint-disable-next-line no-unused-vars
        const {score, ...withoutScore} = el;
        return withoutScore;
      });
    },

    /**
     * Pipeline for getting suggestions from axios, filtering then and placing them into suggestions array
     */
    addressSuggestionsPipeline: async function () {
      let data;
      try {
        data = await Api.addressSuggestions(this.generateAddressString(true));
        // Convert country name to that used by OSM
      } catch (_) {
        return;
        // If autocomplete does not work, just don't show a response
      }

      this.addressSuggestionsRaw = data.features;
      this.addressSuggestions = this.generateAddressSuggestions();
    },

    /**
     * Method which runs on address change, calling the photon API when required
     */
    addressChange: function () {
      window.clearTimeout(this.apiRequestTimeout);
      this.apiRequestTimeout = window.setTimeout(() => {
        this.apiRequestTimeout = null;
        if (this.generateAddressString().length
            > this.$constants.ADDRESS_FORM.API_MIN_QUERY_LENGTH) {
          this.addressSuggestionsPipeline();
        }
      }, this.$constants.ADDRESS_FORM.API_CALL_DEBOUNCE_TIME);
    },

    /**
     * Updates the address values in the model when there is an input event
     * unless it is the street number input
     * Also notifies the parent
     */
    onAddressInput: function (value) {
      this.$set(this.address, this.activeAddressInputName, value);
      // Think Vue might have issues reacting to updates when set via object['key'] syntax?
      // this[this.activeAddressInputName] = value;
      if (this.activeAddressInputName !== "streetNumber") {
        this.addressChange();
      }
      this.sendAddressUpdateEvent();
    },

    /**
     * When a selection is suggested, it should autofill text in the current input field,
     * plus all those below (which should be less specific)
     */
    suggestionSelected: function (suggestion) {
      for (let name of this.getAddressComponentNamesLessSpecificThan(this.activeAddressInputName)) {
        this.$set(this.address, name, suggestion[name]);
        // Vue can't track changes made when using this[propName] (or this.someArray[index])
        // so this alternative syntax is required
      }

      this.sendAddressUpdateEvent(); // Autofill suggestion modifies the addresses so need to send event to parent
    },

    /**
     * Alerts the parent component that the address was updated
     */
    sendAddressUpdateEvent: function () {
      this.$emit("addressupdate", {
        ...this.address,
        toString: () => {
          return this.generateAddressStringFromAddressComponents(this.address, null, false)
        }
      });
    }
  },

  watch: {
    /**
     * Update address suggestions text when the active input address field changes,
     * unless it is focused on the street number
     */
    activeAddressInputName: function () {
      // Couldn't make addressSuggestions a computed property for some reason: would never update
      // Hence, when active address input changes the address suggestions array has to be updated
      // (toString value changes)
      if (this.activeAddressInputName !== "streetNumber") {
        this.addressChange();
      }
      this.addressSuggestions = this.generateAddressSuggestions();
    },

    /**
     * When callee updates address, need to trigger API again
     */
    address: function () {
      if (this.activeAddressInputName !== "streetNumber") {
        this.addressChange();
      }
    }
  },

  computed: {
    /**
     * Converts countryData to { 2DigitCountryCode: countryName } dictionary
     */
    countryCodeToNameDict: function () {
      const dict = {};
      this.countryData.forEach(country => {
        dict[country.code] = country.name;
      });

      return dict;
    }
  }
}
</script>
