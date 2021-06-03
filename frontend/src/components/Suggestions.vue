<!--
Component which has input element and list of suggestions wrapped in a div

Notes:

- `suggestions`:
  - Array of suggestions to show
  - The only required property
  - `toString` is called on the element to get the value to display
  - Optional `disabled` boolean can be on the element
  - Currently, the key is the index
- `value`: value of input element
  - Use `@input` to receive the value (NOT the event) of the input when it changes
    - This component does NOT set the value of the input itself, even when a suggestion is clicked
  - When a suggestion is selected, a `suggestion` event is emitted. This passes an element of the array (not necessarily a string)
- Props are passed to the input element EXCEPT for the `class` property, which is passed
  the `div` wrapping the two elements. This is a quirk of Vue 2
- Children (slots) are not supported
- `keydown` events will not return arrow up/down or enter events 
- `focus` events are available as usual
- `blur` events are returned after a delay (`blurDelay`)
  - This is necessary if the user clicks on a suggestion, it causes a blur event
    on the input element which triggers before the click event. When this happens,
    the suggestion list is hidden, and so without a delay, the click event will not occur
- Styling can be one via the the `divClasses`, `inputClasses`, `ulClasses` and `liClasses` props
  - These should all be objects
  - `liActiveClasses` is applied to the currently selected suggestion, if any
  - `liInactiveClasses` is applied to any unselected suggestions
  - `liDisabledClasses` is applied if the suggestion is disabled
- Any attributes (e.g. class, type) are passed onto the input element
- A ref to the input element is available via `$refs.input`
-->
<template>
  <div :class="divClasses">
    <input
        ref="input"
        v-bind="$attrs"
        :class="inputClasses"

        :value="value"
        @blur="blur"
        @focus="focus"
        @input="args => $emit('input', args.target.value)"
        @keydown="keydown"
    />
    <ul v-if="showSuggestions"
        :class="ulClasses"
    >
      <li
          v-for="(suggestion, i) in suggestions"
          :key="i"
          :class="listItemClasses(suggestion, i)"
          @click="() => suggestionClick(i)"
          @mouseover="() => suggestionMouseover(i)"
      >
        {{ suggestion.toString() }}
      </li>
    </ul>
  </div>
</template>
<style scoped>
.suggestions-list {
  position: relative;
  z-index: 1;
}
</style>
<script>

export default {
  name: "autocomplete",
  components: {},
  inheritAttrs: false,

  props: {
    /**
     * Array of suggestions
     * `toString()` used to display values
     * If object and `disabled` is true, the suggestion appears disabled
     */
    suggestions: {
      required: true,
      default: () => {
        return []
      }
    },

    /**
     * Current value of the text input. Use `@input` to receive the value of the input (not an event)
     */
    value: {
      default: ""
    },

    divClasses: {
      default: () => {
        return {}
      }
    },
    // in Vue 2, class attribute not part of $attrs and gets applied to the root element even if inheritAttrs is false
    inputClasses: {
      default: () => {
        return {}
      }
    },
    ulClasses: {
      default: () => {
        return {
          "list-group": true,
          "suggestions-list": true
        }
      }
    },
    liClasses: {
      default: () => {
        return {
          "list-group-item": true,
        }
      }
    },
    liActiveClasses: {
      default: () => {
        return {
          "active": true
        }
      }
    },
    liInactiveClasses: {
      default: () => {
        return {
          "slightly-transparent-white-background": true
        }
      }
    },
    liDisabledClasses: {
      default: () => {
        return {
          "slightly-transparent-white-background": true,
          "disabled": true
        }
      }
    },
    blurDelay: {
      default: 150 // found 100 to be a bit short on my PC when I had a lot of apps open
    }
  },

  data() {
    return {
      showSuggestions: false,
      index: -1,
      blurTimeout: null,
    }
  },

  methods: {
    /**
     * Handles arrow and enter events, and lets other events pass straight through.
     * Arrow up/down omdifies the selected suggestions, enter triggers suggestion event
     * and hides the suggestion list. If the user types again the suggestions become visible again
     */
    keydown: function (event) {
      this.showSuggestions = true;
      switch (event.code) {
        case "ArrowDown":
          event.preventDefault();
          this.index = Math.min(this.suggestions.length - 1, this.index + 1);
          break;

        case "ArrowUp":
          event.preventDefault();
          if (this.index != -1) {
            this.index = Math.max(this.index - 1, -1);
          }
          break;

        case "Enter":
          this.suggestionSelected();
          event.preventDefault();
          break;

        default:
          this.$emit("keydown", event);
      }
    },

    /**
     * If there is a blur and focus in event within the blurDelay, suggestions will be shown
     * but then hidden once the timeout occurs. This resets the timeout so that this does not occur
     */
    focus: function (event) {
      window.clearTimeout(this.blurTimeout);
      this.showSuggestions = true;
      this.$emit('focus', event);
    },

    /**
     * Delays the blur event by a little bit so that the suggestion click event comes through;
     * without it, the suggestions become hidden so the click event does not occur.
     */
    blur: function (event) {
      this.blurTimeout = window.setTimeout(() => {
        this.showSuggestions = false;
        this.$emit('blur', event);
      }, this.blurDelay);
    },

    /**
     * Sets the index to the given index, hides the suggestions and inputs an `input` event, but only if the element is not disabled
     */
    suggestionClick: function (i) {
      if (this.isDisabled(this.suggestions[i])) {
        return;
      }
      this.suggestionMouseover(i);
      this.suggestionSelected();
    },

    /**
     * Sets the index to that of the index currently being selected, but only if the element is not disabled
     */
    suggestionMouseover: function (i) {
      if (this.isDisabled(this.suggestions[i])) {
        return;
      }
      this.index = i;
    },

    /**
     * Hides the suggestion box and emits an `input` event
     */
    suggestionSelected: function () {
      this.showSuggestions = false;
      // Sanity check before emitting the event
      if (this.index >= 0 && this.index < this.suggestions.length) {
        const suggestion = this.suggestions[this.index];
        this.$emit("input", suggestion.toString());
        this.$emit("suggestion", suggestion);
      }
    },

    /**
     * Given an element of an array, returns true if the `disabled` property exists and is true
     */
    isDisabled: function (el) {
      return typeof el === "object" && el["disabled"] === true;
    },

    /**
     * Returns object containing the classes the list element should have
     */
    listItemClasses: function (el, i) {
      if (this.isDisabled(el)) {
        return {...this.liClasses, ...this.liDisabledClasses};
      } else if (i == this.index) {
        return {...this.liClasses, ...this.liActiveClasses};
      } else {
        return {...this.liClasses, ...this.liInactiveClasses};
      }
    }
  },

  watch: {
    /**
     * Ensures that if the list gets shorter, the current index is never after the last suggestion
     */
    suggestions: function () {
      if (this.index >= this.suggestions.length) {
        this.index = this.suggestions.length - 1;
      }
    }
  }
}

</script>