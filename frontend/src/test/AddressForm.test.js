jest.mock("./../Api");
const { Api } = require("./../Api");


import {shallowMount} from "@vue/test-utils";
import AddressForm from "../components/AddressForm";

Api._setCountryCodes(() => {
  return Promise.resolve({
    data: {
      "AA": "COUNTRY",
      "BB": "COUNTRY2"
    }
  });
});

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
    countrycode: "AA",
    country: "COUNTRY_OSM",
    // osm_id,
  };
}

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
  c.countrycode = "BB";
  c.country = "COUNTRY2";

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

  test("Unknown country code", () => {
    let addressOSM = standardOSMAddress();
    addressOSM.countrycode = "ZZ";
    let address = standardAddress();
    address.country = undefined;
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
