const axios = require('axios');
const FormData = require("form-data");
const fs = require("fs");
const path = require('path');
const SERVER_URL = "http://localhost:9499";
const NUMBER_OF_INSERTS = 5000;
const NUM_BUSINESSES = 1000;
const IMAGE_SIZE = 800;
let cookie = "";

const IMAGE_DIRECTORY = "./images";

if (!fs.existsSync(IMAGE_DIRECTORY)) {
  fs.mkdirSync(IMAGE_DIRECTORY);
}

/**
 * Queries the picsum.photos API to retrieve and download a random image
 */
async function downloadImage() {
  const url = `https://picsum.photos/${IMAGE_SIZE}`;
  const imagePath = path.join(IMAGE_DIRECTORY, Math.random().toString(36).substring(8) + ".jpg");
  const writer = fs.createWriteStream(imagePath);

  await axios({
    url,
    method: 'GET',
    responseType: 'stream'
  }).then(response => response.data.pipe(writer));

  return imagePath;
}

/**
 * Logs into the admin account to upload an image for any business
 */
async function login() {
  await axios.post(`${SERVER_URL}/login`,
      {email: "admin@wasteless.co.nz", password: "admin"}).then((response) => {
    cookie = response.headers["set-cookie"][0].split(";")[0];
  });
}

/**
 * Uploads image to our API
 */
async function uploadImage(businessId, productId, filePath) {
  const data = new FormData();
  data.append('image', fs.createReadStream(filePath));

  let config = {
    method: 'post',
    url: `${SERVER_URL}/businesses/${businessId}/products/${productId}/images`,
    headers: {
      cookie,
      ...data.getHeaders()
    },
    data: data
  };
  
  await axios(config).then(function() {
    console.log("Success");
  }).catch(function(error) {
    console.log(error);
  });
}

/**
 * Runs an async function on elements of an array
 * @param {*} elements 
 * @param {*} job (element, index) => Promise
 * @param {*} simultaneousJobs number of jobs to run simultaneously
 */
const batcher = async (elements, job, simultaneousJobs = 5) => {
  let i = 0;
  while (i < elements.length) {
    let promises = [];
    const iMax = Math.min(elements.length, i + simultaneousJobs);
    for(; i < iMax; i++) {
      promises.push(job(elements[i], i));
    }

    await Promise.allSettled(promises);
  }
}


/**
 * Downloads images and puts them in IMAGE_DIRECTORY
 */
const downloadOnly = async () => {
  const arr = [];
  for (let i = 0; i < NUMBER_OF_INSERTS; i++) {
    arr.push(i);
  }

  await batcher(arr, async i => {
    console.log(`Downloaded image ${i} as ${await downloadImage()}`);
  });
}

/**
 * Uploads all images in IMAGE_DIRECTORY
 */
const uploadFromFilesystem = async () => {
  await login();
  await batcher(fs.readdirSync(IMAGE_DIRECTORY), async (filename, i) => {
    const filePath = path.join(IMAGE_DIRECTORY, filename);
    await uploadImage((i % NUM_BUSINESSES) + 1, i + 1, filePath);
    console.log(`Uploaded image ${i}`);
  });
}


/**
 * Simultaneously downloads and uploads. Images are deleted after upload
 */
const downloadAndUpload = async () => {
  await login();
  const arr = [];
  for (let i = 0; i < NUMBER_OF_INSERTS; i++) {
    arr.push(i);
  }

  await batcher(arr, async (i) => {
    const filePath = await downloadImage();
    await uploadImage((i % NUM_BUSINESSES) + 1, i + 1, filePath);
    fs.deleteFileSync(filePath);
    console.log(`Processed image ${i}`);
  });
}

/**
 * Main script to download and upload images
 */
(async () => {
  // await downloadOnly();
  // await uploadFromFilesystem();

  await downloadAndUpload();
})();


