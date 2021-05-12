<template>
  <div class="w-100">
    <sorted-paginated-item-list
      v-bind:items="listings"
      v-bind:sortOptions="sortOptions"
      v-bind:currentSortOption.sync="currentSortOption"
    >
      <template v-slot:title>
        <h2>Inventory for Business</h2>
      </template>
      <template v-slot:item="slotProps">
        {{ slotProps.item.product.name }}
      </template>
<!--      <template v-slot:right-button>-->
<!--        <button type="button" class="btn btn-info" v-on:click="() => showSortSidebar = true">Another button</button>-->
<!--      </template>-->
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

const mockResult = [];
for(let i = 1; i < 12; i++) {
  mockResult.push({
    "id": i,
    "product": {
      "name": "Watties Baked Beans - "+ i * 100 + "g can",
    }
  });
}

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
      listings: mockResult,
      apiErrorMessage: null,
      sortOptions,
      currentSortOption: { ...sortOptions[0], reversed: false}
    };
  },

  beforeMount: async function() {
    // TODO Uncomment once backend endpoint is created
    this.listings = mockResult;
    // await this.getListingsPipeline();
  },
  
  methods: {
    getListingsPipeline: async function() {
      try {
        this.listings = (await Api.getBusinessInventory(this.businessId)).data;
      } catch(err) {
        if (await Api.handle401.call(this, err)) return false;
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    }
  }
}
</script>
