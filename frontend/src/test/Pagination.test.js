import {mount} from "@vue/test-utils";
import Pagination from "./../components/Pagination.vue";

let wrapper;

afterEach(() => wrapper.destroy());

/**
 * Props to pass
 * @param props
 * @param expected expected array of page numbers in same order returned by `pages` property
 */
const pageNumberTester = async (props, expected) => {
  wrapper = await mount(Pagination, {
    propsData: {
      setPage: jest.fn(),
      ...props,
    }
  });

  expect(wrapper.vm.pages.map(el => el.pageNum)).toEqual(expected);
}

describe("page numbers", () => {
  test("one page", async () => {
    await pageNumberTester({
      current: 1,
      end: 1,
    }, [1]);
  });

  test("a few pages, in the middle", async () => {
    await pageNumberTester({
      current: 4,
      end: 10,
      numPreviousPagesToShow: 2,
      numNextPagesToShow: 2
    }, [3, 2, 3, 4, 5, 6, 5]); // start and end are '<' and '>': go to prev/next page
  });

  test("a few pages, at the start", async () => {
    await pageNumberTester({
      current: 1,
      end: 4,
      numPreviousPagesToShow: 2,
      numNextPagesToShow: 2
    }, [1, 2, 3, 2]);
  });

  test("a few pages, at the end", async () => {
    await pageNumberTester({
      current: 3,
      end: 3,
      numPreviousPagesToShow: 2,
      numNextPagesToShow: 2
    }, [2, 1, 2, 3]);
  });

  test("non-one start page", async () => {
    await pageNumberTester({
      start: 2,
      current: 3,
      end: 6,
      numPreviousPagesToShow: 2,
      numNextPagesToShow: 2
    }, [2, 2, 3, 4, 5, 4]);
  });
});

test("current page link disabled", async () => {
  wrapper = mount(Pagination, {
    propsData: {
      current: 3,
      end: 5,
      setPage: jest.fn()
    }
  });

  expect(wrapper.findAll("li.disabled").length).toBe(1); // only one link disabled
  expect(wrapper.find("li.disabled").text()).toBe("3"); // And the disabled one is the current page
});