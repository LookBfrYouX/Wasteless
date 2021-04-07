<template>
  <div
    class="w-100 d-flex justify-content-center signup-container gradient-background pb-4"
  >
    <div class="container">
      <form
        v-on:submit.prevent="register"
        class="slightly-transparent-inputs"
        method="POST"
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
              class="form-control"
              type="text"
              name="name"
              v-model="name"
              placeholder="Name"
              maxlength="30"
              required
            />
          </div>

          <div class="form-group required col-12 col-md-6">
            <label>Type</label>
            <select
              name="type"
              class="form-control"
              v-bind:class="{ 'is-invalid': typeRequired }"
              v-model="type"
            >
              <option
              v-for="code in types"
              :key="code.message"
              >
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
              class="form-control"
              type="text"
              rows="5"
              name="description"
              placeholder="Description"
              v-model="description"
              maxlength="500"
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

        <div class="row mt-2" v-if="errorMessage.length > 0">
          <div class="col">
            <p class="alert alert-warning">{{ errorMessage }}</p>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.signup-container > div {
  max-width: 50em;
}
</style>

<script>
const Api = require("../Api").default;
const AddressForm = require("./AddressForm").default;

export default {
  name: "registerBusiness",

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
    addressUpdate: function(newAddress) {
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

    register: async function () {
      try {
        var response = await this.callApi({
          name: this.name,
          type: this.type,
          homeAddress: this.address, // API stores address as homeAddress, not address
          description: this.description,
        });
      } catch (err) {
        // TODO: Need to handle errors here
        return;
      }

      this.errorMessage = "";
      this.emailUsed = false;
      window.localStorage.setItem("businessId", response.data.businessId);
      // TODO: push the business page view, not the profile page
      this.$router.push({ name: "profile" });
    },
  },
};
</script>
