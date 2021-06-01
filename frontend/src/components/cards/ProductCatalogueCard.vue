<template>
  <div>
    <div class="row">
      <div class="col-3 pr-0 mb-2">
        <img
            v-if="thumbnailPathIfExists != null"
            :src="thumbnailPathIfExists"
            alt="Product Image"
            class="image-fluid w-100 rounded-circle"
        >
        <!-- using v-else instead of settings the src because I have a feeling that Vue needs the image src to be static for it copy it as a static asset when we build the frontend. -->
        <img
            v-else
            alt="Product Image"
            class="image-fluid w-100 rounded-circle"
            src="@/../assets/images/default-product-thumbnail.svg"
        >
      </div>
      <div class="col-9">
        <div class="d-flex flex-wrap justify-content-between">
          <h4 class="card-title mb-0">
            {{ product.name }} (ID:
            <code class="text-dark">{{ product.id }}</code>)
          </h4>
        </div>
      </div>
    </div>
    <div class="text-muted">Manufacturer: {{ product.manufacturer }}</div>
    <div class="text-muted">Description: {{ product.description }}</div>
    <div class="text-muted">RRP: {{
        $helper.makeCurrencyString(product.recommendedRetailPrice, currency)
      }}
    </div>
    <div class="text-muted">Created: {{
        $helper.isoToDateString(product.created)
      }}
    </div>
  </div>
</template>
<script>
export default {
  props: {
    product: {
      required: true,
      type: Object
    },
    currency: {
      type: Object
    }
  },

  computed: {
    /**
     * Retreive product primary image URL to show as a thumbnail if it exists
     * @return null if no thumbnail image exists, or the URL otherwise
     */
    thumbnailPathIfExists() {
      if (this.product.images && this.product.images[0]) {
        return this.product.images[0].thumbnailFilename;
      }
      return null;
    },

  }
}
</script>
