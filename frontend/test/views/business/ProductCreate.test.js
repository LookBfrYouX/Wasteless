import {shallowMount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";
import ProductCreate from "@/views/business/ProductCreate";
import Vuetify from 'vuetify';

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
      ...globalStateMocks(),
    },
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

describe("Test Computed Vales", () => {
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
    wrapper.vm.$data.queryParams.currency = null;

    // Action
    const computedValue = wrapper.vm.currencyText;

    // Assert
    expect(computedValue).toEqual('(Unknown currency)');
  });
});