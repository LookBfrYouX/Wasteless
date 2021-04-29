package com.navbara_pigeons.wasteless.testprovider;

import com.navbara_pigeons.wasteless.service.CountryDataFetcherService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

/**
 * This test helper class provides the general setup and methods available to all Service and Dao tests.
 * Test classes can extend this MainTestProvider to have access to the functionality.
 */
@SpringBootTest
public class MainTestProvider {
}
