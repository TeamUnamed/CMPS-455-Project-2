package net.cmps455.unamed.project2.api.file;

import net.cmps455.unamed.project2.api.VirtualObject;
import net.cmps455.unamed.project2.api.system.VirtualSystem;

/**
 * Reference Container to a file in {@link VirtualSystem}.
 */
public class VirtualFile extends VirtualObject {

    public VirtualFile(VirtualSystem.SystemID id, String name) {
        super(id, name);
    }

}
