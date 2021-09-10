<template>
  <router-link
    :to="{ name: 'BusinessListingDetail',
      params: {
        businessId: item.inventoryItem.business.id,
        listingId: item.id,
      },
    }"
    class="text-decoration-none text-reset"
  >
    <v-card class="w-100 hover-scale-effect p-relative">
      <div class="allergy-info-container p-2" v-if="item.inventoryItem.product">
        <v-tooltip
          top
          v-for="{ key, shortText, longText, chipClass, backgroundColor, foregroundColor }
                 in allergyChipConfig.filter(({ key }) => item.inventoryItem.product[key])"
          :key="key"
        >
          <template v-slot:activator="{ on, attrs }">
            <v-chip
              small
              v-on="on"
              v-bind="attrs"
              class="mr-2"
              :class="chipClass"
              :color="backgroundColor"
              :text-color="foregroundColor"
            >
              <span>
                {{ shortText }}
              </span>
            </v-chip>
          </template>
          {{ longText }}
        </v-tooltip>
      </div>

      <img
          v-if="item.inventoryItem.product.images.length"
          :src="item.inventoryItem.product.images[0].thumbnailFilename"
          alt="Product Image"
          class="image-fluid w-100 m-0"
      />
      <img
          v-else
          alt="Product Image"
          class="image-fluid w-100 m-0"
          src="@/../assets/images/default-product-thumbnail.svg"
      />
      <v-card-text class="pt-2 pb-0">
        <div class="row">
          <div class="col-6 text-truncate">
            {{ item.inventoryItem.business.name }}
          </div>
          <v-tooltip top>
            <template v-slot:activator="{ on }">
              <div class="col-6">
                <div v-on="on" class="text-end text-truncate">
                  {{ item.inventoryItem.business.address.city }}
                </div>
              </div>
            </template>
            <span>{{ $helper.addressToString(item.inventoryItem.business.address, true) }}</span>
          </v-tooltip>
        </div>
      </v-card-text>
      <v-card-title class="py-0">
        {{ item.inventoryItem.product.name }}
      </v-card-title>
      <v-card-text>
        <div class="row">
          <div class="col-5">
            {{ $helper.makeCurrencyString(item.price, currency, false) }}
            <small> for </small>
            {{ item.quantity }}
          </div>
          <div class="col-7 text-end">
            Closes
            {{ $helper.isoToDateString(item.closes) }}
          </div>
        </div>
      </v-card-text>
    </v-card>
  </router-link>
</template>

<script>
export default {
  name: "ListingItemCard",
  data() {
    return {
      currency: null
    };
  },
  props: {
    item: Object
  },

  beforeMount: function () {
    this.getCurrency();
  },

  methods: {
    /**
     * Gets currency object.
     * @returns {Promise<void>} Currency object, null when the currency doesn't exist or API request error.
     */
    getCurrency: async function () {
      this.currency = this.$helper.getCurrencyForBusinessByCountry(
        this.item.inventoryItem.business.address.country
      );
    },
  },

  computed: {
    /**
     * Information for chip color, text, classes etc.
     */
    allergyChipConfig() {
      return [
        {
          key: "isDairyFree",
          shortText: "DF",
          longText: "Dairy Free",
          chipClass: null,
          backgroundColor: "#ffdd50",
          foregroundColor: "black",
        }, {
          key: "isGlutenFree",
          shortText: "GF",
          longText: "Gluten Free",
          chipClass: null,
          backgroundColor: "#93641c",
          foregroundColor: "white",
        }, {
          key: "isVegetarian",
          shortText: "V",
          longText: "Vegetarian",
          chipClass: null,
          backgroundColor: "#40826d",
          foregroundColor: "white",
        }, {
          key: "isVegan",
          shortText: "VE",
          longText: "Vegan",
          chipClass: null,
          backgroundColor: "#74B74E",
          foregroundColor: "white",
        }, {
          key: "isPalmOilFree",
          shortText: "PO",
          longText: "Palm Oil Free",
          // custom CSS for palm oil free chip
          // if foreground color not black, should be changed in the CSS
          // as well for the inline SVG
          chipClass: "palm-oil-free-chip",
          backgroundColor: "#ffc5c5",
          foregroundColor: "black",
        }
      ];
    },
  },
};
</script>

<style lang="scss" scoped>
@import "~/src/styles/grid-breakpoints.scss";

.v-card {
  height: 500px;
  overflow: hidden;
}

@media (max-width: map-get($grid-breakpoints, "md")) {
  .v-card {
    height: inherit;
  }
}

.listing-container img {
  max-height: 30vh;
}

.allergy-info-container {
  position: absolute;
}

.palm-oil-free-chip span {
  position: relative;
  line-height: 1.1;
}

.palm-oil-free-chip span::after {
  // https://stackoverflow.com/a/28974253/5204356
  background: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' version='1.1' preserveAspectRatio='none' viewBox='0 0 100 100'><line x1='0' y1='100' x2='100' y2='15' style='stroke: black;stroke-width: 5; stroke-linecap: round'/></svg>");
  background-repeat: no-repeat;
  background-position: center center;
  background-size: 100% 100%, auto;
  position: absolute;
  left: -0.1em;
  bottom: 0;
  right: -0.1em;
  top: 0;
  content: "";
}
</style>