import {shallowMount} from "@vue/test-utils";
import ListingItemCard from "@/components/cards/ListingCard";
import {globalStateMocks} from "#/testHelper";

const businessName = 'Mighty McDonalds';
const city = 'Christchurch';
const listingName = 'Sanitarium So Good Oat Milk No Added Sugar';
const closesDate = '2021-06-16T21:16:26+12:00';

const listing = {
  "inventoryItem": {
    "business": {
      "name": businessName,
      "address": {
        "city": city,
        "country": "New Zealand"
      }
    },
    "product": {
      "name": listingName,
      "images": []
    }
  },
  "closes": closesDate
};

describe("Test passing props to Listing card component", () => {
  test("Correct title shows", () => {
    let wrapper = shallowMount(ListingItemCard, {
      propsData: {
        item: listing
      },
      mocks: globalStateMocks()
    })
    expect(wrapper.props().item.inventoryItem.product.name).toEqual(
        listingName);
    expect(wrapper.props().item.inventoryItem.business.name).toEqual(
        businessName);
    expect(wrapper.props().item.closes).toEqual(closesDate);
  })

  test("Calculates and displays correct date info", () => {
    let wrapper = shallowMount(ListingItemCard, {
      propsData: {
        item: listing
      },
      mocks: globalStateMocks(),
    });
    expect(wrapper.text()).toContain(
        globalStateMocks().$helper.isoToDateString(closesDate));
  })
});
