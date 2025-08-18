package by.innowise.poverov.exception;

public class EntityNotFoundCustomException extends RuntimeException {

    public EntityNotFoundCustomException(Object data) {
        super("User with data=" + data + " not found.");
    }
}
