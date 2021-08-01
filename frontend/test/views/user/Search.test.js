import {mount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";

import {Api} from "@/Api";
import Vuetify from 'vuetify'
import {ApiRequestError} from "@/ApiRequestError";
import Search from "@/views/user/Search";

let vuetify = new Vuetify();

jest.mock("@/Api");

window.scrollTo = jest.fn(); // Scrolls to top of screen when prev/next page button clicked

let wrapper;

let response = {
  results: [
    {
      id: 5003,
      firstName: "Alec",
      lastName: "Fox",
      middleName: "",
      nickname: "amf133",
      bio: "This is Alecs bio",
      homeAddress: {
        city: "Christchurch",
        region: "Canterbury",
        country: "New Zealand"
      },
      created: "2020-07-14T14:32:00+12:00",
      role: "ROLE_ADMIN",
      businessesAdministered: []
    },
    {
      id: 5001,
      firstName: "Fletcher",
      lastName: "Dick",
      middleName: "James",
      nickname: "fdi19",
      bio: "Hello! I am Fletcher and I am currently studying Software Engineering.",
      homeAddress: {
        city: "Westminster",
        region: "London",
        country: "United Kingdom"
      },
      created: "2020-07-14T14:32:00+12:00",
      role: "ROLE_USER",
      businessesAdministered: []
    },
    {
      id: 5005,
      firstName: "Haruka",
      lastName: "Ichinose",
      middleName: "",
      nickname: "hic21",
      bio: "This is a fantastic bio",
      homeAddress: {
        city: "Christchurch",
        region: "Canterbury",
        country: "New Zealand"
      },
      created: "2020-07-14T14:32:00+12:00",
      role: "ROLE_ADMIN",
      businessesAdministered: []
    },
  ],
  totalCount: 3
}

beforeEach(() => {
  wrapper = mount(Search, {
    vuetify,
    mocks: globalStateMocks(),
    stubs: ["error-modal", "router-link"], // Add the name of the business listings item component to here
    propsData: {
      search: "a",
    }
  });
  Api.search.mockResolvedValue({
    data: response
  });
});

afterEach(() => wrapper.destroy());

describe("Ensure values are set and changed", () => {
  /**
   * Tests the component is set up correctly.
   */
  test("Check data values are set", async () => {
    await wrapper.vm.pageUpdate();
    expect(wrapper.vm.$data.page).toEqual(1);
    expect(wrapper.vm.$data.totalResults).toEqual(3);
    expect(wrapper.vm.$data.searchParams).toBeDefined();
    expect(wrapper.vm.$data.searchParams.sortBy).toEqual("firstName");
  });

  test("Calling sortUpdate changes sort", async () => {
    await wrapper.vm.sortUpdate("lastName", false);
    expect(wrapper.vm.$data.searchParams.sortBy).toEqual("lastName")
    expect(wrapper.vm.$data.searchParams.isAscending).toBeFalsy();
  });
});

describe("Product API handling", () => {
  /**
   * Tests that the products get set correctly.
   */
  test("Assert products get set from API", async () => {
    await wrapper.vm.query();
    expect(wrapper.vm.$data.userResults).toEqual(response.results);

  });

  /**
   * Tests for the correct error when no data is returned.
   */
  test("API returns error", async () => {
    const message = "It's a Mario!";
    Api.search.mockImplementation(
        () => Promise.reject(new ApiRequestError(message)));
    await wrapper.vm.query();
    expect(wrapper.vm.apiErrorMessage).toEqual(message);
  });
});