<template>
  <div class="w-100 d-flex pb-4">
    <form
      autocomplete="on"
      class="container slightly-transparent-inputs"
      method="POST"
      v-on:submit.prevent="create"
    >
      <div class="row">
        <div class="col">
          <h2>Create a Card</h2>
        </div>
      </div>
      <div class="row">
        <div class="col-12 d-flex flex-wrap flex-column form-group required">
          <label class="form-label">Section</label>
          <span class="d-block text-center form-text">
            The section your card will be listed in
          </span>
          <div
            class="btn-group"
            v-bind:class="{
            }"
          >
            <button
              v-for="([sectionKey, sectionName]) in Object.entries($constants.MARKETPLACE.SECTIONS)"
              v-bind:key="sectionKey"
              type="button"
              class="btn btn-primary"
              v-bind:class="{ active: section == sectionKey }"
              v-on:click="section = sectionKey"
            >
              {{ sectionName }}
            </button>
          </div>
          <div
            class="invalid-feedback"
            v-bind:class="{
              'd-block': !Object.keys($constants.MARKETPLACE.SECTIONS).includes(section)
            }"
          >
            You must select a section
          </div>
        </div>
        <div class="col-12 form-group required">
          <label class="form-label" for="title">Title</label>
          <input
            type="text"
            name="title"
            id="title"
            class="form-control"
            v-model="title"
            placeholder="Title"
            required
          />
        </div>
      </div>
      <div class="row">
        <div class="col-12 form-group">
          <label for="tags">Tags</label>
          <div class="d-flex flex-wrap">
            <button
              class="btn btn-primary"
              v-if="!showSuggestions"
              v-on:click="showSuggestionsInput"
            >
              + Tag
            </button>
            <suggestions
              v-if="showSuggestions"
              inputClasses="form-control"
              name="tags"
              id="tags"
              placeholder="Add a Tag"
              type="text"
              ref="suggestionsInput"
              v-bind:suggestions="keywordSuggestions"
              v-bind:value="tempSuggestionValue"
              v-on:input="keywordInput"
              v-on:suggestion="keywordSuggestionSelected"
              v-on:blur="showSuggestions = false"
            />
            <div class="d-flex">
              <div class="border border-primary d-flex rounded justify-content-center align-items-center"
                v-for="keyword in keywords"
                v-bind:key="keyword.id"
              >
                <span class="mx-1">
                  {{keyword.name}}
                </span>
                <button
                  class="btn btn-outline-primary d-flex justify-content-center align-items-center p-0 mx-1 rounded-circle"
                  type="button"
                  v-on:click="removeKeyword(keyword.id)"
                >
                  <span class="material-icons">close</span>
                </button>
              </div>
            </div>
          </div> 
        </div>
      </div>
      <div class="row">
        <div class="col-12 form-group">
          <label for="description">Description</label>
          <textarea
            id="description"
            v-model="description"
            class="form-control"
            maxlength="500"
            name="description"
            placeholder="Description"
            rows="5"
          />
        </div>
      </div>
      <div class="row">
        <div
          class="col-12"
          v-if="errorMessage != null"
        >
          <div class="alert alert-warning">
            {{errorMessage}}
          </div>
        </div>
        <div class="col-12">
          <button
            type="submit"
            class="btn btn-primary"
          >
            Create card
          </button>
        </div>
      </div>
    </form>
  </div>
</template>
<script>
import Suggestions from "../components/Suggestions.vue";
import {EditDistance} from "./../EditDistance";
const { Api } = require("./../Api");

export default {
  components: { Suggestions },
  props: {
    /**
     * ID of user to create the card as
     */
    userId: {
      required: true,
      type: Number
    },

    /**
     * Name of the initial section to set the card to. See `constants.js/MARKETPLACE/SECTIONS`
     */
    initialSection: {
      required: false,
      type: String,
    }
  },

  data() {
    return {
      section: Object.keys(this.$constants.MARKETPLACE.SECTIONS)[0],
      title: "",
      description: "",
      errorMessage: null,
      tempSuggestionValue: "",
      showSuggestions: false,
      keywords: []
    }
  },

  computed: {
    keywordSuggestions() {
      const keywords = [ "Apple", "Orange", "Cake", "Portal", "Apple MacBook Pro", "Sulfur Nitrate", "Max is awesome"].map((keyword, i) => ({ name: keyword, id: i, toString: () => keyword }));

      const { NUM_SUGGESTIONS, WORST_RATIO, INSERT_COST, DELETE_COST, SUBSTITUTE_COST } = this.$constants.MARKETPLACE.CREATE_CARD.TAG_SUGGESTIONS;

      let suggestions = keywords.map(keyword => {
        keyword.score = new EditDistance(
          this.tempSuggestionValue.toLocaleLowerCase(),
          keyword.name.toLocaleLowerCase(),
          INSERT_COST,
          DELETE_COST,
          SUBSTITUTE_COST
        ).calculate();

        keyword.weightedScore = keyword.score / keyword.name.length;
        keyword.toString = () => keyword.name + " " + keyword.weightedScore;
        return keyword;
      });

      suggestions.sort((a, b) => a.weightedScore - b.weightedScore);
      const selectedSuggestions = new Set(this.keywords.map(({id}) => id));
      suggestions = suggestions.filter(({ id, weightedScore }) => weightedScore < WORST_RATIO && !selectedSuggestions.has(id));
      // Don't return already selected suggestions
      suggestions = suggestions.slice(0, NUM_SUGGESTIONS);
      return suggestions;
    }
  },

  beforeMount() {
    if (this.initialSection) this.section = this.initialSection;
  },

  methods: {
    keywordInput: async function(inputValue) {
      this.tempSuggestionValue = inputValue; 
    },

    keywordSuggestionSelected: async function(keyword) {
      this.keywords.push(keyword);
      this.tempSuggestionValue = "";
    },

    showSuggestionsInput() {
      this.showSuggestions = true;
      this.$nextTick(() => {
        console.log(this.$refs.suggestionsInput.forceFocus());
        this.$refs.suggestionsInput.$refs.input.focus()
      });
    },

    removeKeyword(id) {
      this.keywords = this.keywords.filter(keyword => keyword.id != id);
    },

    /**
     * Pipeline triggered when user submits form; validates the data and if successful,
     * sends request to the server.
     */
    create: async function() {
      if (!Object.keys(this.$constants.MARKETPLACE.SECTIONS).includes(this.section)) {
        this.errorMessage = "You must select a section to list your card in";
        return;
      }

      if (this.title.trim().length == 0) {
        this.errorMessage = "You must add a title to your card";
        return;
      }

      const cardData = {
        creatorId: this.userId,
        section: this.section,
        title: this.title,
        description: this.description,
        keywordIds: this.keywords.map(keyword => keyword.id)
      };

      try {
        const { data } = await Api.createCard(cardData);
        this.errorMessage = null;
        
        this.errorMessage = `Card creation succeeded (ID ${data.cardId})`
        // this.$router.push({
        //   name: "card",
        //   params: {
        //     cardId: data.cardId
        //   }
        // });
      } catch(err) {
        this.errorMessage = err.userFacingErrorMessage;
      }
    }
  },

  watch: {
    initialSection() {
      if (this.initialSection) this.section = this.initialSection;
    }
  }
}
</script>
<style scoped>

</style>