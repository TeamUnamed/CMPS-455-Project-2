package net.cmps455.unamed.project2.api.file;

import net.cmps455.unamed.project2.api.system.VirtualSystem;
import net.cmps455.unamed.project2.api.system.VirtualSystemMemory;

/**
 * Opens a read-only connection to the data of a file.
 */
public class VirtualFileReader {

    private final VirtualFile file;

    public VirtualFileReader(VirtualFile file) throws IllegalAccessException {
        VirtualSystem system = VirtualSystem.For(file);

        system.getMemory().open(VirtualSystemMemory.Connection.READ, file);
        this.file = file;
    }

    /**
     * Read data of the file.
     * @return {@link String} data
     * @throws IllegalAccessException passed from {@link VirtualSystemMemory}
     */
    public String read() throws IllegalAccessException {
        VirtualSystem system = VirtualSystem.For(file);
        return system.getMemory().read(file);
    }

    /**
     * Close the connection to the file's data.
     */
    public void close() {
        VirtualSystem system = VirtualSystem.For(file);
        system.getMemory().close(VirtualSystemMemory.Connection.READ, file);
    }
}
