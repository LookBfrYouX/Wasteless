import {shallowMount} from "@vue/test-utils";
import ListingSearchFilter from "@/components/ListingSearchFilter";

let wrapper;
jest.useFakeTimers();

beforeEach(() => {
  wrapper = shallowMount(ListingSearchFilter);
});
afterEach(() => wrapper.destroy());

describe("Date text", () => {
  test("singleDate", async () => {
    let [d, m, y] = new Date().toLocaleDateString("en-NZ").split("/");
    if (d.length < 2) {d = "0" + d}
    const date = y + "-" + m + "-" + d;

    await wrapper.setData({filterDates: [date]})

    expect(wrapper.vm.dateText).toContain("Before");
  });

  test("singleDateFuture", async () => {
    let [d, m, y] = new Date().toLocaleDateString("en-NZ").split("/");
    if (d.length < 2) {d = "0" + d}
    const date = parseInt(y)+1 + "-" + m + "-" + d;

    await wrapper.setData({filterDates: [date]})

    expect(wrapper.vm.dateText).toContain(parseInt(y)+1);
  });

  test("multipleDate", async () => {
    let [d, m, y] = new Date().toLocaleDateString("en-NZ").split("/");
    if (d.length < 2) {d = "0" + d}
    const date = y + "-" + m + "-" + d;

    await wrapper.setData({filterDates: [date, date]})

    expect(wrapper.vm.dateText).toContain("to");
  });

  test("multipleDateFuture", async () => {
    let [d, m, y] = new Date().toLocaleDateString("en-NZ").split("/");
    if (d.length < 2) {d = "0" + d}
    const date = parseInt(y)+1 + "-" + m + "-" + d;

    await wrapper.setData({filterDates: [date, date]})

    expect(2).toEqual(wrapper.vm.dateText.split(parseInt(y)+1).length - 1);
  });
});
