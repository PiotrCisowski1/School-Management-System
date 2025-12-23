package com.cisowski.schoolmanagement.integration.test;


import com.cisowski.schoolmanagement.appConfig.model.AppConfigEntity;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigUpdateRequest;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigValueType;
import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AppConfigTest extends BaseIntegrationTest {

    @Test
    void shouldUpdateConfigHappyPath() {
        AppConfigValueType configValueType = AppConfigValueType.INTEGER;
        String integerValue = "123";
        AppConfigEntity configEntity = dataHelper.createConfigEntity(configValueType, integerValue, true, null);
        AppConfigUpdateRequest updateRequest = Instancio.create(AppConfigUpdateRequest.class);
        updateRequest.setAuthoritiesToAddAsEditableBy(null);
        updateRequest.setAuthoritiesToRemoveAsEditableBy(null);
        updateRequest.setValue("467");

        given()
                .headers(fullAdminHeaders)
                .body(updateRequest)
        .when()
                .patch("config/" + configEntity.getKey())
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("value", equalTo(updateRequest.getValue()))
                .body("minValue", equalTo(updateRequest.getMinValue()))
                .body("maxValue", equalTo(updateRequest.getMaxValue()))
                .body("description", equalTo(updateRequest.getDescription()));
    }

    @Test
    void shouldGetAllConfigValues() {
        AppConfigEntity configEntity1 = dataHelper.createConfigEntity(null, null, true, null);
        AppConfigEntity configEntity2 = dataHelper.createConfigEntity(null, null, true, null);
        AppConfigEntity configEntity3 = dataHelper.createConfigEntity(null, null, true, null);

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("config")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(3));
    }

    @Test
    void shouldGetConfigByKey() {
        AppConfigEntity configEntity = dataHelper.createConfigEntity(null, null, true, null);

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("config/" + configEntity.getKey())
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("key", equalTo(configEntity.getKey()))
                .body("value", equalTo(configEntity.getValue()))
                .body("valueType", equalTo(configEntity.getValueType().name()))
                .body("minValue", equalTo(configEntity.getMinValue()))
                .body("maxValue", equalTo(configEntity.getMaxValue()))
                .body("description", equalTo(configEntity.getDescription()));
    }


}
