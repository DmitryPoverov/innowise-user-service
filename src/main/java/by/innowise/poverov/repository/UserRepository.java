package by.innowise.poverov.repository;

import by.innowise.poverov.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * Extends {@link org.springframework.data.jpa.repository.JpaRepository} to provide standard database
 * CRUD methods and as well as custom query methods derived from method names.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * {@link JpaRepository}->{@link CrudRepository} already has such a method {@code findById()}.<br><br>
     * My custom implementation: <br>
     * 1. with JPQL:
     * <pre>
     * {@code @Query("SELECT u FROM User u WHERE u.id=:id")
     * Optional<User> findUserById(@Param("id")Long id);}
     * </pre>
     * 2. with SQL:
     * <pre>
     * {@code @Query(value = "SELECT * FROM users WHERE id=:id", nativeQuery = true)
     * Optional<User> findUserById(@Param("id")Long id);}
     * </pre>
     *
     * Retrieves a user by its unique identifier.
     * @param id the ID of the user to find, must not be {@code null}
     * @return an {@link Optional} containing the found user, or empty if no user exists with the given ID
     */
    Optional<User> findUserById(Long id);


    /**
     * {@link JpaRepository}->{@link CrudRepository} already has such a method {@code findAllById()}.<br><br>
     * My custom implementation: <br>
     * 1. with JPQL:
     * <pre>
     * {@code @Query("SELECT u FROM User u WHERE u.id IN (:ids)")
     * List<User> findAllByIdIn(@Param("ids") List<Long> ids);}
     * </pre>
     * 2. with SQL:
     * <pre>
     * {@code @Query(value = "SELECT * FROM users WHERE id IN (:ids)", nativeQuery = true)
     * List<User> findAllByIdIn(@Param("ids") List<Long> ids);}
     * </pre>
     *
     * Retrieves all users whose IDs are contained in the given list.
     * @param ids the list of user IDs to look for, must not be {@code null}
     * @return a list of matching {@link User} entities, possibly empty if none match.
     */
    List<User> findAllByIdIn(List<Long> ids);


    /**
     * My custom implementation: <br>
     * 1. with JPQL:
     * <pre>
     * {@code
     * @Query("SELECT u FROM User u WHERE u.email = :email")}
     * </pre>
     * 2. with SQL:
     * <pre>
     * {@code
     * @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
     * Optional<User> findUserByEmail(@Param("email") String email);}
     * </pre>
     *
     * Retrieves a user by their email address.
     * @param email the email address to look for, must not be {@code null}
     * @return an {@link Optional} containing the found user, or empty if no user exists with the given email.
     */
    Optional<User> findUserByEmail(String email);


    /**
     * My custom implementation with native SQL:
     * <pre>
     * {@code
     * @Query(value = "UPDATE users SET name=:name, surname=:surname, email=:email, birth_date=:birthDate WHERE id=:id",
     * nativeQuery = true)}
     * </pre>
     *
     * The method updates the fields of a user by their ID.<br>
     * @param id the ID of the user to update, must not be null
     * @param name the new name of the user, must not be null
     * @param surname the new surname of the user, must not be null
     * @param email the new email of the user, must not be null
     * @param birthDate the new birthdate of the user, must not be null
     * @return the number of users updated (should be 0 or 1)
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.name=:name, u.surname=:surname, u.email=:email, u.birthDate=:birthDate WHERE u.id=:id")
    int updateUserFieldsById(@Param("id") Long id,
                             @Param("name") String name,
                             @Param("surname") String surname,
                             @Param("email") String email,
                             @Param("birthDate") LocalDate birthDate);


    /**
     * {@link JpaRepository}->{@link CrudRepository} already has such a method {@code deleteById()},
     * but it returns {@code void}. <br><br>
     * My custom implementation: <br>
     * 1. with JPQL:
     * <pre>
     * {@code @Query("DELETE FROM User u WHERE u.id=:id")}
     * </pre>
     * 2. with SQL:
     * <pre>
     * {@code @Query(value = "DELETE FROM users WHERE id=:id", nativeQuery = true)
     * int deleteUserById(@Param("id") Long id);}
     * </pre>
     *
     * Deletes a user by its unique identifier.
     * This operation is transactional to ensure data integrity.
     * @param id the ID of the user to delete, must not be {@code null}
     */
    @Modifying
    int deleteUserById(Long id);

    boolean existsByEmail(String email);
}
