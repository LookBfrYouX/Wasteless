import {mount} from "@vue/test-utils"

import Vuetify from 'vuetify'

let vuetify = new Vuetify();

let wrapper;

afterEach(() => wrapper.destroy());

describe("MultiSearchBar test", () => {
    test("Component mounts with correct data", () => {
        wrapper = mount(MultiSearchBar, {
            vuetify,
            propsData: {

            }
        });
    })
})