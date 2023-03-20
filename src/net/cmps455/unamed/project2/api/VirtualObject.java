package net.cmps455.unamed.project2.api;

import net.cmps455.unamed.project2.api.system.VirtualSystem;

/**
 * Reference Container to an object in {@link VirtualSystem}.
 */
public abstract class VirtualObject {

    private final VirtualSystem.SystemID id;

    protected String name;

    public VirtualObject(VirtualSystem.SystemID id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get the name of this object.
     * @return {@link String} name of object.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the System ID of this object.
     * @return {@link VirtualSystem.SystemID}
     */
    public VirtualSystem.SystemID getId() {
        return id;
    }

    /**
     * Convert object reference to string.
     * @return {@link String} name of object.
     */
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VirtualObject)) return false;

        return this.id.equals(((VirtualObject) obj).id);
    }
}
