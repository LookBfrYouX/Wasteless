package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.InventoryDao;
import com.navbara_pigeons.wasteless.dto.BasicInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.InventoryRegistrationException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import com.navbara_pigeons.wasteless.validation.InventoryServiceValidation;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
  public InventoryServiceImpl(BusinessDao businessDao, UserService userService,
      BusinessService businessService, ProductService productService, InventoryDao inventoryDao) {
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
   * @param businessId    The ID of the business whose products are to be retrieved.
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be Null
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be Null
   * @param sortBy        Defines any inventory item sorting needed and the direction (ascending or
   *                      descending). In the format "fieldName-<acs/desc>", Can be Null
   * @return productCatalogue A List<Product> of products that are in the business product
   * catalogue.
   * @throws BusinessNotFoundException If the business is not listed in the database.
   */
  @Override
  public List<BasicInventoryItemDto> getInventory(long businessId, Integer pagStartIndex,
      Integer pagEndIndex, String sortBy)
      throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException, IllegalArgumentException {

    if (!this.userService.isAdmin() && !this.businessService.isBusinessAdmin(businessId)) {
      throw new InsufficientPrivilegesException("You are not permitted to modify this business");
    }

    Business business = businessDao.getBusinessById(businessId);

    PaginationBuilder pagBuilder = new PaginationBuilder(InventoryItem.class, "id");
    pagBuilder.withPagStartIndex(pagStartIndex)
        .withPagEndIndex(pagEndIndex)
        .withSortByString(sortBy);

    List<InventoryItem> serverResults = inventoryDao.getInventoryItems(business, pagBuilder);

    ArrayList<BasicInventoryItemDto> inventory = new ArrayList<>();
    for (InventoryItem inventoryItem : serverResults) {
      inventory.add(new BasicInventoryItemDto(inventoryItem, publicPathPrefix));
    }
    return inventory;

  }

  /**
   * Returns an inventory item by a given id
   *
   * @param businessId business with inventory to query
   * @param itemId     id of the item to be retrieved
   * @return inventory item or null
   */
  public InventoryItem getInventoryItemById(long businessId, long itemId)
      throws BusinessNotFoundException, InventoryItemNotFoundException {
    Business business = businessDao.getBusinessById(businessId);
    List<InventoryItem> inventory = business.getInventory();

    for (InventoryItem inventoryItem : inventory) {
      if (inventoryItem.getId() == itemId) {
        return inventoryItem;
      }
    }
    throw new InventoryItemNotFoundException(itemId);
  }

  /**
   * This method retrieves a list of all the products listed by a specific business from the
   * ProductDao given the business ID.
   *
   * @param businessId, Create The ID of the business whose products are to be retrieved.
   * @return A List<Product> of products that are in the business product catalogue.
   * @throws BusinessNotFoundException If the business is not listed in the database.
   */
  @Override
  @Transactional
  public long addInventoryItem(long businessId, CreateInventoryItemDto inventoryItemDto)
      throws InventoryRegistrationException, InsufficientPrivilegesException {
    Business business;
    try {
      business = businessDao.getBusinessById(businessId);
      Product product;
      long productId = inventoryItemDto.getProductId();
      product = productService.getProduct(productId);
      if (!businessService.isBusinessAdmin(businessId) && !userService.isAdmin()) {
        throw new InsufficientPrivilegesException(
            "User does not have permission to add an inventory item to the business");
      }
      InventoryItem inventoryItem = new InventoryItem(inventoryItemDto);
      inventoryItem.setProduct(product);
      inventoryItem.setBusiness(business);

      if (inventoryItem.getPricePerItem() == null) {
        inventoryItem.setPricePerItem(product.getRecommendedRetailPrice());
      }
      if (inventoryItem.getTotalPrice() == null) {
        inventoryItem.setTotalPrice(inventoryItem.getPricePerItem() * inventoryItem.getQuantity());
      }
      InventoryServiceValidation.isInventoryItemValid(inventoryItem);

      inventoryDao.saveInventoryItem(inventoryItem);

      return inventoryItem.getId();

    } catch (BusinessNotFoundException | ProductNotFoundException | UserNotFoundException exc) {
      throw new InventoryRegistrationException("BUSINESS, PRODUCT OR USER NOT FOUND");
    }
  }
}