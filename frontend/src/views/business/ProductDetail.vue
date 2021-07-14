<template>
  <div class="container my-4">
    <div class="list-group-item card">
      <div class="row">
        <div class="col-12">
          <div class="d-flex flex-wrap justify-content-between mb-2">
            <h2 class="card-title mb-0">
              {{ name }} (ID:
              <code class="text-dark">{{ productId }}</code>)
            </h2>
          </div>
          <button class="btn btn-white-bg-primary d-flex align-items-end" type="button"
                  @click="$router.go(-1)">
            <span class="material-icons mr-1">arrow_back</span>
            Back
          </button>
        <v-carousel
          class="my-2"
          v-model=currentImage
        >
          <v-carousel-item
            v-for="(image) in (productImages.length? productImages: [{
              filename: require('@/../assets/images/default-product-thumbnail.svg') 
            }])"
            :key="image.id"
          >
          <!-- If there are no images, add the default image -->
          <div class="h-100 d-flex">
            <v-img
              :src="image.filename"
              :lazy-src="image.thumbnailFilename"
              contain
            >
            <!-- Lazy src loads the thumbnail first, then the full image later
            Not sure it we should keep it - the thumbnail is square so it is a bit
            jarring when it switches to the full image
             -->
            </v-img>
          </div>
          </v-carousel-item>
        </v-carousel>
          <div class="mt-2">{{ description }}</div>
          <div class="mt-2">Created: {{ $helper.isoToDateString(created) }}</div>
          <div class="mt-2">RRP: {{
              $helper.makeCurrencyString(recommendedRetailPrice, currency)
            }}
          </div>
        </div>
      </div>
      <div class="d-flex justify-content-end">
        <button class="btn btn-primary mr-0"
                @click="editProductImages(productId)">
          Edit product images
        </button>
      </div>
    </div>
    <error-modal
        :goBack="false"
        :hideCallback="() => apiErrorMessage = null"
        :refresh="true"
        :retry="this.apiPipeline"
        :show="apiErrorMessage !== null"
        title="Error fetching product information"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>

<style>
.primary-image-wrapper img {
  width: 100%;
  border: #1ec996 solid 2px;
}
</style>
<script>
import ErrorModal from "@/components/ErrorModal.vue";

import {ApiRequestError} from "@/ApiRequestError";
import {Api} from "@/Api";

export default {
  name: "productDetail",
  components: {
    ErrorModal,
  },

  data() {
    return {
      name: "",
      description: "",
      recommendedRetailPrice: null,
      created: "",
      productImages: [],
      currentImage: null,
      apiErrorMessage: null,
      currency: null
    }
  },
  props: {
    businessId: {
      required: true,
      type: Number
    },
    productId: {
      required: true,
      type: Number,
    }
  },

  beforeMount: async function () {
    const success = await this.apiPipeline();
    if (success) {
      await this.loadCurrencies();
    }
  },

  methods: {

    /**
     * Loads currency info
     * @return true on success
     */
    loadCurrencies: async function () {
      if (!this.$stateStore.getters.canEditBusiness(this.businessId)) {
        return false;
      }

      try {
        this.currency = await this.$helper.getCurrencyForBusiness(this.businessId,
            this.$stateStore);
      } catch (err) {
        // If can't get currency not that big of a deal
        if (await Api.handle401.call(this, err)) {
          return;
        }
        return false;
      }
      return true;
    },

    /**
     * Calls the API and updates the component's data with the result
     * Does not run pipeline if user should not be able to edit business
     */
    apiPipeline: async function () {
      if (!this.$stateStore.getters.canEditBusiness(this.businessId)) {
        return false;
      }
      try {
        await this.parseApiResponse(this.callApi());
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.apiErrorMessage = err.userFacingErrorMessage;
        return false;
      }

      return true;
    },

    /**
     * Calls the API to get profile information with the given user ID
     * Returns the promise, not the response
     */
    callApi: async function () {
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
        throw new ApiRequestError(
            `Couldn't find product with the ID ${this.productId}. Check if you are signed into the correct business`);
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
        name: "BusinessProductImagesEdit",
        params: {
          businessId: this.businessId,
          productId
        }
      });
    }
  }
}

</script>