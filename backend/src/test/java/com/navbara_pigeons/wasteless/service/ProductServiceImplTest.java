package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.ProductDao;
import com.navbara_pigeons.wasteless.dto.BasicProductDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.enums.ProductSortByOption;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.util.Pair;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class ProductServiceImplTest extends ServiceTestProvider {

    private final String email = "tony@tony.tony";
    private final String password = "tonyTony1";

    private final String otherEmail = "test@test.com";
    private final String otherPassword = "Testtest2";

    private final String productName = "testProduct";

    @Mock
    private BusinessDao businessDao;

    @Mock
    private UserService userService;

    @Mock
    private BusinessService businessService;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductServiceImpl productService;
    private Long userId;
    private Long businessId;
    private Business business;



    @BeforeEach
    void beforeEach() throws Exception {
        // Setting mocks before tests are run to ensure unit testing only
        User user = makeUser(email, password, false);
        long userId = user.getId();
        business = makeBusiness();
        businessId = business.getId();
        business.setPrimaryAdministratorId(userId);
    }

    @Test
    @WithMockUser(username = email, password = password)
    void getProductsWithAdequatePrivilegesExpectResponse() throws UserNotFoundException, InsufficientPrivilegesException, BusinessNotFoundException, InvalidPaginationInputException {
        Product product = makeProduct(productName);
        business.addCatalogueProduct(product);
        when(userService.isAdmin()).thenReturn(true);
        when(businessService.isBusinessAdmin(businessId)).thenReturn(true);
        when(businessDao.getBusinessById(any(Long.class))).thenReturn(business);
        when(productDao.getProducts(any(Business.class), any(PaginationBuilder.class)))
                .thenReturn(Pair.of(business.getProductsCatalogue(), Long.valueOf(business.getProductsCatalogue().size())));
        PaginationDto<BasicProductDto> products = productService.getProducts(businessId, 0,1, null, true);
        Assertions.assertEquals(1, products.getTotalCount());
    }

    @Test
    void getProductsWithNonAdequatePrivilegesExpectInsufficientPrivilegesException() throws InsufficientPrivilegesException {
        Assertions.assertThrows(InsufficientPrivilegesException.class, () -> {
                    Product product = makeProduct(productName);
                    business.addCatalogueProduct(product);
                    when(userService.isAdmin()).thenReturn(false);
                    when(productDao.getProducts(any(Business.class), any(PaginationBuilder.class)))
                            .thenReturn(Pair.of(business.getProductsCatalogue(), Long.valueOf(business.getProductsCatalogue().size())));
            PaginationDto<BasicProductDto> products = productService.getProducts(businessId, 0,1, null, true);
        });
    }


}

