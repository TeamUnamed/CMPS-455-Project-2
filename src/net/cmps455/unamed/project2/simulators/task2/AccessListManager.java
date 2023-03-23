package net.cmps455.unamed.project2.simulators.task2;

import net.cmps455.unamed.project2.api.AccessManager;
import net.cmps455.unamed.project2.api.DomainManager;

import java.util.LinkedList;

public class AccessListManager extends AccessManager {

    private final AccessList[] accessLists;
    private final DomainManager domainManager;
    private final int domainCount;
    private final int fileCount;

    public AccessListManager (DomainManager domainManager, int fileCount) {
        this.domainManager = domainManager;
        this.domainCount = domainManager.getDomainCount();
        this.fileCount = fileCount;

        this.accessLists = new AccessList[fileCount + domainCount];

        for (int i = 0; i < fileCount; i++ ) {
            accessLists[i] = new AccessList(i + 1, false);
        }

        for (int i = 0; i < domainCount; i++) {
            accessLists[i + fileCount] = new AccessList(i + 1, true);
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
        return accessLists[object].hasReadAccess(domainManager.getContext(domain));
    }

    @Override
    public boolean canWrite(int domain, int object) {
        return accessLists[object].hasWriteAccess(domainManager.getContext(domain));
    }

    @Override
    public boolean canSwitch(int domainSource, int domainTarget) {
        return accessLists[fileCount + domainTarget].canSwitch(domainManager.getContext(domainSource));
    }

    @Override
    public void set(int domain, int object, int value) {
        accessLists[object].set(domain, value);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        int count = 1;
        for (AccessList list : accessLists) {
            output.append(list).append(accessLists.length == count++ ? "" : "; ");
        }
        return output.toString();
    }

    private static class AccessList {
        final int id;
        final boolean isDomain;
        final LinkedList<AccessListEntry> list;

        AccessList(int id, boolean isDomain) {
            this.id = id;
            this.isDomain = isDomain;
            this.list = new LinkedList<>();
        }

        boolean hasReadAccess(int domain) {
            int index = list.indexOf(AccessListEntry.For(domain));

            if (index == -1)
                return false;

            return (list.get(index).value & 0b001) == 0b001;
        }

        boolean hasWriteAccess(int domain) {
            int index = list.indexOf(AccessListEntry.For(domain));

            if (index == -1)
                return false;

            return (list.get(index).value & 0b010) == 0b010;
        }

        boolean canSwitch(int domain) {
            int index = list.indexOf(AccessListEntry.For(domain));

            if (index == -1)
                return false;

            return (list.get(index).value & 0b100) == 0b100;
        }

        void set(int domain, int value) {
            int index = list.indexOf(AccessListEntry.For(domain));

            if (index > -1 && value > 0) {
                list.get(index).value = value;
            } else if (index > -1) {
                list.remove(index);
            } else if (value > 0) {
                list.add(new AccessListEntry(domain, value));
            }
        }

        @Override
        public String toString() {
            StringBuilder output = new StringBuilder();
            output.append(isDomain ? "D" : "F").append(id).append(" :: ");
            for (AccessListEntry entry : list) {
                String s =
                        isDomain && entry.value == 0b100 ? "A" :
                        entry.value == 1 ? "R" :
                        entry.value == 2 ? "W" :
                        entry.value == 3 ? "R/W" :
                        "";
                output.append("D").append(entry.key + 1).append(":").append(s).append(" → ");
            }
            output.append("<NULL>");

            return output.toString();
        }
    }

    private static class AccessListEntry {
        int key;
        int value;

        AccessListEntry(int key) {
            this.key = key;
            this.value = -1;
        }

        AccessListEntry(int key, int value) {
            this.key = key;
            this.value = value;
        }

        static AccessListEntry For(int key) {
            return new AccessListEntry(key);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AccessListEntry other))
                return false;

            return this.key == other.key;
        }
    }
}
