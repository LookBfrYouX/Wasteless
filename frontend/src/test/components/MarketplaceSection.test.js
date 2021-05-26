import MarketplaceCardContainer from "../../components/MarketplaceSection";
import { shallowMount } from "@vue/test-utils";

jest.mock("./../Api");
import { Api } from "../../Api";
import { globalStateMocks } from "../testHelper";
import {ApiRequestError} from "../../ApiRequestError";

let wrapper;
beforeEach(() => {
  wrapper = shallowMount(MarketplaceCardContainer, {
    mocks: globalStateMocks(),
    stubs: ["error-modal", "marketplace-card"],
    propsData: {
      section: "",
    }
  });
});

afterEach(() => wrapper.destroy());

describe("API handling", () => {

  test("API returns error", async () => {
    const message = "It's a Mario!";
    Api.getMarketplaceCards.mockImplementation(() => Promise.reject(new ApiRequestError(message)));
    await wrapper.vm.getCardsFromAPI("");
    expect(wrapper.vm.apiErrorMessage).toEqual(message);
  });
});
