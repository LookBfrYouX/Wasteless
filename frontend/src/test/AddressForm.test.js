import {shallowMount} from "@vue/test-utils";
import AddressForm from "../components/AddressForm";

jest.mock("./../Api");
const { Api } = require("./../Api");

let wrapper;

jest.useFakeTimers();

beforeEach(() => {
  wrapper = shallowMount(AddressForm, {
    propsData: {
      address: {
        streetNumber: "",
        streetName: "",
        city: "",
        region: "",
        postcode: "",
        country: "",
      },
      countryData: [
        {
          name: "COUNTRY",
          code: "AA",
        }, {
          name: "COUNTRY2",
          code: "BB"
        }
      ]
    },
    mocks: {},
    stubs: {},
    methods: {}
  });
});

afterEach(() => wrapper.destroy());

const standardAddress = () => {
  return {
    streetNumber: "NUM",
    streetName: "STREET",
    city: "C",
    region: "REGION",
    postcode: "P",
    country: "COUNTRY",
  };
}

const standardOSMAddress = () => {
  return {
    housenumber: "NUM", // streetNumber
    street: "STREET", // streetName
    county: "C", // city
    state: "REGION", // region
    postcode: "P",
    countrycode: "AA",
    country: "COUNTRY_OSM",
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
    expected.streetNumber = expected.streetName = "";
    // Should only return city and up

    expect(address).toEqual(expected);
    expect(toString()).toEqual(
        wrapper.vm.generateAddressStringFromAddressComponents(expected,
            "city"));
  });

  test("actually updates on address input", async () => {
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
      streetNumber: "",
      streetName: "",
      postcode: postcode,
      city: "",
      region: "",
      country: "",
    });

    expect(toString()).toEqual(postcode);

    jest.runAllTimers();

    expect(wrapper.vm.addressSuggestionsPipeline.mock.calls.length).toBe(1);
  });
});

// TODO fix test
// test("generateAddressSuggestions", () => {
//   let a = standardOSMAddress();
//   let b = standardOSMAddress();
//   b.housenumber = "BB";
//   let c = standardOSMAddress();
//   c.country = "COUNTRY2";
//
//   const name = "city";
//   wrapper.setData({
//     addressSuggestionsRaw: [a, b, c].map(addr => {
//       return {
//         type: "Feature",
//         properties: addr
//       }
//     }),
//     activeAddressInputName: name
//   });
//
//   expect(wrapper.vm.generateAddressSuggestions().map(addr => {
//     delete addr.toString; // already tested elsewhere
//     return addr;
//   })).toEqual(
//       // a same as b in terms of the string value, so only a or b should be given
//       [a, c].map(addr => {
//         return wrapper.vm.mapOSMPropertiesToAddressComponents(addr);
//       })
//   );
// });

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

  test("Unknown country code", () => {
    let addressOSM = standardOSMAddress();
    addressOSM.countrycode = "ZZ";
    let address = standardAddress();
    address.country = undefined;
    expect(wrapper.vm.mapOSMPropertiesToAddressComponents(addressOSM)).toEqual(
        address);
  });

  test("Street number missing", () => {
    let addressOSM = standardOSMAddress();
    delete addressOSM.housenumber;
    let address = standardAddress();
    delete address.streetNumber;

    expect(wrapper.vm.mapOSMPropertiesToAddressComponents(addressOSM)).toEqual(
        address);
  });

  test("Street name missing", () => {
    let addressOSM = standardOSMAddress();
    delete addressOSM.street;
    let address = standardAddress();
    delete address.streetName;

    expect(wrapper.vm.mapOSMPropertiesToAddressComponents(addressOSM)).toEqual(
        address);
  });
});

describe("generateAddressStringFromAddressComponents", () => {
  test("All sections", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        standardAddress()
    )).toEqual("NUM STREET, C, REGION, P, COUNTRY");
  });

  test("street number blank", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        {
          ...standardAddress(),
          streetNumber: ""
        }
    )).toEqual("STREET, C, REGION, P, COUNTRY");
  });

  test("street name blank", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        {
          ...standardAddress(),
          streetName: ""
        }
    )).toEqual("C, REGION, P, COUNTRY");
  });

  test("Blank section", () => {
    let address = standardAddress();
    address.streetName = "";
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        address,
    )).toEqual("C, REGION, P, COUNTRY");
  });

  test("Postcode and up", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        standardAddress(),
        "postcode"
    )).toEqual("P, COUNTRY");
  });

  test("Invalid section name", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        standardAddress(),
        "INVALID"
    )).toEqual("NUM STREET, C, REGION, P, COUNTRY");
  });

  test("Invalid section name + one undefined", () => {
    let address = standardAddress();
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        address,
        "INVALID"
    )).toEqual("NUM STREET, C, REGION, P, COUNTRY");
  });

  test("Invalid section name + one undefined + failOnErr", () => {
    let address = standardAddress();
    delete address.streetName;
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        address,
        "INVALID",
        true
    )).toEqual(undefined);
  });
});
