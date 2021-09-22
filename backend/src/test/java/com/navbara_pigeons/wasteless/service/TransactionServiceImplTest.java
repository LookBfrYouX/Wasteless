package com.navbara_pigeons.wasteless.service;

import static org.mockito.Mockito.when;
import com.navbara_pigeons.wasteless.dao.TransactionDaoHibernateImpl;
import com.navbara_pigeons.wasteless.enums.TransactionGranularity;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class TransactionServiceImplTest extends ServiceTestProvider {

  @Mock
  private UserService userService;
  @Mock
  private BusinessService businessService;
  @Mock
  private TransactionDaoHibernateImpl transactionDaoHibernate;
  @InjectMocks
  private TransactionServiceImpl transactionService;

  @Test
  void getTransactionData_asGAA_expectOk() throws Exception {
    // Arrange
    Long mockBusinessId = 5001L;
    ZonedDateTime mockSaleDate = ZonedDateTime.now();
    TransactionGranularity mockGranularity = TransactionGranularity.DAY;
    when(userService.isAdmin()).thenReturn(true);
    when(businessService.isBusinessAdmin(mockBusinessId)).thenReturn(false);
    when(transactionDaoHibernate.getTransactionData(mockBusinessId, mockSaleDate, mockSaleDate,
            mockGranularity)).thenReturn(null);

    // Act & Assert
    Assertions.assertDoesNotThrow(
        () -> transactionService.getTransactionData(mockBusinessId, mockSaleDate, mockSaleDate,
            mockGranularity));
  }

  @Test
  void getTransactionData_asBusinessAdmin_expectOk() throws Exception {
    // Arrange
    Long mockBusinessId = 5001L;
    ZonedDateTime mockSaleDate = ZonedDateTime.now();
    TransactionGranularity mockGranularity = TransactionGranularity.DAY;
    when(userService.isAdmin()).thenReturn(false);
    when(businessService.isBusinessAdmin(mockBusinessId)).thenReturn(true);
    when(transactionDaoHibernate.getTransactionData(mockBusinessId, mockSaleDate, mockSaleDate,
        mockGranularity)).thenReturn(null);

    // Act & Assert
    Assertions.assertDoesNotThrow(
        () -> transactionService.getTransactionData(mockBusinessId, mockSaleDate, mockSaleDate,
            mockGranularity));
  }

  @Test
  void getTransactionData_asRandomUser_expectException() throws Exception {
    // Arrange
    Long mockBusinessId = 5001L;
    ZonedDateTime mockSaleDate = ZonedDateTime.now();
    TransactionGranularity mockGranularity = TransactionGranularity.DAY;
    when(userService.isAdmin()).thenReturn(false);
    when(businessService.isBusinessAdmin(mockBusinessId)).thenReturn(false);
    when(transactionDaoHibernate.getTransactionData(mockBusinessId, mockSaleDate, mockSaleDate,
        mockGranularity)).thenReturn(null);

    // Act & Assert
    Assertions.assertThrows(InsufficientPrivilegesException.class,
        () -> transactionService.getTransactionData(mockBusinessId, mockSaleDate, mockSaleDate,
            mockGranularity));
  }
}
