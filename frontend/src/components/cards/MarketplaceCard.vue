<template>
  <!-- this component produces the marketplace card component from the given props -->
  <div class="container card item-card pt-1 pl-3 pr-3 pb-3 my-2">
    <div class="d-sm-flex align-items-center">
      <a v-on:click="$router.push({name: 'profile', params: {userId: $props.card.creator.id}})"
      href="javascript:void(0)">
        <img alt="User is acting as self"
             class="nav-picture rounded-circle border"
             src="./../../../assets/images/default-user-thumbnail.svg"
        >
        {{ $props.card.creator.firstName }} {{ $props.card.creator.lastName }}
      </a>
      <div class="ml-auto d-flex flex-sm-column text-muted">
        <span class="mr-3 mr-sm-0">
          Created {{ $helper.isoToDateString($props.card.created) }}
        </span>
        <span>
          Expires {{ $helper.isoToDateString($props.card.displayPeriodEnd) }}
        </span>
      </div>
    </div>
    <div class="text-muted">
      {{
        [$props.card.creator.homeAddress.suburb, $props.card.creator.homeAddress.city].join(', ')
      }}
    </div>
    <div class="mt-3 rounded border p-2">
      <h2>{{ $props.card.title }}</h2>
      {{ $props.card.description }}
      <div class="mt-2 d-flex flex-wrap">
        <div v-for="keyword in $props.card.keywords" v-bind:key="keyword.id">
          <Tag :xButton="false" class="mr-2 mb-2">
            {{ keyword.name }}
          </Tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Tag from '../Tag';

export default {
  name: "MarketplaceCard",
  components: {
    Tag
  },
  props: {
    // The card information to be displayed. (See API spec for more details)
    card: Object
  },
  data() {
    return {};
  }
}
</script>

<style scoped>

a {
  color: black;
}

</style>