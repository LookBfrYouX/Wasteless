package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.BasicInventoryDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.util.List;

public interface InventoryService {

  List<BasicInventoryDto> getInventory(long businessId)
      throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException, InventoryItemNotFoundException;

  void registerInventoryItem(CreateInventoryItemDto inventoryItemDto) throws InsufficientPrivilegesException;

}
