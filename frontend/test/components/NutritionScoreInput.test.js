import {mount} from "@vue/test-utils"

import Vuetify from 'vuetify'
import NutritionScoreInput from "@/components/NutritionScoreInput";

let vuetify = new Vuetify();
let wrapper;

beforeEach(() => {
  wrapper = mount(NutritionScoreInput, {
    vuetify,
    propsData: {
      nutritionScore: null
    }
  });
});

afterEach(() => wrapper.destroy());

describe("Nutrition score input test", () => {
  test("Test component is properly mounted", () => {
    expect(wrapper.vm.$data.options).toEqual(['A', 'B', 'C', 'D', 'E']);
    expect(wrapper.vm.$props.nutritionScore).toEqual(null);
  });
});