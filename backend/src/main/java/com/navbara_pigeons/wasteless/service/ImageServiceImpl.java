package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.ImageDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Image;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class ImageServiceImpl implements ImageService {

  private final UserDao userDao;
  private final ImageDao imageDao;
  private final BusinessService businessService;
  private final ProductService productService;
  private final String userPrefix = "U";
  private final UserService userService;

  @Autowired
  public ImageServiceImpl(UserDao userDao, ImageDao imageDao, BusinessService businessService,
      ProductService productService, UserService userService) {
    this.userDao = userDao;
    this.imageDao = imageDao;
    this.businessService = businessService;
    this.productService = productService;
    this.userService = userService;
  }

  /**
   * Upload an image to a businesses product
   *
   * @param businessId The identifier of a business
   * @param productId The identifier of a product to add the image to
   * @param image The image to be uploaded
   * @return The URL of the image just uploaded
   * @throws UserNotFoundException The users credentials could not be found from the JSessionID
   * @throws BusinessNotFoundException When no business is found with the given id
   * @throws ProductNotFoundException When no product is found with the given id
   * @throws IOException When an IO error occurs (will return a 500 status error, this is intended)
   * @throws ImageNotFoundException When a non image file or no file is received instead of an image
   */
  @Transactional
  public String uploadProductImage(long businessId, long productId, MultipartFile image)
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException, IOException,
      ImageNotFoundException {
    if (!businessService.isBusinessAdmin(businessId)) {
      throw new BadCredentialsException(
          "You must be an administer of the business to upload a product image");
    }

    // Get the file extension of the given file
    String fileName = StringUtils.cleanPath(image.getOriginalFilename());
    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

    // Check if "image" is actually an image (gif not accepted)
    ArrayList<String> items = new ArrayList<>();
    items.add("jpg");
    items.add("png");
    if (!items.contains(fileExtension.toLowerCase())) {
      throw new ImageNotFoundException();
    }

    // Crop the image and then save it to the DB + Machine
    cropImageToSquare(image, fileExtension, fileName);
    Image imageEntity = new Image(fileExtension);
    Product productEntity = productService.getProduct(productId);
    productEntity.addProductImage(imageEntity);
    if (productEntity.getPrimaryProductImage() == null) {
      productEntity.setPrimaryProductImage(imageEntity);
    }
    imageDao.saveProductImageToMachine(image, imageEntity.getFilename());
    imageDao.saveProductImageToDb(imageEntity);

    // Return the URI for to download the image
    return "/images/product/" + imageEntity.getFilename();
  }

  /**
   * Change primary image
   *
   * @param businessId The identifier of a business
   * @param productId The identifier of a product to add the image to
   * @param imageId The identifier of an image to be set as the primary image
   */
  public void changePrimaryImage(long businessId, long productId, long imageId)
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException, ImageNotFoundException {
    if (!businessService.isBusinessAdmin(businessId)) {
      throw new BadCredentialsException(
          "You must be an administer of the business to upload a product image");
    }
    Product productEntity = productService.getProduct(productId);
    Image newPrimaryImage = productEntity.getImageById(imageId);
    productEntity.setPrimaryProductImage(newPrimaryImage);
    this.productService.saveProduct(productEntity);
  }

  /**
   * Upload a given profile image to the machine and store the image name in the database.
   *
   * @param image The image file to be saved
   * @return The URI to access the saved image
   * @throws UserNotFoundException The user could not be found from the Session
   * @throws IOException           The new filename could not be created
   */
  @Transactional
  public String uploadProfileImage(Long userId, MultipartFile image)
      throws UserNotFoundException, IOException {
    // Get the User object so we can use their ID
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    User loggedInUser = this.userDao.getUserByEmail(authentication.getName());

    // Get the user by passed id
    User userToEdit = userDao.getUserById(userId);

    if (loggedInUser.getId() != userToEdit.getId()) {
      throw new BadCredentialsException("You cannot edit another users Profile Image");
    }

    // Delete the existing image if it exists
    if (loggedInUser.getImageName() != null) {
      imageDao.deleteProfileImageOnMachine(loggedInUser.getImageName());
    }

    // Get the file extension of the given file
    String fileName = StringUtils.cleanPath(image.getOriginalFilename());
    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

    // Generate a new filename prefixed with 'U{userId}_'
    String newFileName = userPrefix + loggedInUser.getId() + "_image." + fileExtension;

    // Crop the image
    image = cropImageToSquare(image, fileExtension, newFileName);

    // Save the image in the images/user/ directory then save image name in DB
    imageDao.saveProfileImageToMachine(image, newFileName);
    loggedInUser.setImageName(newFileName);

    // Return the URI for to download the image
    return ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path("/users/")
        .path(Long.toString(loggedInUser.getId()))
        .path("/images/")
        .toUriString();
  }

  public byte[] downloadProfileImage(long userId) throws UserNotFoundException, IOException {
    // Get the user by passed id
    User usersImageToDownload = userDao.getUserById(userId);
    if (usersImageToDownload.getImageName() == null) {
      return imageDao.getDefaultProfileImage();
    }
    return imageDao.getProfileImageOnMachine(usersImageToDownload.getImageName());
  }

  private MultipartFile cropImageToSquare(MultipartFile image, String extension, String fileName)
      throws IOException {
    InputStream in = new ByteArrayInputStream(image.getBytes());
    BufferedImage imageToCrop = ImageIO.read(in);

    // Get the image dimensions
    int height = imageToCrop.getHeight();
    int width = imageToCrop.getWidth();

    // The image is not already a square
    if (height != width) {
      // Compute the size of the square
      int squareSize = (Math.min(height, width));

      // Coordinates of the image's middle
      int xc = width / 2;
      int yc = height / 2;

      // Crop the image
      imageToCrop = imageToCrop.getSubimage(
          xc - (squareSize / 2), // x coordinate of the upper-left corner
          yc - (squareSize / 2), // y coordinate of the upper-left corner
          squareSize,            // widht
          squareSize             // height
      );
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(imageToCrop, extension, baos);
    return new MockMultipartFile(fileName, baos.toByteArray());
  }

  /**
   * This service method deletes the product image associated with the business/product if the user has the
   * correct permissions.
   * @param imageId The ID of the image that is to be deleted.
   * @param businessId The ID of the business whose product image is being deleted. (Used to check for user permissions)
   * @param productId The ID of the product whose image is being deleted.
   * @throws UserNotFoundException Thrown if no user is logged in.
   * @throws BusinessNotFoundException Thrown if the business that owns the product does not exist.
   * @throws InsufficientPrivilegesException Thrown if the user is not admin or business admin
   * @throws ProductNotFoundException Thrown if the product in question does not exist.
   * @throws ImageNotFoundException Thrown if the image in question does not exist.
   * @throws IOException Thrown if the system is unable to delete the actual file from persistent storage.
   */
  @Transactional
  public void deleteProductImage(long imageId, long businessId, long productId)
          throws UserNotFoundException, BusinessNotFoundException, InsufficientPrivilegesException, ProductNotFoundException, ImageNotFoundException, IOException {
    if (!this.businessService.isBusinessAdmin(businessId) && !this.userService.isAdmin()){
      throw new InsufficientPrivilegesException("You can not administer this business");
    }
    Product product = this.productService.getProduct(productId);
    Image image = product.getImageById(imageId);
    String thumbPath = image.getThumbnailFilename(); // TODO also unlink this when thumbnails start being stored on disk
    String imgPath = image.getFilename();
    product.deleteProductImage(imageId);
    this.productService.saveProduct(product);
    this.imageDao.deleteImage(image);
    this.imageDao.deleteProductImageFromMachine(imgPath);
  }

  public void deleteUserImage(long userId) {
    // get path
    // remove from database
    // unlink image from fs
  }

}
