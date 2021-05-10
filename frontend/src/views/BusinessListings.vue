<template>
<div>
  <p>TEST</p>
  <sorted-paginated-item-list
    v-bind:items="listings"
    v-slot="slotProps"
  >
    {{JSON.stringify(slotProps.item)}}
  </sorted-paginated-item-list>
  </div>
</template>

<script>
import SortedPaginatedItemList from '../components/SortedPaginatedItemList.vue';

const sampleData = [
  {
    "id": 57,
    "inventoryItem": {
      "id": 101,
      "product": {
        "id": "WATT-420-BEANS",
        "name": "Watties Baked Beans - 420g can",
        "description": "Baked Beans as they should be.",
        "manufacturer": "Heinz Wattie's Limited",
        "recommendedRetailPrice": 2.2,
        "created": "2021-05-10T23:39:27.425Z",
        "images": [
          {
            "id": 1234,
            "filename": "/media/images/23987192387509-123908794328.png",
            "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
          }
        ]
      },
      "quantity": 4,
      "pricePerItem": 6.5,
      "totalPrice": 21.99,
      "manufactured": "2021-05-10",
      "sellBy": "2021-05-10",
      "bestBefore": "2021-05-10",
      "expires": "2021-05-10"
    },
    "quantity": 3,
    "price": 17.99,
    "moreInfo": "Seller may be willing to consider near offers",
    "created": "2021-07-14T11:44:00Z",
    "closes": "2021-07-21T23:59:00Z"
  },
  {
    "id": 57,
    "inventoryItem": {
      "id": 101,
      "product": {
        "id": "WATT-420-BEANS",
        "name": "Watties Baked Beans - 420g can",
        "description": "Baked Beans as they should be.",
        "manufacturer": "Heinz Wattie's Limited",
        "recommendedRetailPrice": 2.2,
        "created": "2021-05-10T23:39:27.425Z",
        "images": [
          {
            "id": 1234,
            "filename": "/media/images/23987192387509-123908794328.png",
            "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
          }
        ]
      },
      "quantity": 4,
      "pricePerItem": 6.5,
      "totalPrice": 21.99,
      "manufactured": "2021-05-10",
      "sellBy": "2021-05-10",
      "bestBefore": "2021-05-10",
      "expires": "2021-05-10"
    },
    "quantity": 3,
    "price": 17.99,
    "moreInfo": "Seller may be willing to consider near offers",
    "created": "2021-07-14T11:44:00Z",
    "closes": "2021-07-21T23:59:00Z"
  }
];

export default {
  components: {
    SortedPaginatedItemList
  },


  data() {
    return {
      listings: sampleData
    };
  }
}
</script>
