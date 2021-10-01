package com.navbara_pigeons.wasteless.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.enums.TransactionGranularity;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionControllerTest extends ControllerTestProvider {

  long BUSINESS_ID = 1001;

  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  void getTransactionHistory_notBusinessAdmin_expectUnauthorized() throws Exception {
    mockMvc.perform(get("/businesses/" + BUSINESS_ID + "/transactions"))
        .andExpect(status().isForbidden());
  }

  @Test
  void getTransactionHistory_notLoggedIn_expectUnauthorized() throws Exception {
    mockMvc.perform(get("/businesses/" + BUSINESS_ID + "/transactions"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(value = "mbi47@uclive.ac.nz")
  void getTransactionHistory_GAA_expectOk() throws Exception {
    mockMvc.perform(get("/businesses/" + BUSINESS_ID + "/transactions"))
        .andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void getTransactionHistory_BusinessAdmin_expectOk() throws Exception {
    mockMvc.perform(get("/businesses/" + BUSINESS_ID + "/transactions"))
        .andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void getTransactionHistory_totalCost_expectConsistent() throws Exception {
    String stringResponse = mockMvc.perform(get("/businesses/" + BUSINESS_ID + "/transactions"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    JsonNode response = objectMapper.readTree(stringResponse);
    JsonNode transactions = response.get("transactions");

    double amount = 0;
    for (JsonNode transaction: transactions) {
      amount += transaction.get("amount").asDouble();
    }

    Assertions.assertEquals(amount, response.get("totalAmount").asDouble());
  }


  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void getTransactionHistory_totalCount_expectConsistent() throws Exception {
    String stringResponse = mockMvc.perform(get("/businesses/" + BUSINESS_ID + "/transactions"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    JsonNode response = objectMapper.readTree(stringResponse);
    JsonNode transactions = response.get("transactions");

    long count = 0;
    for (JsonNode transaction: transactions) {
      count += transaction.get("transactionCount").asLong();
    }

    Assertions.assertEquals(count, response.get("totalTransactionCount").asLong());
  }


  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void getTransactionHistory_expectDatesInOrder() throws Exception {
    String stringResponse = mockMvc.perform(get("/businesses/" + BUSINESS_ID + "/transactions"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    JsonNode response = objectMapper.readTree(stringResponse);
    JsonNode transactions = response.get("transactions");

    ArrayList<String> dates = new ArrayList<>();

    for (JsonNode transaction: transactions) {
      ZonedDateTime dateTime = ZonedDateTime.parse(transaction.get("date").asText());
      // Converting to date then to and then string to get every date in the UTC timezone
      String dateStr = dateTime.toLocalDate().toString();
      dates.add(dateStr);
    }

    // ISO dates are sortable so can sort and check that the order hasn't changed
    Object[] sortedDates = dates.stream().sorted().toArray();
    Assertions.assertArrayEquals(dates.toArray(), sortedDates);
  }


  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void getTransactionHistory_expectDatesInDateRange() throws Exception {
    LocalDate startDate = LocalDate.parse("2021-02-21");
    LocalDate   endDate = LocalDate.parse("2021-02-27");
    String stringResponse = mockMvc.perform(
        get("/businesses/" + BUSINESS_ID + "/transactions")
        .param("startDate", startDate.toString())
            .param("endDate", endDate.toString())
    )
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    JsonNode response = objectMapper.readTree(stringResponse);
    JsonNode transactions = response.get("transactions");

    for (JsonNode transaction: transactions) {
      ZonedDateTime dateTime = ZonedDateTime.parse(transaction.get("date").asText());
      // Converting to date then to and then string to get every date in the UTC timezone
      LocalDate date = dateTime.toLocalDate();
      Assertions.assertFalse(date.isBefore(startDate)); // doing not is before as the two dates can equal each other
      Assertions.assertFalse(date.isAfter(endDate));
    }
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void getTransactionHistory_dayGranularity_expectOk() throws Exception {
    LocalDate startDate = LocalDate.parse("2021-02-21");
    LocalDate   endDate = LocalDate.parse("2021-02-22");
    TransactionGranularity transactionGranularity = TransactionGranularity.DAY;
    String stringResponse = mockMvc.perform(
        get("/businesses/" + BUSINESS_ID + "/transactions")
            .param("startDate", startDate.toString())
            .param("endDate", endDate.toString())
            .param("transactionGranularity", transactionGranularity.toString())
    )
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    JsonNode response = objectMapper.readTree(stringResponse);
    JsonNode transactions = response.get("transactions");
    Assertions.assertEquals(2, transactions.size());
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void getTransactionHistory_weekGranularity_expectOk() throws Exception {
    LocalDate startDate = LocalDate.parse("2021-02-21");
    LocalDate   endDate = LocalDate.parse("2021-02-27");
    TransactionGranularity transactionGranularity = TransactionGranularity.WEEK;
    String stringResponse = mockMvc.perform(
        get("/businesses/" + BUSINESS_ID + "/transactions")
            .param("startDate", startDate.toString())
            .param("endDate", endDate.toString())
            .param("transactionGranularity", transactionGranularity.toString())
    )
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    JsonNode response = objectMapper.readTree(stringResponse);
    JsonNode transactions = response.get("transactions");
    Assertions.assertEquals(1, transactions.size());
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void getTransactionHistory_monthGranularity_expectOk() throws Exception {
    LocalDate startDate = LocalDate.parse("2021-02-21");
    LocalDate   endDate = LocalDate.parse("2021-04-21");
    TransactionGranularity transactionGranularity = TransactionGranularity.MONTH;
    String stringResponse = mockMvc.perform(
        get("/businesses/" + BUSINESS_ID + "/transactions")
            .param("startDate", startDate.toString())
            .param("endDate", endDate.toString())
            .param("transactionGranularity", transactionGranularity.toString())
    )
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    JsonNode response = objectMapper.readTree(stringResponse);
    JsonNode transactions = response.get("transactions");
    Assertions.assertEquals(2, transactions.size());
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void getTransactionHistory_yearGranularity_expectOk() throws Exception {
    LocalDate startDate = LocalDate.parse("2019-02-21");
    LocalDate   endDate = LocalDate.parse("2021-02-21");
    TransactionGranularity transactionGranularity = TransactionGranularity.YEAR;
    String stringResponse = mockMvc.perform(
        get("/businesses/" + BUSINESS_ID + "/transactions")
            .param("startDate", startDate.toString())
            .param("endDate", endDate.toString())
            .param("transactionGranularity", transactionGranularity.toString())
    )
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    JsonNode response = objectMapper.readTree(stringResponse);
    JsonNode transactions = response.get("transactions");
    Assertions.assertEquals(2, transactions.size());
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void getTransactionHistory_nullGranularity_expectDayGranularity() throws Exception {
    LocalDate startDate = LocalDate.parse("2021-02-21");
    LocalDate   endDate = LocalDate.parse("2021-02-22");
    String stringResponse = mockMvc.perform(
        get("/businesses/" + BUSINESS_ID + "/transactions")
            .param("startDate", startDate.toString())
            .param("endDate", endDate.toString())
            .param("transactionGranularity", "")
    )
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    JsonNode response = objectMapper.readTree(stringResponse);
    JsonNode transactions = response.get("transactions");
    System.out.println(transactions);
    Assertions.assertEquals(2, transactions.size());
  }
}
