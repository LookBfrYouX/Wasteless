package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.ImageDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
  private final String userPrefix = "U";
  private final String productPrefix = "P";

  @Autowired
  public ImageServiceImpl(UserDao userDao, ImageDao imageDao) {
    this.userDao = userDao;
    this.imageDao = imageDao;
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
}
