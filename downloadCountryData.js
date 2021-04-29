const https = require("https");
const fs = require("fs");

const url = "https://restcountries.eu/rest/v2/all";

const temporaryFilePath = "./tmp.json";

const frontEndPath = "./frontend/src/assets/countryData.json";
const backEndPath = "./backend/src/main/resources/countryData.json";


const downloadCountryData = (temporaryFilePath) => {
  const file = fs.createWriteStream(temporaryFilePath);

  return new Promise((resolve, error) => {
    https.get(url, response => {
      if (response.statusCode !== 200) {
        console.error(`Not a 200 response: ${response.statusCode}`);
        return error();
      }
      response.pipe(file); // Save to file

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


const filterData = data => data
  .filter(country => country.callingCodes.length != 0 && country.callingCodes[0].length != 0)
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


const pipeline = () => {
  downloadCountryData(temporaryFilePath)
  .then(() => {
    const data = JSON.parse(fs.readFileSync(temporaryFilePath, { encoding: "utf8"}));
    const filteredData = filterData(data);
    
    fs.writeFileSync(frontEndPath, JSON.stringify(filteredData));
    fs.writeFileSync(backEndPath, JSON.stringify(filteredData));
    fs.rmSync(temporaryFilePath);
  }).catch(err => {
    console.error(err);
    process.exit(1);
  });
}

pipeline();