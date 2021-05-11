<template>
  <div>
    <sorted-paginated-item-list
      v-bind:items="listings"
      v-bind:sortOptions="sortOptions"
      v-bind:currentSortOption.sync="currentSortOption"
      v-slot="slotProps"
    >
      {{JSON.stringify(slotProps.item)}}

      <!--<business-listing v-bind:listing="slotProps.item"/> -->
    </sorted-paginated-item-list>
    <error-modal
      title="Error viewing listings"
      v-bind:goBack="false"
      v-bind:hideCallback="() => apiErrorMessage = null"
      v-bind:refresh="true"
      v-bind:retry="this.getListingsPipeline"
      v-bind:show="apiErrorMessage !== null"
    >
      <p>{{ apiErrorMessage }}</p>
    </error-modal>
  </div>
</template>
<script>
import SortedPaginatedItemList from '../components/SortedPaginatedItemList.vue';
import ErrorModal from "../components/Errors/ErrorModal";
import { Api } from "../Api";

// const sampleData = [
//   {
//     "id": 57,
//     "inventoryItem": {
//       "id": 101,
//       "product": {
//         "id": "WATT-420-BEANS",
//         "name": "Watties Baked Beans - 420g can",
//         "description": "Baked Beans as they should be.",
//         "manufacturer": "Heinz Wattie's Limited",
//         "recommendedRetailPrice": 2.2,
//         "created": "2021-05-10T23:39:27.425Z",
//         "images": [
//           {
//             "id": 1234,
//             "filename": "/media/images/23987192387509-123908794328.png",
//             "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
//           }
//         ]
//       },
//       "quantity": 4,
//       "pricePerItem": 6.5,
//       "totalPrice": 21.99,
//       "manufactured": "2021-05-10",
//       "sellBy": "2021-05-10",
//       "bestBefore": "2021-05-10",
//       "expires": "2021-05-10"
//     },
//     "quantity": 3,
//     "price": 17.99,
//     "moreInfo": "Seller may be willing to consider near offers",
//     "created": "2021-07-14T11:44:00Z",
//     "closes": "2021-07-21T23:59:00Z"
//   }
// ];

const sampleData = [{
  id: 10,
  name: "AAA"
}, {
  id: 15,
  name: "BBBB"
}, {
  id: 3,
  name: "CCCC"
}, {
  id: 17,
  name: "ZZZ"
}];


const sortOptions = [{
  name: "id",
  sortMethod: (a, b) => {
    return a.id - b.id;
  }
}, {
  name: "By name",
  sortMethod: (a, b) => a.name > b.name? 1: -1
}
];

export default {
  components: {
    SortedPaginatedItemList,
    ErrorModal
  },

  props: {
    businessId: {
      type: Number,
      required: true
    }
  },

  data() {
    return {
      listings: sampleData,
      apiErrorMessage: null,
      sortOptions,
      currentSortOption: { ...sortOptions[0], reversed: false}
    };
  },

  beforeMount: async function() {
    // TODO ENABLE
    // await this.getListingsPipeline();
  },
  
  methods: {
    getListingsPipeline: async function() {
      try {
        this.listings = (await Api.getBusinessListings(this.businessId)).data;
      } catch(err) {
        if (await Api.handle401.call(this, err)) return false;
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    }
  }
}
</script>
