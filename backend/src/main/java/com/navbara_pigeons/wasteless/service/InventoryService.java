package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.BasicInventoryDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.entity.Inventory;
import com.navbara_pigeons.wasteless.exception.*;
import net.minidev.json.JSONObject;

import java.util.List;

public interface InventoryService {

  List<BasicInventoryDto> getInventory(long businessId)
      throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException, InventoryItemNotFoundException;


  JSONObject addInventoryItem(long businessId, CreateInventoryItemDto inventoryItem) throws InventoryRegistrationException,
          InventoryItemForbiddenException, ProductNotFoundException;

  void saveInventoryItem(Inventory inventory);


}
