const FormData = require("form-data");
const Axios = require('axios');
const SERVER_URL = "http://localhost:9499";
const Fs = require("fs");
let cookie = "";
const Path = require('path')

/**
 * Queries the picsum.photos API to retrieve and download a random image
 */
async function downloadImage() {
  const url = 'https://picsum.photos/200'
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
 * Logs into the amf133 account (which has admin rights)
 */
async function login() {
  await Axios.post(`${SERVER_URL}/login`,
      {email: "amf133@uclive.ac.nz", password: "fun123"}).then((response) => {
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
  Axios(config);
}

(async () => {
  await login();
  await downloadImage();
  await uploadImage(1, 1);
})();

// for (let i = 0; i < 5000; i++) {
//   // download random image
//   let image = await getImage();
//
//   // upload random image
//   await Api.uploadProductImage(image, i%1000, i);
//
//   // delete random image somehow
// }
