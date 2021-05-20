<template>
  <div class="w-100">
    <sorted-paginated-item-list
      v-bind:items="listings"
      v-bind:sortOptions="sortOptions"
      v-bind:currentSortOption.sync="currentSortOption"
    >
      <template v-slot:title>
        <h2>Listings for {{businessName? businessName: "business"}}</h2>
      </template>
      <template v-slot:item="slotProps">
        <div class="hover-white-bg hover-scale-effect slightly-transparent-white-background my-1 p-3 rounded">
        {{JSON.stringify(slotProps.item)}}
        </div>
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

import { helper } from "../helper";

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

const sortOptions = [
  {
    name: "ID",
    sortMethod: helper.sensibleSorter("id")
  }, {
    name: "Name TODO delete. Only this and ID work with the dummy data",
    sortMethod: helper.sensibleSorter("name") 
  }, {
    name: "Price",
    sortMethod: helper.sensibleSorter("price") 
  }, {
    name: "RRP",
    sortMethod: helper.sensibleSorter(el => el.inventoryItem.recommendedRetailPrice)
  }, {
    name: "Name",
    sortMethod: helper.sensibleSorter(el => el.inventoryItem.product.name)
  }, {
    name: "Listing Created",
    // Yes, you can sort dates as a string in this format
    // Add a reversed param to sorter? Should the 'natural' sort for created/closes be oldest first? 
    sortMethod: helper.sensibleSorter("created")
  }, {
    name: "Listing Closes",
    sortMethod: helper.sensibleSorter("closes")
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
      sortOptions: sortOptions,
      currentSortOption: { ...sortOptions[0], reversed: false},
      businessName: null
    };
  },

  beforeMount: async function() {
    return Promise.allSettled([this.updateBusinessName(), this.getListingsPipeline()]);
  },
  
  methods: {
    getListingsPipeline: async function() {
      try {
        this.listings = (await Api.getBusinessListings(this.businessId)).data;
      } catch(err) {
        if (await Api.handle401.call(this, err)) return false;
        this.apiErrorMessage = err.userFacingErrorMessage;
      }
    },

    updateBusinessName: async function() {
      this.businessName = await this.$helper.tryGetBusinessName(this.businessId);
    }
  },

  watch: {
    businessName() {
      if (this.businessName != null) document.title = `Listings for ${this.businessName}`
    }
  }
}
</script>
