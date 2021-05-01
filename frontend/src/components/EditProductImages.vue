<template>
  <div class="container">
    <div class="row">
      <div class="col-12">
        <label>Product Images</label>
      </div>
      <div
          v-for="(img, i) in images"
          v-bind:key="img.id"
          class="col-12 col-sm-6 col-md-4 col-lg-3 p-2"
      >
        <div class="d-flex justify-content-center">
          <img v-bind:src="img.thumbnailFilename" class="img-fluid" />
        </div>
        <div class="d-flex flex-wrap justify-content-center">
          <button v-if="i !== 0" class="btn btn-sm btn-primary m-1">
            Set as primary
          </button>
          <button class="btn btn-sm btn-danger m-1">Delete</button>
        </div>
      </div>
      <div
          class="col-12 col-sm-6 col-md-4 col-lg-3 d-flex align-items-center justify-content-center product-image-width"
      >
        <button
            type="button"
            class="btn"
            @click="onPickFile"
        >
          <span class="material-icons add-product-icon py-4 mb-4 mt-2">
            add_circle_outline
          </span>
        </button>
        <input
            ref="fileInput"
            accept="image/*"
            class="d-none"
            type="file"
            @change="onFilePicked"/>

      </div>
    </div>
    <error-modal
        title="Error fetching product information"
        v-bind:hideCallback="() => apiErrorMessage = null"
        v-bind:refresh="true"
        v-bind:retry="this.apiPipeline"
        v-bind:show="apiErrorMessage !== null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<style scoped>

.add-product-icon {
  font-size: 48px;
  color: #999;
}

.add-product-icon:hover {
  color: #000;
}

</style>
<script>
import ErrorModal from './Errors/ErrorModal.vue';
import {ApiRequestError} from "./../ApiRequestError";
const Api = require("./../Api").default;
export default {
  name: 'editProductImages',
  components: {ErrorModal},

  data() {
    return {
      name: "",
      images: [],
      apiErrorMessage: null,

    }
  },
  props: {
    productId: {
      required: true,
      type: Number,
    }
  },

  beforeMount: function () {
    this.apiPipeline();
  },

  computed: {
    actingAs() {
      return this.$stateStore.getters.getActingAs();
    }
  },

  methods: {
    /**
     * Calls the API and updates the component's data with the result
     */
    apiPipeline: function () {
      this.parseApiResponse(this.callApi(this.productId));
    },

    /**
     * Calls the API to get profile information with the given user ID
     * Returns the promise, not the response
     */
    callApi: function (productId) {
      console.log(ApiRequestError);
      console.log(productId);
      return Promise.resolve({
        name: "bla",
        images: [
          {
            id: 1,
            filename: "",
            thumbnailFilename: "https://via.placeholder.com/200x200"
          },{
            id: 2,
            filename: "",
            thumbnailFilename: "https://via.placeholder.com/200x200"
          },{
            id: 3,
            filename: "",
            thumbnailFilename: "https://via.placeholder.com/200x200"
          },{
            id: 4,
            filename: "",
            thumbnailFilename: "https://via.placeholder.com/200x200"
          }
        ]}
      );
    },

    /**
     * Parses the API response given a promise to the request
     */
    parseApiResponse: async function (apiCall) {
      try {
        const product = await apiCall;
        console.log(product);
        this.name = product.name;
        this.images = product.images;


        // const business = this.$stateStore.getters.getActingAs();

        // this.userInfo = response.data;

        // this.userInfo.imageURL = process.env.VUE_APP_SERVER_ADD + `/users/${this.userId}/images/`;
      } catch (err) {
        console.log(err);
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    onPickFile() {
      this.$refs.fileInput.click()
    },

    onFilePicked(event) {
      const files = event.target.files

      Api.uploadProductImage(files[0], this.actingAs.id, this.productId)
      .then(() => {
        this.apiPipeline();
      }).catch(err => {
        Api.handle401.call(this, err);
        this.apiErrorMessage = err.userFacingErrorMessage;
      })
    },



  }
}
</script>
