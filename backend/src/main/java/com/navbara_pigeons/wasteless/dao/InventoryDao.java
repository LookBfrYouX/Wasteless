package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;

public interface InventoryDao {

  void saveInventoryItem(InventoryItem inventory);

  InventoryItem getInventoryItem(long InventoryId) throws InventoryItemNotFoundException;
}
