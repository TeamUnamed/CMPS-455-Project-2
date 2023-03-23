package net.cmps455.unamed.project2.simulators.task3;

import net.cmps455.unamed.project2.api.AccessManager;
import net.cmps455.unamed.project2.api.DomainManager;

import java.util.LinkedList;

public class CapabilityListManager extends AccessManager {

    private final CapabilityList[] capabilityLists;
    private final DomainManager domainManager;
    private final int domainCount;
    private final int fileCount;

    public CapabilityListManager(DomainManager domainManager, int fileCount) {
        this.domainManager = domainManager;
        this.domainCount = domainManager.getDomainCount();
        this.fileCount = fileCount;

        this.capabilityLists = new CapabilityList[domainCount];

        for (int i = 0; i < domainCount; i++) {
            capabilityLists[i] = new CapabilityList(i);
        }
    }

    @Override
    public int getDomainCount() {
        return domainCount;
    }

    @Override
    public int getFileCount() {
        return fileCount;
    }

    @Override
    public boolean canRead(int domain, int object) {
        return capabilityLists[domainManager.getContext(domain)].hasReadAccess(object);
    }

    @Override
    public boolean canWrite(int domain, int object) {
        return capabilityLists[domainManager.getContext(domain)].hasWriteAccess(object);
    }

    @Override
    public boolean canSwitch(int domainSource, int domainTarget) {
        return capabilityLists[domainManager.getContext(domainSource)].canSwitch(domainTarget);
    }

    @Override
    public void set(int domain, int object, int value) {
        capabilityLists[domain].set(object, value);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        int count = 1;
        for (CapabilityList list : capabilityLists) {
            output.append(list).append(capabilityLists.length == count++ ? "" : "; ");
        }
        return output.toString();
    }

    private class CapabilityList {
        final int id;
        final LinkedList<CapabilityListEntry> list;

        CapabilityList (int id) {
            this.id = id;
            this.list = new LinkedList<>();
        }

        boolean hasReadAccess(int object) {
            int index = list.indexOf(CapabilityListEntry.For(object, false));

            if (index == -1)
                return false;

            return (list.get(index).value & 0b001) == 0b001;
        }

        boolean hasWriteAccess(int object) {
            int index = list.indexOf(CapabilityListEntry.For(object, false));

            if (index == -1)
                return false;

            return (list.get(index).value & 0b010) == 0b010;
        }

        boolean canSwitch(int object) {
            int index = list.indexOf(CapabilityListEntry.For(object, true));

            if (index == -1)
                return false;

            return (list.get(index).value & 0b100) == 0b100;
        }

        void set(int object, int value) {
            boolean isDomain = object >= fileCount;
            int id = object - (isDomain ? fileCount : 0);

            int index = list.indexOf(CapabilityListEntry.For(id, isDomain));

            if (index > -1 && value > 0) {
                list.get(index).value = value;
            } else if (index > -1) {
                list.remove(index);
            } else if (value > 0) {
                list.add(new CapabilityListEntry(id, isDomain, value));
            }
        }

        @Override
        public String toString() {
            StringBuilder output = new StringBuilder();
            output.append("D").append(id+1).append(" :: ");
            for (CapabilityListEntry entry : list) {
                String s =
                        entry.isDomain && entry.value == 4 ? "A"
                        : entry.value == 1 ? "R"
                        : entry.value == 2 ? "W"
                        : entry.value == 3 ? "R/W"
                        : "";

                output.append(entry.isDomain ? "D" : "F").append(entry.key + 1).append(":").append(s).append(" → ");
            }
            output.append("<NULL>");

            return output.toString();
        }
    }

    private static class CapabilityListEntry {
        int key;
        int value;
        boolean isDomain;

        CapabilityListEntry(int key, boolean isDomain) {
            this.key = key;
            this.isDomain = isDomain;
            this.value = -1;
        }

        CapabilityListEntry(int key, boolean isDomain, int value) {
            this.key = key;
            this.isDomain = isDomain;
            this.value = value;
        }

        static CapabilityListEntry For(int key, boolean isDomain) {
            return new CapabilityListEntry(key, isDomain);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CapabilityListEntry other))
                return false;

            return this.key == other.key && this.isDomain == other.isDomain;
        }
    }
}
