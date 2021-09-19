package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransactionDaoHibernateImplTest extends MainTestProvider {

  @Autowired
  TransactionDaoHibernate transactionDaoHibernate;

  @Test
  void setTransactionDao_groupByDay() {
    ZonedDateTime startSaleDate = ZonedDateTime.now().minusYears(1);
    ZonedDateTime endSaleDate = ZonedDateTime.now();

    List results = transactionDaoHibernate.getTransactionData(1001L, startSaleDate, endSaleDate,
        "DAY");

    for (Object thing : results) {
      System.out.println(Arrays.toString((Object[]) thing));
    }
  }
}
