import {shallowMount} from "@vue/test-utils";
import {globalStateMocks} from "./testHelper";
import SearchResults from "../components/ProductCatalogue";

let wrapper;

window.alert = jest.fn();

beforeEach(() => {
  wrapper = shallowMount(SearchResults, {

    mocks: {callApi: jest.fn(() => {
      return Promise.resolve({
        //whatever business Data

      });
    }), ...globalStateMocks()},
  });
});

afterEach(() => wrapper.destroy());

describe("numPages", () => {
  test("No pages", () => {
    wrapper.setData({
      results: Array(0).fill({})
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([]);
  });

  test("One page", () => {
    wrapper.setData({
      results: Array(1).fill({})
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0]);
  });

  test("10 pages", () => {
    wrapper.setData({
      results: Array(80).fill({})
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0, 1, 2, 3, 4, 5, 6, 7]);
  });

  test("Two pages", () => {
    wrapper.setData({
      results: Array(11).fill({})
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0, 1]);
  });

  test("One pages", () => {
    wrapper.setData({
      results: Array(9).fill({})
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0]);
  });
});

describe("sortedResults", () => {
  test("No sorting criteria", () => {
    wrapper.setData({
      sortBy: null
    });

    expect(wrapper.vm.sortedResults).toEqual(wrapper.vm.results);
  });

  test("Sorting id", () => {
    const results = []
    results.fill(10, {
      id: '1',
      name: 'beans',
      description: 'some beans',
      reccomendedRetailPrice: '5',
      created: '2021-04-20T01:25:50.333Z'
    }).map((val, i) => {
      val.id += i;
      return val;
    });

    wrapper.setData({
      reversed: false,
      sortBy: 'id',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
      results
    );
  });

  test("Reverse Sorting id", () => {
    const results = []
    results.fill(10, {
      id: '1',
      name: 'beans',
      description: 'some beans',
      reccomendedRetailPrice: '5',
      created: '2021-04-20T01:25:50.333Z'
    }).map((val, i) => {
      val.id += i;
      return val;
    });

    wrapper.setData({
      reversed: true,
      sortBy: 'id',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
      results.reverse()
    );
  });
  test("Sorting name", () => {
    const results = []
    results.fill(10, {
      id: '1',
      name: 'beans',
      description: 'some beans',
      reccomendedRetailPrice: '5',
      created: '2021-04-20T01:25:50.333Z'
    }).map((val, i) => {
      val.name += i;
      return val;
    });

    wrapper.setData({
      reversed: false,
      sortBy: 'name',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
      results
    );
  });

  test("Reverse Sorting name", () => {
    const results = []
    results.fill(10, {
      id: '1',
      name: 'beans',
      description: 'some beans',
      reccomendedRetailPrice: '5',
      created: '2021-04-20T01:25:50.333Z'
    }).map((val, i) => {
      val.name += i;
      return val;
    });

    wrapper.setData({
      reversed: true,
      sortBy: 'name',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
      results.reverse()
    );
  });
  test("Sorting description", () => {
    const results = []
    results.fill(10, {
      id: '1',
      name: 'beans',
      description: 'some beans',
      reccomendedRetailPrice: '5',
      created: '2021-04-20T01:25:50.333Z'
    }).map((val, i) => {
      val.description += i;
      return val;
    });

    wrapper.setData({
      reversed: false,
      sortBy: 'description',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
      results
    );
  });

  test("Reverse Sorting description", () => {
    const results = []
    results.fill(10, {
      id: '1',
      name: 'beans',
      description: 'some beans',
      reccomendedRetailPrice: '5',
      created: '2021-04-20T01:25:50.333Z'
    }).map((val, i) => {
      val.description += i;
      return val;
    });

    wrapper.setData({
      reversed: true,
      sortBy: 'description',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
      results.reverse()
    );
  });

  test("Sorting reccomendedRetailPrice", () => {
    const results = []
    results.fill(10, {
      id: '1',
      name: 'beans',
      description: 'some beans',
      reccomendedRetailPrice: '5',
      created: '2021-04-20T01:25:50.333Z'
    }).map((val, i) => {
      val.name += i;
      return val;
    });

    wrapper.setData({
      reversed: false,
      sortBy: 'reccomendedRetailPrice',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
      results
    );
  });

  test("Reverse Sorting reccomendedRetailPrice", () => {
    const results = []
    results.fill(10, {
      id: '1',
      name: 'beans',
      description: 'some beans',
      reccomendedRetailPrice: '5',
      created: '2021-04-20T01:25:50.333Z'
    }).map((val, i) => {
      val.reccomendedRetailPrice += i;
      return val;
    });

    wrapper.setData({
      reversed: true,
      sortBy: 'reccomendedRetailPrice',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
      results.reverse()
    );
  });
  test("Sorting Created", () => {
    const results = []
    results.fill(10, {
      id: '1',
      name: 'beans',
      description: 'some beans',
      reccomendedRetailPrice: '5',
      created: '2021-04-20T01:25:50.333Z'
    }).map((val, i) => {
      val.created += i;
      return val;
    });

    wrapper.setData({
      reversed: false,
      sortBy: 'created',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
      results
    );
  });

  test("Reverse Sorting Created", () => {
    const results = []
    results.fill(10, {
      id: '1',
      name: 'beans',
      description: 'some beans',
      reccomendedRetailPrice: '5',
      created: '2021-04-20T01:25:50.333Z'
    }).map((val, i) => {
      val.created += i;
      return val;
    });

    wrapper.setData({
      reversed: true,
      sortBy: 'created',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
      results.reverse()
    );
  });
});
