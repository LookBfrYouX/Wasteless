package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.TransactionDataDto;
import com.navbara_pigeons.wasteless.entity.Transaction;
import com.navbara_pigeons.wasteless.enums.TransactionGranularity;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.time.ZonedDateTime;

public interface TransactionService {

  void saveTransaction(Transaction transaction);

  TransactionDataDto getTransactionData(Long businessId, ZonedDateTime startSaleDate,
      ZonedDateTime endSaleDate, TransactionGranularity granularity)
      throws UserNotFoundException, BusinessNotFoundException, InsufficientPrivilegesException;
}
