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
    expect(wrapper.vm.$data.name).toEqual("Vegemite");
    expect(wrapper.vm.$data.manufacturer).toEqual("Asus");
    expect(wrapper.vm.$data.nutriScore).toEqual("F");
    expect(wrapper.vm.$data.novaGroup).toEqual("4");
    expect(wrapper.vm.$data.errorMessage).toBeNull();
  });

  test("Test nutritional levels have been set correctly", () => {
    wrapper.vm.setNutritionalLevelInformation({ fat: "low", salt: "moderate", "saturated-fat": "low", sugars: "high" });
    expect(wrapper.vm.$data.fat).toEqual("LOW");
    expect(wrapper.vm.$data.saturatedFat).toEqual("LOW")
    expect(wrapper.vm.$data.salt).toEqual("MODERATE")
    expect(wrapper.vm.$data.sugars).toEqual("HIGH")
    expect(wrapper.vm.$data.errorMessage).toBeNull();
  });

  test("Test analysis tags have been set correctly", () => {
    wrapper.vm.setIngredientAnalysisInformation(['en:palm-oil-free', 'en:vegan', 'en:vegetarian', 'en:gluten-free', 'en:dairy-free']);
    expect(wrapper.vm.$data.palmOilFree).toBeTruthy();
    expect(wrapper.vm.$data.vegan).toBeTruthy();
    expect(wrapper.vm.$data.vegetarian).toBeTruthy();
    expect(wrapper.vm.$data.glutenFree).toBeTruthy();
    expect(wrapper.vm.$data.dairyFree).toBeTruthy();
  });

});
