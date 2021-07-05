package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;

public interface InventoryDao {

  void saveInventoryItem(InventoryItem inventory);

  InventoryItem getInventoryItem(long InventoryId) throws InventoryItemNotFoundException;

  List<InventoryItem> getInventoryItems(Business business, PaginationBuilder pagBuilder);
}
