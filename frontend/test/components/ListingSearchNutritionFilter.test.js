import {shallowMount} from "@vue/test-utils";
import ListingSearchNutritionFilterFilter from "@/components/ListingSearchNutritionFilter";

let wrapper;
jest.useFakeTimers();

beforeEach(() => {
  wrapper = shallowMount(ListingSearchNutritionFilterFilter);
});

afterEach(() => wrapper.destroy());

describe("Nova group input restriction", () => {
  test("minSelectedMaxRestricted", async () => {
    const SELECTED_MIN = 2
    const EXPECTED_MAX = [2, 3, 4]
    await wrapper.setData({minNovaGroup: SELECTED_MIN})

    expect(wrapper.vm.maxNovaGroups).toEqual(EXPECTED_MAX);
  });

  test("maxSelectedMinRestricted", async () => {
    const SELECTED_MAX = 2
    const EXPECTED_MIN = [1, 2]
    await wrapper.setData({maxNovaGroup: SELECTED_MAX})

    expect(wrapper.vm.minNovaGroups).toEqual(EXPECTED_MIN);
  });
});

describe("NutriScore input restriction", () => {
  test("minSelectedMaxRestricted", async () => {
    const SELECTED_MIN = "C"
    const EXPECTED_MAX = ["C", "D", "E"]
    await wrapper.setData({minNutriScore: SELECTED_MIN})

    expect(wrapper.vm.maxNutriScores).toEqual(EXPECTED_MAX);
  });

  test("maxSelectedMinRestricted", async () => {
    const SELECTED_MAX = "B"
    const EXPECTED_MIN = ["A", "B"]
    await wrapper.setData({maxNutriScore: SELECTED_MAX})

    expect(wrapper.vm.minNutriScores).toEqual(EXPECTED_MIN);
  });
});

describe("Data changed, events emitted", () => {
  test("Diets changed", async () => {
    await wrapper.setData({diets: [{ name: "Gluten Free", key: "isGlutenFree" }]});
    expect(wrapper.emitted().newDiets).toBeTruthy()
  });

  test("Fats changed", async () => {
    await wrapper.setData({fat: "Unknown"})
    expect(wrapper.emitted().newFat).toBeTruthy()
  });

  test("Saturated fats changed", async () => {
    await wrapper.setData({saturatedFat: "Unknown"})
    expect(wrapper.emitted().newSaturatedFat).toBeTruthy()
  });

  test("Sugars changed", async () => {
    await wrapper.setData({sugars: "Unknown"})
    expect(wrapper.emitted().newSugars).toBeTruthy()
  });

  test("Salts changed", async () => {
    await wrapper.setData({salts: "Unknown"})
    expect(wrapper.emitted().newSalts).toBeTruthy()
  });
});
