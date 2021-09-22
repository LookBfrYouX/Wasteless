package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.TransactionDataDto;
import com.navbara_pigeons.wasteless.enums.TransactionGranularity;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.ProductService;
import com.navbara_pigeons.wasteless.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * This controller class provides the endpoints for viewing business transactions histories
 */
@RestController
@Slf4j
@RequestMapping("")
@Tag(name = "Transaction History Endpoint", description = "The API endpoint for viewing a business's transaction history")
public class TransactionController {
  TransactionService transactionService;

  /**
   * Constructor for transaction controller
   * @param transactionService service used by the controller
   */
  @Autowired
  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }


  @GetMapping("/businesses/{id}/transactions")
  @Operation(summary = "View a business's transaction history", description = "Return revenue/items sold count for a business in a given date range, grouped by a day, week, month or year")
  public ResponseEntity<Object> viewTransactionHistory(
      @Parameter(
        description = "The unique ID number of the business"
      ) @PathVariable long id,
      @Parameter(
        description = "Start date"
      ) @RequestParam(required = false) LocalDate startDate,
      @Parameter(
          description = "End date"
      ) @RequestParam(required = false) LocalDate endDate
    ) throws UserNotFoundException, InsufficientPrivilegesException, BusinessNotFoundException {
    if (startDate == null) startDate = LocalDate.MIN;
    if (endDate == null) endDate = LocalDate.now(ZoneOffset.UTC);
    ZonedDateTime transformedStartDate = startDate.atStartOfDay(ZoneOffset.UTC);
    ZonedDateTime   transformedEndDate = endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC);

    return new ResponseEntity<>(
        transactionService.getTransactionData(id, transformedStartDate, transformedEndDate, TransactionGranularity.DAY),
        HttpStatus.OK
    );
  }
}
