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
class TransactionDataDtoTest extends MainTestProvider {

  private Set<ConstraintViolation<TransactionDataDto>> validate(TransactionDataDto dto) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<TransactionDataDto>> violations = validator.validate(dto);
    return violations;
  }

  private void transactionDataDtoTest(LocalDate date, Integer count, Double amount, Integer numViolations) {
    TransactionReportModel transactionReport = makeTransactionReport(date, count, amount);
    List<TransactionReportModel> transactionReportList = new ArrayList<>();
    transactionReportList.add(transactionReport);
    Assertions.assertEquals(numViolations, validate(new TransactionDataDto(transactionReportList, amount, count)).size());
  }

  @Test
  void transactionDataDto_expectOk() {
    transactionDataDtoTest(LocalDate.now(), 4, 44D, 0);
  }

  @Test
  void transactionDateDto_withNegativeTotalCount_expectError() {
    transactionDataDtoTest(LocalDate.now(), -4, 44D, 1);
  }

  @Test
  void transactionDateDto_withZeroCount_expectOk() {
    transactionDataDtoTest(LocalDate.now(), 0, 44D, 0);
  }

  @Test
  void transactionDateDto_withNegativeTotalAmount_expectError() {
    transactionDataDtoTest(LocalDate.now(), 4, -44D, 1);
  }

  @Test
  void transactionDateDto_withZeroTotalAmount_expectOk() {
    transactionDataDtoTest(LocalDate.now(), 4, 0D, 0);
  }

}
