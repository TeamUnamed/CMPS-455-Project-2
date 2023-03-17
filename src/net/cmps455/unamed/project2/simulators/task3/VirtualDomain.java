package net.cmps455.unamed.project2.simulators.task3;

public interface VirtualDomain extends VirtualObject {

    @Override
    default boolean isDomain() {
        return true;
    }

}
