import CreateCard from "../../views/CreateCard";
import { shallowMount } from "@vue/test-utils";

import { constants } from "./../../constants";

jest.mock("../../Api");
import { Api } from "../../Api";
import { ApiRequestError } from "../../ApiRequestError";
import { globalStateMocks } from "../testHelper";

let wrapper;
afterEach(() => wrapper.destroy());

const mountCard = (initialSection, userId = 3, title = "TITLE") => {
  wrapper = shallowMount(CreateCard, {
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
    expect(wrapper.vm.errorMessage).stringContaining("section");
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
  }) 
});