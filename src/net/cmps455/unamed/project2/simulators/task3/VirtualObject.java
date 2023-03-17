package net.cmps455.unamed.project2;

public interface VirtualObject {
    default boolean isDomain() {
        return false;
    }

    int getID();
}
