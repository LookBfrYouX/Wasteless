<template>
  <v-carousel
    class="my-2 image-carousel-container"
    v-model=currentImage
    :hide-delimiters="carouselImages.length < 2"
    :show-arrows="carouselImages.length >= 2"
    :key="carouselKey"
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
// Pretty sure I can't use import for this since its an image
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
      currentImage: null,
      carouselKey: 0
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
  },

  watch: {
    /**
     * https://github.com/vuetifyjs/vuetify/issues/12949
     * Order seems to be partially dependent on ID - from the issue, this
     * occurs after the list of elements is updated, so use key to create a new 
     * instance of carousel each time the image list is updated
     * 
     * To replicate the issue, upload a few images and pick the last image as the
     * primary image (and remove this method)
     */
    images() {
      this.carouselKey = (this.carouselKey + 1) % 2;
    }
  }
}
</script>
<style>
/* On Safari on macOS, I had to apply this rule manually, don't know why but this rule isn't in Vuetify already */
.image-carousel-container .v-window__next {
  right: 0;
}
</style>