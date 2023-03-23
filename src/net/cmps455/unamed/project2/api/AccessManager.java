package net.cmps455.unamed.project2.api;

public abstract class AccessManager {

    public abstract int getDomainCount();

    public abstract int getFileCount();

    public abstract boolean canRead(int domain, int object);

    public abstract boolean canWrite(int domain, int object);

    public abstract boolean canSwitch(int domainSource, int domainTarget);

    public abstract void set(int domain, int object, int value);

    @Override
    public abstract String toString();
}
