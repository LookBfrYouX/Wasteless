import {mount} from "@vue/test-utils"

import Vuetify from 'vuetify'
import BarcodeInput from "@/components/BarcodeInput";

let vuetify = new Vuetify();

let wrapper;

const info = { data: {
              product: {
                product_name: "Vegemite",
                brands:"Asus",
                nutriscore_grade:"f",
                nova_group:"4",
                nutrient_levels: { fat: "low", salt: "moderate", "saturated-fat": "low", sugars: "high" },
                ingredients_analysis_tags: ['en:palm-oil-free', 'en:vegan', 'en:vegetarian', 'en:gluten-free', 'en:dairy-free']
              },
              status:1,
              status_verbose:"product found"
            }}

jest.mock('@/Api')

const {Api} = require('@/Api')

beforeEach(() => {
  wrapper = mount(BarcodeInput, {
      vuetify,
    });

});

afterEach(() => wrapper.destroy());

describe("BarcodeInput test", () => {
  test("Test component is properly mounted", () => {
    expect(wrapper.vm.$data.barcode).toEqual("");
    expect(wrapper.vm.$data.errorMessage).toBeNull();
  });

  test("Test API call gets product", async () => {
    Api.getOpenFoodFacts.mockResolvedValue(info);
    await wrapper.vm.setProductInformation()
    expect(wrapper.vm.$data.info.name).toEqual("Vegemite");
    expect(wrapper.vm.$data.info.manufacturer).toEqual("Asus");
    expect(wrapper.vm.$data.info.nutriScore).toEqual("F");
    expect(wrapper.vm.$data.info.novaGroup).toEqual("4");
    expect(wrapper.vm.$data.errorMessage).toBeNull();
  });

  test("Test nutritional levels have been set correctly", () => {
    wrapper.vm.setNutritionalLevelInformation({ fat: "low", salt: "moderate", "saturated-fat": "low", sugars: "high" });
    expect(wrapper.vm.$data.info.fat).toEqual("LOW");
    expect(wrapper.vm.$data.info.saturatedFat).toEqual("LOW")
    expect(wrapper.vm.$data.info.salt).toEqual("MODERATE")
    expect(wrapper.vm.$data.info.sugars).toEqual("HIGH")
    expect(wrapper.vm.$data.errorMessage).toBeNull();
  });

  test("Test analysis tags have been set correctly", () => {
    wrapper.vm.setIngredientAnalysisInformation(
        ['en:palm-oil-free', 'en:vegan', 'en:vegetarian', 'en:gluten-free',
          'en:dairy-free']);
    expect(wrapper.vm.$data.info.isPalmOilFree).toBeTruthy();
    expect(wrapper.vm.$data.info.isVegan).toBeTruthy();
    expect(wrapper.vm.$data.info.isVegetarian).toBeTruthy();
    expect(wrapper.vm.$data.info.isGlutenFree).toBeTruthy();
    expect(wrapper.vm.$data.info.isDairyFree).toBeTruthy();
  });

});

describe('Barcode Scan Functionality Tests', () => {
  test('Skeleton component shows before scanner is loaded', async () => {
    // Arrange
    wrapper.vm.$data.scannerLoaded = false;
    wrapper.vm.$data.dialog = true;
    await wrapper.vm.$nextTick();

    // Act & Assert
    expect(wrapper.find('.skeleton').exists()).toBeTruthy();
  });

  test('Skeleton component does not show if scanner is loaded', async () => {
    // Arrange
    wrapper.vm.$data.scannerLoaded = true;
    // wrapper.vm.$data.dialog = true;
    await wrapper.vm.$nextTick();

    // Act & Assert
    expect(wrapper.find('.skeleton').exists()).toBeFalsy();
  });
});
