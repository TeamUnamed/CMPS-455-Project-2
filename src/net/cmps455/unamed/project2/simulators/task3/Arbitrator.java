package net.cmps455.unamed.project2.simulators.task3;

public class Arbitrator {

    private final CapabilityList[] capabilityLists;

    /**
     * Arbitrator Constructor
     * @param capabilityLists Pointer to array of CapabilityLists
     */
    public Arbitrator (CapabilityList[] capabilityLists) {
        this.capabilityLists = capabilityLists;
    }

    private CapabilityList getCapabilitiesForDomainID(int id) {
        for (CapabilityList capabilityList : capabilityLists) {
            if (capabilityList.getDomainId() == id)
                return capabilityList;
        }

        return null;
    }

    public void readFile (VirtualDomain domain, VirtualObject object) throws IllegalAccessException {
        CapabilityList capabilities = getCapabilitiesForDomainID(domain.getID());

        if (capabilities == null) throw new IllegalAccessException("Access Denied: Domain has no permissions!");

        Capability capability = null;

        for (Capability c : capabilities) {
            if (!c.object.equals(object))
                continue;

            capability = c;
        }

        if (capability == null || !capability.has(Capability.READ))
            throw new IllegalAccessException(String.format("Access Denied: %s does not have permission to read %s%n", domain, object));

        // TODO Handle reading files
    }

    public void writeFile (VirtualDomain domain, VirtualObject object) throws IllegalAccessException {
        CapabilityList capabilities = getCapabilitiesForDomainID(domain.getID());

        if (capabilities == null) throw new IllegalAccessException("Access Denied: Domain has no permissions!");

        Capability capability = null;

        for (Capability c : capabilities) {
            if (!c.object.equals(object))
                continue;

            capability = c;
        }

        if (capability == null || !capability.has(Capability.WRITE))
            throw new IllegalAccessException(String.format("Access Denied: %s does not have permission to write %s%n", domain, object));

        // TODO Handle writing files
    }

    public void switchDomain (VirtualDomain source, VirtualDomain target) throws IllegalAccessException {
        CapabilityList capabilities = getCapabilitiesForDomainID(source.getID());

        if (capabilities == null) throw new IllegalAccessException("Access Denied: Domain has no permissions!");

        Capability capability = null;

        for (Capability c : capabilities) {
            if (!c.object.equals(target))
                continue;

            capability = c;
        }

        if (capability == null || !capability.has(Capability.SWITCH))
            throw new IllegalAccessException(String.format("Access Denied: %s does not have permission to switch to %s%n", source, target));

        // TODO Handle switching domains
    }
}
