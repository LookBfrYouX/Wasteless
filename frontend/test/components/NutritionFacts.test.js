import {shallowMount} from "@vue/test-utils";
import NutritionFacts from "@/components/NutritionFacts";

let wrapper;

describe("Image loading computed property", () => {
  test("Nova Group image rendered", () => {
    let product = {
      novaGroup: 3,
      fat: null,
      saturatedFat: "MODERATE",
      sugars: "LOW",
      salt: "HIGH",
    }
    wrapper = shallowMount(NutritionFacts, {
      propsData: {
        product: product
      }
    });
    expect(wrapper.find('.nova').exists()).toBe(true);
  });

  test("Nova Group image NOT rendered", () => {
    let product = {
      novaGroup: null,
      fat: null,
      saturatedFat: "MODERATE",
      sugars: "LOW",
      salt: "HIGH",
    }
    wrapper = shallowMount(NutritionFacts, {
      propsData: {
        product: product
      }
    });
    expect(wrapper.find('.nova').exists()).toBe(false);
  });

  test("Nutri-Score image rendered", () => {
    let product = {
      nutriScore: 'B',
      fat: null,
      saturatedFat: "MODERATE",
      sugars: "LOW",
      salt: "HIGH",
    }
    wrapper = shallowMount(NutritionFacts, {
      propsData: {
        product: product
      }
    });
    expect(wrapper.find('.nutri').exists()).toBe(true);
  });

  test("Nutri-Score image NOT rendered", () => {
    let product = {
      nutriScore: null,
      fat: null,
      saturatedFat: "MODERATE",
      sugars: "LOW",
      salt: "HIGH",
    }
    wrapper = shallowMount(NutritionFacts, {
      propsData: {
        product: product
      }
    });
    expect(wrapper.find('.nutri').exists()).toBe(false);
  });
});
