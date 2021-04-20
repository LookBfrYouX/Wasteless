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
    businesses: [
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
  }
};

export const globalStateMocks = () => {
  const $stateStore = {
    getters: {
      isAdmin: jest.fn(() => GLOBAL_STATE.authUser.role == "ROLE_ADMIN"),
      isLoggedIn: jest.fn(() => true),
      getAuthUser: jest.fn(() => GLOBAL_STATE.authUser),
      getActingAs: jest.fn(() => null)
    },
    actions: {
      makeAdmin: jest.fn(),
      revokeAdmin: jest.fn(),
      setAuthUser: jest.fn(),
      deleteAuthUser: jest.fn(),
      setActingAs: jest.fn()
    }
  }

  const $router = {
    push: jest.fn(),
    go: jest.fn()
  }

  return {
    $stateStore,
    $router
  }
}


