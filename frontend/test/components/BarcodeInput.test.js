import {mount} from "@vue/test-utils"

import Vuetify from 'vuetify'
import BarcodeInput from "@/components/BarcodeInput";

let vuetify = new Vuetify();

let wrapper;

const info = {
  data: {
    product: {
      product_name: "Vegemite",
      brands: "Asus",
      nutriscore_grade: "f",
      nova_group: "4",
      nutrient_levels: {
        fat: "low",
        salt: "moderate",
        "saturated-fat": "low",
        sugars: "high"
      },
      ingredients_analysis_tags: ['en:palm-oil-free', 'en:vegan',
        'en:vegetarian', 'en:gluten-free', 'en:dairy-free']
    },
    status: 1,
    status_verbose: "product found"
  }
}

jest.mock('@/Api')

const {Api} = require('@/Api')

/**
 * Adds a wrapping `div data-app="true"` to the body so that we don't
 * get Vuetify complaining about missing data-app attribute for some components.
 *
 * @return undefined
 */

beforeEach(() => {
  wrapper = mount(BarcodeInput, {
    vuetify,
    stubs: {
      StreamBarcodeReader: true
    }
  });
  const app = document.createElement("div");
  app.setAttribute("data-app", true);
  document.body.append(app);

  wrapper.vm.$data.dialog = true;
  window.MediaStream = jest.fn().mockImplementation(() => ({
    getTracks: jest.fn().mockReturnValue([])
  }))
});

afterEach(() => wrapper.destroy());

describe("BarcodeInput test", () => {
  test("Test component is properly mounted", () => {
    expect(wrapper.vm.$data.barcode).toEqual("");
    expect(wrapper.vm.$data.errorMessage).toBeNull();
  });

  test("Test API call gets product", async () => {
    Api.getOpenFoodFacts.mockResolvedValue(info);
    await wrapper.vm.setProductInformation()
    expect(wrapper.vm.$data.info.name).toEqual("Vegemite");
    expect(wrapper.vm.$data.info.manufacturer).toEqual("Asus");
    expect(wrapper.vm.$data.info.nutriScore).toEqual("F");
    expect(wrapper.vm.$data.info.novaGroup).toEqual("4");
    expect(wrapper.vm.$data.errorMessage).toBeNull();
  });

  test("Test nutritional levels have been set correctly", () => {
    wrapper.vm.setNutritionalLevelInformation(
        {fat: "low", salt: "moderate", "saturated-fat": "low", sugars: "high"});
    expect(wrapper.vm.$data.info.fat).toEqual("LOW");
    expect(wrapper.vm.$data.info.saturatedFat).toEqual("LOW")
    expect(wrapper.vm.$data.info.salt).toEqual("MODERATE")
    expect(wrapper.vm.$data.info.sugars).toEqual("HIGH")
    expect(wrapper.vm.$data.errorMessage).toBeNull();
  });

  test("Test analysis tags have been set correctly", () => {
    wrapper.vm.setIngredientAnalysisInformation(
        ['en:palm-oil-free', 'en:vegan', 'en:vegetarian', 'en:gluten-free',
          'en:dairy-free']);
    expect(wrapper.vm.$data.info.isPalmOilFree).toBeTruthy();
    expect(wrapper.vm.$data.info.isVegan).toBeTruthy();
    expect(wrapper.vm.$data.info.isVegetarian).toBeTruthy();
    expect(wrapper.vm.$data.info.isGlutenFree).toBeTruthy();
    expect(wrapper.vm.$data.info.isDairyFree).toBeTruthy();
  });

});

describe('Barcode Scan Functionality Tests', () => {
  test('Skeleton component shows before scanner is loaded', () => {
    // Arrange
    wrapper.setData({scannerLoaded: false});
    // Act & Assert
    expect(wrapper.find('.skeleton').exists()).toBeTruthy();
  });

  test('barcodeScanCounts records correct barcode counts', () => {
    // Arrange
    const barcode1 = '1234567890123';
    const barcode2 = '0987654321098';
    const barcodes = [
      barcode1, barcode1, barcode1,
      barcode2, barcode2
    ];

    // Act
    barcodes.forEach(wrapper.vm.decodeScannerResult);

    // Assert
    expect(wrapper.vm.$data.barcodeScanCounts.get(barcode1)).toBe(
        barcodes.filter(x => x === barcode1).length);
    expect(wrapper.vm.$data.barcodeScanCounts.get(barcode2)).toBe(
        barcodes.filter(x => x === barcode2).length);
  });

  test('When barcode reads are short of threshold, doesnt set barcode', () => {
    // Arrange
    const threshold = wrapper.vm.$data.threshold;
    const barcode = '1234567890123';
    const barcodes = new Array(threshold - 1).fill(barcode);

    // Act
    barcodes.forEach(wrapper.vm.decodeScannerResult);

    // Assert
    expect(wrapper.vm.$data.barcode).not.toEqual(barcode);
  });

  test('When barcode reads equal threshold, barcode is set', () => {
    // Arrange
    const threshold = wrapper.vm.$data.threshold;
    const barcode = '1234567890123';
    const barcodes = new Array(threshold).fill(barcode);

    // Act
    barcodes.forEach(wrapper.vm.decodeScannerResult);

    // Assert
    expect(wrapper.vm.$data.barcode).toEqual(barcode);
  });

  test('When barcode reads exceed threshold, barcode is set', () => {
    // Arrange
    const threshold = wrapper.vm.$data.threshold;
    const barcode = '1234567890123';
    const barcodes = new Array(threshold + 1).fill(barcode);

    // Act
    barcodes.forEach(wrapper.vm.decodeScannerResult);

    // Assert
    expect(wrapper.vm.$data.barcode).toEqual(barcode);
  });

  test('scannerLoaded is set to True on scanner load', () => {
    // Arrange
    wrapper.setData({scannerLoaded: false});

    // Act
    wrapper.vm.onScannerLoad();

    // Assert
    expect(wrapper.vm.$data.scannerLoaded).toEqual(true);
  });

  test('scannerLoaded is set to False on dialog close', async () => {
    // Arrange
    wrapper.setData({scannerLoaded: true, stream: new MediaStream()});

    // Act (watcher method for dialog)
    wrapper.setData({dialog: false});
    await wrapper.vm.$nextTick();

    // Assert
    expect(wrapper.vm.$data.scannerLoaded).toEqual(false);
  });

  test('scannerLoaded doesnt change if dialog is opened', async () => {
    // Arrange
    wrapper.setData({scannerLoaded: false, stream: new MediaStream()});

    // Act (watcher method for dialog)
    wrapper.setData({dialog: true});
    await wrapper.vm.$nextTick();

    // Assert
    expect(wrapper.vm.$data.scannerLoaded).toEqual(false);
  });

  test('scannerLoaded doesnt change without camera permision', async () => {
    // Arrange
    wrapper.setData({scannerLoaded: false, stream: null});

    // Act (watcher method for dialog)
    wrapper.setData({dialog: true});
    await wrapper.vm.$nextTick();

    // Assert
    expect(wrapper.vm.$data.scannerLoaded).toEqual(false);
  });
});
