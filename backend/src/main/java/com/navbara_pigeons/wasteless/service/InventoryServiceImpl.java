package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dto.BasicInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.util.ArrayList;
import java.util.List;
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

  @Value("${public_path_prefix}")
  private String publicPathPrefix;

  /**
   * InventoryImplementation constructor that takes autowired parameters and sets up the service for
   * interacting with all business related services.
   */
  @Autowired
  public InventoryServiceImpl(BusinessDao businessDao, UserService userService, BusinessService businessService) {
    this.businessDao = businessDao;
    this.userService = userService;
    this.businessService = businessService;
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
  public List<BasicInventoryItemDto> getInventory(long businessId)
      throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException {
    if (this.userService.isAdmin() || this.businessService.isBusinessAdmin(businessId)) {
      ArrayList<BasicInventoryItemDto> inventory = new ArrayList<>();
      Business business = businessDao.getBusinessById(businessId);
      for (InventoryItem inventoryItem : business.getInventory()) {
        inventory.add(new BasicInventoryItemDto(inventoryItem, publicPathPrefix));
      }
      return inventory;
    } else {
      throw new InsufficientPrivilegesException("You are not permitted to modify this business");
    }
  }

  /**
   * Returns an inventory item by a given id
   * @param businessId business with inventory to query
   * @param itemId id of the item to be retrieved
   * @return inventory item or null
   */
  public InventoryItem getInventoryItemById(long businessId, long itemId)
      throws UserNotFoundException, InsufficientPrivilegesException, BusinessNotFoundException {
    List<BasicInventoryItemDto> inventory = getInventory(businessId);

    for (BasicInventoryItemDto inventoryItemDto: inventory) {
      if (inventoryItemDto.getId() == itemId) {
        return new InventoryItem(inventoryItemDto);
      }
    }
    return null;
  }

  @Override
  public void registerInventoryItem(CreateInventoryItemDto inventoryItemDto) throws InsufficientPrivilegesException {
    return;
  }

}
