<template>
  <div class="container mt-4">
    <div class="list-group-item card">
      <div class="d-flex flex-wrap justify-content-between mb-2">
        <h2 class="card-title mb-0">{{ name }} (Id: {{ productId }})</h2>
      </div>
      <button class="btn btn-white-bg-primary d-flex align-items-end" type="button"
              v-on:click="$router.go(-1)">
        <span class="material-icons mr-1">arrow_back</span>
        Back
      </button>
      <div class="mt-2">Description: {{ description }}</div>
      <div class="mt-2">RRP: {{ recommendedRetailPrice }}</div>
      <div class="mt-2">Created: {{ $helper.isoToDateString(created) }}</div>
      <h4 class="mt-2">Product Images</h4>
      <div class="row my-2">
        <div v-for="image in productImages"
             v-bind:key="image.id"
             class="col-12 col-md-6 col-lg-4 p-2">
          <img v-bind:src="image.filename"
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
        v-bind:goBack="false"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
    <error-modal
        v-bind:title="imageApiErrorTitle"
        v-bind:hideCallback="() => imageApiErrorMessage = null"
        v-bind:refresh="true"
        v-bind:retry="false"
        v-bind:show="imageApiErrorMessage !== null"
        v-bind:goBack="false"
    >
      <p>{{ imageApiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<script>
import ErrorModal from "./Errors/ErrorModal.vue";
import {ApiRequestError} from "../ApiRequestError";
const { Api } = require("./../Api");

export default {
  name: "productDetail",
  components: {
    ErrorModal,
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

  computed: {
    /**
     * Get a business object of current acting as entity.
     */
    actingAs() {
      return this.$stateStore.getters.getActingAs();
    },

    /**
     * Get business id if current acting as a business
     */
    businessId() {
      if (this.actingAs === null) {
        return null;
      }
      return this.actingAs.id;
    }
  },

  methods: {

    /**
     * Calls the API and updates the component's data with the result
     */
    apiPipeline: async function() {
      try{
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
    callApi: async function() {
      if (this.businessId === null) {
        throw new ApiRequestError("You must be acting as a business to view the product.")
      }
      return await Api.getProducts(this.businessId);
    },

    /**
     * Parses the API response given a promise to the request.
     */
    parseApiResponse: async function (apiCall) {
      const products = (await apiCall).data;
      const product = products.find(({id}) => id === this.productId);
      if (product === undefined) {
        throw new ApiRequestError(`Couldn't find product with the ID ${this.productId}. Check if you are logged into the correct business`);
      }
      this.name = product.name;
      this.description = product.description;
      this.recommendedRetailPrice = product.recommendedRetailPrice;
      this.created = product.created;
      this.productImages = product.images;
    },

    /**
     * Go to product image editing page.
     * @param productId is an id of product currently viewing
     */
    editProductImages(productId) {
      this.$router.push({
        name: "editProductImages",
        params: {
          productId
        }
      });
    }
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