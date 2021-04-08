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
    <div class="form-group col-12">
      <label>Address Line</label>
      <suggestions
        type="text"
        name="addressLine"
        placeholder="Address line"
        maxlength="200"
        autocomplete="address-line"
        required
        inputClasses="form-control"

        v-on:focus="activeAddressInputName = 'addressLine'"
        v-on:input="onAddressInput"
        v-on:suggestion="suggestionSelected"

        v-bind:value="address.addressLine"
        v-bind:suggestions="addressSuggestions"
      />
    </div>

    <div class="form-group col-12 col-md-6">
      <label>Post code</label>
      <suggestions
        type="text"
        name="postcode"
        placeholder="Post code"
        maxlength="10"
        autocomplete="postal-code"
        required
        inputClasses="form-control"

        v-on:focus="activeAddressInputName = 'postcode'"
        v-on:input="onAddressInput"
        v-on:suggestion="suggestionSelected"

        v-bind:value="address.postcode"
        v-bind:suggestions="addressSuggestions"
      />
    </div>
    <div class="form-group col-12 col-md-6">
      <label>City</label>
      <suggestions
        type="text"
        name="city"
        placeholder="City"
        maxlength="100"
        autocomplete="address-level2"
        required
        inputClasses="form-control"

        v-on:focus="activeAddressInputName = 'city'"
        v-on:input="onAddressInput"
        v-on:suggestion="suggestionSelected"

        v-bind:value="address.city"
        v-bind:suggestions="addressSuggestions"
      />
    </div>
    <div class="form-group col-12 col-md-6">
      <label>Region/state</label>
      <suggestions
        type="text"
        name="state"
        placeholder="Region/state"
        maxlength="100"
        autocomplete="address-level1"
        required
        inputClasses="form-control"

        v-on:focus="activeAddressInputName = 'state'"
        v-on:input="onAddressInput"
        v-on:suggestion="suggestionSelected"

        v-bind:value="address.state"
        v-bind:suggestions="addressSuggestions"
      />
    </div>
    <div class="form-group required col-12 col-md-6">
      <label>Country</label>
      <suggestions
        type="text"
        name="country"
        placeholder="Country"
        maxlength="50"
        autocomplete="country-name"
        required

        inputClasses="form-control"

        v-on:focus="activeAddressInputName = 'country'"
        v-on:input="onAddressInput"
        v-on:suggestion="suggestionSelected"

        v-bind:value="address.country"
        v-bind:suggestions="addressSuggestions"
      />
    </div>
  </div>
</template>
<script>

const axios = require("axios");
const Suggestions = require("./Suggestions").default;

// Fields in order of specifity
// When updating this, ensure all address related functions and input properties are updated as well
export const ADDRESS_SECTION_NAMES = ["addressLine", "postcode", "city", "state",  "country"];
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
        addressLine: "",
        postcode: "",
        city: "",
        state: "",
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
    mapOSMPropertiesToAddressComponents: function(properties) {
      const {
        housenumber, street, //addressLine
        postcode,
        county, // city
        state, // state
        country,
        // osm_id,
      } = properties;

      const components = {
        addressLine: street,
        postcode: postcode,
        city: county,
        state: state,
        country: country
      };

       if (street != undefined && housenumber != undefined) {
        components.addressLine = `${housenumber} ${street}`;
       }
       // If street is undefined but housenumber isn't, leave addressLine undefined

       return components;
    },


    /**
     * Generates address string from current inputs
     * If any values are undefined, it is ignored
     */
    generateAddressString: function() {
      // Was getting strange errors where sometimes a component would be undefined
      // Think its to do with accessing properties via this['someString'] so this
      // is a workaround
      let { addressLine, city, state, postcode, country } = this.address;
      let components = {
        addressLine,
        postcode,
        city,
        state,
        country
      };
      return this.generateAddressStringFromAddressComponents(components);
    },

    /**
     * Generates address string
     * @param addressComponents object containing components of address
     * @param mostSpecificComponentName name most specific address component present in the returned string
     * If invalid, returns all components (default behaviour)
     * @param failOnUndef if true, will return undefined if any components are undefined. If false, will simply ignore. If any are present but are empty strings (or all whitespace), they will be ignored
     * @return undefined if failOnUndef is true and component is undefined, a string otherwise
     */
    generateAddressStringFromAddressComponents: function(addressComponents, mostSpecificComponentName = null, failOnUndef = false) {
      let address = "";
      for(let name of this.getAddressComponentNamesLessSpecificThan(mostSpecificComponentName)) {
        const component = addressComponents[name];
        if (component == undefined) {
          if (failOnUndef) return;
          continue;
        }
        const trimmed = component.trim();
        if (trimmed.length == 0) continue;
        if (address.length != 0) address += ", ";
        address += trimmed
      }
      return address.trim();
    },


    /**
     * Returns an ordered list of sections that are less specific than the given component
     * e.g. if city is given, city, state, postcode, country are returned
     * If componentName is not valid, it will return all section names
     */
    getAddressComponentNamesLessSpecificThan(componentName) {
      let startIndex = ADDRESS_SECTION_NAMES.findIndex(el => el == componentName);
      if (startIndex == -1) startIndex = 0;

      return ADDRESS_SECTION_NAMES.slice(startIndex);
    },

    /**
     * Filters raw address suggestions and formats them in a way suitable for the autofill component
     */
    generateAddressSuggestions: function() {
      const suggestionsDict = {};
      // Using dict instead of array to remove duplicates (e.g. shops in a mall will have different name but otherwise same address)

      for (const { type, properties } of this.addressSuggestionsRaw) {
        if (type != "Feature") continue;
        const addressComponents = this.mapOSMPropertiesToAddressComponents(properties);

        const addressString = this.generateAddressStringFromAddressComponents(addressComponents, this.activeAddressInputName, true);
        if (addressString == undefined) continue;
        // Ignore any suggestions where the necessary components are not present

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
    addressSuggestionsPipeline: async function() {
      const url = `https://photon.komoot.io/api?q=${encodeURIComponent(this.generateAddressString())}`;
      let response;
      try {
        response = await axios.get(url);
      } catch(_) {
        return;
        // If autocomplete does not work, just don't show a response
      }

      const { data } = response;
      this.addressSuggestionsRaw = data.features;
      this.addressSuggestions = this.generateAddressSuggestions();
    },

    /**
     * Method which runs on address change, calling the photon API when required
     */
    addressChange: function() {
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
     * Also notifies the parent
     */
    onAddressInput: function(value) {
      this.$set(this.address, this.activeAddressInputName, value);
      // Think Vue might have issues reacting to updates when set via object['key'] syntax?
      // this[this.activeAddressInputName] = value;
      this.addressChange();
      this.sendAddressUpdateEvent();
    },


    /**
     * When a selection is suggested, it should autofill text in the current input field,
     * plus all those below (which should be less specific)
     */
    suggestionSelected: function(suggestion) {
      for(let name of this.getAddressComponentNamesLessSpecificThan(this.activeAddressInputName)) {
        this.$set(this.address, name, suggestion[name]);
        // Vue can't track changes made when using this[propName] (or this.someArray[index])
        // so this alternative syntax is required
      }

      this.sendAddressUpdateEvent(); // Autofill suggestion modifies the addresses so need to send event to parent
    },

    /**
     * Alerts the parent component that the address was updated
     */
    sendAddressUpdateEvent: function() {
      this.$emit("addressupdate", {
        ...this.address,
        toString: () => {
          return this.generateAddressStringFromAddressComponents(this.address, null, false)
        }
      });
    }
  },

  watch: {
    activeAddressInputName: function() {
      // Couldn't make addressSuggestions a computed property for some reason: would never update
      // Hence, when active address input changes the address suggestions array has to be updated
      // (toString value changes)
      this.addressSuggestions = this.generateAddressSuggestions();
    },

    address: function() {
      this.addressChange();
    }
  }
}
</script>
