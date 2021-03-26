<template>
  <div class="w-100 d-flex justify-content-center signup-container gradient-background pb-4">
    <div class="container">
      <form
        v-on:submit.prevent="register"
        class="slightly-transparent-inputs"
        autocomplete="on"
        method="POST"
      >
        <div class="row">
          <div class="col">
            <h1>Sign Up</h1>
          </div>
        </div>


        <div class="row">
          <div class="form-group required col-12 col-md-6">
            <label for="fname">First Name</label>
            <input
              class="form-control"
              type="text"
              name="fname"
              v-model="firstName"
              placeholder="First name"
              maxlength="30"
              autocomplete="given-name"
              required
            />
          </div>

          <div class="form-group col-12 col-md-6">
            <label for="mname">Middle Name</label>
            <input
              class="form-control"
              type="text"
              name="mname"
              v-model="middleName"
              placeholder="Middle name (optional)"
              autocomplete="additional-name"
              maxlength="30"
            />
          </div>

          <div class="form-group required col-12 col-md-6">
            <label for="lname">Last Name</label>
            <input
              class="form-control"
              type="text"
              name="lname"
              v-model="lastName"
              placeholder="Last name"
              maxlength="30"
              autocomplete="family-name"
              required
            />
          </div>

          <div class="form-group col-12 col-md-6">
            <label for="nickname">Nickname</label>
            <input
              class="form-control"
              type="text"
              name="nickname"
              placeholder="Nickname (optional)"
              autocomplete="nickname"
              v-model="nickname"
              maxlength="30"
            />
          </div>
        </div>


        <div class="row">
          <div class="col form-group required">
            <label for="email" ref="emailLabel">Email</label>
            <input
              v-bind:class="{'form-control': true, 'is-invalid': emailUsed }"
              type="email"
              name="email"
              v-model="email"
              placeholder="Email"
              maxlength="50"
              autocomplete="email"
              required
            />
            <div class="invalid-feedback">Your email address has already been registered</div>
          </div>
        </div>

        <div class="row">
          <div class="form-group required col-12 col-md-6">
              <label for="password" ref="passwordLabel">Password</label>
              <input
                class="form-control"
                type="password"
                name="password"
                v-model="password"
                placeholder="Password"
                minlength="8"
                maxlength="50"
                autocomplete="new-password"
                required
              />
            </div>

          <div class="form-group required col-12 col-md-6">
            <label for="confirmPassword">Confirm Password</label>
            <input
              v-bind:class="{'form-control': true, 'is-invalid': confirmPasswordWrong }"
              type="password"
              name="confirmPassword"
              v-model="confirmPassword"
              placeholder="Confirm password"
              minlength="8"
              maxlength="50"
              autocomplete="new-password"
              required
            />
            <div class="invalid-feedback">The passwords do not match</div>
          </div>
        </div>


        <div class="row">
          <div class="form-group required col-12">
            <label for="dateOfBirth">Date of Birth</label>
            <input
              class="form-control"
              type="date"
              name="dateOfBirth"
              v-model="dateOfBirth"
              placeholder="Date of birth"
              autocomplete="bday"
              required
            />
          </div>
        </div>

        <address-form v-bind:address="address" v-on:addressupdate="addressUpdate"/>

        <div class="form-row">
          <div class="col-12 col-md-3 mb-3">
            <label for="countryCode" ref="countryCodeLabel">Phone Country Code</label>
            <!-- Data list taken from https://github.com/etjossem/country-codes-html/blob/master/_country_codes.html (formatted to json) -->
            <select
              class="form-control"
              v-bind:class="{'is-invalid': countryCodeRequired }"
              name="countryCode"
              autocomplete="tel-country-code"
              v-model="countryCode"
            >
              <!---Add blank element to country code list so that user can choose not to enter phone-->
              <option
                v-for="code in [{value: '', name: ''}, ...countryCodes]"
                :value="code.value"
                :key="code.name"
              >
                {{ code.name }}
              </option>
            </select>
            <div class="invalid-feedback">
              Country code and phone number must both be blank or filled in
            </div>
          </div>
          <div class="col-12 col-md-9 mb-3">
            <label for="phoneNumber">Phone Number</label>
            <input
              class="form-control"
              v-bind:class="{'is-invalid': phoneRequired }"
              v-model="phoneNumber"
              type="tel"
              pattern='^\d{5,13}$'
              placeholder="Phone number"
              autocomplete="tel-national"
            />
            <div class="invalid-feedback">
              Country code and phone number must both be blank or filled in
            </div>
          </div>
        </div>


        <div class="row>">
          <div class="form-group col px-0">
            <label for="bio">Bio</label>
            <textarea
              class="form-control"
              type="text"
              rows="5"
              name="bio"
              placeholder="Bio"
              v-model="bio"
              maxlength="500"
              autocomplete="off"
            />
          </div>
        </div>

        <div class="row">
          <div class="col">
            <input class="btn btn-block btn-primary" type="submit" value="Register"/>
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
const Api = require("./../Api").default;
const AddressForm = require("./AddressForm").default;

import countryCodesJson from "./../assets/countryCodes.json"


export default {
  name: "signUpPage",

  components: {
    "address-form": AddressForm
  },

  data() {
    return {
      countryCodes: countryCodesJson,
      emailUsed: false, // If email address has already been registered
      confirmPasswordWrong: false, // If password and confirm password fields different

      phoneRequired: false, // If country code entered but not phone
      countryCodeRequired: false, // If phone entered but not country code

      errorMessage: "",

      firstName: "",
      middleName: "",
      lastName: "",
      nickname: "",

      dateOfBirth: null,

      email: "",

      password: "",
      confirmPassword: "",

      address: {
        addressLine1: "",
        addressLine2: "",
        postcode: "",
        city: "",
        region: "",
        country: "",
      },
      addressAsString: "",

      countryCode: null,

      phoneNumber: "",

      bio: "",
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
    callApi: function(data) {
      return Api.signUp(data);
    },

    register: async function () {
      this.phoneRequired = false;
      this.confirmPasswordWrong = false;
      this.countryCodeRequired = false;

      if (this.password != this.confirmPassword) {
        this.confirmPasswordWrong = true;
        this.errorMessage = "The passwords do not match";
        this.$refs.passwordLabel.scrollIntoView();
        // scroll into view puts the element at the top of the screen
        // Hence more user friendly to scroll to password instead of confirm password
        // (at least on mobile)
        return;
      }
      const phoneNumberEntered = this.phoneNumber.trim().length > 0;
      const countryCodeEntered = typeof this.countryCode == "number";
      if (phoneNumberEntered && !countryCodeEntered) {
        this.countryCodeRequired = true;
        this.errorMessage = "Country code and phone number must both be blank or filled in";
        this.$refs.countryCodeLabel.scrollIntoView();
        return;
      }


      if(!phoneNumberEntered && countryCodeEntered) {
        this.phoneRequired = true;
        this.errorMessage = "Country code and phone number must both be blank or filled in";
        this.$refs.countryCodeLabel.scrollIntoView();
        return;
      }

      const phoneNumber = (typeof this.countryCode == "number"?
        `+${this.countryCode} `: ""
        ) + this.phoneNumber.trim();

      try {
          var response = await this.callApi({
          firstName: this.firstName,
          middleName: this.middleName,
          lastName: this.lastName,
          nickname: this.nickname,
          email: this.email,
          password: this.password,
          dateOfBirth: this.dateOfBirth,
          homeAddress: this.addressAsString, // API stores address as homeAddress, not address
          phoneNumber: phoneNumber,
          bio: this.bio,
        });
      } catch(err) {
        if (err == undefined || err.response.status == 409) {
          this.emailUsed = true;
          this.errorMessage = "Your email has already been registered";
          this.$refs.emailLabel.scrollIntoView();
        }
        this.errorMessage = err.userFacingErrorMessage;
        return;
      }

      this.errorMessage = "";
      this.emailUsed = false;
      window.localStorage.setItem("userId", response.data.userId);
      this.$router.push({ name: "profile" });
    }
  }
}
</script>
