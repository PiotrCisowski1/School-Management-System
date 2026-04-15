package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PageableGlobalTest extends BaseIntegrationTest {

    @Test
    void shouldLimitMaxPageSizeToHundred() {
        given()
                .headers(fullAdminHeaders)
                .queryParam("size", 230)
        .when()
                .get("config")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("size", equalTo(100));
    }
}
