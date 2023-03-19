package net.cmps455.unamed.project2.api.system;

import java.util.LinkedList;

public class CapabilityList extends LinkedList<Capability> {

    private final VirtualDomain domain;

    public CapabilityList (VirtualDomain domain) {
        this.domain = domain;
    }

    public int getDomainId() {
        return this.domain.getID();
    }

    @Override
    public boolean add(Capability capability) {
        if (capability == null || capability.flag == 0) return false;
        return super.add(capability);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%s :: ", domain));
        this.forEach(capability -> stringBuilder.append(String.format("[%s] → ",capability)));
        stringBuilder.append("<NULL>");
        return stringBuilder.toString();
    }
}
