// this file is for saving country data from rest countries in both front end and back end for correct matching of currencies and country validation
// needs to be in pipeline before java gets compiled. So that we guarantee that the backend always has country data available

const https = require("https");
const fs = require("fs");

const url = "https://restcountries.eu/rest/v2/all";

const temporaryFilePath = "./tmp.json";
// need to save copy in both frontend and backend for matching
const frontEndPath = "./frontend/src/assets/countryData.json";
const backEndPath = "./backend/src/main/resources/countryData.json";

/*
 * saves data to temp file for filtering
 */
const downloadCountryData = (temporaryFilePath) => {
  const file = fs.createWriteStream(temporaryFilePath);

  return new Promise((resolve, error) => {
    https.get(url, response => {
      // check for invalid status codes
      if (response.statusCode !== 200) {
        console.error(`Not a 200 response: ${response.statusCode}`);
        return error();
      }
      response.pipe(file); // Save to file
      // when file is finished writing close
      file.on("finish", () => {
        file.close(() => {
          return resolve();
        });
      })
    }).on("error", err => {
      console.error(err);
      return error(err);
    });
  });
}

/*
 * filtering data received from rest countries to only include required fields
 * also checks for null fields
 */
const filterData = data => data
.filter(country => country.callingCodes.length != 0
    && country.callingCodes[0].length != 0)
.map(country => {
  // Phone codes is an array of strings
  const phoneCode = parseInt(country.callingCodes[0], 10);
  // Some have null currency codes, so filter those out
  const currency = country.currencies.find(currency => currency.code != null);
  return {
    name: country.name,
    phoneExtensionCode: phoneCode,
    code: country.alpha2Code,
    currency
  }
  // After filtering currencies, there may be no currency left
}).filter(country => country.currency !== undefined);

/*
 * pipeline runs individual methods together
 */
const pipeline = () => {
  console.log("Downloading country data");
  downloadCountryData(temporaryFilePath)
  .then(() => {
    console.log("Country data downloaded");
    const data = JSON.parse(
        fs.readFileSync(temporaryFilePath, {encoding: "utf8"}));
    const filteredData = filterData(data);
    console.log(filteredData);
    console.log(`Filtering data. ${filteredData.length} countries found`);
    // saves to frontend
    console.log(`Saving to ${frontEndPath}`);
    fs.writeFileSync(frontEndPath, JSON.stringify(filteredData));
    // saves to backend
    console.log(`Saving to ${backEndPath}`);
    fs.writeFileSync(backEndPath, JSON.stringify(filteredData));
    // deletes temp file
    fs.rmSync(temporaryFilePath);
  }).catch(err => {
    console.error(err);
    // process.exit(1);
  });
}

pipeline();
