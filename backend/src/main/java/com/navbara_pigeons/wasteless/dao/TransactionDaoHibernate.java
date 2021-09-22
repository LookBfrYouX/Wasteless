package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.dto.TransactionDataDto;
import com.navbara_pigeons.wasteless.enums.TransactionGranularity;
import java.time.ZonedDateTime;

public interface TransactionDaoHibernate {

  TransactionDataDto getTransactionData(Long businessId, ZonedDateTime startSaleDate, ZonedDateTime endSaleDate,
      TransactionGranularity granularity);
}
