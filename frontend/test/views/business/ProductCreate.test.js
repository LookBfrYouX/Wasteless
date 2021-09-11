import {shallowMount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";
import ProductCreate from "@/views/business/ProductCreate";
import Vuetify from 'vuetify';
import {ApiRequestError} from "@/ApiRequestError";

jest.mock("@/Api");
const {Api} = require("@/Api.js");

let vuetify = new Vuetify();

window.alert = jest.fn();

let wrapper;

beforeEach(async () => {
  wrapper = shallowMount(ProductCreate, {
    vuetify,
    propsData: {
      businessId: 1
    },
    stubs: ["error-modal"],
    mocks: {
      ...globalStateMocks()
    },
  });

  wrapper.setData({
    queryParams: {
      name: 'Product Name',
      recommendedRetailPrice: 100
    }
  });

  const mockCurrency = {
    "code": "FAB",
    "name": "Fabulous Fabian",
    "symbol": "F"
  };
  wrapper.vm.$helper.getCurrencyForBusiness = jest.fn().mockResolvedValue(
      mockCurrency);
  await wrapper.vm.currencyPipeline();
  // ApiPipeline called in lifecycle so can't mock calls before the method is called. Hence, need to run it twice
  await wrapper.vm.$nextTick();
});

afterEach(() => wrapper.destroy());

describe("Test currencyText Computed Value", () => {
  test("Test currencyText with valid currency", async () => {
    // Arrange (in Before Each)
    // Action
    const computedValue = wrapper.vm.currencyText;

    // Assert
    expect(wrapper.vm.$helper.getCurrencyForBusiness.mock.calls.length).toBe(1);
    expect(computedValue).toEqual('F (FAB)');
  });

  test("Test currencyText with valid currency", async () => {
    // Arrange
    wrapper.vm.$data.currency = null;

    // Action
    const computedValue = wrapper.vm.currencyText;

    // Assert
    expect(computedValue).toEqual('(Unknown currency)');
  });
});

describe("Test callApi method", () => {
  test("callApi succeeds with businessId", async () => {
    // Arrange
    const expectedResponse = {"productId": 5012};
    Api.createProduct.mockReturnValue(expectedResponse);

    // Act
    const response = await wrapper.vm.callApi()

    // Assert
    expect(response).toEqual(expectedResponse);

  });
});
describe("Test createProduct method", () => {
  test("createProduct fails with invalid price", async () => {
    // Arrange
    wrapper.setData({queryParams: {recommendedRetailPrice: 'one'}});
    const errorMessage = "Please enter a valid price";

    // Act
    await wrapper.vm.createProduct();

    // Assert
    expect(wrapper.vm.$data.errorMessage).toEqual(errorMessage);
  });

  test("createProduct fails with price of zero", async () => {
    // Arrange
    wrapper.setData({queryParams: {recommendedRetailPrice: 0}});
    const errorMessage = "Please enter a valid price greater than 0";

    // Act
    await wrapper.vm.createProduct();

    // Assert
    expect(wrapper.vm.$data.errorMessage).toEqual(errorMessage);
  });

  test("createProduct fails with negative price", async () => {
    // Arrange
    wrapper.setData({queryParams: {recommendedRetailPrice: -1}});
    const errorMessage = "Please enter a valid price greater than 0";

    // Act
    await wrapper.vm.createProduct();

    // Assert
    expect(wrapper.vm.$data.errorMessage).toEqual(errorMessage);
  });

  test("createProduct fails with empty name", async () => {
    // Arrange
    wrapper.setData({queryParams: {name: ""}});
    const errorMessage = "Please enter a name for your product";

    // Act
    await wrapper.vm.createProduct();

    // Assert
    expect(wrapper.vm.$data.errorMessage).toEqual(errorMessage);
  });

  test("createProduct fails with null name", async () => {
    // Arrange
    wrapper.setData({queryParams: {name: null}});
    const errorMessage = "Please enter a name for your product";

    // Act
    await wrapper.vm.createProduct();

    // Assert
    expect(wrapper.vm.$data.errorMessage).toEqual(errorMessage);
  });

  test("createProduct fails with name of \" \"", async () => {
    // Arrange
    wrapper.setData({queryParams: {name: " "}});
    const errorMessage = "Please enter a name for your product";

    // Act
    await wrapper.vm.createProduct();

    // Assert
    expect(wrapper.vm.$data.errorMessage).toEqual(errorMessage);
  });

  test("createProduct Fails due to failed API call", async () => {
    // Arrange
    const message = 'Could not create product';
    Api.createProduct.mockImplementation(
        () => Promise.reject(new ApiRequestError(message))
    );

    // Act
    await wrapper.vm.createProduct();

    // Assert
    expect(wrapper.vm.$data.errorMessage).toEqual(message);
  });

  test("callApi Succeeds with valid inputs", async () => {
    // Arrange
    const expectedResponse = {"productId": 5012};
    Api.createProduct.mockReturnValue(expectedResponse);

    // Act
    await wrapper.vm.createProduct();

    // Assert
    expect(wrapper.vm.$router.push).toHaveBeenCalledWith(
        {
          "name": "BusinessProducts",
          "params": {
            "businessId": wrapper.vm.$props.businessId
          }
        });
  });
});

describe("autofill behaviour", () => {
  const clearFields = async () => {
    wrapper.setData({
      queryParams: {
        name: "",
        description: "",
        manufacturer: "",
        recommendedRetailPrice: "",

        // dietary certifications
        isGlutenFree: false,
        isDairyFree: false,
        isVegetarian: false,
        isVegan: false,
        isPalmOilFree: false,

        // nutrient levels
        fat: null,
        saturatedFat: null,
        sugars: null,
        salt: null,

        nutriScore: null,
        novaGroup: null
      }
    });
    await wrapper.vm.$nextTick();
  }

  const exampleResponse = () => ({
    name: "Hi There",
    manufacturer: "Beautiful weather today, isn't it?",

    // dietary certifications
    isGlutenFree: true,
    isDairyFree: true,
    isVegetarian: true,
    isVegan: true,
    isPalmOilFree: true,

    // nutrient levels
    fat: "High",
    saturatedFat: "Moderate",
    sugars: "Low",
    salt: "High",

    nutriScore: "A",
    novaGroup: "B"
  });

  test("all default values - all values set", async () => {
    // Arrange
    await clearFields();
    const info = exampleResponse();

    // Act
    wrapper.vm.autofill(info);

    // Assert
    // expect everything to be set
    Object.keys(info).forEach(key => expect(wrapper.vm.queryParams[key]).toEqual(info[key]));
  });

  test("empty strings overwritten", async () => {
    // Arrange
    await clearFields();
    const info = exampleResponse();
    wrapper.vm.queryParams.name = "    ";

    // Act
    wrapper.vm.autofill(info);

    // Assert
    expect(wrapper.vm.queryParams.name).toEqual(info.name);
  });

  test("non-empty strings not overwritten", async () => {
    // Arrange
    await clearFields();
    const info = exampleResponse();
    const expected = "Don't overwrite meeeeeee. I have so much to live for!";
    wrapper.vm.queryParams.name = expected;

    // Act
    wrapper.vm.autofill(info);

    // Assert
    expect(wrapper.vm.queryParams.name).toEqual(expected);
  });


  test("true booleans not overwritten", async () => {
    // Arrange
    await clearFields();
    const info = exampleResponse();
    info.isGlutenFree = false;
    wrapper.vm.queryParams.isGlutenFree = true;

    // Act
    wrapper.vm.autofill(info);

    // Assert
    expect(wrapper.vm.queryParams.isGlutenFree).toEqual(true);
  });
});
