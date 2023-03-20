package net.cmps455.unamed.project2.api.file;

import net.cmps455.unamed.project2.api.system.VirtualSystem;
import net.cmps455.unamed.project2.api.system.VirtualSystemMemory;

/**
 * Opens a connection to the data of a file.
 */
public class VirtualFileWriter {

    private final VirtualFile file;

    public VirtualFileWriter(VirtualFile file) throws IllegalAccessException {
        VirtualSystem system = VirtualSystem.For(file);

        system.getMemory().open(VirtualSystemMemory.Connection.WRITE, file);
        this.file = file;
    }

    /**
     * Read data of the file.
     * @return {@link String} data
     * @throws IllegalAccessException passed from {@link VirtualSystemMemory}
     */
    @SuppressWarnings("unused")
    public String read() throws IllegalAccessException {
        VirtualSystem system = VirtualSystem.For(file);
        return system.getMemory().read(file);
    }

    /**
     * Writes over the data of a file
     * @param s {@link String} data to write.
     * @return Previous {@link String} data associated with file.
     * @throws IllegalAccessException passed from {@link VirtualSystemMemory}
     */
    @SuppressWarnings("UnusedReturnValue")
    public String write(String s) throws IllegalAccessException {
        VirtualSystem system = VirtualSystem.For(file);
        return system.getMemory().write(file, s);
    }

    /**
     * Close the connection to the file's data.
     */
    public void close() {
        VirtualSystem system = VirtualSystem.For(file);
        system.getMemory().close(VirtualSystemMemory.Connection.WRITE, file);
    }

}
