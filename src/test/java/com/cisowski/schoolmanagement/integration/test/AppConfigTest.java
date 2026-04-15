package com.cisowski.schoolmanagement.integration.test;


import com.cisowski.schoolmanagement.appConfig.model.AppConfigEntity;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigSummaryResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigUpdateRequest;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigValueType;
import com.cisowski.schoolmanagement.common.model.PagedResponse;
import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import io.restassured.common.mapper.TypeRef;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
                .body("totalElements", equalTo(3));
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

    @Test
    void shouldGetAllPagedConfigSummaryResponses() {
        List<AppConfigEntity> configs = createTestConfigValues();

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("config")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("totalElements", equalTo(configs.size()));
    }

    @Test
    void shouldGetAllPagedConfigSummaryResponsesWithPageableParameters() {
        List<AppConfigEntity> configs = createTestConfigValues();
        AppConfigEntity firstConfig = configs.stream()
                .max(Comparator.comparing(AppConfigEntity::getKey))
                .get();
        int pageSize = 5;
        int pageNo = 0;
        String pageSort = "key,desc";

        PagedResponse<AppConfigSummaryResponse> response = given()
                .headers(fullAdminHeaders)
                .queryParam("page", pageNo)
                .queryParam("size", pageSize)
                .queryParam("sort", pageSort)
        .when()
                .get("config")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("page", equalTo(0))
                .body("size", equalTo(5))
                .body("totalElements", equalTo(configs.size()))
                .extract().
                as(new TypeRef<PagedResponse<AppConfigSummaryResponse>>() {});
        assertEquals(firstConfig.getKey(), response.getContent().get(0).getKey());
    }

    @Test
    void shouldGetAllPagedConfigSummaryResponsesWithDefaultPageable() {
        List<AppConfigEntity> configs = createTestConfigValues();
        AppConfigEntity firstConfig = configs.stream()
                .min(Comparator.comparing(AppConfigEntity::getId))
                .get();

        PagedResponse<AppConfigSummaryResponse> response = given()
                .headers(fullAdminHeaders)
                .when()
                .get("config")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page", equalTo(0))
                .body("size", equalTo(20))
                .body("totalElements", equalTo(configs.size()))
                .extract().
                as(new TypeRef<PagedResponse<AppConfigSummaryResponse>>() {});
        assertEquals(firstConfig.getKey(), response.getContent().get(0).getKey());
    }

    private List<AppConfigEntity> createTestConfigValues() {
        AppConfigEntity configEntity1 = dataHelper.createConfigEntity(AppConfigValueType.TEXT, "test", true, null);
        AppConfigEntity configEntity2 = dataHelper.createConfigEntity(AppConfigValueType.TEXT, "test", true, null);
        AppConfigEntity configEntity3 = dataHelper.createConfigEntity(AppConfigValueType.TEXT, "test", true, null);
        AppConfigEntity configEntity4 = dataHelper.createConfigEntity(AppConfigValueType.TEXT, "test", true, null);
        AppConfigEntity configEntity5 = dataHelper.createConfigEntity(AppConfigValueType.TEXT, "test", true, null);
        AppConfigEntity configEntity6 = dataHelper.createConfigEntity(AppConfigValueType.TEXT, "test", true, null);
        AppConfigEntity configEntity7 = dataHelper.createConfigEntity(AppConfigValueType.TEXT, "test", true, null);
        AppConfigEntity configEntity8 = dataHelper.createConfigEntity(AppConfigValueType.TEXT, "test", true, null);
        AppConfigEntity configEntity9 = dataHelper.createConfigEntity(AppConfigValueType.TEXT, "test", true, null);
        return List.of(configEntity1, configEntity2, configEntity3, configEntity4, configEntity5, configEntity6, configEntity7, configEntity8, configEntity9);
    }
}
