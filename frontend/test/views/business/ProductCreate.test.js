import {shallowMount} from "@vue/test-utils";
import {GLOBAL_STATE, globalStateMocks} from "#/testHelper";
import ProductCreate from "@/views/business/ProductCreate";

jest.mock("@/Api");
const {Api} = require("@/Api.js");

jest.useFakeTimers();
let wrapper;
window.alert = jest.fn();

beforeEach(() => {
  wrapper = shallowMount(ProductCreate, {
    mocks: globalStateMocks(),
    stubs: ["nutrient-levels-edit"],
    propsData: {
      businessId: 4
    }
  });
});

afterEach(() => wrapper.destroy());

test("Nutrient levels setter acting properly", async () => {
  const nutrientLevels = {
    fat: null,
    saturatedFat: "LOW",
    sugars: "MODERATE",
    sodium: "HIGH"
  };

  wrapper.nutrientLevels = { ...nutrientLevels };
  // nutrient levels wrapped in object for NutrientLevelsEdit, but is stored in 
  // top-level data object without any wrappers, so the setter must do this conversion
  await wrapper.vm.$nextTick();
  Object.entries(nutrientLevels).forEach(([key, value]) => {
    expect(wrapper.vm[key]).toBe(value);
  });
});