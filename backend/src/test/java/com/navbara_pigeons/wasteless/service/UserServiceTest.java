package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessRegistrationException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.UnhandledException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.security.model.BasicUserDetails;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserServiceTest extends MainTestProvider {
    @Mock
    UserDao userDaoMock;

    @Mock
    BusinessDao businessDaoMock;

    @InjectMocks
    UserServiceImpl userService;

    final String EMAIL_1 = "example@example.com";
    final String EMAIL_2 = "example@example.com";
    final String BUSINESS_1_NAME = "BUS";
    final String PASSWORD_1 = "ABCabc123!@#";


    @Disabled
    @Test
    public void saveUser_normal() {
    }

    @Test
    @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
    public void getUserById_self() throws UserNotFoundException, UnhandledException {
        User user = makeUser(EMAIL_1, PASSWORD_1, false);
        user.setId(100);
        when(userDaoMock.getUserById(user.getId())).thenReturn(user);
        JSONObject userJson = userService.getUserById(user.getId(), true);
        assertUserWithJson(user, userJson, false);
    }

    @Test
    @WithMockUser()
    public void getUserById_otherWhenNotAdmin() throws UserNotFoundException, UnhandledException {
        User user = makeUser(EMAIL_1, PASSWORD_1, false);
        user.setId(100);
        when(userDaoMock.getUserById(user.getId())).thenReturn(user);
        JSONObject userJson = userService.getUserById(user.getId(), true);
        assertUserWithJson(user, userJson, true);
    }

    @Test
    @WithMockUser(authorities = { "ADMIN" })
    public void getUserById_otherWhenAdmin() throws UserNotFoundException, UnhandledException {
        User user = makeUser(EMAIL_1, PASSWORD_1, false);
        user.setId(100);
        when(userDaoMock.getUserById(user.getId())).thenReturn(user);
        JSONObject userJson = userService.getUserById(user.getId(), true);
        assertUserWithJson(user, userJson, false);
    }

    @Test
    @WithMockUser()
    public void getUserById_notFound() throws UserNotFoundException {
        when(userDaoMock.getUserById(Mockito.anyLong())).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1000, true));
    }

    @Test
    @Disabled
    @WithMockUser()
    public void getUserById_isPrimaryBusinessAdmin()
        throws UserNotFoundException, BusinessNotFoundException, UnhandledException {
        User user1 = makeUser(EMAIL_1, PASSWORD_1, false);
        user1.setId(100);
        when(userDaoMock.getUserById(user1.getId())).thenReturn(user1);

        Business business = makeBusiness(BUSINESS_1_NAME, user1);
        business.setId(101);
        when(businessDaoMock.getBusinessById(business.getId())).thenReturn(business);

        user1.addBusiness(business);

        JSONObject userJson = userService.getUserById(user1.getId(), true);

        assertUserWithJson(user1, userJson, true);
        assertBusinessList(List.of(business), (JSONArray) userJson.get("businesses"));
        // TODO change to businessesAdministered
    }

    @Test
    @Disabled
    @WithMockUser()
    public void getUserById_isBusinessAdmin()
        throws UserNotFoundException, BusinessNotFoundException, UnhandledException {
        User user1 = makeUser(EMAIL_1, PASSWORD_1, false);
        user1.setId(100);
        when(userDaoMock.getUserById(user1.getId())).thenReturn(user1);

        User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
        user2.setId(100);
        when(userDaoMock.getUserById(user2.getId())).thenReturn(user2);

        Business business = makeBusiness(BUSINESS_1_NAME, user2);
        business.addAdministrator(user1);
        business.setId(101);
        when(businessDaoMock.getBusinessById(business.getId())).thenReturn(business);

        user1.addBusiness(business);
        user2.addBusiness(business);

        JSONObject userJson = userService.getUserById(user1.getId(), true);

        assertUserWithJson(user1, userJson, true);
        assertBusinessList(List.of(business), (JSONArray) userJson.get("businesses"));
        // TODO change to businessesAdministered
    }

    void assertBusiness(Business expectedBusiness, JSONObject business) {
        assertNotNull(business);
        assertEquals(expectedBusiness.getBusinessType(), business.getAsString("businessType"));
        assertAddressEquals(expectedBusiness.getAddress(), (JSONObject) business.get("address"), false);
        assertEquals(expectedBusiness.getCreated(), ZonedDateTime.parse(business.getAsString("created")));
        assertEquals(expectedBusiness.getId(), business.getAsNumber("id"));
    }

    void assertBusinessList(List<Business> expectedBusinesses, JSONArray businesses) {
        assertNotNull(businesses);

        HashSet<Business> businessesNotFoundYet = new HashSet<>();
        expectedBusinesses.forEach(business -> businessesNotFoundYet.add(business));

        for (Object businessObject : businesses) {
            JSONObject businessJson = (JSONObject) businessObject;
            int id = (int) businessJson.getAsNumber("id");
            Optional<Business> expectedBusinessOptional = expectedBusinesses.stream().filter(bus -> bus.getId() == id).findFirst();
            if (expectedBusinessOptional.isEmpty()) fail();
            Business expectedBusiness = expectedBusinessOptional.get();
            assertBusiness(expectedBusiness, businessJson);
            businessesNotFoundYet.remove(expectedBusiness);
        }

        if (businessesNotFoundYet.size() > 0) fail();
    }

    /**
     * Checks that the JSON response matches user. Does NOT check business array
     * @param user
     * @param response
     * @param publicOnly
     */
    void assertUserWithJson(User user, JSONObject response, boolean publicOnly) {
        assertEquals(user.getFirstName(), response.getAsString("firstName"));
        assertEquals(user.getLastName(), response.getAsString("lastName"));
        assertEquals(user.getNickname(), response.getAsString("nickname"));

        assertEquals(user.getCreated(), ZonedDateTime.parse(response.getAsString("created")));
        assertEquals(user.getMiddleName(), response.getAsString("middleName"));
        assertEquals(user.getRole(), response.getAsString("role"));

        if (!publicOnly) {
            assertEquals(user.getEmail(), response.getAsString("email"));
            assertEquals(user.getDateOfBirth(), response.getAsString("dateOfBirth"));
            assertEquals(user.getPhoneNumber(), response.getAsString("phoneNumber"));
        } else {
            Assertions.assertNull(response.get("email"));
            Assertions.assertNull(response.get("dateOfBirth"));
            Assertions.assertNull(response.get("phoneNumber"));
        }

        assertAddressEquals(user.getHomeAddress(), (JSONObject) response.get("homeAddress"), publicOnly);
    }

    void assertAddressEquals(Address address, JSONObject responseAddress, boolean publicOnly) {
        assertEquals(address.getCity(), responseAddress.getAsString("city"));
        assertEquals(address.getRegion(), responseAddress.getAsString("region"));
        assertEquals(address.getCountry(), responseAddress.getAsString("country"));

        if (!publicOnly) {
            assertEquals(address.getStreetNumber(), responseAddress.getAsString("streetNumber"));
            assertEquals(address.getStreetName(), responseAddress.getAsString("streetName"));
            assertEquals(address.getPostcode(), responseAddress.getAsString("postcode"));
        } else {
            Assertions.assertNull(responseAddress.get("streetNumber"));
            Assertions.assertNull(responseAddress.get("streetName"));
            Assertions.assertNull(responseAddress.get("postcode"));
        }
    }


    public SecurityContext dontUseThisGenerateSecurityContextForUser(User user) {
        return new SecurityContext() {
            @Override
            public Authentication getAuthentication() {
                return new Authentication() {
                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        if (user.getRole() == "ROLE_ADMIN") authorities.add(new SimpleGrantedAuthority("ADMIN"));
                        return authorities;
                    }

                    @Override
                    public Object getCredentials() {
                        return null;
                    }

                    @Override
                    public Object getDetails() {
                        return null;
                    }

                    @Override
                    public Object getPrincipal() {
                        return new BasicUserDetails(user);
                    }

                    @Override
                    public boolean isAuthenticated() {
                        return true;
                    }

                    @Override
                    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

                    }

                    @Override
                    public String getName() {
                        return user.getEmail();
                    }
                };
            }
            @Override
            public void setAuthentication(Authentication authentication) {
            }
        };
    }

}
