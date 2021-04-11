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
    bio: "bio"
  }
};

export const globalStateMocks = () => {
  const $stateStore = {
    getters: {
      isAdmin: jest.fn(() => GLOBAL_STATE.authUser.role == "ROLE_ADMIN"),
      isLoggedIn: jest.fn(() => true),
      getAuthUser: jest.fn(() => GLOBAL_STATE)
    },
    setters: {
      makeAdmin: jest.fn(),
      revokeAdmin: jest.fn()
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


