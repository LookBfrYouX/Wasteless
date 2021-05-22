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
        <div class="col-12 d-flex flex-wrap flex-column">
          <label class="form-label h4">Section</label>
          <span class="d-block text-center form-text">
            The section your card will be listed in
          </span>
          <div class="btn-group">
            <button
              v-for="([sectionKey, sectionName]) in Object.entries($constants.MARKETPLACE.CATEGORIES)"
              v-bind:key="sectionKey"
              type="button"
              class="btn btn-primary"
              v-bind:class="{active: section == sectionName}"
              v-on:click="section = sectionName"
            >
              {{ sectionName }}
            </button>
          </div>
        </div>
        <div class="col-12 form-group">
          <label class="form-label" for="title">Title</label>
          <input
            type="text"
            name="title"
            id="title"
            class="form-control"
            v-model="title"
            placeholder="Title"
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
    </form>
  </div>
</template>
<script>
export default {
  props: {
    userId: {
      required: true,
      type: Number
    }
  },

  data() {
    return {
      section: "",
      title: "",
      description: ""
    }
  },

  methods: {
    createCard: async function() {
      // const cardData = {
      //   creatorId: this.userId,
      //   section: this.section,
      //   title: this.title,
      //   description: this.description
      // };

      try {
        const { data } = await Promise.resolve({ cardId: 40 });
        this.$router.push({
          name: "card",
          params: {
            cardId: data.cardId
          }
        });
      } catch(err) {
        this.errorMessage = err.userFacingErrorMessage;
      }
    }
  }
}
</script>
<style>

</style>