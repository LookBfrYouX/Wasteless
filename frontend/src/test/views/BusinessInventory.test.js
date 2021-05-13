import BusinessInventory from "../../views/BusinessInventory";
import {shallowMount} from "@vue/test-utils";
import {Api} from "../../Api";

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

// Mock the API response and set its return value to the test data
Api.getBusinessInventory = jest.fn();
Api.getBusinessInventory.mockReturnValue({data: testData});

// Mount the component to test
const wrapper = shallowMount(BusinessInventory,
    {
      propsData: {
        businessId: 1
      }
});

describe("Test getting Inventory from the API", () => {
  test("Test successful handling of API response", async () => {
    await wrapper.vm.getInventory();
    expect(wrapper.vm.$data.listings).toEqual(testData);
  });
});