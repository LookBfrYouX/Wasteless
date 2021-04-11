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
              class="form-control"
              v-bind:class="{'is-invalid': emailErrorMessage !== null }"
              type="email"
              name="email"
              v-model="email"
              placeholder="Email"
              maxlength="50"
              autocomplete="email"
              required
            />
            <div class="invalid-feedback">{{ emailErrorMessage }}</div>
          </div>
        </div>

        <div class="row">
          <div class="form-group required col-12 col-md-6 mb-0">
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
                pattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\S+$).{8,}$"
                title="The password must be at least 8 characters long and contain lowercase, uppercase and number characters"
                required
              />
              <!-- ENSURE BACKEND PASSWORD REGEXP ALSO UPDATED -->
          </div>
          <div class="form-group col-12 order-md-3">
            <small class="form-text">
              Your password must be at least 8 characters long and contain lowercase, uppercase and number characters. It may not contain spaces
            </small>
          </div>
          <div class="form-group required col-12 col-md-6 mb-0">
            <label for="confirmPassword">Confirm Password</label>
            <input
              class="form-control"
              v-bind:class="{'is-invalid': confirmPasswordErrorMessage !== null }"
              type="password"
              name="confirmPassword"
              v-model="confirmPassword"
              placeholder="Confirm password"
              minlength="8"
              maxlength="50"
              autocomplete="new-password"
              required
            />
            <div class="invalid-feedback">{{confirmPasswordErrorMessage}}</div>
          </div>
        </div>


        <div class="row">
          <div class="form-group required col-12">
            <label for="dateOfBirth" ref="dateOfBirthLabel">Date of Birth</label>
            <input
              class="form-control"
              v-bind:class="{ 'is-invalid': dateOfBirthErrorMessage !== null }"
              type="date"
              name="dateOfBirth"
              v-model="dateOfBirth"
              placeholder="Date of birth"
              autocomplete="bday"
              required
            />
            <div class="invalid-feedback">{{ dateOfBirthErrorMessage }}</div>
          </div>
        </div>

        <address-form v-bind:address="address" v-on:addressupdate="addressUpdate"/>

        <div class="form-row">
          <div class="col-12 col-md-3 mb-3">
            <label for="countryCode" ref="countryCodeLabel">Phone Country Code</label>
            <!-- Data list taken from https://github.com/etjossem/country-codes-html/blob/master/_country_codes.html (formatted to json) -->
            <select
              class="form-control"
              v-bind:class="{ 'is-invalid': countryCodeErrorMessage !== null }"
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
            <div class="invalid-feedback">{{ countryCodeErrorMessage }}</div>
          </div>
          <div class="col-12 col-md-9 mb-3">
            <label for="phoneNumber">Phone Number</label>
            <input
              class="form-control"
              v-bind:class="{'is-invalid': phoneErrorMessage !== null }"
              v-model="phoneNumber"
              type="tel"
              pattern='^\d{5,13}$'
              placeholder="Phone number"
              autocomplete="tel-national"
            />
            <div class="invalid-feedback">{{ phoneErrorMessage }}</div>
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

import countryCodesJson from "./../assets/countryCodes.json";


export default {
  name: "signUpPage",

  components: {
    "address-form": AddressForm
  },

  data() {
    return {
      countryCodes: countryCodesJson,
      emailErrorMessage: null, // If email address has already been registered
      confirmPasswordErrorMessage: null, // If password and confirm password fields different
      dateOfBirthErrorMessage: null, // too young etc.
      // Safari desktop doesn't support type date, so need manual checking

      phoneErrorMessage: null, // If country code entered but not phone
      countryCodeErrorMessage: null, // If phone entered but not country code

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
        streetNumber: "",
        streetName: "",
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
    /**
     * Method which is called when update address event occurs on address form component
     */
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

    /**
     * Validates date of birth, returning error message if invalid
     * @param{string} dateOfBirth
     * @param{Date} currentDate. Uses Date.now if not given
     * @return {string|null} returns null if there is no error
     */
    validateDateOfBirth: function(dateOfBirth, currentDate = undefined) {
      if (currentDate == undefined) currentDate = new Date(Date.now());
      const MIN_AGE = 13;
      const regexp = /(\d{4})-(\d{2})-(\d{2})/;
      const result = regexp.exec(dateOfBirth);
      if (result === null) return "Date of birth must be in 'YYYY-MM-DD' format";

      const year = parseInt(result[1], 10);
      const monthIndex = parseInt(result[2], 10) - 1; // month: JS uses zero indexing
      const day = parseInt(result[3], 10);

      const date = new Date(year, monthIndex, day);

      // Check if date exists e.g. 2020-15-54
      if (date.getFullYear() != year || date.getMonth() != monthIndex || date.getDate() != day) {
        return "Date of birth given bad date";
      }


      const yearDelta = currentDate.getFullYear() - year;
      if (yearDelta > MIN_AGE) return null; // More than 13 years old
      if (yearDelta == MIN_AGE) {
        const monthDelta = currentDate.getMonth() - monthIndex;
        if (monthDelta > 0) return null;
        if (monthDelta == 0) {
          const dayDelta = currentDate.getDate() - day;
          if (dayDelta >= 0) return null;
        }
      }

      return "You must be 13 years or older to sign up";
    },

    /**
     * Function responsible for registration pipeline, from when register button is
     * clicked to redirect
     */
    register: async function () {
      this.phoneErrorMessage = null;
      this.countryCodeErrorMessage = null;
      this.confirmPasswordErrorMessage = null;
      this.dateOfBirthErrorMessage = null;
      this.emailErrorMessage = null;

      if (this.password != this.confirmPassword) {
        this.errorMessage = this.confirmPasswordErrorMessage = "The passwords do not match";
        this.$refs.passwordLabel.scrollIntoView();
        // scroll into view puts the element at the top of the screen
        // Hence more user friendly to scroll to password instead of confirm password
        // (at least on mobile)
        return;
      }
      const phoneNumberEntered = this.phoneNumber.trim().length > 0;
      const countryCodeEntered = typeof this.countryCode == "number";
      if (phoneNumberEntered && !countryCodeEntered) {
        this.errorMessage = this.countryCodeErrorMessage = "Country code and phone number must both be blank or filled in";
        this.$refs.countryCodeLabel.scrollIntoView();
        return;
      }

      if(!phoneNumberEntered && countryCodeEntered) {
        this.errorMessage = this.phoneErrorMessage = "Country code and phone number must both be blank or filled in";
        this.$refs.countryCodeLabel.scrollIntoView();
        return;
      }

      const phoneNumber = (typeof this.countryCode == "number"?
        `+${this.countryCode} `: ""
        ) + this.phoneNumber.trim();

      const dateOfBirth = this.dateOfBirth === null? null: this.dateOfBirth.trim();
      if (this.validateDateOfBirth(dateOfBirth) !== null) {
        this.dateOfBirthErrorMessage = this.errorMessage = this.validateDateOfBirth(dateOfBirth);
        this.$refs.dateOfBirthLabel.scrollIntoView();
        return;
      }

      try {
          var response = await this.callApi({
          firstName: this.firstName,
          middleName: this.middleName,
          lastName: this.lastName,
          nickname: this.nickname,
          email: this.email,
          password: this.password,
          dateOfBirth: dateOfBirth,
          homeAddress: this.address, // API stores address as homeAddress, not address
          phoneNumber: phoneNumber,
          bio: this.bio,
        });
      } catch(err) {
        if (err.status === 409) {
          this.emailErrorMessage = this.errorMessage = "Your email has already been registered";
          this.$refs.emailLabel.scrollIntoView();
        }
        this.errorMessage = err.userFacingErrorMessage;
        return;
      }

      this.errorMessage = "";
      this.emailErrorMessage = null;
      this.confirmPasswordErrorMessage = null;
      this.dateOfBirthErrorMessage = null;

      // TODO: Below
      console.warn("TODO SIGN UP SHOULD RETURN FULL USER PROFILE; USE THIS TO SET AUTHUSER");
      window.localStorage.setItem("userId", response.data.userId);
      this.$router.push({ name: "profile" });
    }
  }
}
</script>
