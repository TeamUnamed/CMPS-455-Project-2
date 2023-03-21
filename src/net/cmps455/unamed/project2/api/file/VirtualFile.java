package net.cmps455.unamed.project2.api.file;

import net.cmps455.unamed.project2.api.VirtualObject;
import net.cmps455.unamed.project2.api.action.VirtualAction;
import net.cmps455.unamed.project2.api.system.VirtualSystem;

/**
 * Reference Container to a file in {@link VirtualSystem}.
 */
public class VirtualFile extends VirtualObject {

    /**
     * Action representing ability to read from a file.
     */
    public static final VirtualAction READ = new VirtualAction("read");
    /**
     * Action representing ability to write to a file.
     */
    public static final VirtualAction WRITE = new VirtualAction("write");
    /**
     * Action representing ability to read from or write to a file.
     */
    public static final VirtualAction READ_WRITE = new VirtualAction("read/write", READ, WRITE);

    public VirtualFile(VirtualSystem.SystemID id, String name) {
        super(id, name);
    }

}
