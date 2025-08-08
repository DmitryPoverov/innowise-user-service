package by.innowise.poverov.repository;

import by.innowise.poverov.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Card} entities.
 * Extends {@link JpaRepository} to provide standard database CRUD methods and
 * custom query methods derived from method names.
 */
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * {@link JpaRepository} -> {@link CrudRepository} already has a method {@code save(S entity)}.<br><br>
     * This is a default method that delegates to the {@link #save(Object)} method.
     * It serves as an explicit alias for creating a card. Saves a given {@link Card} entity.
     *
     * @param card the {@code Card} entity to save, must not be {@code null}
     * @return the saved {@code Card} entity
     */
    default Card saveCard(Card card) {
        return save(card);
    }

    /**
     * {@link JpaRepository} -> {@link CrudRepository} already has such a method {@code findById()}.<br><br>
     * My custom implementation:<br>
     * 1. with JPQL:
     * <pre>
     * {@code @Query("SELECT c FROM Card c WHERE c.id=:id")
     * Optional<Card> findCardById(@Param("id") Long id);}
     * </pre>
     * 2. with SQL:
     * <pre>
     * {@code @Query(value = "SELECT * FROM card_info WHERE id=:id", nativeQuery = true)
     * Optional<Card> findCardById(@Param("id") Long id);}
     * </pre>
     *
     * Retrieves a card by its unique identifier.
     * @param id the ID of the card to find, must not be {@code null}
     * @return an {@link Optional} containing the found card, or empty if no card exists with the given ID
     */
    Optional<Card> findCardById(Long id);

    /**
     * {@link JpaRepository} -> {@link CrudRepository} already has such a method {@code findAllById()}.<br><br>
     * My custom implementation:<br>
     * 1. with JPQL:
     * <pre>
     * {@code @Query("SELECT c FROM Card c WHERE c.id IN (:ids)")
     * List<Card> findAllByIdIn(@Param("ids") List<Long> ids);}
     * </pre>
     * 2. with SQL:
     * <pre>
     * {@code @Query(value = "SELECT * FROM card_info WHERE id IN (:ids)", nativeQuery = true)
     * List<Card> findAllByIdIn(@Param("ids") List<Long> ids);}
     * </pre>
     *
     * Retrieves all cards whose IDs are contained in the given list.
     * @param ids the list of card IDs to look for, must not be {@code null}
     * @return a list of matching {@link Card} entities, possibly empty if none match
     */
    List<Card> findAllByIdIn(List<Long> ids);

    /**
     * My custom implementation with native SQL:<br>
     * <pre>
     * {@code
     * @Modifying(clearAutomatically = true)
     * @Query(value = "UPDATE card_info SET number=:number, holder=:holder, expiration_date=:expirationDate WHERE id=:id", nativeQuery = true)}
     * </pre>
     *
     * The method updates the fields of a card by their ID.
     * @param id the ID of the card to update, must not be {@code null}
     * @param number the new card number, must not be {@code null}
     * @param holder the new cardholder, must not be {@code null}
     * @param expirationDate the new expiration date, must not be {@code null}
     * @return the number of cards updated (should be 0 or 1)
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Card c SET c.number=:number, c.holder=:holder, c.expirationDate=:expirationDate WHERE c.id=:id")
    int updateCardFieldsById(@Param("id") Long id,
                             @Param("number") String number,
                             @Param("holder") String holder,
                             @Param("expirationDate") LocalDate expirationDate);

    /**
     * {@link JpaRepository} -> {@link CrudRepository} already has such a method {@code deleteById()},
     * but it returns {@code void}.<br><br>
     * My custom implementation:<br>
     * 1. with JPQL:
     * <pre>
     * {@code @Modifying @Query("DELETE FROM Card c WHERE c.id=:id")}
     * </pre>
     * 2. with SQL:
     * <pre>
     * {@code @Modifying @Query(value = "DELETE FROM card_info WHERE id=:id", nativeQuery = true)
     * int deleteCardById(@Param("id") Long id);}
     * </pre>
     *
     * Deletes a card by its unique identifier.
     * This operation is transactional to ensure data integrity.
     * @param id the ID of the card to delete, must not be {@code null}
     */
    @Modifying
    @Transactional
    void deleteCardById(Long id);
}