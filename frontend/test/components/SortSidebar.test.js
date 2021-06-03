import {mount} from "@vue/test-utils";
import SortSidebar from "@/components/SortSidebar.vue";

let wrapper;

beforeEach(() => {
  wrapper = mount(SortSidebar, {
    propsData: {
      sortOptions: ["A", "B", "C"].map(name => ({name})),
      currentSortOption: {name: "B", reversed: true},
      closeClicked: jest.fn(),
    }
  });
});

afterEach(() => wrapper.destroy());

describe("behaviour when element clicked", () => {
  test("different option clicked", async () => {
    wrapper.vm.sortByClicked({name: "A"});
    await wrapper.vm.$nextTick();
    const event = wrapper.emitted()["update:currentSortOption"];
    expect(event.length).toEqual(1);
    // For some reason, each emit returns an array. Take first element
    expect(event[0][0]).toEqual({
      name: "A",
      reversed: true // reversed at start, so stays reversed
    })
  });

  test("same option clicked", async () => {
    wrapper.vm.sortByClicked({name: "B"});
    await wrapper.vm.$nextTick();
    const event = wrapper.emitted()["update:currentSortOption"];
    expect(event.length).toEqual(1);
    expect(event[0][0]).toEqual({
      name: "B",
      reversed: false // reversed at start, so gets flipped
    })
  });
});
