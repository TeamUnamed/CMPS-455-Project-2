package net.cmps455.unamed.project2.api;

public class DomainManager {

    private final int[] context;

    /**
     * Constructor
     * @param domainCount domains to support
     */
    public DomainManager(int domainCount) {
        this.context = new int[domainCount];
        for (int i = 0; i < domainCount; i++) {
            context[i] = i;
        }
    }

    /**
     * Gets the number of domains support by this DomainManager
     * @return number of domains supported
     */
    public int getDomainCount() {
        return context.length;
    }

    /**
     * Gets the current context of a domain
     * @param domain domain to get context for
     * @return context of a domain
     */
    public int getContext(int domain) {
        return context[domain];
    }

    /**
     * Sets the context of a domain
     * @param domainSource domain to set the context of
     * @param domainTarget domain to set the context to
     */
    public void setContext(int domainSource, int domainTarget) {
        context[domainSource] = domainTarget;
    }
}