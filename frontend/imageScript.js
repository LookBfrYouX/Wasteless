const Axios = require('axios');
const FormData = require("form-data");
const Fs = require("fs");
const Path = require('path')
const SERVER_URL = "http://localhost:9499";
const NUMBER_OF_INSERTS = 5000;
let cookie = "";

/**
 * Queries the picsum.photos API to retrieve and download a random image
 */
async function downloadImage() {
  const url = 'https://picsum.photos/50'
  const path = Path.resolve(__dirname, '', 'a.jpg')
  const writer = Fs.createWriteStream(path)

  const response = await Axios({
    url,
    method: 'GET',
    responseType: 'stream'
  })
  response.data.pipe(writer);
}

/**
 * Logs into the admin account to upload an image for any business
 */
async function login() {
  await Axios.post(`${SERVER_URL}/login`,
      {email: "admin@wasteless.co.nz", password: "admin"}).then((response) => {
    cookie = response.headers["set-cookie"][0].split(";")[0];
  });
}

/**
 * Uploads image to our API
 */
async function uploadImage(businessId, productId) {
  let filePath = __dirname + "/a.jpg";
  const data = new FormData();
  data.append('image', Fs.createReadStream(filePath));

  let config = {
    method: 'post',
    url: `${SERVER_URL}/businesses/${businessId}/products/${productId}/images`,
    headers: {
      cookie,
      ...data.getHeaders()
    },
    data: data
  };
  Axios(config).then(function() {
    console.log("Success");
  }).catch(function(error) {
    console.log(error);
  });
}

/**
 * Main script to download and upload images
 */
(async () => {
  await login();
  for (let i = 1; i < NUMBER_OF_INSERTS; i++) {
    await downloadImage();
    await uploadImage(i%1000, i);
  }
})();


