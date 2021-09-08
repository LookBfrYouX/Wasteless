import {mount} from "@vue/test-utils";

import Vuetify from 'vuetify';
import DietaryCertificationsInput
  from "@/components/DietaryCertificationsInput";

let vuetify = new Vuetify();

let wrapper;

beforeEach(() => {
  wrapper = mount(DietaryCertificationsInput, {
    vuetify,
    propsData: {
      value: {
        isGlutenFree: false,
        isDairyFree: false,
        isVegetarian: false,
        isVegan: false,
        isPalmOilFree: false
      }
    }
  });
});

afterEach(() => wrapper.destroy());

describe("DietaryCertificationsInput test", () => {
  test("Test props with empty certifications", async () => {
    // Arrange
    const props = {
      isGlutenFree: false,
      isDairyFree: false,
      isVegetarian: false,
      isVegan: false,
      isPalmOilFree: false
    };
    const expectedList = [];

    // Action
    wrapper.setProps({value: props});
    await wrapper.vm.$nextTick();

    // Assert
    expect(wrapper.vm.certifications).toEqual(expectedList);
  });

  test("Test invalid props", async () => {
    // Arrange
    const props = {
      sillyProp1: false,
      isDairyFree: null
    };
    const expectedList = [];

    // Action
    wrapper.setProps({value: props});
    await wrapper.vm.$nextTick();

    // Assert
    expect(wrapper.vm.certifications).toEqual(expectedList);
  });

  test("Test props with single certification", async () => {
    // Arrange
    const props = {
      isGlutenFree: false,
      isDairyFree: true,
      isVegetarian: false,
      isVegan: false,
      isPalmOilFree: false
    };
    const expectedList = ['Dairy Free'];

    // Action
    wrapper.setProps({value: props});
    await wrapper.vm.$nextTick();

    // Assert
    expect(wrapper.vm.certifications).toEqual(expectedList);
  });

  test("Test props with all certifications", async () => {
    // Arrange
    const props = {
      isGlutenFree: true,
      isDairyFree: true,
      isVegetarian: true,
      isVegan: true,
      isPalmOilFree: true
    };
    const expectedList = ['Gluten Free', 'Dairy Free', 'Vegetarian', 'Vegan',
      'Palm Oil Free'];

    // Action
    wrapper.setProps({value: props});
    await wrapper.vm.$nextTick();

    // Assert
    expect(wrapper.vm.certifications).toEqual(expectedList);
  });

  test("Test certificationSetter method emits correctly", async () => {
    // Arrange
    let expectedProps = {
      isGlutenFree: false,
      isDairyFree: true,
      isVegetarian: true,
      isVegan: true,
      isPalmOilFree: false
    };
    const list = ['Dairy Free', 'Vegetarian', 'Vegan'];

    // Action
    wrapper.vm.certificationSetter(list);

    // Assert
    expect(wrapper.emitted('input')[0][0]).toEqual(expectedProps);
  });
});