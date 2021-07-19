import {mount} from "@vue/test-utils"
import SimpleSortBar from "@/components/SimpleSortBar";

import Vue from 'vue'
import Vuetify from 'vuetify'

Vue.use(Vuetify);
let vuetify = new Vuetify();

let wrapper;

afterEach(() => wrapper.destroy());

describe("SimpleSortBar test", () => {
    test("Test value is returned with single valid key", () => {
        wrapper = mount(SimpleSortBar, {
            vuetify,
            propsData: {
                items: [{key: 'name', value: 'name-desc'}],
            },
        });

        wrapper.vm.sortChange('name');
        expect(wrapper.emitted().update[0]).toEqual(['name-desc']);
    })

    test("Test value is returned with multiple valid keys", () => {
        wrapper = mount(SimpleSortBar, {
            vuetify,
            propsData: {
                items: [
                    {key: 'Name A-Z', value: 'name-asc'},
                    {key: 'RRP Lowest', value: 'recommendedRetailPrice-asc'},
                    {key: 'Date Created', value: 'date-asc'}
                ],
            },
        });

        wrapper.vm.sortChange('RRP Lowest');
        wrapper.vm.sortChange('Date Created');
        expect(wrapper.emitted().update[0]).toEqual(['recommendedRetailPrice-asc']);
        expect(wrapper.emitted().update[1]).toEqual(['date-asc']);
    })

    test("Test nothing is returned with invalid key", () => {
        wrapper = mount(SimpleSortBar, {
            vuetify,
            propsData: {
                items: [{key: 'name', value: 'name-desc'}],
            },
        });

        wrapper.vm.sortChange('price');
        expect(wrapper.emitted().update).toBeUndefined();
    })
})
