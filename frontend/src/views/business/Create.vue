<template>
  <div
      class="w-100 d-flex justify-content-center register-business-container gradient-background pb-4"
  >
    <div class="container">
      <form
          class="slightly-transparent-inputs"
          method="POST"
          @submit.prevent="register"
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
                maxlength="50"
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
                :class="{ 'is-invalid': typeRequired }"
                class="form-control"
                name="type"
                required
            >
              <option v-for="code in types" :key="code.message">
                {{ code }}
              </option>
            </select>
          </div>
        </div>

        <address-form
            :address="address"
            @addressupdate="addressUpdate"
        />

        <div class="row>">
          <div class="form-group col px-0">
            <label for="description">Description</label>
            <textarea
                id="description"
                v-model="description"
                class="form-control"
                maxlength="250"
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
import {Api} from "@/Api";
import AddressForm from "@/components/AddressForm";

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
        suburb: "",
        postcode: "",
        city: "",
        region: "",
        country: "",
      },
      addressAsString: "",
    };
  },
  computed: {
    authUser() {
      return this.$stateStore.getters.getAuthUser();
    }
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

    /**
     * Returns the age from a given date of birth
     */
    getAge: function(birthDateString) {
      let today = new Date();
      let birthDate = new Date(birthDateString);
      let age = today.getFullYear() - birthDate.getFullYear();
      let m = today.getMonth() - birthDate.getMonth();
      if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }
      return age;
    },

    /**
     * Calls the API to create business information
     * Returns a promise, not a response
     */
    register: async function () {
      const user = this.authUser;
      if (user == null) {
        this.errorMessage = "You must be signed in to create a business";
        return;
      }

      if (this.getAge(this.authUser.dateOfBirth) < 16) {
        this.errorMessage = "You must be more than 16 years of age to register a business!"
        return;
      }

      let business = {
        primaryAdministratorId: user.id,
        name: this.name,
        description: this.description,
        address: {
          streetNumber: this.address.streetNumber,
          streetName: this.address.streetName,
          suburb: this.address.suburb,
          postcode: this.address.postcode,
          city: this.address.city,
          region: this.address.region,
          country: this.address.country,
        },
        businessType: this.type, // API stores the type as businessType not type
      }

      var response;
      try {
        response = await this.callApi(business);
      } catch (err) {
        if (await Api.handle401.call(this, err)) {
          return;
        }
        this.errorMessage = err.userFacingErrorMessage;
        return;
      }

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
        name: "BusinessDetail",
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
