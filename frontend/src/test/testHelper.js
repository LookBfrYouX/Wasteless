import { helper } from "./../helper";

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
        homeAddress: {
          city: "Christchurch",
          country: "New Zealand",
          postcode: "8041",
          region: "Canterbury",
          streetName: "Ilam Road",
          streetNumber: "53"
        },
        id: 1,
        name: "TestName",
        primaryAdministratorId: 4
      }
    ]
  },
  actingAs: null
};

export const globalStateMocks = () => {
  const $stateStore = {
    getters: {
      isAdmin: jest.fn(() => GLOBAL_STATE.authUser.role == "ROLE_ADMIN"),
      isLoggedIn: jest.fn(() => true),
      getAuthUser: jest.fn(() => GLOBAL_STATE.authUser),
      getActingAs: jest.fn(() => GLOBAL_STATE.actingAs)
    },
    actions: {
      makeAdmin: jest.fn(),
      revokeAdmin: jest.fn(),
      setAuthUser: jest.fn(),
      deleteAuthUser: jest.fn(),
      setActingAs: jest.fn(business => GLOBAL_STATE.actingAs = business),
      deleteActingAs: jest.fn(() => GLOBAL_STATE.actingAs = null)
    }
  }

  const $router = {
    push: jest.fn(),
    go: jest.fn()
  }

  const $helper = helper;

  return {
    $stateStore,
    $router,
    $helper
  }
}


