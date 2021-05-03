<template>
  <div class="container">
    <div class="row">
      <div class="col-12">
        <h2>Product Images</h2>
        <h5>{{name}}</h5>
      </div>
      <div
          v-for="(img, i) in images"
          v-bind:key="img.id"
          class="col-12 col-sm-6 col-md-4 col-lg-3 p-2"
      >
        <div class="d-flex justify-content-center">
          <img
            v-bind:src="getImagePath(img.filename)"
            class="img-fluid"
          />
          <!-- TODO change to thumbnailFilename -->
        </div>
        <div class="d-flex flex-wrap justify-content-center">
          <button v-if="i !== 0"
                  v-on:click="setAsPrimary(img.id)"
                  class="btn btn-sm btn-primary m-1">
            Set as primary
          </button>
<!--          Primary image is the first image in the array. -->
          <button v-on:click="deleteImage(img.id)"
                  class="btn btn-sm btn-danger m-1">Delete</button>
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
        v-bind:goBack="false"
        v-bind:show="apiErrorMessage !== null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
    <error-modal
        v-bind:title="imageApiErrorTitle"
        v-bind:hideCallback="() => imageApiErrorMessage = null"
        v-bind:refresh="true"
        v-bind:retry="false"
        v-bind:goBack="false"
        v-bind:show="imageApiErrorMessage !== null"
    >
      <p>{{ imageApiErrorMessage }}</p>
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

const BASE_PRODUCT_IMAGE_PATH = "/user-content/images/products/";

export default {
  name: 'editProductImages',
  components: {ErrorModal},

  data() {
    return {
      name: "",
      images: [],
      apiErrorMessage: null,
      imageApiErrorMessage: null,
      imageApiErrorTitle: ""
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
    /**
     * Get a business object of current acting as entity.
     */
    actingAs() {
      return this.$stateStore.getters.getActingAs();
    }
  },

  methods: {
    /**
     * Calls the API and updates the component's data with the result
     */
    apiPipeline: function () {
      return this.parseApiResponse(this.callApi());
    },

    /**
     * Calls the API to get profile information with the given user ID
     * Returns the promise, not the response
     */
    callApi: async function () {
      const response = await Api.getProducts(this.$stateStore.getters.getActingAs().id);
      return response;
    },

    /**
     * Parses the API response given a promise to the request
     */
    parseApiResponse: async function (apiCall) {
      try {
        const products = (await apiCall).data;
        const product = products.find(({id}) => id === this.productId);
        // find the product the correct id
        if (product === undefined) {
          return new ApiRequestError(`Couldn't find product with the ID ${this.productId}. Check if you are logged into the corerct business`);
        }
        this.name = product.name;
        this.images = product.images;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    /**
     * Called when add image icon is clicked.
     */
    onPickFile() {
      this.$refs.fileInput.click()
    },

    /**
     * Uploads image and call Api again to update images.
     * @param event TODO Better comments
     */
    onFilePicked(event) {
      const files = event.target.files;
      Api.uploadProductImage(files[0], this.actingAs.id, this.productId)
      .then(() => {
        return this.apiPipeline();
      }).catch(err => {
        Api.handle401.call(this, err);
        this.imageApiErrorTitle = "Error uploading new product image";
        this.imageApiErrorMessage = err.userFacingErrorMessage;
      });
    },

    /**
     * Sets an clicked image as a primary image and call Api again to update images.
     * @param imageId is an id property of image object.
     */
    setAsPrimary(imageId) {
      Api.changePrimaryImage(this.actingAs.id, this.productId, imageId)
      .then(() => {
        return this.apiPipeline();
      }).catch(err => {
        Api.handle401.call(this, err);
        this.imageApiErrorTitle = "Error setting a primary image";
        this.imageApiErrorMessage = err.userFacingErrorMessage;
      });
    },

    /**
     * Delete an clicked image and call Api again to update images.
     * @param imageId is an id property of image object.
     */
    deleteImage(imageId) {
      Api.deleteProductImage(this.actingAs.id, this.productId, imageId)
      .then(() => {
        return this.apiPipeline();
      }).catch(err => {
        Api.handle401.call(this, err);
        this.imageApiErrorTitle = "Error deleting the image";
        this.imageApiErrorMessage = err.userFacingErrorMessage;
      });
    },

    /**
     * Given filename to the product image, return path
     */
    getImagePath(filename) {
      return BASE_PRODUCT_IMAGE_PATH + filename;
    }
  }
}
</script>
