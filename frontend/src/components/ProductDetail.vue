<template>
  <div class="container">
    <div class="list-group-item card">
      <div class="d-flex flex-wrap justify-content-between">
        <h4 class="card-title mb-0">{{ name }} (Id: {{ productId }})</h4>
      </div>
      <div class="text-muted">Description: {{ description }}</div>
      <div class="text-muted">RRP: {{ recommendedRetailPrice }}</div>
      <div class="text-muted">Created: {{ created }}</div>

      <div class="row">
        <div v-if="!edittingImage"
             class="image-list-wrapper">
          <div v-for="image in productImages"
               v-bind:key="image.id"
               class="col-12 col-sm-6 col-md-4 col-lg-3 p-2">
            <img v-bind:src="getImagePath(image.filename)"
                 class="img-fluid"
                 alt="Product Image">
          </div>
          <button class="btn btn-primary"
                  v-on:click="editProductImages(productId)">
            Edit product images
          </button>
        </div>
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
    <error-modal
        v-bind:title="imageApiErrorTitle"
        v-bind:hideCallback="() => imageApiErrorMessage = null"
        v-bind:refresh="true"
        v-bind:retry="false"
        v-bind:show="imageApiErrorMessage !== null"
    >
      <p>{{ imageApiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<script>
import ErrorModal from "./Errors/ErrorModal.vue";
import EditProductImages from "@/components/EditProductImages";
import {ApiRequestError} from "@/ApiRequestError";
const Api = require("./../Api").default;
const BASE_PRODUCT_IMAGE_PATH = "/user-content/images/products/";

export default {
  name: "productDetail",
  components: {
    ErrorModal,
    EditProductImages
  },

  data() {
    return {
      name: "",
      description: "",
      recommendedRetailPrice: "",
      created: "",
      productImages: [],
      edittingImage: false,
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

  methods: {

    apiPipeline: function() {
      return this.parseApiResponse(this.callApi());
    },

    callApi: async function() {
      const response = await Api.getProducts(this.$stateStore.getters.getActingAs().id);
      return response;
    },

    parseApiResponse: async function (apiCall) {
      try {
        const products = (await apiCall).data;
        const product = products.find(({id}) => id === this.productId);
        if (product === undefined) {
          return new ApiRequestError(`Couldn't find product with the ID ${this.productId}. Check if you are logged into the correct business`);
        }
        console.log(this.productId);
        this.name = product.name;
        this.description = product.description;
        this.recommendedRetailPrice = product.recommendedRetailPrice;
        this.created = product.created;
        this.productImages = product.images;
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    getImagePath(filename) {
      return BASE_PRODUCT_IMAGE_PATH + filename;
    },

    editProductImages(productId) {
      this.$router.push({
        name: "editProductImages",
        params: {
          productId
        }
      });
    }
  },
}

</script>