package net.cmps455.unamed.project2.api;

public abstract class AccessManager {

    /**
     * Get the number of domains support by the AccessManager
     * @return number of supported domains
     */
    public abstract int getDomainCount();

    /**
     * Get the number of files support by the AccessManager
     * @return number of supported files
     */
    public abstract int getFileCount();

    /**
     * Can a domain read from a file
     * @param domain domain to check permission of
     * @param object file to check permission for
     * @return {@code true} if allowed; {@code false} otherwise
     */
    public abstract boolean canRead(int domain, int object);

    /**
     * Can a domain write to a file
     * @param domain domain to check permission of
     * @param object file to check permission for
     * @return {@code true} if allowed; {@code false} otherwise
     */
    public abstract boolean canWrite(int domain, int object);

    /**
     * Can a domain switch to another domain
     * @param domainSource domain to check permission of
     * @param domainTarget domain to check permission for
     * @return {@code true} if allowed; {@code false} otherwise
     */
    public abstract boolean canSwitch(int domainSource, int domainTarget);

    /**
     * Set the permission of a domain for an object
     * @param domain domain to set permission of
     * @param object object to set permission for
     * @param value permission value to set
     */
    public abstract void set(int domain, int object, int value);

    @Override
    public abstract String toString();
}
