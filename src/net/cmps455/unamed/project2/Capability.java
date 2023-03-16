package net.cmps455.unamed.project2;

public class Capability {

    public final AccessLocked object;
    public final int permission;

    public Capability(AccessLocked object, int permission) {
        this.object = object;
        this.permission = permission;
    }
}
