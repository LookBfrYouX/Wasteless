package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Image;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

/**
 * A Data Access Object so handle Images on the machine.
 */
@Repository
public class ImageDaoImpl implements ImageDao {

  private final String storagePath = "./src/main/resources/images/";
  private final String defaultUserPath = "user/default-user.png";
  private final String defaultBusinessPath = "business/default-business.png";
  private final EntityManager entityManager;

  public ImageDaoImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Save a product image name to the Db
   * @param image The image to be saved
   * @throws IOException Exception if saving to the machine fails
   */
  @Override
  public void saveProductImageToDb(Image image) {
    Session currentSession = getSession();
    currentSession.saveOrUpdate(image);
  }

  /**
   * Save a product image to the machines local storage
   * @param image The image to be saved
   * @param filename The filename to save the image under
   * @throws IOException Exception if saving to the machine fails
   */
  @Override
  public void saveProductImageToMachine(MultipartFile image, String filename) throws IOException {
    Path destination = Paths.get(storagePath + "product/" + filename);
    Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
  }

  /**
   * Save the Users Profile image to the local storage
   * @param image The byte data of the image
   * @param imageName The image name of the file to be saved
   * @throws IOException Exception if saving to the machine fails
   */
  public void saveProfileImageToMachine(MultipartFile image, String imageName) throws IOException {
    Path destination = Paths.get(storagePath + "user/" + imageName);
    Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
  }

  /**
   * Delete the Users profile image from the machine
   * @param imageName the name of the image to delete
   * @throws IOException Exception if deleting the image has failed
   */
  public void deleteProfileImageOnMachine(String imageName) throws IOException {
    Path destination = Paths.get(storagePath + "user/" + imageName);
    Files.delete(destination);
  }

  /**
   * Get the Profile image from the local storage
   * @param imageName The name of the image to get and return
   * @return the byte data of the image
   * @throws IOException Exception if retrieving the image has failed
   */
  public byte[] getProfileImageOnMachine(String imageName) throws IOException {
    Path destination = Paths.get(storagePath + "user/" + imageName);
    if (!Files.exists(destination)) {
      throw new IOException("User does not have an image uploaded");
    }
    return Files.readAllBytes(destination);
  }

  /**
   * Get the default User Profile image
   * @return The byte data of the image
   * @throws IOException Exception if accessing the machine fails
   */
  public byte[] getDefaultProfileImage() throws IOException {
    Path destination = Paths.get(storagePath + defaultUserPath);
    return Files.readAllBytes(destination);
  }

  /**
   * Get the entity manager session
   *
   * @return Instance of the Session class
   */
  private Session getSession() {
    return this.entityManager.unwrap(Session.class);
  }
}
