import {mount} from "@vue/test-utils";

import Vuetify from 'vuetify';
import DietaryCertificationsInput
  from "@/components/DietaryCertificationsInput";

let vuetify = new Vuetify();

let data = {
  queryParams: {
    isGlutenFree: false,
    isDairyFree: false,
    isVegetarian: false,
    isVegan: false,
    isPalmOilFree: false
  },
  certifications: [],
  options: ['Gluten Free', 'Dairy Free', 'Vegetarian', 'Vegan', 'Palm Oil Free']
};

let wrapper;

beforeEach(() => {
  wrapper = mount(DietaryCertificationsInput, {
    vuetify,
    propsData: data
  });
});

afterEach(() => wrapper.destroy());

describe("DietaryCertificationsInput test", () => {
  test("Component is properly mounted", () => {
    expect(wrapper.vm.$data).toEqual(data);
  });

  test("Test onChange method output with empty certifications", () => {
    // Arrange
    wrapper.vm.$data.certifications = [];
    let expectedParams = {
      isGlutenFree: false,
      isDairyFree: false,
      isVegetarian: false,
      isVegan: false,
      isPalmOilFree: false
    };

    // Action
    wrapper.vm.onChange();

    // Assert
    expect(wrapper.vm.$data.queryParams).toEqual(expectedParams);
  });

  test("Test onChange method output with invalid certification", () => {
    // Arrange
    wrapper.vm.$data.certifications = ['Invalid Input!'];
    let expectedParams = {
      isGlutenFree: false,
      isDairyFree: false,
      isVegetarian: false,
      isVegan: false,
      isPalmOilFree: false
    };

    // Action
    wrapper.vm.onChange();

    // Assert
    expect(wrapper.vm.$data.queryParams).toEqual(expectedParams);
  });

  test("Test onChange method output with single certification", () => {
    // Arrange
    wrapper.vm.$data.certifications = ['Dairy Free'];
    let expectedParams = {
      isGlutenFree: false,
      isDairyFree: true,
      isVegetarian: false,
      isVegan: false,
      isPalmOilFree: false
    };

    // Action
    wrapper.vm.onChange();

    // Assert
    expect(wrapper.vm.$data.queryParams).toEqual(expectedParams);
  });

  test("Test onChange method output with all certifications", () => {
    // Arrange
    wrapper.vm.$data.certifications = wrapper.vm.$data.options;
    let expectedParams = {
      isGlutenFree: true,
      isDairyFree: true,
      isVegetarian: true,
      isVegan: true,
      isPalmOilFree: true
    };

    // Action
    wrapper.vm.onChange();

    // Assert
    expect(wrapper.vm.$data.queryParams).toEqual(expectedParams);
  });

  test("Test onChange method emits correctly", () => {
    // Arrange
    wrapper.vm.$data.certifications = ['Dairy Free', 'Vegetarian', 'Vegan'];
    let expectedParams = {
      isGlutenFree: false,
      isDairyFree: true,
      isVegetarian: true,
      isVegan: true,
      isPalmOilFree: false
    };

    // Action
    wrapper.vm.onChange();

    // Assert
    expect(wrapper.emitted('input')[0][0]).toEqual(expectedParams);
  });
});