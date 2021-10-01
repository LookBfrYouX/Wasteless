import {shallowMount} from "@vue/test-utils";
import NutrientLevelListItem from "@/components/NutrientLevelListItem";
import {constants} from "@/constants";

let wrapper;

describe("Displaying Nutrient Level data", () => {
  test("LOW level item found from constants", () => {
    let nutrientsLevel = "LOW"
    wrapper = shallowMount(NutrientLevelListItem, {
      propsData: {
        level: nutrientsLevel,
      }
    });
    let expectedLevelItem = constants.PRODUCT.NUTRIENT_LEVELS_MAP.find(el => el.value === nutrientsLevel);
    expect(wrapper.vm.config).toEqual(expectedLevelItem);
  });

  test("MODERATE level item found from constants", () => {
    let nutrientsLevel = "MODERATE"
    wrapper = shallowMount(NutrientLevelListItem, {
      propsData: {
        level: nutrientsLevel,
      }
    });
    let expectedLevelItem = constants.PRODUCT.NUTRIENT_LEVELS_MAP.find(el => el.value === nutrientsLevel);
    expect(wrapper.vm.config).toEqual(expectedLevelItem);
  });

  test("HIGH level item found from constants", () => {
    let nutrientsLevel = "HIGH"
    wrapper = shallowMount(NutrientLevelListItem, {
      propsData: {
        level: nutrientsLevel,
      }
    });
    let expectedLevelItem = constants.PRODUCT.NUTRIENT_LEVELS_MAP.find(el => el.value === nutrientsLevel);
    expect(wrapper.vm.config).toEqual(expectedLevelItem);
  });

  test("Nutrients level not specified", () => {
    let nutrientsLevel = null
    wrapper = shallowMount(NutrientLevelListItem, {
      propsData: {
        level: nutrientsLevel,
      }
    });
    let expectedLevelItem = constants.PRODUCT.NUTRIENT_LEVELS_MAP.find(el => el.value === nutrientsLevel);
    expect(wrapper.vm.config).toEqual(expectedLevelItem);
  });
});