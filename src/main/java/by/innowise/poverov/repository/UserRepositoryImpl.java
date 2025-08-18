package by.innowise.poverov.repository;

import by.innowise.poverov.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User updateUserById(Long id, User user) {
        User userFromDB = entityManager.find(User.class, id);
        if (userFromDB != null) {
            userFromDB.setName(user.getName());
            userFromDB.setSurname(user.getSurname());
            userFromDB.setBirthDate(user.getBirthDate());
            userFromDB.setEmail(user.getEmail());
            return userFromDB;
        }
        return null;
    }
}
