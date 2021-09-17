package com.navbara_pigeons.wasteless.dao;

import java.time.ZonedDateTime;

public interface TransactionDaoHibernate {

  void getTransactionData(int businessId, ZonedDateTime startSaleDate, ZonedDateTime endSaleDate,
      String granularity);

}
