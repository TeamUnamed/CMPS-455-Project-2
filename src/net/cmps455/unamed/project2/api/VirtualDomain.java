package net.cmps455.unamed.project2.api;

import net.cmps455.unamed.project2.api.system.VirtualSystem;

/**
 * Reference Container to a domain in {@link VirtualSystem}.
 */
public class VirtualDomain extends VirtualObject {

    public VirtualDomain(VirtualSystem.SystemID id, String name) {
        super(id, name);
    }

}
