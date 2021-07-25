<template>
  <v-app>
  <div class="w-100 d-flex my-3">
    <form
        autocomplete="on"
        class="container slightly-transparent-inputs"
        method="POST"
        @submit.prevent="create"
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
              :class="{
            }"
              class="btn-group"
          >
            <button
                v-for="([sectionKey, sectionName]) in Object.entries($constants.MARKETPLACE.SECTIONS)"
                :key="sectionKey"
                :class="{ active: section == sectionKey }"
                class="btn btn-primary"
                type="button"
                @click="section = sectionKey"
            >
              {{ sectionName }}
            </button>
          </div>
          <div
              :class="{
              'd-block': !Object.keys($constants.MARKETPLACE.SECTIONS).includes(section)
            }"
              class="invalid-feedback"
          >
            You must select a section
          </div>
        </div>
        <div class="col-12 form-group required">
          <label class="form-label" for="title">Title</label>
          <input
              id="title"
              v-model="title"
              class="form-control"
              name="title"
              maxlength="50"
              placeholder="Title"
              required
              type="text"
          />
        </div>
      </div>
      <div class="row">
        <div class="col-12 form-group">
          <v-btn
              v-if="!addKeywords"
            @click="getAllKeywords()"
            class="btn btn-primary">
            Add keywords
          </v-btn>
          <p
            v-if="addKeywords">Find keywords to add</p>
          <v-autocomplete
              v-if="addKeywords"
              background-color="transparent"
              chips
              clearable
              deletable-chips
              multiple
              :items="allKeywordsName"
              v-model="selectedKeywordsName"
          ></v-autocomplete>
        </div>
      </div>
      <div class="row">
        <div class="col-12 form-group">
          <label for="description">Description</label>
          <textarea
              id="description"
              v-model="description"
              class="form-control"
              maxlength="250"
              name="description"
              placeholder="Description"
              rows="5"
          />
        </div>
      </div>
      <div class="row">
        <div
            v-if="errorMessage != null"
            class="col-12"
        >
          <div class="alert alert-warning">
            {{ errorMessage }}
          </div>
        </div>
        <div class="col-12">
          <button
              class="btn btn-primary"
              type="submit"
          >
            Create card
          </button>
        </div>
      </div>
    </form>
  </div>
  </v-app>
</template>
<style scoped>

</style>
<script>

// While there is no backend, use this static list of tags
import temporaryTags from "../../assets/temporaryTags.json";

import {Api} from "@/Api";

export default {
  // components: {Suggestions, Tag},
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
      tags: [],
      addKeywords: false,
      allKeywords: null,
      allKeywordsName: [],
      selectedKeywordsName: []
    }
  },

  beforeMount() {
    if (this.initialSection) {
      this.section = this.initialSection;
    }
  },

  methods: {
    /**
     * Get all keywords from the database to show in the combobox.
     */
    getAllKeywords: async function () {
      try {
        const data = (await Api.getAllKeywords()).data;
        this.allKeywords = data;
        this.allKeywordsName = data.map(keyword => keyword.name);
        this.addKeywords = true;
      } catch (err) {
        this.errorMessage = "Cannot retrieve keywords"
      }
    },

    /**
     * Returns a list of keyword ids of selected keywords. Used when creating a card.
     */
    findKeywordIds() {
      const keywordIds = [];
      if (this.allKeywords != null && this.selectedKeywordsName.length !== 0) {
        this.allKeywords.forEach((keyword) => {
          if (this.selectedKeywordsName.includes(keyword.name)) {
            keywordIds.push(keyword.id);
          }
        });
      }
      return keywordIds;
    },

    /**
     * Pipeline triggered when user submits form; validates the data and if successful,
     * sends request to the server.
     */
    create: async function () {
      if (!Object.keys(this.$constants.MARKETPLACE.SECTIONS).includes(this.section)) {
        this.errorMessage = "You must select a section to list your card in";
        return;
      }

      if (this.title.trim().length == 0) {
        this.errorMessage = "You must add a title to your card";
        return;
      }

      const keywordIds = this.findKeywordIds();

      const cardData = {
        creatorId: this.userId,
        section: this.section,
        title: this.title,
        description: this.description.trim(),
        // remove extra newlines etc. at the start and end
        tagIds: keywordIds
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
      } catch (err) {
        this.errorMessage = err.userFacingErrorMessage;
      }
    }
  },

  watch: {
    /**
     * If initial section changes, update it
     */
    initialSection() {
      if (this.initialSection) {
        this.section = this.initialSection;
      }
    }
  }
}
</script>
