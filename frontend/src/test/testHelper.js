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
    id: 100,
    administrators: [
      "string"
    ],
    primaryAdministratorId: 20,
    name: "Lumbridge General Store",
    description: "A one-stop shop for all your adventuring needs",
    address: {
      streetNumber: "3/24",
      streetName: "Ilam Road",
      city: "Christchurch",
      region: "Canterbury",
      country: "New Zealand",
      postcode: "90210"
    },
    businessType: "Accommodation and Food Services",
    created: "2020-07-14T14:52:00Z"
  }]
},
  actingAs: null,


}
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
      setActingAs: jest.fn((business) => GLOBAL_STATE.actingAs == business),
      deleteAuthUser: jest.fn()
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
