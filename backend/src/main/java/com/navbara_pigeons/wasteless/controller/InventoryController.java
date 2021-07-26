package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.enums.InventorySortByOption;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.InventoryRegistrationException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.management.InvalidAttributeValueException;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller class provides the endpoints for dealing with inventory items. All requests for
 * inventory items listed by businesses are received here.
 */
@RestController
@Slf4j
@RequestMapping("")
@Tag(name = "Inventory Endpoint", description = "The API endpoint for Business Inventory related requests")
public class InventoryController {

  private final InventoryService inventoryService;

  @Autowired
  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  /**
   * This endpoint retrieves a list of all products listed by a particular business (id).
   *
   * @param id            The ID of the business whose inventory is to be displayed
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param sortBy        Defines the field to be sorted, can be null and defaults to the 'id'
   *                      field.
   * @param isAscending   Boolean value, whether the sort order should be in ascending order. Is not
   *                      required and defaults to True.
   * @return response A JSONObject containing the information of all inventory items listed for the
   * business.
   */
  @GetMapping("/businesses/{id}/inventory")
  @Operation(summary = "Show a businesses inventory", description = "Return a paginated/sorted list of a specific businesses inventory")
  public ResponseEntity<Object> showBusinessInventory(
      @Parameter(
          description = "The unique ID number of the business"
      ) @PathVariable long id,
      @Parameter(
          description = "The start index of the list to return, implemented for pagination, Can be "
              + "Null. This index is inclusive."
      ) @RequestParam(required = false) Integer pagStartIndex,
      @Parameter(
          description = "The stop index of the list to return, implemented for pagination, Can be "
              + "Null. This index is inclusive."
      ) @RequestParam(required = false) Integer pagEndIndex,
      @Parameter(
          description = "Defines the field to be sorted, can be null."
      ) @RequestParam(required = false) InventorySortByOption sortBy,
      @Parameter(
          description = "Boolean value, whether the sort order should be in ascending order. Is not"
              + " required and defaults to True."
      ) @RequestParam(required = false, defaultValue = "true") boolean isAscending)
      throws UserNotFoundException, InsufficientPrivilegesException, InventoryItemNotFoundException,
      BusinessNotFoundException, InvalidAttributeValueException, InvalidPaginationInputException {
    log.info("RETRIEVED INVENTORY ITEMS FOR BUSINESS: " + id);
    return new ResponseEntity<>(
        this.inventoryService.getInventory(id, pagStartIndex, pagEndIndex, sortBy, isAscending),
        HttpStatus.valueOf(200));
  }

  @PostMapping("/businesses/{id}/inventory")
  public ResponseEntity<JSONObject> addToBusinessInventory(
      @PathVariable long id, @RequestBody @Valid CreateInventoryItemDto inventoryDto)
      throws InventoryRegistrationException, UserNotFoundException, BusinessNotFoundException,
      ProductNotFoundException, InsufficientPrivilegesException {
    JSONObject response = new JSONObject();
    response.appendField("inventoryItemId", inventoryService.addInventoryItem(id, inventoryDto));
    log.info(
        "ADDED NEW INVENTORY ITEM FOR PRODUCT id: "
            + inventoryDto.getProductId()
            + " FOR BUSINESS: "
            + id);
    return new ResponseEntity<>(response, HttpStatus.valueOf(201));
  }
}
