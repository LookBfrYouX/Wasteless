import BusinessInventory from "../../views/BusinessInventory";
import {shallowMount} from "@vue/test-utils";
import {Api} from "../../Api";
import {ApiRequestError} from "../../ApiRequestError";

// The possible error messages that can the API can respond with
const errorMessages = {
  403: "You don't have permission view this businesses inventory",
  406: "The business does not exist - either the URL was typed in wrong or the business was deleted"
};

const testData = [
  {
    id: 1,
    product: {
      name: "Watties Baked Beans - 200g can"
    }
  },
  {
    id: 2,
    product: {
      name: "Watties Baked Beans - 400g can"
    }
  }
]

// Mock the API response
Api.getBusinessInventory = jest.fn();

// Mount the component to test
const wrapper = shallowMount(BusinessInventory,
    {
      propsData: {
        businessId: 1
      }
    });

describe("Test getting Inventory from the API", () => {
  test("Test successful handling of API response", async () => {
    Api.getBusinessInventory.mockReturnValue({data: testData});
    await wrapper.vm.getInventory();
    expect(wrapper.vm.$data.listings).toEqual(testData);
  });

  test("Test 403 error API response", async () => {
    const mockError = {
      response: {
        status: 403
      }
    };
    Api.getBusinessInventory.mockImplementation(() => {
      throw ApiRequestError.createFromMessageMap(mockError, errorMessages)
    });
    await wrapper.vm.getInventory();
    expect(wrapper.vm.$data.apiErrorMessage).toEqual(
        errorMessages[mockError.response.status]);
  });

  test("Test 406 error API response", async () => {
    const mockError = {
      response: {
        status: 406
      }
    };
    Api.getBusinessInventory.mockImplementation(() => {
      throw ApiRequestError.createFromMessageMap(mockError, errorMessages)
    });
    await wrapper.vm.getInventory();
    expect(wrapper.vm.$data.apiErrorMessage).toEqual(
        errorMessages[mockError.response.status]);
  });
});