package net.cmps455.unamed.project2.simulators.task3;

import java.util.Collection;
import java.util.LinkedList;

public class CapabilityList extends LinkedList<Capability> {

    @Override
    public boolean add(Capability capability) {
        if (capability.permission.value == 0) return false;
        return super.add(capability);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.forEach(capability -> stringBuilder.append(String.format("[%-15s] → ",capability)));
        stringBuilder.append("<NULL>");
        return stringBuilder.toString();
    }
}
