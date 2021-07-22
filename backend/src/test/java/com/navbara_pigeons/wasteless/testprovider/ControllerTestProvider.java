package com.navbara_pigeons.wasteless.testprovider;

import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.entity.Business;

public class ControllerTestProvider extends MainTestProvider {

  protected CreateBusinessDto makeCreateBusinessDto() {
    Business business = makeBusiness();
    return new CreateBusinessDto(business);
  }
}
