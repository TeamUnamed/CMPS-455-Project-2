package net.cmps455.unamed.project2.api.system;

public abstract class VirtualObject {

    private static int counter = 1;
    private final int id;
    private final VirtualSystem system;

    protected String name;

    public VirtualObject(String name, VirtualSystem system) {
        this.id = counter++;
        this.name = name;
        this.system = system;
    }

    public String getName() {
        return this.name;
    }

    public final int getID() {
        return this.id;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VirtualObject)) return false;

        return this.id == ((VirtualObject) obj).id;
    }
}
