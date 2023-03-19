package net.cmps455.unamed.project2.api.system;

import javax.naming.NoPermissionException;
import java.nio.file.InvalidPathException;
import java.util.HashMap;

public abstract class VirtualSystem {

    /**
     * Creates a new Domain with name or gets an existing one
     * if the name is already in use.
     * @param name Name of the Domain
     * @return a new {@link Domain Domain} /or/ a reference to an existing one.
     */
    public Domain newDomain(String name) {
        InternalSystem internal = getInternal();
        if (internal.files.containsKey(name)) return internal.domains.get(name).domain;

        ExternalSystem external = new ExternalSystem(internal);
        external.setContext(new Domain(name, external));

        internal.domains.put(name, new DomainEntry(external.getContext(), new CapabilityList(), external));
        return external.getContext();
    }

    /**
     * Gets an array containing all active Domains.
     * @return array of {@link Domain Domains}
     */
    public Domain[] getDomains() {
        InternalSystem internal = getInternal();
        Domain[] domains = new Domain[internal.domains.size()];

        int count = 0;
        for (DomainEntry entry : internal.domains.values()) {
            domains[count++] = entry.domain;
        }

        return domains;
    }

    /**
     * Grants permission for a Domain to perform an action on an Object in the System.
     * @param source {@link Domain Domain} to receive permission.
     * @param target {@link Object Object} to set permission for.
     * @param permission Integer code of action to grant permission for.
     */
    public void setPermission(Domain source, VirtualObject target, int permission) {
        this.setPermission(source, target, permission, true);
    }

    /**
     * Set permission for a Domain to perform an action on an Object in the System.
     * @param source {@link Domain Domain} to receive permission.
     * @param target {@link Object Object} to set permission for.
     * @param permission Integer code of action to grant permission for.
     * @param flag Grant permission if <u>True</u> /or/ revoke permission if <u>False</u>.
     */
    public void setPermission(Domain source, VirtualObject target, int permission, boolean flag) {
        InternalSystem internal = getInternal();
        if (!internal.domains.containsKey(source.getName()))
            throw new InvalidPathException(source.getName(), "Domain does not exist in system.");

        if (!internal.domains.containsKey(target.getName()) && !internal.files.containsKey(target.getName()))
            throw new InvalidPathException(target.getName(), "Object does not exist in system.");

        CapabilityList capabilityList = internal.domains.get(source.getName()).capabilityList;
        int index = capabilityList.indexOf(target);

        if (index == -1 && flag) {
            capabilityList.add(Capability.create(target, permission));

        } else {
            Capability capability = capabilityList.get(index);

            if (capability.has(permission) && !flag) {
                capabilityList.set(index, capability.subtract(permission));

            } else if (!capability.has(permission) && flag) {
                capabilityList.set(index, capability.add(permission));
            }
        }

    }

    public String getPermissions(Domain source) {
        InternalSystem internal = getInternal();
        if (!internal.domains.containsKey(source.getName()))
            throw new InvalidPathException(source.getName(), "Domain does not exist in system.");

        DomainEntry entry = internal.domains.get(source.getName());

        return entry.domain.getName() + " :: " + entry.capabilityList.toString();
    }

    /**
     * Creates a new File with name or gets an existing one
     * if the name is already in use.
     * @param name Name of the File
     * @return a new {@link File File} /or/ a reference to an existing one.
     */
    public File newFile(String name) {
        InternalSystem internal = getInternal();
        if (internal.files.containsKey(name)) return internal.files.get(name);

        File file = new File(name, this);

        internal.files.put(name, file);
        return file;
    }

    /**
     * Gets an array containing all active Domains.
     * @return array of {@link Domain Domains}
     */
    public File[] getFiles() {
        return getInternal().files.values().toArray(new File[0]);
    }

    public VirtualSystem For(Domain domain) {
        if (!isInternal()) return this;

        return getInternal().domains.get(domain.getName()).system;
    }

    /**
     * Switches the context of the source Domain to the context of
     * the target domain.
     * @param source {@link Domain Domain} to switch from.
     * @param target {@link Domain Domain} to switch to.
     * @throws NoPermissionException Domain does not have permission to switch to
     * the target domain's /or/ a caller with access to the internal system (no context)
     * attempted to switch domains.
     */
    public abstract void Switch(Domain source, Domain target) throws NoPermissionException;

    /**
     * Gets information on if this system is internal or external.
     * @return <u>True</u> if this is an {@link InternalSystem Internal System} /or/
     * <u>False</u> if this is an {@link ExternalSystem External System}.
     */
    public abstract boolean isInternal();

    /**
     * Returns a reference to the internal system.
     * @return {@link InternalSystem Internal system} of this virtual system.
     */
    protected abstract InternalSystem getInternal();

    public static VirtualSystem NewInternal() {
        return new InternalSystem();
    }

    /**
     * Internal System of {@link VirtualSystem}
     * @see ExternalSystem
     */
    private static class InternalSystem extends VirtualSystem {
        final HashMap<String, DomainEntry> domains;
        final HashMap<String, File> files;

        InternalSystem() {
            this.domains = new HashMap<>();
            this.files = new HashMap<>();
        }

        @Override
        public void Switch(Domain source, Domain target) throws NoPermissionException {
            throw new NoPermissionException("Cannot switch Domains from internal system.");
        }

        @Override
        protected InternalSystem getInternal() {
            return this;
        }

        @Override
        public boolean isInternal() {
            return true;
        }
    }

    /**
     * External System of {@link VirtualSystem}
     * @see InternalSystem
     */
    private static class ExternalSystem extends VirtualSystem {

        final InternalSystem root;
        private Domain context;

        ExternalSystem(InternalSystem root) {
            this.root = root;
        }

        @Override
        public void Switch(Domain source, Domain target) throws NoPermissionException {
            if (!root.domains.containsKey(source.getName()))
                throw new InvalidPathException(source.getName(), "Domain does not exist in system.");

            if (!root.domains.containsKey(target.getName()))
                throw new InvalidPathException(target.getName(), "Domain does not exist in system.");

            if (!root.domains.get(source.getName()).hasPermission(target, Capability.SWITCH))
                throw new NoPermissionException(String.format("%s does not have permission to switch to %s%n", source.getName(), target.getName()));

            root.domains.get(source.getName()).switchedTo = target;
            System.out.printf("[SYSTEM] Domain %s has switched to Domain %s%n", source.getName(), target.getName());
        }

        @Override
        protected InternalSystem getInternal() {
            return root;
        }

        @Override
        public boolean isInternal() {
            return false;
        }

        public Domain getContext() {
            return context;
        }

        public void setContext(Domain context) {
            this.context = context;
        }
    }

    /**
     * <p>Stores information on Domains & their capability lists.
     * <p>Provides assistance with helper methods to manage data.
     */
    private class DomainEntry {

        final Domain domain;
        final CapabilityList capabilityList;
        final ExternalSystem system;

        private Domain switchedTo;

        DomainEntry (Domain domain, CapabilityList capabilityList, ExternalSystem system) {
            this.domain = domain;
            this.capabilityList = capabilityList;
            this.system = system;
        }

        boolean hasPermission(Object target, int permission) {
            // Logic for domain switching
            CapabilityList capabilityList = this.capabilityList;
            if (switchedTo != null)
                capabilityList = getInternal().domains.get(switchedTo.getName()).capabilityList;

            for (Capability c : capabilityList) {
                if (c.object.equals(target) && c.has(permission)) return true;
            }

            return false;
        }
    }

}
