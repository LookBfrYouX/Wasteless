import MarketplaceCardContainer from "@/components/MarketplaceSection";
import {mount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";
import {ApiRequestError} from "@/ApiRequestError";

import {Api} from "@/Api";
import Vue from 'vue'
import Vuetify from 'vuetify'

Vue.use(Vuetify);
let vuetify = new Vuetify();

jest.mock("@/Api");

let wrapper;

let response = {
  results: [
    {
      id: 1,
      name: "Product 1",
      description: "Description 1",
      manufacturer: "Manufacturer 1",
      recommendedRetailPrice: 4.67,
      created: "2020-07-14T14:32:00+12:00",
      primaryProductImage: null,
      images: []
    },
    {
      id: 2,
      name: "Product 2",
      description: "Description 2",
      manufacturer: "Manufacturer 2",
      recommendedRetailPrice: 3.0,
      created: "2020-07-14T14:32:00+12:00",
      primaryProductImage: null,
      images: []
    },
    {
      id: 3,
      name: "Product 3",
      description: "Description 3",
      manufacturer: "Manufacturer 3",
      recommendedRetailPrice: 3.3,
      created: "2020-07-14T14:32:00+12:00",
      primaryProductImage: null,
      images: []
    }
  ],
  totalCount: 3
}

beforeEach(() => {
  wrapper = mount(MarketplaceCardContainer, {
    vuetify,
    mocks: globalStateMocks(),
    stubs: ["error-modal", "marketplace-card"],
    propsData: {
      section: "",
    }
  });
});

afterEach(() => wrapper.destroy());

describe("Card API handling", () => {
  /**
   * Tests that the cards get set correctly.
   */
  test("Assert cards get set from API", async () => {
    window.scrollTo = jest.fn();
    Api.getMarketplaceCards.mockResolvedValue({
      data: response
    });
    await wrapper.vm.getCardsFromAPI();
    expect(wrapper.vm.$data.cards).toEqual(response.results);

  });

  /**
   * Tests for the correct error when no data is returned.
   */
  test("API returns error", async () => {
    const message = "It's a Mario!";
    Api.getMarketplaceCards.mockImplementation(
        () => Promise.reject(new ApiRequestError(message)));
    await wrapper.vm.getCardsFromAPI("");
    expect(wrapper.vm.apiErrorMessage).toEqual(message);
  });
});
