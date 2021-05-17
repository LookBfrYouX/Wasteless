package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.controller.BusinessController;
import com.navbara_pigeons.wasteless.controller.ProductController;
import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@CucumberContextConfiguration
public class CucumberTestProvider extends MainTestProvider {

  @Autowired
  protected UserController userController;

  @Autowired
  protected BusinessController businessController;

  @Autowired
  protected ProductController productController;

  protected void login(String email) {
    // https://stackoverflow.com/questions/36584184/spring-security-withmockuser-does-not-work-with-cucumber-tests
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(
            email,
            "N/A",
            createAuthorityList("ADMIN")
        )
    );
  }

  protected void login() {
    login("dnb36@uclive.ac.nz");
  }
}
