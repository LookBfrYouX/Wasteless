import {shallowMount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";
import SalesReport from "@/views/business/SalesReport";

jest.mock("@/Api");
const {Api} = require("@/Api.js");

let wrapper;

const normalizeDateToStartOfDay = SalesReport.methods.normalizeDateToStartOfDay;
const normalizeDateToStartOfWeek = SalesReport.methods.normalizeDateToStartOfWeek.bind(SalesReport.methods);
const normalizeDateToStartOfMonth = SalesReport.methods.normalizeDateToStartOfMonth.bind(SalesReport.methods);
const normalizeDateToStartOfYear = SalesReport.methods.normalizeDateToStartOfYear.bind(SalesReport.methods);

describe("transformedTransactionData", () => {
  /**
   * 
   * @param {*} thisData object with minDate, maxDate, granularity
   * @param {*} transactionDataWithZeroes transactionData with entries for periods
   *            with zero transactions. These are removed before being passed to
   *            the method but not when used to compare the method's output
   */
  const runMethodAndCompare = (thisData, transactionDataWithZeroes) => {
    const result = SalesReport.computed.transformedTransactionData.call({
      ...thisData,
      transactionData: transactionDataWithZeroes.filter(el => el.transactionCount > 0),
      generateUserFacingDateText: () => "MOCKED",
      $helper: {
        makeCurrencyString: () => "MOCKED"
      },
    });

    expect(result.map(el => ({
      // remove dateRangeText and amountText since that is derived data
      date: el.date.toISOString(),
      amount: el.amount,
      transactionCount: el.transactionCount
    }))).toEqual(transactionDataWithZeroes);
  }
  test("day granularity, gap in middle", () => {
    const transactionData = [
      {
        date: "2020-01-01T11:30:20.000Z",
        transactionCount: 1,
        amount: 3.14
      }, {
        date: "2020-01-02T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
      }, {
        date: "2020-01-03T11:30:20.000Z",
        transactionCount: 3,
        amount: 2.71
      }
    ];
    runMethodAndCompare({
      minDate: normalizeDateToStartOfDay(new Date(transactionData[0].date)),
      maxDate: new Date("2020-01-04T00:00:00.000Z"),
      granularity: "Day"
    }, transactionData);
  });
  
  test("day granularity, no data at start", () => {
    const transactionData = [
      {
        date: "2020-01-01T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
      }, {
        date: "2020-01-02T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
      }, {
        date: "2020-01-03T11:30:20.000Z",
        transactionCount: 1,
        amount: 3.14
      }, {
        date: "2020-01-04T11:30:20.000Z",
        transactionCount: 3,
        amount: 2.71
      }
    ];
    runMethodAndCompare({
      minDate: new Date(transactionData[0].date),
      maxDate: new Date("2020-01-04T07:00:00.000Z"),
      granularity: "Day"
    }, transactionData);
  });
  
  
  test("day granularity, no data at end", () => {
    const transactionData = [
      {
        date: "2020-01-01T11:30:20.000Z",
        transactionCount: 1,
        amount: 3.14
      }, {
        date: "2020-01-02T11:30:20.000Z",
        transactionCount: 3,
        amount: 2.71
      }, {
        date: "2020-01-03T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
      }, {
        date: "2020-01-04T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
      } 
    ];
    runMethodAndCompare({
      minDate: normalizeDateToStartOfDay(new Date(transactionData[0].date)),
      maxDate: new Date("2020-01-04T07:00:00.000Z"),
      granularity: "Day"
    }, transactionData);
  });

});