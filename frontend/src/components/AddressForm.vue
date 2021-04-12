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
          inputClasses="form-control"
          maxlength="50"
          name="streetnumber"
          placeholder="Street number"
          required
          type="text"

          v-bind:suggestions="[]"
          v-bind:value="address.streetNumber"
          v-on:focus="activeAddressInputName = 'streetNumber'"

          v-on:input="onAddressInput"
          v-on:suggestion="suggestionSelected"
      />
      <!-- No suggestions for street number -->
    </div>
    <div class="form-group col-12 col-md-9">
      <label>Street name</label>
      <suggestions
          autocomplete="address-line"
          inputClasses="form-control"
          maxlength="200"
          name="streetName"
          placeholder="Street name"
          required
          type="text"

          v-bind:suggestions="addressSuggestions"
          v-bind:value="address.streetName"
          v-on:focus="activeAddressInputName = 'streetName'"

          v-on:input="onAddressInput"
          v-on:suggestion="suggestionSelected"
      />
    </div>
    <div class="form-group col-12 col-md-6">
      <label>City</label>
      <suggestions
          autocomplete="address-level2"
          inputClasses="form-control"
          maxlength="100"
          name="city"
          placeholder="City"
          required
          type="text"

          v-bind:suggestions="addressSuggestions"
          v-bind:value="address.city"
          v-on:focus="activeAddressInputName = 'city'"

          v-on:input="onAddressInput"
          v-on:suggestion="suggestionSelected"
      />
    </div>
    <div class="form-group col-12 col-md-6">
      <label>Region</label>
      <suggestions
          autocomplete="address-level1"
          inputClasses="form-control"
          maxlength="100"
          name="region"
          placeholder="Region"
          required
          type="text"

          v-bind:suggestions="addressSuggestions"
          v-bind:value="address.region"
          v-on:focus="activeAddressInputName = 'region'"

          v-on:input="onAddressInput"
          v-on:suggestion="suggestionSelected"
      />
    </div>
    
    <div class="form-group col-12 col-md-6">
      <label>Post code</label>
      <suggestions
          autocomplete="postal-code"
          inputClasses="form-control"
          maxlength="10"
          name="postcode"
          placeholder="Post code"
          required
          type="text"

          v-bind:suggestions="addressSuggestions"
          v-bind:value="address.postcode"
          v-on:focus="activeAddressInputName = 'postcode'"

          v-on:input="onAddressInput"
          v-on:suggestion="suggestionSelected"
      />
    </div>
    <div class="form-group required col-12 col-md-6">
      <label>Country</label>
      <suggestions
          autocomplete="country-name"
          inputClasses="form-control"
          maxlength="50"
          name="country"
          placeholder="Country"
          required

          type="text"

          v-bind:suggestions="addressSuggestions"
          v-bind:value="address.country"
          v-on:focus="activeAddressInputName = 'country'"

          v-on:input="onAddressInput"
          v-on:suggestion="suggestionSelected"
      />
    </div>
  </div>
</template>
<script>

const axios = require("axios");
const Suggestions = require("./Suggestions").default;

// Fields in order of specifity
// When updating this, ensure all address related functions and input properties are updated as well
export const ADDRESS_SECTION_NAMES = ["streetNumber", "streetName", "city", "region", "postcode", "country"];
Object.freeze(ADDRESS_SECTION_NAMES);

const API_CALL_DEBOUNCE_TIME = 100;
const API_MIN_QUERY_LENGTH = 3;
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
        region: "",
        postcode: "",
        country: "",
      }
    }
  },

  data() {
    return {
      activeAddressInputName: null,
      addressSuggestionsRaw: [],
      addressSuggestions: [],
    }
  },

  methods: {
    /**
     * Map OSM properties object to address components used in the sign up page
     * @argument properties OSM properties object
     * @return object. Components may be undefined
     */
    mapOSMPropertiesToAddressComponents: function (properties) {
      const {
        housenumber, // streetNumber
        street, // streetName
        county, // city
        state, // region
        postcode,
        country,
        // osm_id,
      } = properties;

      const components = {
        streetNumber: housenumber,
        streetName: street,
        city: county,
        region: state,
        postcode: postcode,
        country: country
      };

      return components;
    },

    /**
     * Generates address string from current inputs
     * If any values are undefined, it is ignored
     */
    generateAddressString: function () {
      // Was getting strange errors where sometimes a component would be undefined
      // Think its to do with accessing properties via this['someString'] so this
      // is a workaround
      let {streetNumber, streetName, city, region, postcode, country} = this.address;
      let components = {
        streetNumber,
        streetName,
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
          if (name == "streetName") address = "";
          // If street name is blank then get rid of street number too
          continue;
        }
        if (address.length != 0) {
          if (name === "streetName") address += " ";
          // No comma between street number and street name; just a space
          else address += ", ";
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
     * Filters raw address suggestions and formats them in a way suitable for the autofill component
     */
    generateAddressSuggestions: function () {
      const suggestionsDict = {};
      // Using dict instead of array to remove duplicates (e.g. shops in a mall will have different name but otherwise same address)

      for (const {type, properties} of this.addressSuggestionsRaw) {
        if (type != "Feature") {
          continue;
        }
        const addressComponents = this.mapOSMPropertiesToAddressComponents(properties);

        const addressString = this.generateAddressStringFromAddressComponents(addressComponents,
            this.activeAddressInputName, true);
        if (addressString == undefined) {
          // Ignore any suggestions where the necessary components are not present
          continue;
        }

        suggestionsDict[addressString] = {
          ...addressComponents,
          toString: () => addressString
        }
      }

      return Array.from(Object.values(suggestionsDict));
    },

    /**
     * Pipeline for getting suggestions from axios, filtering then and placing them into suggestions array
     */
    addressSuggestionsPipeline: async function () {
      const url = `https://photon.komoot.io/api?q=${encodeURIComponent(
          this.generateAddressString())}`;
      let response;
      try {
        response = await axios.get(url);
      } catch (_) {
        return;
        // If autocomplete does not work, just don't show a response
      }

      const {data} = response;
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
        if (this.generateAddressString().length > API_MIN_QUERY_LENGTH) {
          this.addressSuggestionsPipeline();
        }
      }, API_CALL_DEBOUNCE_TIME);
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
      if (this.activeAddressInputName !== "streetNumber") this.addressChange();
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
      if (this.activeAddressInputName !== "streetNumber") this.addressChange();
      this.addressSuggestions = this.generateAddressSuggestions();
    },

    /**
     * When callee updates address, need to trigger API again
     */
    address: function () {
      if (this.activeAddressInputName !== "streetNumber") this.addressChange();
    }
  }
}
</script>
