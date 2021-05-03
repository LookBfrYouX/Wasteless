<template>
  <div class="container mt-4">
    <div class="list-group-item card">
      <div class="d-flex flex-wrap justify-content-between mb-2">
        <h2 class="card-title mb-0">{{ name }} (Id: {{ productId }})</h2>
      </div>
      <button class="btn btn-white-bg-primary d-flex align-items-end" type="button"
              v-on:click="$router.go(-1)">
        <span class="material-icons mr-1">arrow_back</span>
        Back to Product Catalogue
      </button>
      <div class="mt-2">Description: {{ description }}</div>
      <div class="mt-2">RRP: {{ recommendedRetailPrice }}</div>
      <div class="mt-2">Created: {{ created }}</div>

      <div class="row my-2">
        <div v-for="image in productImages"
             v-bind:key="image.id"
             class="col-12 col-md-6 col-lg-4 p-2">
          <img v-bind:src="getImagePath(image.filename)"
               class="img-fluid"
               alt="Product Image">
        </div>
      </div>
      <div class="d-flex justify-content-end">
        <button class="btn btn-primary mr-0"
                v-on:click="editProductImages(productId)">
          Edit product images
        </button>
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