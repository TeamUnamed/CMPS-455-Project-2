package net.cmps455.unamed.project2;

public interface AccessLocked {
    default boolean isDomain() {
        return false;
    }
}
