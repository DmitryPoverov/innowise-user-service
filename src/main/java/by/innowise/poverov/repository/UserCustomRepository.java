package by.innowise.poverov.repository;

import by.innowise.poverov.entity.User;

/**
 * Custom repository interface for User update operations.
 * <br>
 * This interface defines a method to update a User by its ID.
 * The implementation will handle the update logic.
 */
public interface UserCustomRepository {

    /**
     * Update a user by its ID.
     *
     * @param user the User object containing new data, must not be null
     * @param id the ID of the user to update, must not be null
     * @return the updated User object
     */
    User updateUserById(Long id, User user);
}
