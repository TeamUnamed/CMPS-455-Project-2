package net.cmps455.unamed.project2.api.system;

import net.cmps455.unamed.project2.api.Capability;
import net.cmps455.unamed.project2.api.VirtualObject;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VirtualSystemMemory {

    private final ReadWriteLock[] locks;
    private final Connection[] connections;
    private final String[] data;

    private final Semaphore mutex;

    VirtualSystemMemory (int size) {
        this.locks = new ReentrantReadWriteLock[size];
        this.connections = new Connection[size];
        this.data = new String[size];

        this.mutex = new Semaphore(1);

        for (int i = 0; i < size; i++)
            locks[i] = new ReentrantReadWriteLock();
    }

    /**
     * Open a new connection with an object's data.
     * @param type {@link Connection} type.
     * @param obj {@link VirtualObject} to get data for.
     * @throws IllegalAccessException if the object is <u>null</u> /or/ the calling
     * VirtualDomain does not have permission to open connection type.
     */
    public void open(Connection type, VirtualObject obj) throws IllegalAccessException {
        if (obj== null)
            throw new IllegalArgumentException("Virtual Object cannot be null");

        int index = obj.getId().id;
        ReadWriteLock lock = locks[index];

        if (type == Connection.READ) {
            if (!VirtualSystem.For(obj).getPermission(obj, Capability.READ))
                throw new IllegalAccessException("No read permission for " + obj.getName());

            lock.readLock().lock();
        } else {
            if (!VirtualSystem.For(obj).getPermission(obj, Capability.READ))
                throw new IllegalAccessException("No write permission for " + obj.getName());
            lock.writeLock().lock();
        }

        if (connections[index] != type) {
            try {
                mutex.acquire();
                connections[index] = type;
            } catch (InterruptedException e) {
                throw new IllegalAccessException("Connection interrupted");
            }

            mutex.release();
        }

    }

    /**
     * Close connection with file's data.
     * @param type {@link Connection} type.
     * @param obj {@link VirtualObject} associated with data.
     */
    public void close(Connection type, VirtualObject obj) {
        if (obj== null)
            throw new IllegalArgumentException("Virtual Object cannot be null");

        int index = obj.getId().id;

        if (connections[index] != type)
            return;

        ReadWriteLock lock = locks[index];

        if (type == Connection.READ) {
            lock.readLock().unlock();
        } else {
            lock.writeLock().unlock();
        }
    }

    /**
     * Read data for associated object.
     * @param obj {@link VirtualObject} with associated data.
     * @return {@link String} data.
     * @throws IllegalAccessException if the object is <u>null</u> /or/ the connection is closed.
     */
    public String read(VirtualObject obj) throws IllegalAccessException {
        if (obj== null)
            throw new IllegalArgumentException("Virtual Object cannot be null");
        if (connections[obj.getId().id] != Connection.READ)
            throw new IllegalAccessException("Connection not open");

        return data[obj.getId().id];
    }

    /**
     * Write to the data for associated object.
     * @param obj {@link VirtualObject} with associated data.
     * @param s {@link String} data to write.
     * @return {@link String} previously associated data.
     * @throws IllegalAccessException if the object is <u>null</u> /or/ the connection is closed.
     */
    public String write(VirtualObject obj, String s) throws IllegalAccessException {
        if (obj== null)
            throw new IllegalArgumentException("Virtual Object cannot be null");
        if (connections[obj.getId().id] != Connection.WRITE)
            throw new IllegalAccessException("Connection not open");

        String old = data[obj.getId().id];
        data[obj.getId().id] = s;
        return old;
    }

    /**
     * Connection types
     */
    public enum Connection {READ, WRITE}
}
