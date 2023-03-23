package net.cmps455.unamed.project2.api;

public class DomainManager {

    private final int[] context;

    public DomainManager(int domainCount) {
        this.context = new int[domainCount];
        for (int i = 0; i < domainCount; i++) {
            context[i] = i;
        }
    }

    public int getDomainCount() {
        return context.length;
    }

    public int getContext(int domain) {
        return context[domain];
    }

    public void setContext(int domainSource, int domainTarget) {
        context[domainSource] = domainTarget;
    }
}