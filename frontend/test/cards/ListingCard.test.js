import {shallowMount} from "@vue/test-utils";
import ListingItemCard from "@/components/cards/ListingCard";
import {globalStateMocks} from "#/testHelper";

const businessName = 'Mighty McDonalds';
const city = 'Christchurch';
const listingName = 'Sanitarium So Good Oat Milk No Added Sugar';
const closesDate = '2021-06-16T21:16:26+12:00';

const listing = {
  "id": 5004,
  "inventoryItem": {
    "id": 5003,
    "business": {
      "id": 1001,
      "primaryAdministratorId": 5004,
      "name": businessName,
      "description": "A Good business",
      "address": {
        "streetNumber": "79",
        "streetName": "Place Road",
        "suburb": "Ilam",
        "postcode": "8041",
        "city": city,
        "region": "Canterbury",
        "country": "New Zealand"
      },
      "businessType": "Retail Trade",
      "created": "2020-07-14T14:32:00+12:00"
    },
    "product": {
      "id": 5005,
      "name": listingName,
      "description": "Made in Australia from at least 97% Australian ingredients.",
      "manufacturer": "Ozi ozi ozi",
      "recommendedRetailPrice": 3.3,
      "created": "2020-07-14T14:32:00+12:00",
      "primaryProductImage": null,
      "images": []
    },
    "quantity": 15,
    "pricePerItem": 3,
    "totalPrice": 20,
    "expires": "2021-08-16",
    "manufactured": "2021-08-13",
    "sellBy": "2021-08-15",
    "bestBefore": "2021-08-16"
  },
  "quantity": 15,
  "price": 45,
  "moreInfo": null,
  "created": "2021-05-16T21:16:17+12:00",
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
