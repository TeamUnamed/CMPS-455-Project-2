package net.cmps455.unamed.project2.api;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileManager {

    private final String[] fileData;
    private final ReadWriteLock[] locks;

    public FileManager(int fileCount) {
        this.fileData = new String[fileCount];
        this.locks = new ReadWriteLock[fileCount];
        for (int i = 0; i < fileCount; i++) {
            locks[i] = new ReentrantReadWriteLock();
        }
    }

    public void open(int file, FileManager.Access access) {
        if (access == FileManager.Access.READ)
            locks[file].readLock().lock();
        else
            locks[file].writeLock().lock();
    }

    public void close(int file, FileManager.Access access) {
        if (access == FileManager.Access.READ)
            locks[file].readLock().unlock();
        else
            locks[file].writeLock().unlock();
    }

    public String read(int file) {
        return fileData[file];
    }

    public void write(int file, String data) {
        fileData[file] = data;
    }

    public enum Access {
        READ, WRITE
    }
}