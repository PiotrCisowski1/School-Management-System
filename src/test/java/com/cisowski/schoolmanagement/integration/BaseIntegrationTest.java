package com.cisowski.schoolmanagement.integration;

import com.cisowski.schoolmanagement.integration.helper.DatabaseHelper;
import com.cisowski.schoolmanagement.integration.helper.TestDataHelper;
import com.cisowski.schoolmanagement.users.common.model.UserDetailsEntity;
import com.cisowski.schoolmanagement.common.security.authentication.JwtService;
import com.cisowski.schoolmanagement.users.common.service.SchoolUserDetailsServiceImpl;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.CollectionUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    protected final List<String> userTypesOtherThanAdmin = List.of("PARENT", "STUDENT", "TEACHER");
    protected Headers fullAdminHeaders;

    @Autowired
    private SchoolUserDetailsServiceImpl userDetailsService;

    @Autowired
    protected TestDataHelper dataHelper;

    static {
        PostgresTestContainer.POSTGRES_CONTAINER.start();
    }

    @LocalServerPort
    protected int port;

    @Autowired
    private DatabaseHelper dbHelper;

    @Autowired
    private JwtService tokenService;


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PostgresTestContainer.POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", PostgresTestContainer.POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", PostgresTestContainer.POSTGRES_CONTAINER::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "false");
    }

    @BeforeEach
    void setUpBase() {
        RestAssured.port = port;
        RestAssured.basePath = "/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.defaultParser = Parser.JSON;

        dbHelper.clearData();
        dbHelper.fillBasicAuthorities();

        fullAdminHeaders = createHeadersWithRandomAdminUser();
    }

    @AfterEach
    void tearDownBase() {
        dbHelper.clearData();
        RestAssured.reset();
    }

    protected Headers buildBasicHeaders(String email) {
        List<Header> headerList = new ArrayList<>();
        UserDetailsEntity userDetails = (UserDetailsEntity) userDetailsService.loadUserByUsername(email);
        if(userDetails != null && userDetails.getUser() != null) {
            Header header = new Header("Authorization", "Bearer " + tokenService.generateToken(userDetails.getUser()));
            headerList.add(header);
        }

        Header header = new Header("Content-Type", ContentType.JSON.toString());
        headerList.add(header);

        return new Headers(headerList);
    }

    protected Headers createHeadersWithRandomAdminUser() {
        String adminEmail = dataHelper.createRandomAdminUser().getEmail();
        return buildBasicHeaders(adminEmail);
    }

    protected Headers createHeadersWithRandomTeacherUser() {
        String teacherEmail = dataHelper.createTeacher(Collections.emptyList()).getEmail();
        return buildBasicHeaders(teacherEmail);
    }

    protected Headers createHeadersForRandomUserNotAdmin(List<String> userTypes) {
        if(CollectionUtils.isEmpty(userTypes))
            userTypes = userTypesOtherThanAdmin;
        Random random = new Random();
        String randomUserType = userTypes.get(random.nextInt(userTypes.size()));

        String userEmail = switch (randomUserType) {
            case "TEACHER" -> dataHelper.createTeacher(Collections.emptyList()).getEmail();
            case "STUDENT" -> dataHelper.createStudent(dataHelper.createYearbook(Collections.emptyList(), null), Collections.emptyList()).getEmail();
            case "PARENT" -> dataHelper.createRandomParent().getEmail();
            default -> null;
        };
        return buildBasicHeaders(userEmail);
    }

    protected Integer postEntity(Headers headers, String endpointPath, Object request) {
        return given()
                .headers(headers)
                .body(request)
            .when()
                .post(endpointPath)
            .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .extract().path("id");
    }
}
