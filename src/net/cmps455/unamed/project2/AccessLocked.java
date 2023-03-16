package net.cmps455.unamed.project2;

public interface AccessLocked {

    int getUID();

    default boolean isDomain() {
        return false;
    }
}
