import {mount} from "@vue/test-utils"

import Vuetify from 'vuetify'
import NovaGroupInput from "@/components/NovaGroupInput";

let vuetify = new Vuetify();
let wrapper;

const options = [
  {
    text: 'N/A',
    value: null
  }, {
    text: '1',
    value: '1',
  }, {
    text: '2',
    value: '2',
  }, {
    text: '3',
    value: '3'
  }, {
    text: '4',
    value: '4'
  }
];

beforeEach(() => {
  wrapper = mount(NovaGroupInput, {
    vuetify,
    propsData: {
      value: null
    }
  });
});

afterEach(() => wrapper.destroy());

describe("Nutrition score input test", () => {
  test("Test component is properly mounted", () => {
    expect(wrapper.vm.$data.options).toEqual(options);
    expect(wrapper.vm.$props.value).toEqual(null);
  });
  test("Score is emitted to parent component", async () => {
    let inputEl = wrapper.find("input");
    inputEl.trigger("focus");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.space");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.down");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.down");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.enter");
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().input.length).toBe(1);
    expect(wrapper.emitted().input[0][0]).toBe("1");
  });
});