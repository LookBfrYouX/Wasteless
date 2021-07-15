<template>
  <v-carousel
    class="my-2"
    v-model=currentImage
    :hide-delimiters="carouselImages.length < 2"
    :show-arrows="carouselImages.length >= 2"
  >
    <v-carousel-item
      v-for="image in carouselImages"
      :key="image.id"
    >
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
</template>
<script>
const defaultImage = require("@/../assets/images/default-product-thumbnail.svg");

export default {
  name: "ImageCarousel",
  props: {
    /**
     * Array of image objects.
     * `filename` and `thumbnailFilename` contain full path to the image,
     * `id` has unique ID
     */
    images: {
      type: Array,
      default: () => [],
      required: true
    }
  },

  data() {
    return {
      currentImage: null
    }
  },

  computed: {
    /**
     * Adds the default image if there are no images 
     */ 
    carouselImages() {
      if (this.images.length) return this.images;
      return [{
        filename: defaultImage,
        id: 1
      }];
    }
  }
}
</script>
<style>
/* Don't know why but this rule isn't in Vuetify already */
.v-window__next {
  right: 0;
}
</style>