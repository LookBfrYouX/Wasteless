import {shallowMount} from "@vue/test-utils";
import Suggestions from "../../components/Suggestions";

jest.useFakeTimers();

let wrapper;

beforeEach(() => {
  wrapper = shallowMount(Suggestions, {
    propsData: {
      suggestions: []
    },
    mocks: {},
    stubs: {},
    methods: {}
  });
});

afterEach(() => wrapper.destroy());

const genSuggestion = val => {
  return {
    val,
    toString: () => val.toString()
  };
}

const defaultSuggestions = ["A", "B", "C"].map(el => genSuggestion(el));

describe("arrow behaviour", () => {

  /**
   * Sets suggestion to an array and focuses on input element
   * @returns input element
   */
  const setup = async (suggestions = undefined) => {
    if (suggestions == undefined) {
      suggestions = defaultSuggestions;
    }
    await wrapper.setProps({
      suggestions
    });

    const input = wrapper.find("input");
    await input.trigger("focus");

    return {
      input
    }
  }

  test("arrow down", async () => {
    const {input} = await setup();
    for (const index of [0, 1, 2, 2]) {
      await input.trigger("keydown",
          {code: "ArrowDown", preventDefault: jest.fn});
      expect(wrapper.vm.index).toBe(index);
    }
  });

  test("arrow up", async () => {
    const {input} = await setup();
    await wrapper.setData({index: 2});
    for (const index of [1, 0, -1, -1]) {
      await input.trigger("keydown",
          {code: "ArrowUp", preventDefault: jest.fn});
      expect(wrapper.vm.index).toBe(index);
    }
  });

  test("enter", async () => {
    const {input} = await setup();
    await wrapper.setData({index: 1});
    await input.trigger("keydown", {code: "Enter", preventDefault: jest.fn});

    expect(wrapper.emitted("input")[0][0]).toBe(
        defaultSuggestions[1].toString());
    expect(wrapper.emitted("suggestion")[0][0]).toEqual(defaultSuggestions[1]);
  });

  test("suggestions length decrease", async () => {
    await setup();
    await wrapper.setData({index: 2});
    await wrapper.setProps({
      suggestions: wrapper.vm.suggestions.slice(0, -1) // Remove last element form the array
    });
    expect(wrapper.vm.index).toBe(1); // Should reduce to the last suggestion
  });

  test("emitted event on input event", async () => {
    const {input} = await setup();
    const val = "abcd";

    await input.setValue(val);
    await input.trigger("change");

    await wrapper.vm.$nextTick();
    expect(wrapper.emitted("input")[0][0]).toBe(val);
    // Outer array is as multiple events could have been emitted
    // Inner array is for value of event
  });

  test("emitted events on suggestion selection", async () => {
    await setup();

    const li = wrapper.find("li:nth-child(2)");
    // Second element in array
    await li.trigger("click");
    expect(wrapper.emitted("input")[0][0]).toBe(
        defaultSuggestions[1].toString());
    expect(wrapper.emitted("suggestion")[0][0]).toEqual(defaultSuggestions[1]);
  });

  jest.useFakeTimers();

  test("focus causes suggestion list to appear", async () => {
    const {input} = await setup();
    expect(wrapper.vm.showSuggestions).toBe(true);
  });

  test("blur causes suggestion list to disappear", async () => {
    const {input} = await setup();

    await input.trigger("blur");
    jest.runAllTimers();
    expect(wrapper.vm.showSuggestions).toBe(false);
  });

  test("blur then focus doesn't causes suggestion list to disappear",
      async () => {
        const {input} = await setup();

        await input.trigger("blur");
        await wrapper.vm.$nextTick();
        await input.trigger("focus");
        jest.runAllTimers();
        expect(wrapper.vm.showSuggestions).toBe(true);
      });
});

describe("isDisabled", () => {
  test("string given", () => {
    expect(wrapper.vm.isDisabled("")).toBe(false);
  });

  test("undefined", () => {
    expect(wrapper.vm.isDisabled()).toBe(false);
  });

  test("object given, no disabled", () => {
    expect(wrapper.vm.isDisabled({ bla: "asfdf"})).toBe(false);
  });

  test("object given, disabled true", () => {
    expect(wrapper.vm.isDisabled({ disabled: true})).toBe(true);
  });

  test("object given, disabled false", () => {
    expect(wrapper.vm.isDisabled({ disabled: false})).toBe(false);
  });

  test("object given, disabled 1", () => {
    expect(wrapper.vm.isDisabled({ disabled: 1})).toBe(false);
  });
});