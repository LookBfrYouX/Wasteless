import { shallowMount } from "@vue/test-utils";
import { globalStateMocks } from "#/testHelper";
import SortedPaginatedItemList from "@/components/SortedPaginatedItemList.vue";

let wrapper;

const standardItems = () => {
  return [
    {
      id: 5,
      name: "a"
    }, {
      id: 1,
      name: "d"
    }, {
      id: 3,
      name: "e"
    }, {
      id: 2,
      name: "c"
    }, {
      id: 4,
      name: "b"
    }
  ]
}

const standardSortOptions = () => {
  return [{
    name: "ID",
    // Sorts IDs ascending
    sortMethod: (a, b) => a.id - b.id
  }, {
    name: "Name",
    sortMethod: (a, b) => a.name == b.name? 0: (a.name > b.name? 1: -1)
  }]
}

const standardCurrentSortOption = () => ({...standardSortOptions()[0], reversed: false});

const standardResultsPerPage = 2;

/**
 * Mounts the SortedPaginatedItemList with some default or given props
 * @param {*} propsData props data; this will override defaults (IDs ascending)
 * @returns 
 */
const mountWithProps = (propsData = {}) => {
  wrapper = shallowMount(SortedPaginatedItemList, {
    propsData: {
      items: standardItems(),
      sortOptions: standardSortOptions(),
      currentSortOption: standardCurrentSortOption(),
      resultsPerPage: standardResultsPerPage,
      ...propsData
    },
    mocks: {
      ...globalStateMocks()
    },
    stubs: ["pagination", "sort-sidebar"]
  });

  return wrapper;
}

describe("Pagination", () => {
  test("one page sort by ID", async () => {
    await mountWithProps({
      resultsPerPage: 10
    });
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.itemsToDisplay.map(el => el.id)).toEqual([1, 2, 3, 4, 5]);
  });

  test("one page sort by ID reversed", async () => {
    await mountWithProps({
      resultsPerPage: 10,
      currentSortOption: {
        ...standardSortOptions()[0],
        reversed: true
      }
    });
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.itemsToDisplay.map(el => el.id)).toEqual([5, 4, 3, 2, 1]);
  });

  test("one page sort by name", async () => {
    await mountWithProps({
      resultsPerPage: 10,
      currentSortOption: standardSortOptions()[1]
    });
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.itemsToDisplay.map(el => el.name)).toEqual(["a", "b", "c", "d", "e"]);
  });

  test("page 1 of 3", async () => {
    await mountWithProps({
      resultsPerPage: 2
    });
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.itemsToDisplay.map(el => el.id)).toEqual([1, 2]);
  });

  test("page 2 of 3", async () => {
    await mountWithProps({
      resultsPerPage: 2
    });
    await wrapper.setData({ page: 2 });
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.itemsToDisplay.map(el => el.id)).toEqual([3, 4]);
  });

  test("page 3 of 3", async () => {
    await mountWithProps({
      resultsPerPage: 2
    });
    await wrapper.setData({ page: 3 });
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.itemsToDisplay.map(el => el.id)).toEqual([5]);
  });
});

describe("custom item identifier for list", () => {
  const consoleSpy = jest.spyOn(console, "error").mockImplementation(jest.fn());
  test("duplicated keys returning errors with custom function", async () => {
    // https://stackoverflow.com/questions/55515179/jest-how-to-test-that-console-error-is-called
    consoleSpy.mockClear();
    await mountWithProps({
      itemIdentifier: () => 1
    });
    await wrapper.vm.$nextTick();
    expect(consoleSpy).toHaveBeenCalled();
  });

  test("custom key not returning errors", async () => {
    // https://stackoverflow.com/questions/55515179/jest-how-to-test-that-console-error-is-called
    consoleSpy.mockClear();
    await mountWithProps({
      items: [standardItems(), { ...standardItems()[0], name: "asdf"}], // id duplicated but not name
      itemIdentifier: "name"
    });
    await wrapper.vm.$nextTick();
    expect(consoleSpy).toHaveBeenCalledTimes(0);
  });

  test("custom method not returning errors", async () => {
    // https://stackoverflow.com/questions/55515179/jest-how-to-test-that-console-error-is-called
    consoleSpy.mockClear();
    await mountWithProps({
      items: [standardItems(), { ...standardItems()[0], name: "asdf"}], // id duplicated but not name
      itemIdentifier: item => item.id
    });
    await wrapper.vm.$nextTick();
    expect(consoleSpy).toHaveBeenCalledTimes(0);
  });
});

afterEach(() => wrapper.destroy());
