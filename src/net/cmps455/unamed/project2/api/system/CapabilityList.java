package net.cmps455.unamed.project2.api.system;

import java.util.LinkedList;

public class CapabilityList extends LinkedList<Capability> {

    public boolean add(Capability capability) {
        if (capability == null || capability.flag == 0) return false;
        return super.add(capability);
    }

    public int indexOf(VirtualObject o) {
        return super.indexOf(Capability.create(o));
    }

    public int lastIndexOf(VirtualObject o) {
        return super.lastIndexOf(Capability.create(o));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.forEach(capability -> stringBuilder.append(String.format("[%s] → ",capability)));
        stringBuilder.append("<NULL>");

        return stringBuilder.toString();
    }

}
