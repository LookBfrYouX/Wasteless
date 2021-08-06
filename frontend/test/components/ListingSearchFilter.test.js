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
    const today = new Date();
    let date = today.getFullYear() + "-" + today.getMonth() + "-" + today.getDay()

    await wrapper.setData({dates: [date]})

    expect(wrapper.vm.dateText).toContain("Before");
    expect(wrapper.vm.dateText).toContain(today.getFullYear());
  }),

  test("multipleDateInFuture", async () => {
    const today = new Date();
    let date = today.getFullYear()+1 + "-" + today.getMonth() + "-" + today.getDay()

    await wrapper.setData({dates: [date, date]})

    expect(wrapper.vm.dateText).toContain(today.getFullYear()+1)
  });
});




