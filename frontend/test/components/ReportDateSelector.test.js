import {shallowMount} from "@vue/test-utils";
import ReportDateSelector from "@/components/ReportDateSelector";

let wrapper;
jest.useFakeTimers();

beforeEach(() => {
  wrapper = shallowMount(ReportDateSelector, {
    propsData: {
      startDate: new Date("2020-05-02"),
      endDate: new Date("2020-05-02"),
      defaultDates: {
        startDate: new Date("2020-05-02"),
        endDate: new Date("2020-05-02"),
      }
    }
  });
});
afterEach(() => wrapper.destroy());

describe("dateRangeSelected", () => {
  test("notSelected", async () => {
    const expected = [];

    await wrapper.vm.dateRangeSelected(null);

    expect(wrapper.vm.filterDates).toEqual(expected);
  });

  test("today", async () => {
    const expected = new Date().toISOString().split('T')[0];

    await wrapper.vm.dateRangeSelected("Today");

    expect(wrapper.vm.filterDates[0]).toEqual(expected);
  });

  test("weekAgo", async () => {
    const expected = new Date(
        new Date().setDate(new Date().getDate() - 6)).toISOString().split(
        'T')[0];

    await wrapper.vm.dateRangeSelected("Last week");

    expect(wrapper.vm.filterDates[0]).toEqual(expected);
  });

  test("monthAgo", async () => {
    const expected = new Date(
        new Date().setMonth(new Date().getMonth() - 1)).toISOString().split(
        'T')[0];

    await wrapper.vm.dateRangeSelected("Last month");

    expect(wrapper.vm.filterDates[0]).toEqual(expected);
  });

  test("yearAgo", async () => {
    const expected = new Date(new Date().setFullYear(
        new Date().getFullYear() - 1)).toISOString().split('T')[0];

    await wrapper.vm.dateRangeSelected("Last year");

    expect(wrapper.vm.filterDates[0]).toEqual(expected);
  });
});

describe("dateSorted", () => {
  test("correctOrder", async () => {
    // Arrange
    const firstDate = new Date(
        new Date().setDate(new Date().getDate() - 7)).toISOString().split(
        'T')[0];
    const secondDate = new Date().toISOString().split('T')[0];

    // Act
    wrapper.vm.datePickerChange([secondDate, firstDate]);
    await wrapper.vm.$nextTick();

    // Assert
    expect(wrapper.vm.filterDates[0]).toEqual(firstDate);
    expect(wrapper.vm.filterDates[1]).toEqual(secondDate);
  });

  test("wrongOrder", async () => {
    // Arrange
    const firstDate = new Date(
        new Date().setDate(new Date().getDate() - 7)).toISOString().split(
        'T')[0];
    const secondDate = new Date().toISOString().split('T')[0];

    // Act
    wrapper.vm.datePickerChange([secondDate, firstDate]);
    await wrapper.vm.$nextTick();
    // Assert
    expect(wrapper.vm.filterDates[0]).toEqual(firstDate);
    expect(wrapper.vm.filterDates[1]).toEqual(secondDate);
  });
});