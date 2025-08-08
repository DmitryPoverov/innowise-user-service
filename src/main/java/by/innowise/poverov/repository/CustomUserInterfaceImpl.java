package by.innowise.poverov.repository;

import by.innowise.poverov.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CustomUserInterfaceImpl implements CustomUserInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User updateUserById(Long id, User user) {
        User userFromDB = entityManager.find(User.class, id);
        if (userFromDB != null) {
            userFromDB.setName(user.getName());
            userFromDB.setSurname(user.getSurname());
            userFromDB.setBirthDate(user.getBirthDate());
            userFromDB.setEmail(user.getEmail());
            return entityManager.merge(userFromDB);
        }
        return null;
    }
}
