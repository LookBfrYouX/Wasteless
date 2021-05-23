package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Inventory;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;

public interface InventoryDao {

  void saveInventoryItem(Inventory inventory);

  Inventory getInventoryItem(long InventoryId) throws InventoryItemNotFoundException;
}
