const FormData = require("form-data");
const Axios = require('axios');
const SERVER_URL = "http://localhost:9499";
const Fs = require("fs");
let cookie = "";
const Path = require('path')

const instance = Axios.create({
  baseURL: SERVER_URL,
  timeout: 10000,
  withCredentials: true
});

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
  response.data.pipe(writer)
}

async function login() {
  await instance.post("/login",
      {email: "amf133@uclive.ac.nz", password: "fun123"}).then((response) => {
    cookie = response.headers["set-cookie"][0].split(";")[0];
  });
}

async function uploadImage() {
  let filePath = __dirname + "/a.jpg";

  Fs.readFile(filePath, (error ,imageData) => {
    console.log(imageData.toString('binary'));
    Axios.post(
        `http://localhost:9499/businesses/${1}/products/${1}/images`, imageData.toString('binary'), {
          headers: {
            'Content-Type': 'multipart/form-data; boundary=<calculated when request is sent>',
            cookie
          }
        }).then((response) => {
      console.log(response)
    }).catch((err) => {
      console.log(err)
    })
  });
}

(async () => {
  await login();
  await downloadImage();
  await uploadImage();
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
