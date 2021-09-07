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
  test("Score is emitted to parent component", async () => {
    let inputEl = wrapper.find("input");
    console.log(inputEl);
    inputEl.trigger("focus");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.space");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.down");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.enter");
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().input.length).toBe(1);
    expect(wrapper.emitted().input[0][0]).toBe("A");
  });
});