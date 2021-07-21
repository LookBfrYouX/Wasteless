import InventoryItemEntry from "@/views/business/InventoryCreate";
import {shallowMount} from "@vue/test-utils";
import {ApiRequestError} from "@/ApiRequestError";

jest.mock("@/Api");
const {Api} = require("@/Api");

let wrapper;

jest.useFakeTimers();

beforeEach(async () => {
  wrapper = await shallowMount(InventoryItemEntry, {
    propsData: {
      businessId: 1
    },
    stubs: ['error-modal', 'v-autocomplete']
  });
});

const products = [];
for (let i = 0; i < 100; i++) {
  products.push({
    id: i,
    name: Math.random().toString()
  });
}
Api.getProducts.mockResolvedValue({data: products});

describe("Correct today date", () => {
  test("Jan 1", () => {
    wrapper.vm.setDateInputs(new Date(2020, 0, 1));
    expect(wrapper.vm.todayDate).toBe("2020-01-01");
  });

  test("Dec 31", () => {
    wrapper.vm.setDateInputs(new Date(2020, 11, 31));
    expect(wrapper.vm.todayDate).toBe("2020-12-31");
  });
});

test("Error message shows", async () => {
  Api.addItemToInventory.mockImplementation(
      () => Promise.reject(new ApiRequestError("Anything")));
  await wrapper.setData({
    product: {
      id: 214
    },
  })
  await wrapper.vm.addItem();

  const errorComponent = wrapper.find('.alert');
  expect(errorComponent.exists()).toBe(true);
  expect(wrapper.vm.errorMessage).not.toBeNull();
})

afterEach(() => wrapper.destroy());
