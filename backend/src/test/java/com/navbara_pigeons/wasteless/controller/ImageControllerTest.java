package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;

public class ImageControllerTest extends ControllerTestProvider {

    @Test
    @WithAnonymousUser
    void deleteImage_withAnonymousUser_rejectedWith401() {
        String endpointUrl = "";
    }

}