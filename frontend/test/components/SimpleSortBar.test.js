import {createLocalVue, mount} from "@vue/test-utils"
import SimpleSortBar from "@/components/SimpleSortBar";

import Vue from 'vue'
import Vuetify from 'vuetify'

Vue.use(Vuetify);
let vuetify = new Vuetify();

let wrapper;

afterEach(() => wrapper.destroy());

describe("SimpleSortBar test", () => {
    test("just testing for now", () => {
        wrapper = mount(SimpleSortBar, {
            vuetify,
            propsData: {
                items: [{key: 'name', value: 'name-desc'}],
            },
        });

        wrapper.vm.sortChange('name');
        console.log(wrapper.emitted())
    })
})
