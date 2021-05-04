import {shallowMount} from "@vue/test-utils";
import {globalStateMocks} from "./testHelper";
import ProductDetail from "../components/ProductDetail";
import {ApiRequestError} from "../ApiRequestError";


jest.mock("./../Api.js");
const { Api } = require("./../Api.js");

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

window.alert = jest.fn();

afterEach(() => wrapper.destroy());

describe("Retrieving business id", () => {
  test("Not acting as business", () => {
    const mocks = globalStateMocks();
    mocks.$stateStore.actions.setActingAs(null);
    wrapper = shallowMount(ProductDetail, {
      propsData: {
        productId: 1,
      },
      mocks,
    });
    expect(wrapper.vm.businessId).toEqual(null);
  });

  test("Acting as business", () => {
    const mocks = globalStateMocks();
    mocks.$stateStore.actions.setActingAs(1);
    wrapper = shallowMount(ProductDetail, {
      propsData: {
        productId: 1,
      },
      mocks,
    });
    expect(wrapper.vm.businessId).toEqual(1);
  });
});

describe("Parsing API response to get product images", () => {

  test("Acting as a business", async () => {
    const mocks = globalStateMocks();
    wrapper = shallowMount(ProductDetail, {
      businessId: 1,
      propsData: {
        productId: 1,
      },
      mocks
    });
    const getProducts = jest.fn(() => Promise.resolve());
    Api._setMethod("getProducts", getProducts);
    await wrapper.vm.callApi();
    expect(getProducts.mock.calls.length).toBe(1);
  });
});



