
import {shallowMount} from "@vue/test-utils";
import SalesReportTable from "@/components/SalesReportTable";
import Vuetify from 'vuetify';

const vuetify = new Vuetify();

jest.mock("@/Api");

let wrapper = null;

let mountSalesReportTable = () => {
  wrapper = shallowMount(SalesReportTable, {
    vuetify,
    propsData: {
      granularity: "Week",
      transactionInformation: null
    },
  });
  return wrapper;
}

afterEach = () => {
  if (wrapper != null) {
    wrapper.destroy();
    wrapper = null;
  }
}

describe("headers", () => {
  test("sort by amount", async () => {
    mountSalesReportTable();

    let transactionInformation = [
      { amount: 1, amountText: "N" }, // should sort by amount, not amountText so amountText can be anything (as long as they are unique)
      { amount: 3, amountText: "K" },
      { amount: 4, amountText: "O" },
      { amount: 2, amountText: "I" },
    ];
    wrapper.setData({ transactionInformation });
    await wrapper.vm.$nextTick();

    const unsortedArray = wrapper.vm.transactionInformation.map(el => el.amountText);

    unsortedArray.sort(
      // header contains a sort function that takes in just amount text
      wrapper.vm.headers.find(el => el.value == "amountText").sort
    );

    expect(unsortedArray.join("")).toEqual("NIKO");
  });

  test("sort by date range text", async () => {
    mountSalesReportTable();

    let transactionInformation = [
      { date: 2, dateRangeText: "A" },
      { date: 5, dateRangeText: "T" },
      { date: 1, dateRangeText: "R" },
      { date: 3, dateRangeText: "B" },
      { date: 4, dateRangeText: "I" },
      { date: 3, dateRangeText: "B" }, // duplicated key so date must be the same
    ];
    wrapper.setData({ transactionInformation });
    await wrapper.vm.$nextTick();

    const unsortedArray = wrapper.vm.transactionInformation.map(el => el.dateRangeText);

    unsortedArray.sort(
      // header contains a sort function that takes in just date range text
      wrapper.vm.headers.find(el => el.value == "dateRangeText").sort
    );

    expect(unsortedArray.join("")).toEqual("RABBIT");
  });
});


describe("tableData", () => {
  test("null value", () => {
    expect(SalesReportTable.computed.tableData.call({
      transactionInformation: null
    })).toEqual([]);
  });

  test("hide zeroes enabled", () => {
    expect(SalesReportTable.computed.tableData.call({
      transactionInformation: [
        { transactionCount: 4 },
        { transactionCount: 0 },
        { transactionCount: 3 },
        { transactionCount: 0 },
        { transactionCount: 1 },
      ],
      hideZeroes: true
    }).length).toEqual(3);
  });
 
  test("hide zeroes disabled", () => {
    expect(SalesReportTable.computed.tableData.call({
      transactionInformation: [
        { transactionCount: 4 },
        { transactionCount: 0 },
        { transactionCount: 3 },
        { transactionCount: 0 },
        { transactionCount: 1 },
      ],
      hideZeroes: false
    }).length).toEqual(5);
  });
});
