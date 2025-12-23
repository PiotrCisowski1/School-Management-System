package com.cisowski.schoolmanagement.integration;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer {
    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER;

    static {
        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("sms_test")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        POSTGRES_CONTAINER.start();
    }
}
