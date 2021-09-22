package com.navbara_pigeons.wasteless.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.dto.UserIdDto;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import lombok.With;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerTest extends ControllerTestProvider {

  long BUSINESS_ID = 1001;

  @Test
  @WithUserDetails(value = "hic21@uclive.ac.nz")
  void getTransactionHistory_notBusinessAdmin_expectUnauthorized() throws Exception {
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
}
