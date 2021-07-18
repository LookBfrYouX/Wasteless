import axios from 'axios';
import {Api} from '../frontend/src/Api';

/**
 * Queries the picsum.photos API to retrieve and download a random image
 */
async function getImage() {
  return await axios
  .get(`https://picsum.photos/200`)
  .then((response) => {
    return response;
  })
}

await Api.login({email: "amf133@uclive.ac.nz", password: "fun123"});

for (let i = 0; i < 5000; i++) {
  // download random image
  let image = getImage()

  // upload random image
  Api.uploadProductImage(image, i%1000, i);

  // delete random image somehow
  console.log("........");
}
