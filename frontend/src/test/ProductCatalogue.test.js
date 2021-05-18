import {shallowMount} from "@vue/test-utils";
import {globalStateMocks} from "./testHelper";
import ProductCatalogue from "../components/ProductCatalogue";


let wrapper;

const mockProduct = (id = 1) => {
  return ({
    id: id,
    name: 'beans',
    description: 'some beans',
    recommendedRetailPrice: 5.01,
    created: '2021-04-20T01:25:50.333Z'
  });
}

const mockProducts = (count) => {
  const products = [];
  for (let i = 1; i <= count; i++) {
    products.push(mockProduct(i));
  }
  return products;
}

window.alert = jest.fn();

beforeEach(() => {
  wrapper = shallowMount(ProductCatalogue, {
    propsData: {
      businessId: 1
    },
    mocks: {
      callApi: jest.fn(() => {
        return Promise.resolve({
          //whatever business Data

        });
      }), ...globalStateMocks()
    },
    stubs: ["router-link", "not-acting-as-business"]
  });
});

afterEach(() => wrapper.destroy());

describe("numPages", () => {
  test("No pages", () => {
    wrapper.setData({
      results: []
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([]);
  });

  test("One page, 9/10", () => {
    wrapper.setData({
      results: mockProducts(9),
      resultsPerPage: 10
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0]);
  });

  test("1 page, 10/10", () => {
    wrapper.setData({
      results: mockProducts(10),
      resultsPerPage: 10
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0]);
  });

  test("2 pages, 11/10", () => {
    wrapper.setData({
      results: mockProducts(11),
      resultsPerPage: 10
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0, 1]);
  });

  test("10 pages, 19/2", () => {
    wrapper.setData({
      results: mockProducts(19),
      resultsPerPage: 2
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0, 1, 2, 3, 4, 5, 6, 7, 8, 9]);
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
    results.fill(10, mockProduct()).map((val, i) => {
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
    const results = mockProducts(10);

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
    const results = mockProducts(10).map((val, i) => {
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
    const results = mockProducts(10).map((val, i) => {
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
    const results = mockProducts(10).map((val, i) => {
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
    const results = mockProducts(10).map((val, i) => {
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

  test("Sorting recommendedRetailPrice", () => {
    const results = mockProducts(10).map((val, i) => {
      val.recommendedRetailPrice += i;
      return val;
    });

    wrapper.setData({
      reversed: false,
      sortBy: 'recommendedRetailPrice',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
        results
    );
  });

  test("Reverse Sorting recommendedRetailPrice", () => {
    const results = mockProducts(10).map((val, i) => {
      val.recommendedRetailPrice += i;
      return val;
    });

    wrapper.setData({
      reversed: true,
      sortBy: 'recommendedRetailPrice',
      results: results
    });

    expect(wrapper.vm.sortedResults).toEqual(
        results.reverse()
    );
  });

  // TODO created should be treated as date first
  // test("Sorting Created", () => {
  //   const results = []
  //   const results = mockProducts(10).map((val, i) => {
  //     val.created += i;
  //     return val;
  //   });

  //   wrapper.setData({
  //     reversed: false,
  //     sortBy: 'created',
  //     results: results
  //   });

  //   expect(wrapper.vm.sortedResults).toEqual(
  //     results
  //   );
  // });

  // test("Reverse Sorting Created", () => {
  //   const results = []
  //   results.fill(10, {
  //     id: '1',
  //     name: 'beans',
  //     description: 'some beans',
  //     reccomendedRetailPrice: '5',
  //     created: '2021-04-20T01:25:50.333Z'
  //   }).map((val, i) => {
  //     val.created += i;
  //     return val;
  //   });

  //   wrapper.setData({
  //     reversed: true,
  //     sortBy: 'created',
  //     results: results
  //   });

  //   expect(wrapper.vm.sortedResults).toEqual(
  //     results.reverse()
  //   );
  // });
});
