package com.navbara_pigeons.wasteless.dto;

import static org.mockito.ArgumentMatchers.any;
import com.navbara_pigeons.wasteless.model.TransactionReportModel;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransactionDataDtoTest extends MainTestProvider {

  private Set<ConstraintViolation<TransactionDataDto>> validate(TransactionDataDto dto) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<TransactionDataDto>> violations = validator.validate(dto);
    return violations;
  }

  @Test
  public void transactionDataDto_expectOk() {
    LocalDate localDate = LocalDate.now();
    Integer mockCount = 4;
    Double mockAmount = 44D;
    TransactionReportModel transactionReport = makeTransactionReport(localDate, mockCount, mockAmount);
    List<TransactionReportModel> transactionReportList = new ArrayList<>();
    transactionReportList.add(transactionReport);
    Assertions.assertEquals(0, validate(new TransactionDataDto(transactionReportList, mockCount, mockAmount)).size());
  }

  @Test
  public void transactionDateDto_withNegativeTotalCount_expectError() {
    LocalDate localDate = LocalDate.now();
    Integer mockCount = -4;
    Double mockAmount = 44D;
    TransactionReportModel transactionReport = makeTransactionReport(localDate, mockCount, mockAmount);
    List<TransactionReportModel> transactionReportList = new ArrayList<>();
    transactionReportList.add(transactionReport);
    Assertions.assertEquals(1, validate(new TransactionDataDto(transactionReportList, mockCount, mockAmount)).size());
  }

  @Test
  public void transactionDateDto_withZeroCount_expectOk() {
    LocalDate localDate = LocalDate.now();
    Integer mockCount = 0;
    Double mockAmount = 44D;
    TransactionReportModel transactionReport = makeTransactionReport(localDate, mockCount, mockAmount);
    List<TransactionReportModel> transactionReportList = new ArrayList<>();
    transactionReportList.add(transactionReport);
    Assertions.assertEquals(0, validate(new TransactionDataDto(transactionReportList, mockCount, mockAmount)).size());
  }

  @Test
  public void transactionDateDto_withNegativeTotalAmount_expectError() {
    LocalDate localDate = LocalDate.now();
    Integer mockCount = 4;
    Double mockAmount = -44D;
    TransactionReportModel transactionReport = makeTransactionReport(localDate, mockCount, mockAmount);
    List<TransactionReportModel> transactionReportList = new ArrayList<>();
    transactionReportList.add(transactionReport);
    Assertions.assertEquals(1, validate(new TransactionDataDto(transactionReportList, mockCount, mockAmount)).size());
  }

  @Test
  public void transactionDateDto_withZeroTotalAmount_expectOk() {
    LocalDate localDate = LocalDate.now();
    Integer mockCount = 4;
    Double mockAmount = 0D;
    TransactionReportModel transactionReport = makeTransactionReport(localDate, mockCount, mockAmount);
    List<TransactionReportModel> transactionReportList = new ArrayList<>();
    transactionReportList.add(transactionReport);
    Assertions.assertEquals(0, validate(new TransactionDataDto(transactionReportList, mockCount, mockAmount)).size());
  }

}
