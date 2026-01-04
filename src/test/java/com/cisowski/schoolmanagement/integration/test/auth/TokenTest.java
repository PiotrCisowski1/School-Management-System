package com.cisowski.schoolmanagement.integration.test.auth;

import com.cisowski.schoolmanagement.common.security.authentication.JwtService;
import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.users.common.model.LoginResponse;
import com.cisowski.schoolmanagement.users.common.model.UserDetailsEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TokenTest extends BaseIntegrationTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService tokenService;

    @Test
    void shouldRefreshTokenAndReturnNewOne() {
        Header header = new Header("Content-Type", ContentType.JSON.toString());
        List<Header> headerList = new ArrayList<>(List.of(header));

        UserEntity user = dataHelper.createRandomAdminUser();
        UserDetailsEntity userDetails = (UserDetailsEntity) userDetailsService.loadUserByUsername(user.getEmail());
        String token = tokenService.generateToken(userDetails.getUser());

        Header authHeader = new Header("Authorization", "Bearer " + token);
        headerList.add(authHeader);

        Headers headers = new Headers(headerList);

        LoginResponse response = given()
                .headers(headers)
        .when()
                .post("/token/refresh")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract().as(LoginResponse.class);
        assertNotEquals(token, response.getToken());
    }

    @Test
    void shouldNotLetRefreshToUnauthenticatedUser() {
        given()
                .headers(new Headers())
        .when()
                .post("/token/refresh")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }


}
