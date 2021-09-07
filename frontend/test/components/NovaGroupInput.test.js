import {mount} from "@vue/test-utils"

import Vuetify from 'vuetify'
import BarcodeInput from "@/components/BarcodeInput";

let vuetify = new Vuetify();

let wrapper;

afterEach(() => wrapper.destroy());

describe("BarcodeInput test", () => {
  test("Test component is properly mounted", () => {
    wrapper = mount(BarcodeInput, {
      vuetify,
    });

    expect(wrapper.vm.$data.barcode).toEqual("");
    expect(wrapper.vm.$data.errorMessage).toBeNull();
  });
});