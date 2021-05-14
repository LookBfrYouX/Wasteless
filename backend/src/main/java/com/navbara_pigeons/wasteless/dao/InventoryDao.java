package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Inventory;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import java.util.List;

public interface InventoryDao {

  Inventory getInventoryItem(long businessId) throws InventoryItemNotFoundException;

  List<Inventory> getBusinessesInventory(long businessId) throws InventoryItemNotFoundException;

}
