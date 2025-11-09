package com.cisowski.schoolmanagement.integration;

public interface BasicCrudHappyPathTests {

    /**
     * Should create a new entity and successfully fetch it by ID.
     */
    void shouldCreateAndFetchEntity();

    /**
     * Should fetch all entities after creating several of them.
     */
    void shouldFetchAllEntities();

    /**
     * Should create an entity, update it, and verify the update.
     */
    void shouldCreateUpdateAndFetchEntity();

    /**
     * Should create an entity, delete it, and verify it no longer exists.
     */
    void shouldCreateDeleteAndNotFetchEntity();
}
