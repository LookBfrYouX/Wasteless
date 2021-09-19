import {mount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";
import ProductDetail from "@/views/business/ProductDetail";
import Vuetify from "vuetify";
import {ApiRequestError} from "@/ApiRequestError";

jest.mock("@/Api");
const {Api} = require("@/Api.js");
const vuetify = new Vuetify();

let wrapper;
const response = { data: {
  results: [
    {
      id: 1,
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
      ],
      nutriScore: "A",
      novaGroup: 2,
      fat: "HIGH",
      saturatedFat: null,
      sugars: null,
      salt: null,
      isGlutenFree: null,
      isDairyFree: null,
      isVegetarian: null,
      isVegan: null,
      isPalmOilFree: null
    }
  ],
  totalCount: 1
}}


Api.getProducts.mockResolvedValue(response);
window.alert = jest.fn();

afterEach(() => wrapper.destroy());

describe("Component mounts correctly", () => {
  test("Component mounts", async () => {
    wrapper = mount(ProductDetail, {
      vuetify,
      propsData: {
        businessId: 1,
        productId: 1
      },
      stubs: ["error-modal", "router-link"],
      mocks: {
        ...globalStateMocks()
      },
    });
    await wrapper.vm.callApi();
    expect(wrapper.vm.$data.product.nutriScore).toBeDefined();
    expect(wrapper.vm.$data.product.nutriScore).toEqual(response.data.results[0].nutriScore);
    expect(wrapper.vm.$data.product.novaGroup).toBeDefined();
    expect(wrapper.vm.$data.product.novaGroup).toEqual(response.data.results[0].novaGroup);
  });
});

describe("API Pipeline", () => {
  test("parseApiRequest fails on invalid product id", async () => {
    try {
      wrapper = mount(ProductDetail, {
        vuetify,
        propsData: {
          businessId: 1,
          productId: 2
        },
        stubs: ["error-modal", "router-link"],
        mocks: {
          ...globalStateMocks()
        },
      });
      await wrapper.vm.parseApiResponse(await wrapper.vm.callApi());
    } catch (err) {
      expect(err).toBeDefined();
      expect(err.message).toEqual("API Request Error  - Couldn't find product with the ID 2. Check if you are signed into the correct business")
    }
  });

  test("callApi fails on null product id", async () => {
    try {
      wrapper = { destroy: () => {}}; // This just defines a function with a destroy method that does nothing.
      await ProductDetail.methods.callApi.call({ businessId: null });
    } catch (err) {
      expect(err).toBeDefined();
      expect(err.message).toEqual("API Request Error  - You must be acting as a business to view the product.")
    }
  });
});

describe("Parsing API response to get product images", () => {

  test("Acting as a business", async () => {

    const mocks = globalStateMocks();
    wrapper = mount(ProductDetail, {
      vuetify,
      propsData: {
        productId: 1,
        businessId: 1
      },
      mocks,
      stubs: ["router-link"]
    });
    const getProducts = jest.fn(() => Promise.resolve());
    Api.getProducts.mockImplementation(getProducts);
    await wrapper.vm.callApi();
    expect(getProducts.mock.calls.length).toBe(1);
  });
});
