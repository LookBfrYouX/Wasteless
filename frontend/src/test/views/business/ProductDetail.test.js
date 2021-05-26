import {shallowMount} from "@vue/test-utils";
import {globalStateMocks } from "@/test/testHelper";
jest.mock("@/Api");
const {Api} = require("../../../Api.js");

import ProductDetail from "../../../views/business/ProductDetail";


let wrapper;

const mockProduct = () => {
  return ({
    productId: 1,
    name: 'Earl Grey',
    description: '100 tea bags',
    manufacturer: 'TWININGS',
    recommendedRetailPrice: 5.01,
    created: '2021-04-20T01:25:50.333Z',
    images: [
      {
        filename: "/user-content/images/products/example_1.svg"
      },
      {
        filename: "/user-content/images/products/example_2.svg"
      }
    ]
  });
}

Api.getProducts.mockResolvedValue(mockProduct());
window.alert = jest.fn();

afterEach(() => wrapper.destroy());

describe("Parsing API response to get product images", () => {

  test("Acting as a business", async () => {

    const mocks = globalStateMocks();
    wrapper = shallowMount(ProductDetail, {
      propsData: {
        productId: 1,
        businessId: 1
      },
      mocks
    });
    const getProducts = jest.fn(() => Promise.resolve());
    Api.getProducts.mockImplementation(getProducts);
    await wrapper.vm.callApi();
    expect(getProducts.mock.calls.length).toBe(1);
  });
});
