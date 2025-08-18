package by.innowise.poverov.exception;

public class EntityIsNotUniqueCustomException extends RuntimeException {

    public EntityIsNotUniqueCustomException(Object data) {
        super("User with data=" + data + " already exists.");
    }
}
