package net.cmps455.unamed.project2.api;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileManager {

    private final String[] fileData;
    private final ReadWriteLock[] locks;

    /**
     * Constructor
     * @param fileCount number of files to support
     */
    public FileManager(int fileCount) {
        this.fileData = new String[fileCount];
        this.locks = new ReadWriteLock[fileCount];
        for (int i = 0; i < fileCount; i++) {
            locks[i] = new ReentrantReadWriteLock();
        }
    }

    /**
     * Open a file
     * @apiNote Uses a {@link ReentrantReadWriteLock ReadWriteLock} to lock file access
     * @param file file to open
     * @param access access type
     * @see FileManager.Access
     */
    public void open(int file, FileManager.Access access) {
        if (access == FileManager.Access.READ)
            locks[file].readLock().lock();
        else
            locks[file].writeLock().lock();
    }

    /**
     * Close a file
     * @apiNote Uses a {@link ReentrantReadWriteLock ReadWriteLock} to lock file access
     * @param file file to close
     * @param access access type
     * @see FileManager.Access
     */
    public void close(int file, FileManager.Access access) {
        if (access == FileManager.Access.READ)
            locks[file].readLock().unlock();
        else
            locks[file].writeLock().unlock();
    }

    /**
     * Read data from a file
     * @param file file to read data from
     * @return data of a file
     */
    public String read(int file) {
        return fileData[file];
    }

    /**
     * Write data to a file
     * @param file file to write data to
     * @param data data to write
     */
    public void write(int file, String data) {
        fileData[file] = data;
    }

    /**
     * Access type of a file
     */
    public enum Access {
        READ, WRITE
    }
}