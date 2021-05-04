import { helper } from "./../helper";
import { constants } from "./../constants";
import { store } from "./../store";

export const GLOBAL_STATE = {
  authUser: {
    firstName: "firstName",
    middleName: "middleName",
    lastName: "lastName",
    nickname: "nickname",
    role: "ROLE_ADMIN",
    email: "email@example.com",
    dateOfBirth: "2000-01-01",
    homeAddress: {
      streetNumber: "streetNumber",
      streetName: "streetName",
      postcode: "postcode",
      city: "city",
      region: "region",
      country: "country",
    },
    phoneNumber: "phoneNumber",
    bio: "bio",
    businessesAdministered: [
      {
        businessType: "Retail Trade",
        created: "2020-07-14 14:32:00.0",
        description: "A Good business",
        address: {
          city: "Christchurch",
          country: "New Zealand",
          postcode: "8041",
          region: "Canterbury",
          streetName: "Ilam Road",
          streetNumber: "53"
        },
        id: 1,
        name: "TestBusinessName",
        primaryAdministratorId: 4
      }
    ]
  },
};


export const globalStateMocks = () => {
  store.actions.setAuthUser(GLOBAL_STATE.authUser);
  store.actions.setActingAs(GLOBAL_STATE.authUser.businessesAdministered[0]);

  const $router = {
    push: jest.fn(),
    go: jest.fn()
  }

  const $route = {
    name: "TEST_ROUTE",
    params: {}
  };

  const $helper = helper;
  const $constants = constants;

  return {
    $stateStore: store,
    $router,
    $route,
    $helper,
    $constants
  }
}
