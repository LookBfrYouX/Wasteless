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
const { Api } = require("./../Api");

export default {
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
      errorMessage: null
    }
  },

  beforeMount() {
    if (this.initialSection) this.section = this.initialSection;
  },

  methods: {
    /**
     * Pipeline triggered when user submits form; validates the data and if successful,
     * sends request to the server.
     */
    create: async function() {
      if (!Object.keys(this.$constants.MARKETPLACE.SECTIONS).includes(this.section)) {
        this.errorMessage = "You must select a category to list your card in";
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
        keywordIds: []
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
<style>

</style>