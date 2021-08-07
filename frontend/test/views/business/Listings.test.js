import {shallowMount} from "@vue/test-utils";
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
      "inventoryItem": {
        "id": 1,
        "name": "TestName",
        "address": {}
      },
      "product": {},
      "quantity": 20
    },
    {
      "inventoryItem": {
        "id": 2,
        "name": "TestName",
        "address": {}
      },
      "product": {},
      "quantity": 10
    }
  ],
  "totalCount": 2
};

jest.mock("@/Api");
window.scrollTo = jest.fn()

let wrapper;
beforeEach(() => {
  wrapper = shallowMount(Listings, {
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
    expect(wrapper.vm.$data.totalResults).toEqual(listings.totalCount);
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