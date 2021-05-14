package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.controller.BusinessController;
import com.navbara_pigeons.wasteless.controller.ProductController;
import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import io.cucumber.spring.CucumberContextConfiguration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@CucumberContextConfiguration
public class CucumberTestProvider extends MainTestProvider {

  @Autowired
  protected UserController userController;

  @Autowired
  protected BusinessController businessController;

  @Autowired
  protected ProductController productController;

  void adminLogin() {
    UserCredentials credentials = new UserCredentials();
    credentials.setEmail("mbi47@uclive.ac.nz");
    credentials.setPassword("fun123");
    this.userController.login(credentials);
  }

}
