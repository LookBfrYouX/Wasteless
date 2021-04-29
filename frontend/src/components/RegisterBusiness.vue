<template>
  <div
      class="w-100 d-flex justify-content-center signup-container gradient-background pb-4"
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
            <label>Description</label>
            <textarea
                v-model="description"
                class="form-control"
                maxlength="500"
                name="description"
                placeholder="Description"
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
const Api = require("../Api").default;
const AddressForm = require("./AddressForm").default;

export default {
  name: "Register Business",

  components: {
    "address-form": AddressForm,
  },

  data() {
    return {
      errorMessage: "",

      name: "",
      description: "",
      type: "",

      types: [
        "Accommodation and Food Services",
        "Retail Trade",
        "Charitable organisation",
        "Non-profit organisation",
      ],

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
      const {toString, ...addressObject} = newAddress;
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

    register: async function () {
      // TODO: Associate userId with the registered business account
      try {
        var response = await this.callApi({
          primaryAdministratorId: window.localStorage.getItem("userId"),
          name: this.name,
          description: this.description,
          address: {
            streetNumber: this.address.streetNumber,
            streetName: this.address.streetName,
            postcode: this.address.postcode,
            city: this.address.city,
            region: this.address.region,
            country: this.address.country
          }, // API stores address as homeAddress, not address
          businessType: this.type, // API stores the type as businessType not type
        });
      } catch (err) {
        // TODO: Need to handle errors here
        this.errorMessage = err.userFacingErrorMessage;
        return;
      }

      this.errorMessage = "";
      this.emailUsed = false;
      window.localStorage.setItem("businessId", response.data.businessId);
      // TODO: push the business page view, not the profile page
      this.$router.push({name: "Profile"});
    },
  },
};
</script>

<style scoped>
.signup-container > div {
  max-width: 50em;
}
</style>
