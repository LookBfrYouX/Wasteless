import BusinessListings from "@/views/business/Listings";
import {mount} from "@vue/test-utils";
import {Api} from "@/Api";
import {globalStateMocks} from "#/testHelper";
import SortedPaginatedItemList from "@/components/SortedPaginatedItemList";
import Vue from 'vue'
import Vuetify from 'vuetify'
import {ApiRequestError} from "@/ApiRequestError";

Vue.use(Vuetify);
let vuetify = new Vuetify();

const listings = {
  results: [{id: 20, inventoryItem: {product: {images: []}}},
    {id: 40, inventoryItem: {product: {images: []}}}],
  totalCount: 2
}

jest.mock("@/Api");
window.scrollTo = jest.fn()

let wrapper;
beforeEach(() => {
  wrapper = mount(BusinessListings, {
    vuetify,
    mocks: globalStateMocks(),
    stubs: ["error-modal", "router-link"], // Add the name of the business listings item component to here
    propsData: {
      businessId: 1
    }
  });
});

afterEach(() => wrapper.destroy());

describe("API handling", () => {
  test("Items actually get set", async () => {
    Api.getBusinessListings.mockResolvedValue({
      data: listings
    });
    await wrapper.vm.getListingsPipeline();
    expect(
        wrapper.vm.$data.listings).toEqual(
        listings.results);
  });

  test("API returns error", async () => {
    const message = "It's a Mario!";
    Api.getBusinessListings.mockImplementation(
        () => Promise.reject(new ApiRequestError(message)));
    await wrapper.vm.getListingsPipeline();
    expect(wrapper.vm.apiErrorMessage).toEqual(message);
  });
});