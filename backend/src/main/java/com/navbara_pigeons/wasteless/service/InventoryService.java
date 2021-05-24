package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.BasicInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.exception.*;

import java.util.List;

public interface InventoryService {

  List<BasicInventoryItemDto> getInventory(long businessId)
      throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException, InventoryItemNotFoundException;


  long addInventoryItem(long businessId, CreateInventoryItemDto inventoryItem) throws InventoryRegistrationException, ProductNotFoundException, BusinessNotFoundException, UserNotFoundException, InsufficientPrivilegesException;


  InventoryItem getInventoryItemById(long businessId, long itemId) throws UserNotFoundException, InsufficientPrivilegesException, BusinessNotFoundException, InventoryItemNotFoundException;

}
