<template>
  <div class="w-100">
    <sorted-paginated-item-list
      v-bind:items="listings"
      v-bind:sortOptions="sortOptions"
      v-bind:currentSortOption.sync="currentSortOption"
    >
      <template v-slot:title>
        <h2>Listings for business</h2>
      </template>
      <template v-slot:item="slotProps">
        {{JSON.stringify(slotProps.item)}}
        <!--<business-listing v-bind:listing="slotProps.item"/> -->
      </template>
      <template v-slot:right-button>
        <button type="button" class="btn btn-info" v-on:click="() => showSortSidebar = true">Another button</button>
      </template>
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
//       "sellBy": "2021-05-10",p
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
  id: 9,
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

for(let i = 0; i < 10; i++) {
  sampleData.push({
    id: i * 2 + 4,
    name: btoa(Math.random().toString())
  });
}

/**
 * Gets property from an object given key or lambda
 * @param {Object} obj object to extract property from
 * @param {Function|String} key Key is either a key to an object or a lambda which takes in an object and returns a key
 */
const getProp = (obj, key) => {
  if (key instanceof Function) return key(obj);
  return obj[key];
}

/**
 * Method which returns sort method for a string or numeric property of an object
 * @param{Function|String} key object key to sort by, or lambda which extracts key from an object
 */
const sensibleSorter = (key) => {
  return (a, b) => {
    a = getProp(a, key);
    b = getProp(b, key);
    // For number can use a - b, but this works with both strings and numbers
    if (a === b) return 0;
    return a > b? 1: -1;
  }
}

const sortOptions = [
  {
    name: "ID",
    sortMethod: sensibleSorter("id")
  }, {
    name: "Name TODO delete. Only this and ID work with the dummy data",
    sortMethod: sensibleSorter("name") 
  }, {
    name: "Price",
    sortMethod: sensibleSorter("price") 
  }, {
    name: "RRP",
    sortMethod: sensibleSorter(el => el.inventoryItem.recommendedRetailPrice)
  }, {
    name: "Name",
    sortMethod: sensibleSorter(el => el.inventoryItem.product.name)
  }, {
    name: "Listing Created",
    // Yes, you can sort dates as a string in this format
    // Add a reversed param to sorter? Should the 'natural' sort for created/closes be oldest first? 
    sortMethod: sensibleSorter("created")
  }, {
    name: "Listing Closes",
    sortMethod: sensibleSorter("closes")
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
