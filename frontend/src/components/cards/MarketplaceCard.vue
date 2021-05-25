<template>
  <!-- this component produces the marketplace card component from the given props -->
  <div class="container card item-card pt-1 pl-3 pr-3 pb-3 my-2">
    <div class="d-sm-flex align-items-center">
      <router-link class="text-decoration-none" v-bind:to="{ name: 'profile', params: {userId: card.creator.id }}">
        <img alt="User is acting as self"
             class="nav-picture rounded-circle border"
             src="./../../../assets/images/default-user-thumbnail.svg"
        >
        {{ card.creator.firstName }} {{ card.creator.lastName }}
      </router-link>
      <div class="ml-auto d-flex flex-sm-column text-muted">
        <span class="mr-3 mr-sm-0">
          Created {{ $helper.isoToDateString(card.created) }}
        </span>
        <span>
          Expires {{ $helper.isoToDateString(card.displayPeriodEnd) }}
        </span>
      </div>
    </div>
    <div class="text-muted">
      {{
        [card.creator.homeAddress.city, card.creator.homeAddress.region].join(', ')
      }}
    </div>
    <div class="mt-2 rounded border p-2">
      <h2>{{ card.title }}</h2>
      <div class="description">
        {{ card.description }}
      </div>
      <div class="mt-2 d-flex flex-wrap">
        <div v-for="keyword in card.keywords" v-bind:key="keyword.id">
          <tag :xButton="false" class="mr-2 mb-2">
            {{ keyword.name }}
          </tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import tag from '../Tag';

export default {
  name: "MarketplaceCard",
  components: {
    tag
  },
  props: {
    // The card information to be displayed. (See API spec for more details)
    card: {
      type: Object,
      required: true
    }

  },
  data() {
    return {};
  }
}
</script>

<style scoped>
.description {
  white-space: pre-wrap;
}
</style>