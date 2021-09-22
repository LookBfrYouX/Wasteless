package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.enums.TransactionGranularity;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/** This controller class provides the endpoints for viewing business transactions histories */
@RestController
@Slf4j
@RequestMapping("")
@Tag(
    name = "Transaction History Endpoint",
    description = "The API endpoint for viewing a business's transaction history")
public class TransactionController {
  TransactionService transactionService;

  /**
   * Constructor for transaction controller
   *
   * @param transactionService service used by the controller
   */
  @Autowired
  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  /**
   * Transforms the LocalDate used as method argument to a ZonedDateTime required by the service
   *
   * @param startDate start date. May be null
   * @param endDate end date. May be null
   * @return pair of dates transformed so that: - Start date set to minimum date if not given, and
   *     time to start of day - End date set to the current date if not given - End time is
   *     transformed to last second - Times are in UTC time as that is what is used elsewhere in the
   *     backend. There is no concept of a local timezone for businesses or users so it makes the
   *     most sense to just not send the time in the frontend
   */
  private Pair<ZonedDateTime, ZonedDateTime> transformDateRange(
      LocalDate startDate, LocalDate endDate) {
    if (startDate == null) startDate = LocalDate.MIN;
    if (endDate == null) endDate = LocalDate.now(ZoneOffset.UTC);
    ZonedDateTime transformedStartDate = startDate.atStartOfDay(ZoneOffset.UTC);
    ZonedDateTime transformedEndDate =
        endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).minusSeconds(1);
    return Pair.of(transformedStartDate, transformedEndDate);
  }

  @GetMapping("/businesses/{id}/transactions")
  @Operation(
      summary = "View a business's transaction history",
      description =
          "Return revenue/items sold count for a business in a given date range, grouped by a day, week, month or year")
  public ResponseEntity<Object> viewTransactionHistory(
      @Parameter(description = "The unique ID number of the business") @PathVariable long id,
      @Parameter(
              description =
                  "Start date for transaction history. Time set to start of day in the UTC timezone")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          @RequestParam(required = false)
          LocalDate startDate,
      @Parameter(
              description =
                  "End date for transaction history. Time set to end of day in the UTC timezone")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          @RequestParam(required = false)
          LocalDate endDate,
      @Parameter(
              description =
                  "Granularity to show the sales report")
          @RequestParam(required = false, defaultValue = "DAY") TransactionGranularity transactionGranularity)
      throws UserNotFoundException, InsufficientPrivilegesException, BusinessNotFoundException {
    Pair<ZonedDateTime, ZonedDateTime> dateRange = transformDateRange(startDate, endDate);
    log.info("TRANSACTION HISTORY FOR BUSINESS " + id + " IN DATE RANGE " + startDate + " TO " + endDate);
    return new ResponseEntity<>(
        transactionService.getTransactionData(
            id, dateRange.getLeft(), dateRange.getRight(), transactionGranularity),
        HttpStatus.OK);
  }
}
