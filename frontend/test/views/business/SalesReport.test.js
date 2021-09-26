import {shallowMount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";
import SalesReport from "@/views/business/SalesReport";
import Vuetify from 'vuetify';

const vuetify = new Vuetify();

jest.mock("@/Api");
const {Api} = require("@/Api.js");

let wrapper = null;

let mountSalesReport = () => {
  wrapper = shallowMount(SalesReport, {
    vuetify,
    propsData: {
      businessId: 1
    },
    stubs: ["error-modal"],
    mocks: {
      ...globalStateMocks()
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


describe("getTransactions", () => {
  const generateResponse = () => {
    return {
      data: {
        totalAmount: 3141.59,
        totalTransactionCount: 13,
        transactions: [
          {
            date: "2020-05-03T09:32:13.324+13:00",
            amount: 27.18,
            transactionCount: 2
          }
        ]
      }
    }
  }
  test("API call args", async () => {
    await mountSalesReport();
    wrapper.setData({
      minDate: new Date("2020-01-01T23:59:00.000Z"),
      maxDate: new Date("2025-08-01T23:59:00.000Z"),
      granularity: "Year"
    });

    Api.getTransactions = jest.fn(() => Promise.resolve(generateResponse()));
    // called automatically on mount before we can set data to a testable value,
    // so need to call getTransaction manually

    await wrapper.vm.getTransactions();
    expect(Api.getTransactions.mock.calls[0]).toEqual([
      1, // business ID
      {
        startDate: "2020-01-01",
        endDate: "2025-08-01",
        transactionGranularity: "YEAR"
      }
    ]);
  });

  test("API props set", async () => {
    Api.getTransactions = jest.fn(() => Promise.resolve(generateResponse()));
    await mountSalesReport();

    await wrapper.vm.getTransactions();

    expect(wrapper.vm.totalAmount).toEqual(generateResponse().data.totalAmount);
    expect(wrapper.vm.totalTransactionCount).toEqual(generateResponse().data.totalTransactionCount);
    expect(wrapper.vm.transactions).toEqual(generateResponse().data.transactions);
  });

  test("Handles error", async () => {
    Api.getTransactions = jest.fn(() => Promise.reject({
      userFacingErrorMessage: "MSG"
    }));

    await mountSalesReport();

    await wrapper.vm.getTransactions();
    expect(wrapper.vm.apiErrorMessage).toBe("MSG");
  });
});

describe("getBusinessInformation", () => {
  test("successful API response", async () => {
    const business = {
      name: "BUS NAME",
      address: {
        country: "Fake Zealand"
      }
    };

    Api.businessProfile = jest.fn(() => Promise.resolve({
      data: business
    }));

    mountSalesReport(); // can't await lifecycle so need to call businessInformation again
    
    await wrapper.vm.getBusinessInformation();
    expect(wrapper.vm.business).toEqual(business);
  });

  
  test("successful API response", async () => {
    const business = {
      name: "BUS NAME",
      address: {
        country: "Fake Zealand"
      }
    };

    const currency = {
      code: "FNZ",
      name: "Fake Dollar",
      symbol: "¯\\_(ツ)_/¯"
    };

    Api.businessProfile = jest.fn(() => Promise.resolve({
      data: business
    }));

    mountSalesReport(); // can't await lifecycle so need to call businessInformation again
    wrapper.vm.$helper.getCurrencyForBusinessByCountry = jest.fn(() => currency);
    
    await wrapper.vm.getBusinessInformation();
    expect(wrapper.vm.currency).toEqual(currency);
    expect(wrapper.vm.$helper.getCurrencyForBusinessByCountry.mock.calls[0][0]).toEqual("Fake Zealand");
  });

  test("failed API response", async () => {
    Api.businessProfile = jest.fn(() => Promise.reject({
      userFacingErrorMessage: "Bla"
    }));

    mountSalesReport();
    await wrapper.vm.getBusinessInformation();
    expect(await wrapper.vm.getBusinessInformation()).toBe(false);
  });


});

const normalizeDateToStartOfDay = SalesReport.methods.normalizeDateToStartOfDay;
const normalizeDateToStartOfWeek = SalesReport.methods.normalizeDateToStartOfWeek.bind(SalesReport.methods);
const normalizeDateToStartOfMonth = SalesReport.methods.normalizeDateToStartOfMonth.bind(SalesReport.methods);
const normalizeDateToStartOfYear = SalesReport.methods.normalizeDateToStartOfYear.bind(SalesReport.methods);

describe("normalizeDateToStartOfDay", () => {
  test("12PM UTC", () => {
    expect(normalizeDateToStartOfDay(new Date("2020-01-01T12:00:00.000Z")).toISOString())
    .toEqual("2020-01-01T00:00:00.000Z")
  });
  
  test("Midnight UTC", () => {
    expect(normalizeDateToStartOfDay(new Date("2020-01-01T00:00:00.000Z")).toISOString())
    .toEqual("2020-01-01T00:00:00.000Z")
  });
  
  test("7AM +5", () => {
    expect(normalizeDateToStartOfDay(new Date("2020-01-01T07:00:00.000+05:00")).toISOString())
    .toEqual("2020-01-01T00:00:00.000Z")
  });
});

describe("normalizeDateToStartOfWeek", () => {
  test("Wednesday 12PM UTC", () => {
    expect(normalizeDateToStartOfWeek(new Date("2020-01-01T12:00:00.000Z")).toISOString())
    .toEqual("2019-12-29T00:00:00.000Z")
  });
});

describe("normalizeDateToStartOfMonth", () => {
  test("February 29th 12PM UTC", () => {
    expect(normalizeDateToStartOfMonth(new Date("2020-02-29T12:00:00.000Z")).toISOString())
    .toEqual("2020-02-01T00:00:00.000Z")
  });
});

describe("normalizeDateToStartOfYear", () => {
  test("February 29th 12PM UTC", () => {
    expect(normalizeDateToStartOfYear(new Date("2020-02-29T12:00:00.000Z")).toISOString())
    .toEqual("2020-01-01T00:00:00.000Z")
  });
});

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
      ...SalesReport.methods,
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
      }, {
        date: "2020-01-04T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
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

  test("week granularity, gap in middle", () => {
    const transactionData = [
      {
        date: "2020-01-01T11:30:20.000Z",
        transactionCount: 1,
        amount: 3.14
      }, {
        date: "2020-01-05T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
      }, {
        date: "2020-01-13T11:30:20.000Z",
        transactionCount: 3,
        amount: 2.71
      }, {
        date: "2020-01-19T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
      }
    ];
    runMethodAndCompare({
      minDate: normalizeDateToStartOfDay(new Date(transactionData[0].date)),
      maxDate: new Date("2020-01-19T00:00:00.000Z"),
      granularity: "Week"
    }, transactionData);
  });

  test("Month granularity, gap in middle", () => {
    const transactionData = [
      {
        date: "2020-01-07T11:30:20.000Z",
        transactionCount: 1,
        amount: 3.14
      }, {
        date: "2020-02-01T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
      }, {
        date: "2020-03-06T11:30:20.000Z",
        transactionCount: 3,
        amount: 2.71
      }, {
        date: "2020-04-01T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
      }
    ];
    runMethodAndCompare({
      minDate: normalizeDateToStartOfDay(new Date(transactionData[0].date)),
      maxDate: new Date("2020-04-01T00:00:00.000Z"),
      granularity: "Month"
    }, transactionData);
  });

  test("Year granularity, gap in middle", () => {
    const transactionData = [
      {
        date: "2020-01-01T11:30:20.000Z",
        transactionCount: 1,
        amount: 3.14
      }, {
        date: "2021-01-01T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
      }, {
        date: "2022-03-06T11:30:20.000Z",
        transactionCount: 3,
        amount: 2.71
      }, {
        date: "2023-01-01T00:00:00.000Z",
        transactionCount: 0,
        amount: 0
      }
    ];
    runMethodAndCompare({
      minDate: normalizeDateToStartOfDay(new Date(transactionData[0].date)),
      maxDate: new Date("2023-06-01T00:00:00.000Z"),
      granularity: "Year"
    }, transactionData);
  });

});
