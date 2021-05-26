<template>
  <div class="w-100 d-flex my-3">
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
          <div class=" d-flex flex-wrap align-items-center">
            <label for="tags" class="mr-2 mb-2 py-2">Tags: </label>
            <span class="mr-2 mb-2 py-2" v-if="!tags.length">None</span>
            <tag
              v-else
              v-for="tag in tags"
              class="mr-2 mb-2"
              v-bind:key="tag.id"
              v-bind:xButton="true"
              v-on:xClick="removeTag(tag.id)"
            >
              {{tag.name}}
            </tag>
          </div>
          <div class="add-tag-container">
            <button
              class="btn btn-primary w-100"
              v-if="!showSuggestions"
              v-on:click="showSuggestionsInput"
            >
              Add Tag 
            </button>
            <suggestions
              v-else
              inputClasses="form-control"
              name="tags"
              id="tags"
              placeholder="Add a Tag"
              type="text"
              ref="suggestionsInput"
              v-bind:liActiveClasses="{'bg-primary': true, 'text-light': true}"
              v-bind:suggestions="tagSuggestions"
              v-bind:value="tagInputValue"
              v-on:input="value => tagInputValue = value"
              v-on:suggestion="tagSuggestionSelected"
              v-on:blur="showSuggestions = false"
            />
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
<style scoped>
.add-tag-container {
  width: min(100%, 20em);
}
</style>
<script>
import Suggestions from "../../components/Suggestions.vue";
import Tag from "../../components/Tag.vue";
import {EditDistance} from "../../EditDistance";

// While there is no backend, use this static list of tags
import temporaryTags from "../../assets/temporaryTags.json";

const { Api } = require("../../Api");

export default {
  components: { Suggestions, Tag },
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
      // Initialize section first section in the constants list 
      section: Object.keys(this.$constants.MARKETPLACE.SECTIONS)[0],
      title: "",
      description: "",
      errorMessage: null,
      tagInputValue: "",
      // Value of tag suggestions input field
      showSuggestions: false,
      allTags: temporaryTags,
      // Array of objects { id: Number, name: String }
      tags: []
    }
  },

  computed: {
    /**
     * Computes the suggestions that should be shown to the user given the static list of suggestions
     * and value of the add tag input field
     */
    tagSuggestions() {
      const { NUM_SUGGESTIONS, WORST_RATIO, INSERT_COST, DELETE_COST, SUBSTITUTE_COST } = this.$constants.MARKETPLACE.CREATE_CARD.TAG_SUGGESTIONS;

      let suggestions = this.allTags.map(tag => {
        tag.score = new EditDistance(
          this.tagInputValue.toLocaleLowerCase(),
          tag.name.toLocaleLowerCase(),
          INSERT_COST,
          DELETE_COST,
          SUBSTITUTE_COST
        ).calculate();
        
        tag.weightedScore = tag.score / tag.name.length;
        // weighted score takes into account length of tag so that it doesn't prefer shorter suggestions
        
        tag.toString = () => tag.name; // toString used to show the suggestion

        return tag;
      });

      suggestions.sort((a, b) => a.weightedScore - b.weightedScore);
      const selectedSuggestions = new Set(this.tags.map(({id}) => id));
      suggestions = suggestions.filter(({ id, weightedScore }) => weightedScore < WORST_RATIO && !selectedSuggestions.has(id));
      // Filter out bad suggestions (scores too high) and already selected suggestions

      suggestions = suggestions.slice(0, NUM_SUGGESTIONS);
      // If there are too many suggestions, only return the best few

      if (suggestions.length == 0) {
        // If there are no suggestions add a disabled item to show this (makes it clear that the user needs to change the suggestion) 
        suggestions = [{
          toString: () => "No suggestions",
          disabled: true
        }];
      }
      return suggestions;
    }
  },

  beforeMount() {
    if (this.initialSection) this.section = this.initialSection;
  },

  methods: {
    /**
     * When tag is selected clear the input field and add the tag to the list of selected tags
     */
    tagSuggestionSelected: async function(tag) {
      this.tags.push(tag);
      this.tagInputValue = "";
    },

    /**
     * When the add tag button is clicked, show the suggestion and force focus on the suggestions input element
     */
    showSuggestionsInput() {
      this.showSuggestions = true;
      this.$nextTick(() => {
        // Need to wait until next tick for the input element to be created on the DOM
        // so that focus can be forced
        this.$refs.suggestionsInput.$refs.input.focus()
      });
    },

    /**
     * Removes the tag with the given ID
     */
    removeTag(id) {
      this.tags = this.tags.filter(tag => tag.id != id);
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
        description: this.description.trim(),
        // remove extra newlines etc. at the start and end
        tagIds: this.tags.map(tag => tag.id)
      };

      try {
        await Api.createCard(cardData);
        this.errorMessage = null;
        
        this.$router.push({
          name: "Marketplace",
          params: {
            section: this.section
          }
        });
      } catch(err) {
        this.errorMessage = err.userFacingErrorMessage;
      }
    }
  },

  watch: {
    /**
     * If initial section changes, update it
     */
    initialSection() {
      if (this.initialSection) this.section = this.initialSection;
    }
  }
}
</script>