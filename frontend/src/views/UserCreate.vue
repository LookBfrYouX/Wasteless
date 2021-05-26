<template>
  <div class="w-100 d-flex justify-content-center user-create-container gradient-background pb-4">
    <div class="container">
      <form
          autocomplete="on"
          class="slightly-transparent-inputs"
          method="POST"
          v-on:submit.prevent="UserCreate"
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
                id="fname"
                v-model="firstName"
                autocomplete="given-name"
                class="form-control"
                maxlength="30"
                name="fname"
                placeholder="First name"
                required
                type="text"
            />
          </div>

          <div class="form-group col-12 col-md-6">
            <label for="mname">Middle Name</label>
            <input
                id="mname"
                v-model="middleName"
                autocomplete="additional-name"
                class="form-control"
                maxlength="30"
                name="mname"
                placeholder="Middle name (optional)"
                type="text"
            />
          </div>

          <div class="form-group required col-12 col-md-6">
            <label for="lname">Last Name</label>
            <input
                id="lname"
                v-model="lastName"
                autocomplete="family-name"
                class="form-control"
                maxlength="30"
                name="lname"
                placeholder="Last name"
                required
                type="text"
            />
          </div>

          <div class="form-group col-12 col-md-6">
            <label for="nickname">Nickname</label>
            <input
                id="nickname"
                v-model="nickname"
                autocomplete="nickname"
                class="form-control"
                maxlength="30"
                name="nickname"
                placeholder="Nickname (optional)"
                type="text"
            />
          </div>
        </div>


        <div class="row">
          <div class="col form-group required">
            <label ref="emailLabel" for="email">Email</label>
            <input
                id="email"
                v-model="email"
                autocomplete="email"
                class="form-control"
                maxlength="50"
                name="email"
                pattern="^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$"
                placeholder="Email"
                required
                title="You must enter a valid email address. Dotless domains are not supported"
                type="email"
                v-bind:class="{'is-invalid': emailErrorMessage !== null }"
            />
            <!-- regexp from https://html.spec.whatwg.org/multipage/input.html#e-mail-state-(type%3Demail), but modified to disallow dot-less domains -->
            <div class="invalid-feedback">{{ emailErrorMessage }}</div>
          </div>
        </div>

        <div class="row">
          <div class="form-group required col-12 col-md-6 mb-0">
            <label ref="passwordLabel" for="password">Password</label>
            <input
                id="password"
                v-model="password"
                autocomplete="new-password"
                class="form-control"
                maxlength="50"
                minlength="8"
                name="password"
                pattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\S+$).{8,}$"
                placeholder="Password"
                required
                title="The password must be at least 8 characters long and contain lowercase, uppercase and number characters"
                type="password"
            />
            <!-- ENSURE BACKEND PASSWORD REGEXP ALSO UPDATED -->
          </div>
          <div class="form-group col-12 order-md-3">
            <small class="form-text">
              Your password must be at least 8 characters long and contain lowercase, uppercase and
              number characters. It may not contain spaces
            </small>
          </div>
          <div class="form-group required col-12 col-md-6 mb-md-0">
            <label for="confirmPassword">Confirm Password</label>
            <input
                id="confirmPassword"
                v-model="confirmPassword"
                autocomplete="new-password"
                class="form-control"
                maxlength="50"
                minlength="8"
                name="confirmPassword"
                placeholder="Confirm password"
                required
                type="password"
                v-bind:class="{'is-invalid': confirmPasswordErrorMessage !== null }"
            />
            <div class="invalid-feedback">{{ confirmPasswordErrorMessage }}</div>
          </div>
        </div>


        <div class="row">
          <div class="form-group required col-12">
            <label ref="dateOfBirthLabel" for="dateOfBirth">Date of Birth</label>
            <input
                id="dateOfBirth"
                v-model="dateOfBirth"
                autocomplete="bday"
                class="form-control"
                name="dateOfBirth"
                placeholder="Date of birth"
                v-bind:min="oldestBirthdateAsString"
                v-bind:max="youngestBirthdateAsString"
                required
                type="date"
                v-bind:class="{ 'is-invalid': dateOfBirthErrorMessage !== null }"
            />
            <div class="invalid-feedback">{{ dateOfBirthErrorMessage }}</div>
          </div>
        </div>

        <address-form v-bind:address="address" v-on:addressupdate="addressUpdate"/>

        <div class="form-row">
          <div class="col-12 col-md-3 mb-3">
            <label ref="countryCodeLabel" for="countryCode">Phone Country Code</label>
            <!-- Data list taken from https://github.com/etjossem/country-codes-html/blob/master/_country_codes.html (formatted to json) -->
            <select
                id="countryCode"
                v-model="countryCode"
                autocomplete="tel-country-code"
                class="form-control"
                name="countryCode"
                v-bind:class="{ 'is-invalid': countryCodeErrorMessage !== null }"
            >
              <!---Add blank element to country code list so that user can choose not to enter phone-->
              <option value=""></option>
              <option
                  v-for="country in countryData"
                  v-bind:key="country.code"
                  v-bind:value="country.phoneExtensionCode"
              >
                {{ country.name }} (+{{ country.phoneExtensionCode }})
              </option>
            </select>
            <div class="invalid-feedback">{{ countryCodeErrorMessage }}</div>
          </div>
          <div class="col-12 col-md-9 mb-3">
            <label for="phoneNumber">Phone Number</label>
            <input
                id="phoneNumber"
                v-model="phoneNumber"
                autocomplete="tel-national"
                class="form-control"
                name="phoneNumber"
                placeholder="Phone number"
                type="text"
                maxlength="20"
                v-bind:class="{'is-invalid': phoneErrorMessage !== null }"
            />
            <div class="invalid-feedback">{{ phoneErrorMessage }}</div>
          </div>
        </div>


        <div class="row>">
          <div class="form-group col px-0">
            <label for="bio">Bio</label>
            <textarea
                id="bio"
                v-model="bio"
                autocomplete="off"
                class="form-control"
                maxlength="500"
                name="bio"
                placeholder="Bio"
                rows="5"
                type="text"
            />
          </div>
        </div>

        <div class="row">
          <div class="col">
            <input class="btn btn-block btn-primary" type="submit" value="Sign Up"/>
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

<style scoped>
.user-create-container > div {
  max-width: 50em;
}
</style>

<script>
const {Api} = require("../Api.js");
const AddressForm = require("../components/AddressForm").default;
const countryData = require("../assets/countryData.json");

export default {
  name: "UserCreate",

  components: {
    "address-form": AddressForm
  },

  props: {
    /**
     * Current date. used for date of birth validation
     */
    currentDate: {
      required: false,
      type: Date,
      default: () => new Date()
    }
  },

  data() {
    return {
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
        suburb: "",
        postcode: "",
        city: "",
        region: "",
        country: "",
      },
      addressAsString: "",

      countryCode: null,

      phoneNumber: "",

      bio: "",

      countryData
    };
  },

  computed: {
    youngestBirthdate() {
      return new Date(this.currentDate.getFullYear() -  this.$constants.SIGN_UP.MIN_AGE, this.currentDate.getMonth(), this.currentDate.getDate());
    },

    youngestBirthdateAsString() {
      const date = this.youngestBirthdate;
      return this.$helper.toYyyyMmDdString(date.getFullYear(), date.getMonth() + 1, date.getDate());
    },

    oldestBirthDate() {
      return new Date(this.currentDate.getFullYear() - this.$constants.SIGN_UP.MAX_AGE, this.currentDate.getMonth(), this.currentDate.getDate());
    },

    oldestBirthdateAsString() {
      const date = this.oldestBirthDate;
      return this.$helper.toYyyyMmDdString(date.getFullYear(), date.getMonth() + 1, date.getDate());
    }
  },

  methods: {
    /**
     * Method which is called when update address event occurs on address form component
     */
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
      return Api.UserCreate(data);
    },

    /**
     * Validates date of birth, returning error message if invalid
     * @param{string} dateOfBirth
     * @return {string|null} returns null if there is no error
     */
    validateDateOfBirth: function (dateOfBirth) {
      const regexp = /(\d{4})-(\d{2})-(\d{2})/;
      const result = regexp.exec(dateOfBirth);
      if (result === null) {
        return "Date of birth must be in 'YYYY-MM-DD' format";
      }

      const year = parseInt(result[1], 10);
      const monthIndex = parseInt(result[2], 10) - 1; // month: JS uses zero indexing
      const day = parseInt(result[3], 10);

      const date = new Date(year, monthIndex, day);

      // Check if date exists e.g. 2020-15-54
      if (date.getFullYear() != year || date.getMonth() != monthIndex || date.getDate() != day) {
        return "Date of birth given bad date";
      }

      if (date.getTime() > this.youngestBirthdate.getTime()) {
        return `You must be ${this.$constants.SIGN_UP.MIN_AGE} years or older to sign up`;
      }

      if (date.getTime() < this.oldestBirthDate.getTime()) {
        return `You cannot be older than ${this.$constants.SIGN_UP.MAX_AGE} years to sign up`;
      }

      return null;
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

      if (this.phoneNumber.trim().length >= 20) {
        this.errorMessage = this.phoneErrorMessage = "Phone number is a maximum of 20 digits long";
        this.$refs.countryCodeLabel.scrollIntoView();
        return;
      }
      const phoneNumberEntered = this.phoneNumber.trim().length > 0;
      const countryCodeEntered = typeof this.countryCode == "number";
      if (phoneNumberEntered && !countryCodeEntered) {
        this.errorMessage = this.countryCodeErrorMessage = "Country code and phone number must both be blank or filled in";
        this.$refs.countryCodeLabel.scrollIntoView();
        return;
      }

      if (!phoneNumberEntered && countryCodeEntered) {
        this.errorMessage = this.phoneErrorMessage = "Country code and phone number must both be blank or filled in";
        this.$refs.countryCodeLabel.scrollIntoView();
        return;
      }

      const phoneNumber = (typeof this.countryCode == "number" ?
              `+${this.countryCode} ` : ""
      ) + this.phoneNumber.trim();

      const dateOfBirth = this.dateOfBirth === null ? null : this.dateOfBirth.trim();
      if (this.validateDateOfBirth(dateOfBirth) !== null) {
        this.dateOfBirthErrorMessage = this.errorMessage = this.validateDateOfBirth(dateOfBirth);
        this.$refs.dateOfBirthLabel.scrollIntoView();
        return;
      }

      let userData = {
        firstName: this.firstName,
        middleName: this.middleName,
        lastName: this.lastName,
        nickname: this.nickname,
        email: this.email,
        password: this.password,
        dateOfBirth: dateOfBirth,
        homeAddress: this.address, // API stores address as homeAddress, not address
        phoneNumber: phoneNumber,
        bio: this.bio
      }

      let response;

      try {
        response = await this.callApi(userData);
      } catch (err) {
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

      // Instead of calling /user API to get user info, just use the sign up request
      delete userData.password;
      userData.id = response.data.userId;
      await this.$stateStore.actions.setAuthUser(userData);
      await this.$router.push({ name: "UserDetail" });
    }
  }
}
</script>
