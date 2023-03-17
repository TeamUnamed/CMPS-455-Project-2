package net.cmps455.unamed.project2.simulators.task3;

public interface VirtualObject {
    default boolean isDomain() {
        return false;
    }

    int getID();
}
