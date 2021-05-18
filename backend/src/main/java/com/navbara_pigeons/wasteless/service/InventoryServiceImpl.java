package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.InventoryDao;
import com.navbara_pigeons.wasteless.dto.BasicInventoryDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Inventory;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.navbara_pigeons.wasteless.validation.InventoryServiceValidation;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Class for dealing with all business logic to do with Inventories
 */
@Service
public class InventoryServiceImpl implements InventoryService {

  private final BusinessDao businessDao;
  private final UserService userService;
  private final BusinessService businessService;
  private final ProductService productService;
  private final InventoryDao inventoryDao;

  @Value("${public_path_prefix}")
  private String publicPathPrefix;

  /**
   * InventoryImplementation constructor that takes autowired parameters and sets up the service for
   * interacting with all business related services.
   */
  @Autowired
  public InventoryServiceImpl(BusinessDao businessDao, UserService userService, BusinessService businessService, ProductService productService, InventoryDao inventoryDao) {
    this.businessDao = businessDao;
    this.userService = userService;
    this.businessService = businessService;
    this.productService = productService;
    this.inventoryDao = inventoryDao;
  }

  /**
   * This method retrieves a list of all the products listed by a specific business from the
   * ProductDao given the business ID.
   *
   * @param businessId The ID of the business whose products are to be retrieved.
   * @return productCatalogue A List<Product> of products that are in the business product
   * catalogue.
   * @throws BusinessNotFoundException If the business is not listed in the database.
   */
  @Override
  public List<BasicInventoryDto> getInventory(long businessId)
      throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException {
    if (this.userService.isAdmin() || this.businessService.isBusinessAdmin(businessId)) {
      ArrayList<BasicInventoryDto> inventory = new ArrayList<>();
      Business business = businessDao.getBusinessById(businessId);
      for (Inventory inventoryItem : business.getInventory()) {
        inventory.add(new BasicInventoryDto(inventoryItem, publicPathPrefix));
      }
      return inventory;
    } else {
      throw new InsufficientPrivilegesException("You are not permitted to modify this business");
    }
  }

  @Override
  public JSONObject addInventoryItem(long businessId, CreateInventoryItemDto inventoryItem) throws InventoryRegistrationException, InventoryItemForbiddenException, ProductNotFoundException {
    Business business;
    try {
      business = businessDao.getBusinessById(businessId);
      if (!businessService.isBusinessAdmin(businessId) && !userService.isAdmin()) {
        throw new InventoryItemForbiddenException(
                "User does not have permission to add an inventory item to the business");
      }
    } catch (UserNotFoundException | BusinessNotFoundException exc) {
      throw new InventoryRegistrationException("User or business not found");
    }
    Product product;
    LocalDate date = LocalDate.now();
    long productid = inventoryItem.getProductId();
    product = productService.getProduct(productid);

    Inventory inventory = new Inventory();
    if (!InventoryServiceValidation.requiredFieldsNotEmpty(inventoryItem)) {
      throw new InventoryRegistrationException("Expiry needs to be included");
    };
    InventoryServiceValidation.datesValid(inventoryItem, date);
    InventoryServiceValidation.quantityValid(inventoryItem.getQuantity());
    InventoryServiceValidation.priceValid(inventoryItem.getPricePerItem());
    InventoryServiceValidation.priceValid(inventoryItem.getTotalPrice());

    inventory.setProduct(product);
    inventory.setBusiness(business);
    inventory.setQuantity(inventoryItem.getQuantity());
    inventory.setPricePerItem(inventoryItem.getPricePerItem());
    inventory.setTotalPrice(inventoryItem.getTotalPrice());
    inventory.setExpires(inventoryItem.getExpires());
    inventory.setManufactured(inventoryItem.getManufactured());
    inventory.setSellBy(inventoryItem.getSellBy());
    inventory.setBestBefore(inventoryItem.getBestBefore());

    inventoryDao.saveInventoryItem(inventory);

    JSONObject response = new JSONObject();
    response.appendField("inventoryItemId", inventory.getId());

    return response;
  }

  @Transactional
  public void saveInventoryItem(Inventory inventory) {
    this.inventoryDao.saveInventoryItem(inventory);
  }

}
