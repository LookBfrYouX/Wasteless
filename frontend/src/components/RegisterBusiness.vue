<template>
  <div
    class="w-100 d-flex justify-content-center register-business-container gradient-background pb-4"
  >
    <div class="container">
      <form
        class="slightly-transparent-inputs"
        method="POST"
        v-on:submit.prevent="register"
      >
        <div class="row">
          <div class="col">
            <h1>Register Business</h1>
          </div>
        </div>

        <div class="row">
          <div class="form-group required col-12 col-md-6">
            <label>Name</label>
            <input
              v-model="name"
              class="form-control"
              maxlength="30"
              name="name"
              placeholder="Name"
              required
              type="text"
            />
          </div>

          <div class="form-group required col-12 col-md-6">
            <label>Type</label>
            <select
              v-model="type"
              class="form-control"
              name="type"
              required
              v-bind:class="{ 'is-invalid': typeRequired }"
            >
              <option v-for="code in types" :key="code.message">
                {{ code }}
              </option>
            </select>
          </div>
        </div>

        <address-form
          v-bind:address="address"
          v-on:addressupdate="addressUpdate"
        />

        <div class="row>">
          <div class="form-group col px-0">
            <label for="description">Description</label>
            <textarea
              v-model="description"
              class="form-control"
              maxlength="500"
              name="description"
              placeholder="Description"
              id="description"
              rows="5"
              type="text"
            />
          </div>
        </div>

        <div class="row">
          <div class="col">
            <input
              class="btn btn-block btn-primary"
              type="submit"
              value="Register"
            />
          </div>
        </div>

        <div v-if="errorMessage.length > 0" class="row mt-2">
          <div class="col">
            <p class="alert alert-warning">{{ errorMessage }}</p>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
const { Api } = require("./../Api.js");
const AddressForm = require("./AddressForm").default;

export default {
  name: "RegisterBusiness",

  components: {
    "address-form": AddressForm,
  },

  data() {
    return {
      errorMessage: "",
      name: "",
      description: "",
      type: "",

      types: this.$constants.BUSINESSES.TYPES,

      typeRequired: false, // If phone entered but not country code

      address: {
        streetNumber: "",
        streetName: "",
        postcode: "",
        city: "",
        region: "",
        country: "",
      },
      addressAsString: "",
    };
  },
  methods: {
    addressUpdate: function (newAddress) {
      const { toString, ...addressObject } = newAddress;
      // Don't want the toString method to be part of the address, so remove it
      this.addressAsString = toString();
      this.address = addressObject;
    },

    /**
     * Wrapper which simply calls the sign up method of the api
     */
    callApi: function (data) {
      return Api.registerBusiness(data);
    },

    /**
     * Calls the API to create business information
     * Returns a promise, not a response
     */
    register: async function () {
      const user = this.$stateStore.getters.getAuthUser();
      let business = {
        primaryAdministratorId: user.id,
        name: this.name,
        description: this.description,
        address: {
          streetNumber: this.address.streetNumber,
          streetName: this.address.streetName,
          postcode: this.address.postcode,
          city: this.address.city,
          region: this.address.region,
          country: this.address.country,
        },
        businessType: this.type, // API stores the type as businessType not type
      }

      var response = await this.callApi(business);

      // When setting acting as, business ID is stored and it gets the business from
      // the authUser object. Hence the new business must be inserted into the authUser.
      // Need to set id and created - guess the latter as it will be updated later when
      // going to the business profile page
      business.id = response.data.businessId;
      business.created = new Date().toISOString(); // temporary created value

      user.businessesAdministered.push(business);
      this.$stateStore.actions.setAuthUser(user); // Add business to state store
      this.$stateStore.actions.setActingAs(business.id);
      await this.$router.push({
        name: "businessProfile",
        params: {
          businessId: business.id
        },
      });
    },
  },
};
</script>

<style scoped>
.register-business-container > div {
  max-width: 50em;
}
</style>
