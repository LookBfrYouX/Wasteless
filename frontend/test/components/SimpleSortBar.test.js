import {mount} from "@vue/test-utils"
import SimpleSortBar from "@/components/SimpleSortBar";

import Vuetify from 'vuetify'

let vuetify = new Vuetify();

let wrapper;

afterEach(() => wrapper.destroy());

describe("SimpleSortBar test", () => {
    test("Test value is returned with single valid key", () => {
        wrapper = mount(SimpleSortBar, {
            vuetify,
            propsData: {
                items: [{key: 'name', value: 'name', isAscending: true}],
            },
        });

        wrapper.vm.sortChange('name');
        expect(wrapper.emitted().update[0]).toEqual(['name', true]);
    })

    test("Test value is returned with multiple valid keys", () => {
        wrapper = mount(SimpleSortBar, {
            vuetify,
            propsData: {
                items: [
                    {key: 'Name A-Z', value: 'name', isAscending: true},
                    {key: 'RRP Lowest', value: 'recommendedRetailPrice', isAscending: true},
                    {key: 'Date Created', value: 'date', isAscending: true}
                ],
            },
        });

        wrapper.vm.sortChange('RRP Lowest');
        wrapper.vm.sortChange('Date Created');
        expect(wrapper.emitted().update[0]).toEqual(['recommendedRetailPrice', true]);
        expect(wrapper.emitted().update[1]).toEqual(['date', true]);
    })

    test("Test nothing is returned with invalid key", () => {
        wrapper = mount(SimpleSortBar, {
            vuetify,
            propsData: {
                items: [{key: 'name', value: 'name', isAscending: true}],
            },
        });

        wrapper.vm.sortChange('price');
        expect(wrapper.emitted().update).toBeUndefined();
    })
})
