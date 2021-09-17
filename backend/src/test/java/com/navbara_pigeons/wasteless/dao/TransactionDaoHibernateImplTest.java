package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransactionDaoHibernateImplTest extends MainTestProvider {

  @Autowired
  TransactionDaoHibernate transactionDaoHibernate;

  void testTest() {
    ZonedDateTime startSaleDate = ZonedDateTime.now().minusYears(1);
    ZonedDateTime endSaleDate = ZonedDateTime.now();

    transactionDaoHibernate.getTransactionData(5001, startSaleDate, endSaleDate, "DAY");
  }
}
