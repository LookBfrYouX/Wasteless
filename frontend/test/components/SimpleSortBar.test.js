import {createLocalVue, mount} from "@vue/test-utils"
import SimpleSortBar from "@/components/SimpleSortBar";
import vuetify from "@/plugins/vuetify";

describe("SimpleSortBar test", () => {
    const localVue = createLocalVue();
    localVue.use(vuetify)
    test("just testing for now", () => {
        const wrapper = mount(SimpleSortBar, {
            propsData: {
                items: [{key: 'name', value: 'name-desc'}],
            }
        });

        wrapper.vm.sortChange('name');
        console.log(wrapper.emitted())
    })
})
