package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.FullBusinessDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessRegistrationException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.service.BusinessService;
import com.navbara_pigeons.wasteless.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * This controller class provides the endpoints for dealing with businesses. All requests for information
 * about businesses are received here. IMPORTANT NOTE: Endpoints for information about the products listed by businesses
 * can be found in the ProductController class.
 */
@Controller
@Slf4j
@RequestMapping("")
public class BusinessController {

    public BusinessController(@Autowired BusinessService businessService) {
        this.businessService = businessService;
    }

    private final BusinessService businessService;

    /**
     * Endpoint allowing a user to register a business account Returns error with message if service
     * business logic doesnt pass
     *
     * @param business The Business object that is sent from the front-end.
     * @return Returns the newly created businesses id and 201 status code in a ResponseEntity
     * @throws ResponseStatusException Unknown Error.
     */
    @PostMapping("/businesses")
    public ResponseEntity<JSONObject> registerBusiness(@RequestBody FullBusinessDto business) {
        try {
            JSONObject businessId = businessService.saveBusiness(new Business(business));
            log.info("BUSINESS CREATED SUCCESSFULLY: " + business.getId());
            return new ResponseEntity<>(businessId, HttpStatus.valueOf(201));
        } catch(BusinessRegistrationException exc) {
            log.error("COULD NOT REGISTER BUSINESS (" + exc.getMessage() + "): " + business.getName());
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Bad Request");
        } catch (AddressValidationException exc) {
            log.error("COULD NOT REGISTER BUSINESS (" + exc.getMessage() + "): " + business.getName());
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Bad address given");
        } catch (BusinessTypeException exc) {
            log.error("INVALID/UN SUPPLIED BUSINESS TYPE");
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Invalid/un supplied business type");
        } catch (Exception exc) {
            log.error("CRITICAL BUSINESS REGISTRATION ERROR (" + exc.getMessage() + ")");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error.");
        }
    }

    /**
     * Search for a specific business using the id field.
     *
     * @param id unique identifier of the business being searched for
     * @return business entity matching the given id
     * @throws ResponseStatusException HTTP 401 Unauthorised & 406 Not Acceptable
     */
    @GetMapping("/businesses/{id}")
    public ResponseEntity<Object> getBusinessById(@PathVariable String id) {
        try {
            log.info("GETTING BUSINESS BY ID: " + id);
            return new ResponseEntity<>(businessService.getBusinessById(Long.parseLong(id)), HttpStatus.valueOf(200));
        } catch (BusinessNotFoundException exc) {
            log.error("BUSINESS NOT FOUND ERROR: " + id);
            throw new ResponseStatusException(HttpStatus.valueOf(406), exc.getMessage());
        } catch (NumberFormatException exc) {
            log.error("INVALID ID FORMAT ERROR: " + id);
            throw new ResponseStatusException(HttpStatus.valueOf(406), "ID format not valid");
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error.");
        }
    }
}
