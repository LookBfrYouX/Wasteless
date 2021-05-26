import CreateMarketplaceCard from "../../../views/marketplace/CardCreate";
import { shallowMount } from "@vue/test-utils";

import { constants } from "../../../constants";

jest.mock("@/Api");
import { Api } from "../../../Api";
import {ApiRequestError} from "@/ApiRequestError";
import { globalStateMocks } from "@/test/testHelper";

let wrapper;
afterEach(() => wrapper.destroy());

const mountCard = (initialSection, userId = 3, title = "TITLE") => {
  wrapper = shallowMount(CreateMarketplaceCard, {
    propsData: {
      userId,
      initialSection
    },
    mocks: globalStateMocks()
  });

  wrapper.vm.title = title;
  return wrapper;
}

describe("Card section handling", () => {
  test("bad initial section name", async () => {
    Api.createCard.mockClear();
    Api.createCard.mockResolvedValue({ cardId: 4 });
    mountCard("BAD SECTION");
    await wrapper.vm.create();
    expect(Api.createCard.mock.calls.length).toBe(0);
    expect(wrapper.vm.errorMessage).toEqual(expect.stringContaining("section"));
  });

  test("no initial section name", async () => {
    Api.createCard.mockClear();
    Api.createCard.mockResolvedValue({ cardId: 4 });
    mountCard();
    await wrapper.vm.create();
    expect(Api.createCard.mock.calls.length).toBe(1);
    expect(wrapper.vm.errorMessage).toBeFalsy();
  });

  test("valid initial section name", async () => {
    Api.createCard.mockClear();
    Api.createCard.mockResolvedValue({ cardId: 4 });
    const sectionName = Object.keys(constants.MARKETPLACE.SECTIONS)[1];
    mountCard(sectionName);
    await wrapper.vm.create();
    expect(Api.createCard.mock.calls.length).toBe(1);
    expect(Api.createCard.mock.calls[0][0].section).toBe(sectionName);
    expect(wrapper.vm.errorMessage).toBeFalsy();
  });
});


describe("API error handling", () => {
  test("API error", async () => {
    Api.createCard.mockClear();
    Api.createCard.mockImplementation(() => Promise.reject(new ApiRequestError("MESSAGE")));
    mountCard();
    await wrapper.vm.create();
    expect(wrapper.vm.errorMessage).toEqual("MESSAGE");
  });


  test("API succeeds", async () => {
    Api.createCard.mockClear();
    Api.createCard.mockResolvedValue({ cardId: 5 });
    mountCard();
    wrapper.vm.$router.push.mockClear();
    await wrapper.vm.create();
    expect(wrapper.vm.errorMessage).toBeNull();
    expect(wrapper.vm.$router.push.mock.calls.length).toBe(1);
  });
});

describe("Test tag suggestions", () => {
  test("No suggestions", async () => {
    mountCard();
    wrapper.setData({
      allTags: []
    });

    await wrapper.vm.$nextTick();
    const suggestions = wrapper.vm.tagSuggestions;
    expect(suggestions.length).toBe(1);
    expect(suggestions[0].disabled).toBeTruthy();
  });


  test("Suggestions are at least somewhat sensible", async () => {
    mountCard();
    const tags = ["Cat", "Dog", "No code is good code", "Hydrofluoric Acid", "日常"].map((tag, i) => ({ name: tag, id: i }));
    wrapper.setData({
      allTags: tags,
      tagInputValue: "acid"
    });

    await wrapper.vm.$nextTick();
    const suggestions = wrapper.vm.tagSuggestions;
    expect(suggestions.length).toBe(1);
    expect(suggestions[0].toString()).toBe("Hydrofluoric Acid");
  });
});
