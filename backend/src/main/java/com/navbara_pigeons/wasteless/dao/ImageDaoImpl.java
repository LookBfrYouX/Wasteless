package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Image;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

/**
 * A Data Access Object so handle Images on the machine.
 */
@Repository
public class ImageDaoImpl implements ImageDao {

  public final String productsDirectory = "products"; // products stored in storagePath/products/
  private final EntityManager entityManager;
  @Value("${user_generated_images_directory}")
  private String storagePath;

  public ImageDaoImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Save a product image name to the Db
   *
   * @param image The image to be saved
   * @throws IOException Exception if saving to the machine fails
   */
  @Override
  public void saveProductImageToDb(Image image) {
    Session currentSession = getSession();
    currentSession.saveOrUpdate(image);
  }

  /**
   * Given filename of product, returns path to file
   *
   * @param filename name of product image file
   * @return normalized path
   */
  public Path getPathToProduct(String filename) {
    Path destination = Paths.get(storagePath, filename);
    return destination.normalize();
  }

  /**
   * Save a product image to the machines local storage
   *
   * @param image    The image to be saved
   * @param filename The filename to save the image under
   * @throws IOException Exception if saving to the machine fails
   */
  @Override
  public void saveProductImageToMachine(MultipartFile image, String filename) throws IOException {
    Path destination = getPathToProduct(filename);
    Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
  }

  public void deleteProductImageFromMachine(String filename) throws IOException {
    Path imagePath = getPathToProduct(filename);
    Files.delete(imagePath);
  }

  public void deleteImage(Image image) {
    Session currentSession = getSession();
    currentSession.delete(image);
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
