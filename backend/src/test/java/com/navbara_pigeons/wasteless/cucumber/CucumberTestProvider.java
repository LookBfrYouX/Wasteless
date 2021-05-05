package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.controller.BusinessController;
import com.navbara_pigeons.wasteless.controller.ProductController;
import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

@CucumberContextConfiguration
public class CucumberTestProvider extends MainTestProvider {

  @Autowired
  protected UserController userController;

  @Autowired
  protected BusinessController businessController;

  @Autowired
  protected ProductController productController;

}
