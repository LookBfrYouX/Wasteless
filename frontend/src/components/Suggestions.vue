<!--
Component which has input element and list of suggestions wrapped in a div

Notes:

- `suggestions`:
  - Array of suggestions to show
  - The only required property
  - `toString` is called on the element to get the value to display
  - Currently, the key is the index
- `value`: value of input element
  - Use `v-on:input` to receive the value (NOT the event) of the input when it changes
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

-->
<template>
  <div v-bind:class="divClasses">
    <input
        v-bind="$attrs"

        v-bind:class="inputClasses"

        v-bind:value="value"
        v-on:blur="blur"
        v-on:focus="focus"
        v-on:input="args => $emit('input', args.target.value)"
        v-on:keydown="keydown"
    />
    <ul v-if="showSuggestions"
        v-bind:class="ulClasses"
    >
      <li
          v-for="(suggestion, i) in suggestions"
          v-bind:key="i"
          :data-index="i"
          v-bind:class="{
          ...(i == index? liActiveClasses: liInactiveClasses),
          ...liClasses
        }"
          v-on:click="suggestionClick"
          v-on:mouseover="suggestionMouseover"
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
    suggestions: {
      required: true,
      default: () => {
        return []
      }
    },
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
    }, liInactiveClasses: {
      default: () => {
        return {
          "slightly-transparent-background": true
        }
      }
    },
    blurDelay: {
      default: 100
    }
  },

  data() {
    return {
      showSuggestions: false,
      index: -1,
      blurTimeout: null
    }
  },

  methods: {
    keydown: function (event) {
      // If user clicked enter on suggestion, suggestions are hidden, so if they start typing again show suggestions
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

    focus: function (event) {
      // If there is a blur and focus in event within the blurDelay, suggestions will be
      // shown and immediately hidden again. This prevents this
      window.clearTimeout(this.blurTimeout);
      this.showSuggestions = true;
      this.$emit('focus', event);
    },

    blur: function (event) {
      // Without this, when user clicks the suggestion blur is called before click,
      // causing li to become hidden
      this.blurTimeout = window.setTimeout(() => {
        this.showSuggestions = false;
        this.$emit('blur', event);
      }, this.blurDelay);
    },

    /**
     * Sets the index to the clicked element, hides the suggestions and inputs an `input` event
     */
    suggestionClick: function (event) {
      this.suggestionMouseover(event);
      this.suggestionSelected();
    },

    /**
     * Sets the index to that of the index currently being selected
     */
    suggestionMouseover: function (event) {
      const index = event.target.getAttribute("data-index");
      this.index = index;
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