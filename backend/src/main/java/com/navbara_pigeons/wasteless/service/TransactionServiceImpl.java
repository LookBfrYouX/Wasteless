package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.TransactionDao;
import com.navbara_pigeons.wasteless.dao.TransactionDaoHibernate;
import com.navbara_pigeons.wasteless.dto.TransactionDataDto;
import com.navbara_pigeons.wasteless.entity.Transaction;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A TransactionService implementation.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

  TransactionDao transactionDao;
  TransactionDaoHibernate transactionDaoHibernate;
  BusinessService businessService;
  UserService userService;

  /**
   * TransactionServiceImpl constructor that takes autowired parameters and sets up the service for
   * interacting with all transaction related services.
   *
   * @param transactionDao The Transaction Data Access Object.
   */
  @Autowired
  public TransactionServiceImpl(TransactionDao transactionDao,
      TransactionDaoHibernate transactionDaoHibernate, BusinessService businessService,
      UserService userService) {
    this.transactionDao = transactionDao;
    this.transactionDaoHibernate = transactionDaoHibernate;
    this.businessService = businessService;
    this.userService = userService;
  }

  /**
   * Method to save a Transaction entity to the database.
   *
   * @param transaction Passed in Transaction model
   */
  @Override
  public void saveTransaction(Transaction transaction) {
    transactionDao.save(transaction);
  }

  /**
   * Method to get transactional data from a business with given id
   *
   * @param businessId    Id of the business to query transactions from
   * @param startSaleDate Start of date range to query by
   * @param endSaleDate   End of date range to query by
   * @param granularity   Granularity to group by dates
   * @return TransactionDataDto with transaction data
   * @throws UserNotFoundException           Thrown when user who made request is not found in db
   * @throws BusinessNotFoundException       Thrown businessId doesn't match any business
   * @throws InsufficientPrivilegesException Thrown when user does not have access to query these
   *                                         transactions
   */
  @Override
  public TransactionDataDto getTransactionData(Long businessId, ZonedDateTime startSaleDate,
      ZonedDateTime endSaleDate, String granularity)
      throws UserNotFoundException, BusinessNotFoundException, InsufficientPrivilegesException {
    if (!businessService.isBusinessAdmin(businessId) && !userService.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "User does not have permission to retrieve transactions from business with id "
              + businessId);
    }

    return this.transactionDaoHibernate.getTransactionData(businessId, startSaleDate, endSaleDate,
        granularity);
  }
}
