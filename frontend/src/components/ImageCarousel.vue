<template>
  <v-carousel
      :key="carouselKey"
      v-model="currentImage"
      :hide-delimiters="carouselImages.length < 2"
      :show-arrows="carouselImages.length >= 2"
      class="my-2 image-carousel-container"
  >
    <v-carousel-item
        v-for="image in carouselImages"
        :key="image.id"
    >
      <div class="h-100 d-flex">
        <v-img
            :src="image.filename"
            contain
        >
          <!-- lazy-src can be bound to thumbnailFilename,
          but thumbnails are square so it is quite jarring when it switches from
          the thumbnail to the full image. Hence, it is not being used
            -->
        </v-img>
      </div>
    </v-carousel-item>
  </v-carousel>
</template>
<script>
// Importing image allows Vue to copy the file to /public on build
import defaultImage from "@/../assets/images/default-product-thumbnail.svg";

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
      if (this.images.length) {
        return this.images;
      }
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