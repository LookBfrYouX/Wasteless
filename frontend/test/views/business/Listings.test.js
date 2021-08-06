import {mount} from "@vue/test-utils";
import {Api} from "@/Api";
import {globalStateMocks} from "#/testHelper";
import Vue from 'vue'
import Vuetify from 'vuetify'
import {ApiRequestError} from "@/ApiRequestError";
import Listings from "@/views/business/Listings";

Vue.use(Vuetify);
let vuetify = new Vuetify();

const listings = {
  "results": [
    {
      "id": 5004,
      "inventoryItem": {
        "id": 5003,
        "business": {
          "id": 1001,
          "primaryAdministratorId": 5004,
          "name": "TestName",
          "description": "A Good business",
          "address": {
            "streetNumber": "79",
            "streetName": "Place Road",
            "suburb": "Ilam",
            "postcode": "8041",
            "city": "Christchurch",
            "region": "Canterbury",
            "country": "New Zealand"
          },
          "businessType": "Retail Trade",
          "created": "2020-07-14T14:32:00+12:00"
        },
        "product": {
          "id": 5005,
          "name": "Sanitarium So Good Oat Milk No Added Sugar",
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
      "closes": "2021-06-16T21:16:26+12:00"
    },
    {
      "id": 5003,
      "inventoryItem": {
        "id": 5002,
        "business": {
          "id": 1001,
          "primaryAdministratorId": 5004,
          "name": "TestName",
          "description": "A Good business",
          "address": {
            "streetNumber": "79",
            "streetName": "Place Road",
            "suburb": "Ilam",
            "postcode": "8041",
            "city": "Christchurch",
            "region": "Canterbury",
            "country": "New Zealand"
          },
          "businessType": "Retail Trade",
          "created": "2020-07-14T14:32:00+12:00"
        },
        "product": {
          "id": 5003,
          "name": "Sanitarium So Good Almond Milk Unsweetened Long Life",
          "description": "So good almond milk unsweetened from sanitarium new zealand is a delicious plant-based milk made from almonds. So good almond unsweetened can be enjoyed by the glass, on cereal, or in your favourite recipe. It’s a good source of calcium and a source of vitamins e, b2 and b12. It's low in fat and is also 100% lactose, gluten and dairy free. Plus it contains no added sugar!.",
          "manufacturer": "Ozi ozi ozi",
          "recommendedRetailPrice": 3,
          "created": "2020-07-14T14:32:00+12:00",
          "primaryProductImage": null,
          "images": []
        },
        "quantity": 10,
        "pricePerItem": 4.62,
        "totalPrice": 20,
        "expires": "2021-08-16",
        "manufactured": "2021-08-13",
        "sellBy": "2021-08-15",
        "bestBefore": "2021-08-16"
      },
      "quantity": 9,
      "price": 45,
      "moreInfo": null,
      "created": "2021-05-16T21:16:17+12:00",
      "closes": "2021-06-16T21:16:26+12:00"
    },
    {
      "id": 5002,
      "inventoryItem": {
        "id": 5001,
        "business": {
          "id": 1001,
          "primaryAdministratorId": 5004,
          "name": "TestName",
          "description": "A Good business",
          "address": {
            "streetNumber": "79",
            "streetName": "Place Road",
            "suburb": "Ilam",
            "postcode": "8041",
            "city": "Christchurch",
            "region": "Canterbury",
            "country": "New Zealand"
          },
          "businessType": "Retail Trade",
          "created": "2020-07-14T14:32:00+12:00"
        },
        "product": {
          "id": 5001,
          "name": "Anchor Milk Standard Blue Top",
          "description": "Essential goodness for your family. Anchor blue top is the milk that new zealanders grow up on. Its brimming with important nutrients, and it delivers more energy than most types of milk. Enjoy a glassful of great nz tradition. Number shown on pack is a guide only and not indicative of what will be supplied.",
          "manufacturer": "North Korean Milk GmbH",
          "recommendedRetailPrice": 4.67,
          "created": "2020-07-14T14:32:00+12:00",
          "primaryProductImage": null,
          "images": [
            {
              "id": 1,
              "filename": "/user-content/images/products/I02b16d42-21a7-4bdb-b50c-7cc2e8cafba4.png",
              "thumbnailFilename": "/user-content/images/products/I02b16d42-21a7-4bdb-b50c-7cc2e8cafba4_thumbnail.png"
            }
          ]
        },
        "quantity": 20,
        "pricePerItem": 4.67,
        "totalPrice": 20,
        "expires": "2021-08-16",
        "manufactured": "2021-08-13",
        "sellBy": "2021-08-15",
        "bestBefore": "2021-08-16"
      },
      "quantity": 3,
      "price": 12,
      "moreInfo": null,
      "created": "2021-05-16T21:16:17+12:00",
      "closes": "2021-06-16T21:16:26+12:00"
    },
    {
      "id": 5001,
      "inventoryItem": {
        "id": 5001,
        "business": {
          "id": 1001,
          "primaryAdministratorId": 5004,
          "name": "TestName",
          "description": "A Good business",
          "address": {
            "streetNumber": "79",
            "streetName": "Place Road",
            "suburb": "Ilam",
            "postcode": "8041",
            "city": "Christchurch",
            "region": "Canterbury",
            "country": "New Zealand"
          },
          "businessType": "Retail Trade",
          "created": "2020-07-14T14:32:00+12:00"
        },
        "product": {
          "id": 5001,
          "name": "Anchor Milk Standard Blue Top",
          "description": "Essential goodness for your family. Anchor blue top is the milk that new zealanders grow up on. Its brimming with important nutrients, and it delivers more energy than most types of milk. Enjoy a glassful of great nz tradition. Number shown on pack is a guide only and not indicative of what will be supplied.",
          "manufacturer": "North Korean Milk GmbH",
          "recommendedRetailPrice": 4.67,
          "created": "2020-07-14T14:32:00+12:00",
          "primaryProductImage": null,
          "images": [
            {
              "id": 1,
              "filename": "/user-content/images/products/I02b16d42-21a7-4bdb-b50c-7cc2e8cafba4.png",
              "thumbnailFilename": "/user-content/images/products/I02b16d42-21a7-4bdb-b50c-7cc2e8cafba4_thumbnail.png"
            }
          ]
        },
        "quantity": 20,
        "pricePerItem": 4.67,
        "totalPrice": 20,
        "expires": "2021-08-16",
        "manufactured": "2021-08-13",
        "sellBy": "2021-08-15",
        "bestBefore": "2021-08-16"
      },
      "quantity": 2,
      "price": 9,
      "moreInfo": "fletcher was here RAWR XD",
      "created": "2021-05-16T21:16:17+12:00",
      "closes": "2021-06-16T21:16:26+12:00"
    }
  ],
  "totalCount": 4
};

jest.mock("@/Api");
window.scrollTo = jest.fn()

let wrapper;
beforeEach(() => {
  wrapper = mount(Listings, {
    vuetify,
    mocks: globalStateMocks(),
    stubs: ["error-modal", "router-link"]
  });
  Api.getListings.mockResolvedValue(listings);
});

afterEach(() => wrapper.destroy());

describe("API Handling", () => {
  test("Listings are set", async () => {
    await wrapper.vm.getListingsPipeline();
    expect(wrapper.vm.$data.listings).toEqual(listings.results);
  });

  test("API Handles Error", async () => {
    const message = "Error Message!";
    Api.getListings.mockImplementation(
        () => Promise.reject(new ApiRequestError(message)));
    await wrapper.vm.getListingsPipeline();
    expect(wrapper.vm.apiErrorMessage).toEqual(message);
  });
});

describe("Pagination methods correctly set values", () => {
  test("Check data values are set", async () => {
    await wrapper.vm.pageUpdate();
    expect(wrapper.vm.$data.page).toEqual(1);
    expect(wrapper.vm.$data.totalResults).toEqual(4);
    expect(wrapper.vm.$data.searchParams).toBeDefined();
    expect(wrapper.vm.$data.searchParams.sortBy).toEqual("name");
  });

  test("Calling sortUpdate changes sort", async () => {
    await wrapper.vm.sortUpdate("quantity", false);
    expect(wrapper.vm.$data.searchParams.sortBy).toEqual(
        "quantity")
    expect(wrapper.vm.$data.searchParams.isAscending).toBeFalsy();
  });
});