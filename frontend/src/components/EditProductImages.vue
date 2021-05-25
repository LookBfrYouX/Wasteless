<template>
  <div class="container mt-4">
    <div class="row">
      <div class="col-12">
        <h1>Images for '{{ name }}'</h1>
      </div>
    </div>
    <div class="row">
      <div class="ml-3 d-flex justify-content-end">
        <button class="btn btn-success"
                v-on:click="$router.go(-1)">
          Go Back
        </button>
      </div>
    </div>
    <div class="row">
      <div
          v-for="(img, i) in images"
          v-bind:key="img.id"
          class="col-12 col-sm-6 col-md-4 col-lg-3 p-2 d-flex flex-column justify-content-end"
      >
        <!-- align buttons on the bottom -->
        <div class="d-flex justify-content-center">
          <img
              class="img-fluid w-100"
              v-bind:alt="`Product image ${i + 1}`"
              v-bind:src="img.filename"
          />
        </div>
        <div class="d-flex flex-wrap justify-content-center">
          <button v-if="i !== 0"
                  class="btn btn-sm btn-info m-1"
                  v-on:click="setAsPrimary(img.id)">
            Set as primary
          </button>
          <!--          Primary image is the first image in the array. -->
          <button class="btn btn-sm btn-danger m-1"
                  v-on:click="deleteImage(img.id)">Delete
          </button>
        </div>
      </div>
      <div
          class="col-12 col-sm-6 col-md-4 col-lg-3 d-flex align-items-center justify-content-center product-image-width"
      >
        <button
            class="bg-transparent border-0"
            type="button"
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
        v-bind:goBack="false"
        v-bind:hideCallback="() => {
          apiErrorMessage = null;
        }"
        v-bind:refresh="true"
        v-bind:retry="this.apiPipeline"
        v-bind:show="apiErrorMessage !== null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
    <error-modal
        v-bind:goBack="false"
        v-bind:hideCallback="() => imageApiErrorMessage = null"
        v-bind:refresh="true"
        v-bind:retry="false"
        v-bind:show="imageApiErrorMessage !== null"
        v-bind:title="imageApiErrorTitle"
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

const {Api} = require("./../Api");

export default {
  name: 'editProductImages',
  components: {
    ErrorModal,
  },

  data() {
    return {
      name: "No product found",
      images: [],
      apiErrorMessage: null,
      imageApiErrorMessage: null,
      imageApiErrorTitle: "",
    }
  },
  props: {
    productId: {
      required: true,
      type: Number,
    },
    businessId: {
      required: true,
      type: Number,
    },
  },

  beforeMount: function () {
    this.apiPipeline();
  },

  methods: {
    /**
     * Calls the API and updates the component's data with the result.
     * If user is not (acting as business or is admin acting as self) simply returns
     */
    apiPipeline: async function () {
      if (!this.$stateStore.getters.canEditBusiness(this.businessId)) {
        return false;
      }

      try {
        return await this.parseApiResponse(this.callApi());
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    /**
     * Calls the API to get profile information with the given user ID
     * Returns the promise, not the response
     */
    callApi: async function () {
      if (this.businessId === null) {
        throw new ApiRequestError("You must be acting as a business to edit the product.")
      }
      return await Api.getProducts(this.businessId);
    },

    /**
     * Parses the API response given a promise to the request
     */
    parseApiResponse: async function (apiCall) {
      const products = (await apiCall).data;
      const product = products.find(({id}) => id === this.productId);
      // find the product the correct id
      if (product === undefined) {
        throw new ApiRequestError(
            `Couldn't find product with the ID ${this.productId}. Check if you are logged into the correct business`);
      }
      this.name = product.name;
      this.images = product.images;
    },

    /**
     * Called when add image icon is clicked.
     */
    onPickFile() {
      this.$refs.fileInput.click()
    },

    /**
     * Uploads image and call Api again to update images.
     * @param event is a DOM event when the user picks a file.
     */
    onFilePicked(event) {
      const files = event.target.files;
      Api.uploadProductImage(files[0], this.businessId, this.productId)
      .then(() => {
        return this.apiPipeline();
      }).catch(async (err) => {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.imageApiErrorTitle = "Error uploading new product image";
        this.imageApiErrorMessage = err.userFacingErrorMessage;
      });
    },

    /**
     * Sets an clicked image as a primary image and call Api again to update images.
     * @param imageId is an id property of image object.
     */
    setAsPrimary(imageId) {
      Api.changePrimaryImage(this.businessId, this.productId, imageId)
      .then(() => {
        return this.apiPipeline();
      }).catch(async (err) => {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.imageApiErrorTitle = "Error setting a primary image";
        this.imageApiErrorMessage = err.userFacingErrorMessage;
      });
    },

    /**
     * Delete an clicked image and call Api again to update images.
     * @param imageId is an id property of image object.
     */
    deleteImage(imageId) {
      // If the user double clicks delete the server 500s
      // Remove the image immediately so that they can't do this
      // Regardless of if the request suceeds or fails, refresh the pipeline
      const index = this.images.findIndex(img => img.id == imageId);
      if (index == -1) {
        return;
      }
      this.images.splice(index);

      Api.deleteProductImage(this.businessId, this.productId, imageId)
      .catch(async (err) => {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.imageApiErrorTitle = "Error deleting the image";
        this.imageApiErrorMessage = err.userFacingErrorMessage;
      }).finally(() => {
        return this.apiPipeline();
      })
    },
  },

  watch: {
    /**
     * Watch acting as is switched by clicking navbar dropdown
     */
    businessId() {
      this.$helper.goToProfile.bind(this)();
    }
  }
}
</script>
