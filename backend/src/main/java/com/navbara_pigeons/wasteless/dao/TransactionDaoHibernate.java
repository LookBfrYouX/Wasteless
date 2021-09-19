package com.navbara_pigeons.wasteless.dao;

import java.time.ZonedDateTime;
import java.util.List;

public interface TransactionDaoHibernate {

  List getTransactionData(Long businessId, ZonedDateTime startSaleDate, ZonedDateTime endSaleDate,
      String granularity);

}
