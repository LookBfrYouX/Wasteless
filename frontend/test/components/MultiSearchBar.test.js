import {mount} from "@vue/test-utils"

import Vuetify from 'vuetify'
import MultiSearchBar from "@/components/MultiSearchBar";

let vuetify = new Vuetify();

let wrapper;
let sortItems = [ // Sort options. Key is displayed and value is emitted when selection changes.
    {key: "Name A-Z", value: "name", isAscending: true},
    {key: "Name Z-A", value: "name", isAscending: false},
    {key: "Lowest RRP", value: "recommendedRetailPrice", isAscending: true},
    {key: "Highest RRP", value: "recommendedRetailPrice", isAscending: false},
];
let sortKeys = ['Business Name', 'Location'];

afterEach(() => wrapper.destroy());

describe("MultiSearchBar test", () => {
    test("Component mounts with correct data", () => {
        wrapper = mount(MultiSearchBar, {
            vuetify,
            propsData: {
                sortItems: sortItems,
                sortKeys: sortKeys,
            }
        });
        // Test initial state
        expect(wrapper.vm.$data.searchParams.searchString === "");
        expect(wrapper.vm.$data.searchParams.selectedKeys === []);
    });

    test("Correct event data is emmitted when changes are made", () => {
        wrapper = mount(MultiSearchBar, {
            vuetify,
            propsData: {
                sortItems: sortItems,
                sortKeys: sortKeys,
            }
        });
        wrapper.vm.$data.searchParams.searchString = "search";
        wrapper.vm.$data.searchParams.selectedKeys = ['Location'];
        wrapper.vm.doUpdate();
        expect(wrapper.emitted('multi-search-bar-update')[0][0].searchString).toEqual("search");
        expect(wrapper.emitted('multi-search-bar-update')[0][0].selectedKeys).toEqual(['Location']);
        wrapper.vm.sortUpdate("name", false);
        expect(wrapper.vm.$data.searchParams.sortBy).toEqual("name");
        expect(wrapper.vm.$data.searchParams.isAscending).toEqual(false);
    })

})