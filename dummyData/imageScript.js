const FormData = require("form-data");
const axios = require('axios');
const SERVER_URL = "http://localhost:9499";
const fs = require("fs");
let cookie = "";

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 10000,
  withCredentials: true
});

/**
 * Queries the picsum.photos API to retrieve and download a random image
 */
async function getImage() {
  return await axios
  .get(`https://picsum.photos/200`)
  .then((response) => {
    return response.data;
  })
}

(async () => {
  await instance.post("/login",
      {email: "amf133@uclive.ac.nz", password: "fun123"}).then((response) => {
    cookie = response.headers["set-cookie"][0].split(";")[0];
  });
  let image = await getImage();
  fs.writeFileSync("./a.jpg", image);


  const formData = new FormData();
  formData.append("image", fs.createReadStream("./a.jpg"));

  console.log(formData);

  instance.post(
      `/businesses/${1}/products/${1}/images`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          cookie
        }
      }).then((response) => {
    console.log(response)
  }).catch((err)=>{console.log(err)})


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
