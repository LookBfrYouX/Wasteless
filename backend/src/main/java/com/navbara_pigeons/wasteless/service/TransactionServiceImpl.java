package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.TransactionDao;
import com.navbara_pigeons.wasteless.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A TransactionService implementation.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

  TransactionDao transactionDao;

  /**
   * TransactionServiceImpl constructor that takes autowired parameters and sets up the service for
   * interacting with all transaction related services.
   *
   * @param transactionDao The Transaction Data Access Object.
   */
  @Autowired
  public TransactionServiceImpl(TransactionDao transactionDao) {
    this.transactionDao = transactionDao;
  }

  /**
   * Method to save a Transaction entity to the database.
   *
   * @param transaction Passed in Transaction model
   */
  public void saveTransaction(Transaction transaction) {
    transactionDao.save(transaction).getId();
  }

}
