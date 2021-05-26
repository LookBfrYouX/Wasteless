import InventoryItemEntry from "../../../views/business/InventoryCreate";
import { shallowMount } from "@vue/test-utils";
import {ApiRequestError} from "../../../ApiRequestError";

jest.mock("./../Api");
const {Api} = require("../../../Api");

let wrapper;

jest.useFakeTimers();

beforeEach(async () => {
  wrapper = await shallowMount(InventoryItemEntry, {
    propsData: {
      businessId: 1
    },
    stubs: ['error-modal']
  });
});

const products = [];
for(let i = 0; i < 100; i++) {
  products.push({
    id: i,
    name: Math.random().toString()
  });
}
Api.getProducts.mockResolvedValue({ data: products });

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

test("Products list gets populated", async () => {
  await wrapper.vm.$nextTick();
  const optionEls = wrapper.find("#productDropdown").findAll("option");
  expect(optionEls.length).toBe(products.length);

  for(let i = 0; i < optionEls.length; i++) {
    const optionEl = optionEls.at(i);
    expect(optionEl.text()).toEqual(products[i].name);
  }
});

test("Error modal shows", async () => {
  Api.getProducts.mockImplementation( () => Promise.reject(new ApiRequestError("Anything")));
  await wrapper.vm.populateDropdown();
  expect(wrapper.vm.apiErrorMessage).not.toBeNull()
})

test("Error message shows", async () => {
  Api.addItemToInventory.mockImplementation( () => Promise.reject(new ApiRequestError("Anything")));
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
