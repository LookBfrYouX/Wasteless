import {mount} from "@vue/test-utils"

import Vuetify from 'vuetify'
import NutriScoreInput from "@/components/NutriScoreInput";

let vuetify = new Vuetify();
let wrapper;

beforeEach(() => {
  wrapper = mount(NutriScoreInput, {
    vuetify,
    propsData: {
      value: 'B'
    }
  });
});

afterEach(() => wrapper.destroy());

describe("Nutrition score input test", () => {
  test("Score is emitted to parent component", async () => {
    let inputEl = wrapper.find("input");
    inputEl.trigger("focus");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.space");
    await wrapper.vm.$nextTick();
    // first keydown needed to select N/A; second keydown actually moves it down
    inputEl.trigger("keydown.down");
    await wrapper.vm.$nextTick();

    inputEl.trigger("keydown.down");
    await wrapper.vm.$nextTick();

    inputEl.trigger("keydown.enter");
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().input.length).toBe(1);
    expect(wrapper.emitted().input[0][0]).toBe('A');
  });
});
