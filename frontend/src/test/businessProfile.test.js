import {shallowMount} from "@vue/test-utils";
import BusinessProfile from "../views/business/Detail";
import {globalStateMocks} from "./testHelper";

jest.useFakeTimers();

let wrapper;

window.alert = jest.fn();

beforeEach(() => {
  wrapper = shallowMount(BusinessProfile, {
    propsData: {
      businessId: 20
    },
    mocks: {
      callApi: jest.fn(() => Promise.resolve({
        name: "returned business name",
        description: "returned business description",
        businessType: "returned business type",
        address: {
          streetNumber: "returned business streetNumber",
          streetName: "returned business streetName",
          postcode: "returned business postcode",
          city: "returned business city",
          region: "returned business region",
          country: "returned business country",
        },
      })),
      ...globalStateMocks()
    },
    stubs: ["router-link"]
  });
});

afterEach(() => wrapper.destroy());

describe("API response", () => {
  test("values for business information being set", async () => {
    const data = {
      name: "Bargains",
      description: "This is a lovely description for Bargain's",
      businessType: "Retail Trade",
      address: "10 Downing Street, Covent Garden, United Kingdom"
    }
    await wrapper.vm.parseApiResponse(Promise.resolve({data}));
    expect(wrapper.vm.businessInfo).toEqual(data);
  });
  test("values for business information being set", async () => {
    const data = {
      name: "",
      description: "This is a lovely description for Bargain's",
      businessType: "Retail Trade",
      address: "10 Downing Street, Covent Garden, United Kingdom"
    }
    await wrapper.vm.parseApiResponse(Promise.reject({data}));
    expect(wrapper.vm.businessInfo).not.toEqual(data);
  });
});
