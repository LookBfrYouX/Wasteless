import {mount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";
import NutrientLevelsEdit from "@/components/NutrientLevelsEdit";
import Vuetify from "vuetify";
let vuetify = new Vuetify();

const defaultProps = () => {
  return {
    fat: null,
    saturatedFat: "LOW",
    sugars: "MODERATE",
    sodium: "HIGH"
  }
}

let wrapper;

beforeEach(() => {
  wrapper = mount(NutrientLevelsEdit, {
    vuetify,
    propsData: {
      value: defaultProps()
    },
    mocks: globalStateMocks()
  });
});

afterEach(() => wrapper.destroy());

describe("Slider input causes event to be emitted", () => {
  test("All four sliders are correctly emit events on left/right arrow key entry", async () => {
    // https://github.com/vuetifyjs/vuetify/blob/master/packages/vuetify/src/components/VSlider/__tests__/VSlider.spec.ts
    const containers = await wrapper.findAll("div.bg-white");
    expect(containers.length).toBe(4);

    const nutrients = [
      {
        key: "fat",
        shiftRight: true, // keydown.right if true, keydown.left otherwise
        expectedValue: "LOW"
      }, {
        key: "saturatedFat",
        shiftRight: false, // in pairs: null <=> low, and moderate <=> high
        expectedValue: null
      }, {
        key: "sugars",
        shiftRight: true,
        expectedValue: "HIGH"
      }, {
        key: "sodium",
        shiftRight: false,
        expectedValue: "MODERATE"
      }
    ]

    for(let i = 0; i < 4; i++) {
      // sodium is high (right most value) so shift that one left
      const {key, shiftRight, expectedValue} = nutrients[i];
      const slider = await containers.at(i).find('.v-slider__thumb-container');
      const keyEvent = shiftRight? "keydown.right": "keydown.left";
      // Shift slider value left or right by one tick
      await slider.trigger(keyEvent);
      expect(wrapper.emitted().input.length).toBe(i + 1);
      // Expect event to be emitted, and value for the given key to be updated
      expect(wrapper.emitted().input[i][0][key]).toBe(expectedValue);
    }
  });
});
