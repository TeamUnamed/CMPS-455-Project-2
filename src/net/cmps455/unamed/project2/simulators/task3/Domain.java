package net.cmps455.unamed.project2.simulators.task3;

import net.cmps455.unamed.project2.AccessLocked;

public class Domain implements AccessLocked {

    private String name;

    public Domain(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isDomain() {
        return true;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
