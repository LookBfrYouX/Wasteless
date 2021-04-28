import {shallowMount} from "@vue/test-utils";
import AddressForm from "../components/AddressForm";
import jest, {beforeEach, afterEach, describe, test, expect} from "jest"

jest.mock("./../Api");
const { Api } = require("./../Api");

let wrapper;

jest.useFakeTimers();

beforeEach(() => {
  wrapper = shallowMount(AddressForm, {
    propsData: {
      address: {
        addressLine: "",
        postcode: "",
        city: "",
        state: "",
        country: "",
      }
    },
    mocks: {},
    stubs: {},
    methods: {}
  });
});

afterEach(() => wrapper.destroy());

const standardAddress = () => {
  return {
    addressLine: "NUM STREET",
    postcode: "P",
    city: "C",
    state: "STATE",
    country: "COUNTRY",
  };
}

const standardOSMAddress = () => {
  return {
    housenumber: "NUM",
    street: "STREET", //addressLine
    postcode: "P",
    county: "C", // city
    state: "STATE", // state
    country: "COUNTRY",
    // osm_id,
  };
}

describe("Full pipeline", () => {
  test("addressSuggestionsPipeline", async () => {
    const responseOsmObject = {
      type: "Feature",
      properties: standardOSMAddress()
    };

    Api._setMethod("addressSuggestions", () => {
      return Promise.resolve({
        features: [responseOsmObject]
      });
    });

    await wrapper.setProps({
      address: standardAddress()
    });
    jest.runAllTimers();
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.addressSuggestionsRaw).toEqual([responseOsmObject]);

    expect(wrapper.vm.addressSuggestions.length).toBe(1);
    // eslint-disable-next-line no-unused-vars
    const { toString, ...response } = wrapper.vm.addressSuggestions[0];
    expect(response).toEqual(standardAddress());
  });
});

describe("text entry", () => {
  test("props updated", async () => {
    wrapper.vm.addressSuggestionsPipeline = jest.fn();

    await wrapper.setProps({
      address: standardAddress()
    });

    jest.runAllTimers();

    expect(wrapper.vm.addressSuggestionsPipeline.mock.calls.length).toBe(1);
  });

  test("suggestionSelected", async () => {
    wrapper.vm.activeAddressInputName = "city";
    wrapper.vm.suggestionSelected(standardAddress());

    await wrapper.vm.$nextTick();

    const events = wrapper.emitted("addressupdate");
    expect(events.length).toBe(1);
    const {toString, ...address} = events[0][0];

    let expected = standardAddress();
    expected.addressLine = "";
    expected.postcode = "";

    expect(address).toEqual(expected);
    expect(toString()).toEqual(
        wrapper.vm.generateAddressStringFromAddressComponents(expected,
            "city"));
  });

  test("on address input", async () => {
    wrapper.vm.addressSuggestionsPipeline = jest.fn();

    const postcode = "POSTCODE";
    wrapper.setData({
      activeAddressInputName: "postcode"
    });

    wrapper.vm.onAddressInput(postcode);

    await wrapper.vm.$nextTick();

    const events = wrapper.emitted("addressupdate");
    expect(events.length).toBe(1);
    const {toString, ...address} = events[0][0];

    expect(address).toEqual({
      addressLine: "",
      postcode: postcode,
      city: "",
      state: "",
      country: "",
    });

    expect(toString()).toEqual(postcode);

    jest.runAllTimers();

    expect(wrapper.vm.addressSuggestionsPipeline.mock.calls.length).toBe(1);
  });
});

test("generateAddressSuggestions", () => {
  let a = standardOSMAddress();
  let b = standardOSMAddress();
  b.housenumber = "DIFF";
  let c = standardOSMAddress();
  c.country = "DIFF COUNTRY";

  const name = "postcode";
  wrapper.setData({
    addressSuggestionsRaw: [a, b, c].map(addr => {
      return {
        type: "Feature",
        properties: addr
      }
    }),
    activeAddressInputName: name
  });

  expect(wrapper.vm.generateAddressSuggestions().map(addr => {
    delete addr.toString; // already tested elsewhere
    return addr;
  })).toEqual(
      // a same as b in terms of the string value, so only a or b should be given
      [b, c].map(addr => {
        return wrapper.vm.mapOSMPropertiesToAddressComponents(addr);
      })
  );
});

describe("mapOSMPropertiesToAddressComponents", () => {
  test("Standard", () => {
    expect(wrapper.vm.mapOSMPropertiesToAddressComponents(standardOSMAddress()))
    .toEqual(standardAddress())
  });

  test("Missing field", () => {
    let addressOSM = standardOSMAddress();
    delete addressOSM.county;
    let address = standardAddress();
    delete address.city;

    expect(wrapper.vm.mapOSMPropertiesToAddressComponents(addressOSM)).toEqual(
        address);
  });

  test("Street number missing", () => {
    let addressOSM = standardOSMAddress();
    delete addressOSM.housenumber;
    let address = standardAddress();
    address.addressLine = addressOSM.street;

    expect(wrapper.vm.mapOSMPropertiesToAddressComponents(addressOSM)).toEqual(
        address);
  });

  test("Street name missing", () => {
    let addressOSM = standardOSMAddress();
    delete addressOSM.street;
    let address = standardAddress();
    delete address.addressLine;

    expect(wrapper.vm.mapOSMPropertiesToAddressComponents(addressOSM)).toEqual(
        address);
  });
});

describe("generateAddressStringFromAddressComponents", () => {
  test("All sections", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        standardAddress()
    )).toEqual("NUM STREET, P, C, STATE, COUNTRY");
  });

  test("Blank section", () => {
    let address = standardAddress();
    address.addressLine = "  ";
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        address,
    )).toEqual("P, C, STATE, COUNTRY");
  });

  test("Postcode and up", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        standardAddress(),
        "postcode"
    )).toEqual("P, C, STATE, COUNTRY");
  });

  test("Invalid section name", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        standardAddress(),
        "INVALID"
    )).toEqual("NUM STREET, P, C, STATE, COUNTRY");
  });

  test("Invalid section name + one undefined", () => {
    let address = standardAddress();
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        address,
        "INVALID"
    )).toEqual("NUM STREET, P, C, STATE, COUNTRY");
  });

  test("Invalid section name + one undefined + failOnErr", () => {
    let address = standardAddress();
    delete address.addressLine;
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        address,
        "INVALID",
        true
    )).toEqual(undefined);
  });
});
