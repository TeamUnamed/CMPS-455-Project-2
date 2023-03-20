package net.cmps455.unamed.project2.api.system;

import net.cmps455.unamed.project2.api.Capability;
import net.cmps455.unamed.project2.api.CapabilityList;
import net.cmps455.unamed.project2.api.VirtualDomain;
import net.cmps455.unamed.project2.api.VirtualObject;
import net.cmps455.unamed.project2.api.file.VirtualFile;

import javax.naming.NoPermissionException;
import java.nio.file.InvalidPathException;
import java.util.HashMap;


/* Sorry <3 */

/**
 * Reference to an internal {@link VirtualSystem}.
 */
public abstract class VirtualSystem {

    /**
     * Creates a new Domain with name or gets an existing one
     * if the name is already in use.
     * @param name Name of the Domain
     * @return a new {@link VirtualDomain Domain} /or/ a reference to an existing one.
     */
    public abstract VirtualDomain newDomain(String name);

    /**
     * Gets an array containing all active Domains.
     * @return array of {@link VirtualDomain Domains}
     */
    public abstract VirtualDomain[] getDomains();

    /**
     * Grants permission for a Domain to perform an action on an Object in the System.
     * @param source {@link VirtualDomain Domain} to receive permission.
     * @param target {@link Object Object} to set permission for.
     * @param permission Integer code of action to grant permission for.
     */
    public void setPermission(VirtualDomain source, VirtualObject target, int permission) {
        this.setPermission(source, target, permission, true);
    }

    /**
     * Set permission for a Domain to perform an action on an Object in the System.
     * @param source {@link VirtualDomain} to receive permission.
     * @param target {@link VirtualObject} to set permission for.
     * @param permission Integer code of action to grant permission for.
     * @param flag Grant permission if <u>True</u> /or/ revoke permission if <u>False</u>.
     */
    public abstract void setPermission(VirtualDomain source, VirtualObject target, int permission, boolean flag);

    /**
     * Checks the permission for a Domain to perform an action on an Object in the System.
     * @param source {@link VirtualDomain} permission holder.
     * @param target {@link VirtualObject} target of permission.
     * @param permission Integer code of action.
     * @return <u>True</u> if the Domain has permission, <u>False</u> otherwise.
     */
    @SuppressWarnings("unused")
    public abstract boolean getPermission(VirtualDomain source, VirtualObject target, int permission);

    /**
     * Checks the permission for the Domain calling the method to perform an action on
     * an object in the System.
     * @param target {@link VirtualObject} target for permission.
     * @param permission Integer code of action.
     * @return <u>True</u> if the Domain has permission, <u>False</u> otherwise.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean getPermission(VirtualObject target, int permission);

    /**
     * Coverts the permission set for a Domain to a string.
     * @param source {@link VirtualDomain} to find permissions for.
     * @return A sting listing the permission set.
     */
    public abstract String permissionsToString(VirtualDomain source);

    /**
     * Creates a new File with name or gets an existing one
     * if the name is already in use.
     * @param name Name of the File
     * @return a new {@link VirtualFile File} /or/ a reference to an existing one.
     */
    public abstract VirtualFile newFile(String name);

    /**
     * Gets an array containing all active Domains.
     * @return array of {@link VirtualDomain Domains}
     */
    public abstract VirtualFile[] getFiles();

    /**
     * Switches the context of the source Domain to the context of
     * the target domain.
     * @param source {@link VirtualDomain Domain} to switch from.
     * @param target {@link VirtualDomain Domain} to switch to.
     * @throws NoPermissionException Domain does not have permission to switch to
     * the target domain's /or/ a caller with access to the internal system (no context)
     * attempted to switch domains.
     */
    public abstract void Switch(VirtualDomain source, VirtualDomain target) throws NoPermissionException;

    /**
     * Logs a message to the output through this system.
     * <p> Automatically add calling Object to the prefix of the log message.
     * <p> This will print in a similar method as {@link java.io.PrintStream#printf(String, Object...)}
     * followed by a new line.
     * @param msg Message to output.
     * @param args <i>Optional</i> arguments to replace in message.
     */
    public abstract void log (String msg, Object ... args);

    /**
     * Returns the object containing the system's memory.
     * @return {@link VirtualSystemMemory} memory.
     */
    public abstract VirtualSystemMemory getMemory();

    /**
     * Create a new VirtualSystem
     * @return a new {@link VirtualSystem} instance.
     **/
    public static VirtualSystem New(int memory_size) {
        return new SystemEntry(new InternalSystem(memory_size));
    }

    /**
     * Returns a reference to the Virtual System that contains this object.
     * @param obj {@link VirtualObject} in a Virtual System.
     * @return {@link VirtualDomain} containing the object.
     */
    public static VirtualSystem For (VirtualObject obj) {
        // Always returns a reference to an internal system.
        InternalSystem internal = (InternalSystem) obj.getId().getSystem();

        // Check delegatee id first
        // Returns the entry of the one who requested the object's reference
        SystemEntry entry = internal.entries.get(obj.getId().delegatee_id);
        if (entry != null)
            return entry;

        // Check id
        // Returns the entry of the object's reference
        entry = internal.entries.get(obj.getId().id);
        if (entry != null)
            return entry;

        // Return a new System Entry
        return new SystemEntry(internal);
    }

    /* ---------------------------------------- */
    /* Private Member Classes                   */
    /* ---------------------------------------- */

    /**
     * Internal System of {@link VirtualSystem}
     * <p> Contains all internal logic of the system.
     * <p> A reference of this <i>should</i> never be passed.
     * @see SystemEntry
     */
    static class InternalSystem extends VirtualSystem {
        final VirtualSystemMemory memory;
        final HashMap<Integer, SystemEntry> entries;
        final HashMap<Integer, VirtualFile> files;

        InternalSystem(int memory_size) {
            this.memory = new VirtualSystemMemory(memory_size + 1);
            this.entries = new HashMap<>();
            this.files = new HashMap<>();
        }

        @Override
        public VirtualDomain newDomain(String name) {
            for (SystemEntry entry : entries.values()) {
                if (entry.domain.getName().equals(name))
                    return entry.domain;
            }

            VirtualDomain domain = new VirtualDomain(new SystemID(), name);
            SystemEntry entry = new SystemEntry(this, domain);

            entries.put(domain.getId().id, entry);
            return domain;
        }

        @Override
        public VirtualDomain[] getDomains() {
            VirtualDomain[] domains = new VirtualDomain[this.entries.size()];

            int count = 0;
            for (SystemEntry entry : this.entries.values()) {
                domains[count++] = entry.domain;
            }

            return domains;
        }

        @Override
        public void setPermission(VirtualDomain source, VirtualObject target, int permission, boolean flag) {
            assertDomainExists(source);

            if (!entries.containsKey(target.getId().id) && !files.containsKey(target.getId().id))
                throw new InvalidPathException(target.getName(), "Object does not exist in system.");

            CapabilityList capabilityList = entries.get(source.getId().id).capabilityList;
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

        @Override
        public boolean getPermission(VirtualDomain source, VirtualObject target, int permission) {
            assertDomainExists(source);

            if (!entries.containsKey(target.getId().id) && !files.containsKey(target.getId().id))
                throw new InvalidPathException(target.getName(), "Domain does not exist in system.");

            VirtualDomain context = entries.get(source.getId().id).context;

            // Accessing context allows for domain switching permissions
            CapabilityList capabilityList = entries.get(context.getId().id).capabilityList;

            for (Capability c : capabilityList) {
                if (c.object.equals(target) && c.has(permission)) return true;
            }

            return false;
        }

        @Override
        public boolean getPermission(VirtualObject target, int permission) {
            throw new IllegalAccessError("Permission Denied");
        }

        @Override
        public String permissionsToString(VirtualDomain source) {
            assertDomainExists(source);

            SystemEntry reference = entries.get(source.getId().id);

            return reference.domain.getName() + " :: " + reference.capabilityList.toString();
        }

        @Override
        public VirtualFile newFile(String name) {
            for (VirtualFile file : files.values()) {
                if (file.getName().equals(name))
                    return file;
            }

            VirtualFile file = new VirtualFile(new SystemID(), name);

            files.put(file.getId().id, file);
            return file;
        }

        @Override
        public void Switch(VirtualDomain source, VirtualDomain target) throws NoPermissionException {
            log("Domain %s is requesting to switch to Domain %s", getDomainName(source), target.getName());
            assertDomainExists(source);
            assertDomainExists(target);

            if (!getPermission(source, target, Capability.SWITCH))
                throw new NoPermissionException(String.format("%s does not have permission to switch to %s", getDomainName(source), target.getName()));

            log("Domain %s is switching to Domain %s", getDomainName(source), target.getName());
            entries.get(source.getId().id).context = target;
        }

        @Override
        public VirtualFile[] getFiles() {
            return files.values().toArray(new VirtualFile[0]);
        }

        @Override
        public void log (String msg, Object ... args) {
            if (args.length == 0)
                System.out.println(msg);
            else
                System.out.printf("[System] " + msg + "%n", args);
        }

        @Override
        public VirtualSystemMemory getMemory() {
            return memory;
        }

        /**
         * Overload of {@link VirtualSystem#getFiles()}.
         * <p> Gets the Virtual Files with delegatee id set to the passed domain.
         * <p> The systems always knows which domain it is passing object references to.
         */
        private VirtualFile[] getFiles(VirtualDomain domain) {
            VirtualFile[] files_copy = new VirtualFile[files.size()];

            int i = 0;
            for (VirtualFile file : files.values()) {
                SystemID id_copy = new SystemID(file.getId().id, domain.getId().id);
                files_copy[i++] = new VirtualFile(id_copy, file.getName());
            }

            return files_copy;
        }

        /**
         * Overload of {@link VirtualSystem#getDomains()}.
         * <p> Gets the Virtual Domains with delegatee id set to the passed domain.
         * <p> The systems always knows which domain it is passing object references to.
         */
        private VirtualDomain[] getDomains(VirtualDomain domain) {
            VirtualDomain[] domains_copy = new VirtualDomain[entries.size()];

            int i = 0;
            for (SystemEntry entry : entries.values()) {
                SystemID id_copy = new SystemID(entry.domain.getId().id, domain.getId().id);
                domains_copy[i++] = new VirtualDomain(id_copy, entry.domain.getName());
            }

            return domains_copy;
        }

        /**
         * Helper Method
         * <p> Get the expanded domain name with any switched contexts attached
         * @param domain {@link VirtualDomain}'s name to get
         * @return name of the VirtualDomain
         */
        private String getDomainName(VirtualDomain domain) {
            if (!entries.containsKey(domain.getId().id)) return "~" + domain.getName();

            VirtualDomain context = entries.get(domain.getId().id).context;

            return domain.getName() + (domain.equals(context) ? "" : "@" + context.getName());
        }

        /**
         * Helper Method
         * <p> Literally throws an error if the domain doesn't exist in the system.
         * @param domain {@link VirtualDomain} to assert existence.
         */
        private void assertDomainExists(VirtualDomain domain) {
            if (!entries.containsKey(domain.getId().id))
                throw new InvalidPathException(domain.getName(), "Domain does not exist in system.");
        }
    }

    /**
     * System Entry for {@link VirtualSystem}
     * <p> i.e. Entry point for Virtual Objects to interact with the Virtual System
     * @see InternalSystem
     */
    private static class SystemEntry extends VirtualSystem {

        private final InternalSystem internal;

        private VirtualDomain domain;
        private VirtualDomain context;
        private CapabilityList capabilityList;

        /**
         * Get a new SystemEntry for passing to a non-domain.
         * @param internal The {@link InternalSystem}
         */
        SystemEntry(InternalSystem internal) {
            this.internal = internal;
        }

        /**
         * Get a new SystemEntry for passing to a virtual domain.
         * @param internal The {@link InternalSystem}
         * @param domain The {@link VirtualDomain}
         */
        SystemEntry(InternalSystem internal, VirtualDomain domain) {
            this(internal);
            this.domain = domain;
            this.context = domain;
            this.capabilityList = new CapabilityList();
        }

        /**
         * {@inheritDoc}
         * <p> Uses {@link InternalSystem#getDomains(VirtualDomain)} if entry is for a domain context.
         * <p> Uses {@link InternalSystem#getDomains()} if entry is not for a domain context.
         */
        @Override
        public VirtualDomain[] getDomains() {
            return (domain == null) ? internal.getDomains() : internal.getDomains(domain);
        }

        /**
         * {@inheritDoc}
         * <p> Uses {@link InternalSystem#getFiles(VirtualDomain)} if entry is for a domain context.
         * <p> Uses {@link InternalSystem#getFiles()} if entry is not for a domain context.
         */
        @Override
        public VirtualFile[] getFiles() {
            return (domain == null) ? internal.getFiles() : internal.getFiles(domain);
        }

        /**
         * {@nocontext}
         * <p> {@inheritDoc}
         * <p> Uses {@link InternalSystem#newDomain(String)}.
         */
        @Override
        public VirtualDomain newDomain(String name) {
            assertNoContext();

            return internal.newDomain(name);
        }

        @Override
        public VirtualFile newFile(String name) {
            assertNoContext();

            return internal.newFile(name);
        }

        @Override
        public boolean getPermission(VirtualDomain source, VirtualObject target, int permission) {
            return internal.getPermission(source, target, permission);
        }

        @Override
        public boolean getPermission(VirtualObject target, int permission) {
            return internal.getPermission(domain, target, permission);
        }

        @Override
        public void setPermission(VirtualDomain source, VirtualObject target, int permission, boolean flag) {
            assertNoContext();

            internal.setPermission(source, target, permission, flag);
        }

        @Override
        public String permissionsToString(VirtualDomain source) {
            return internal.permissionsToString(source);
        }

        @Override
        public void Switch(VirtualDomain source, VirtualDomain target) throws NoPermissionException {
            if (domain == null || !domain.equals(source))
                throw new IllegalAccessError("Permission Denied");

            internal.Switch(source, target);
        }

        @Override
        public void log (String msg, Object ... args) {
            if (domain == null) internal.log(msg, args);

            String prefix = domain.getName() + (domain.equals(context) ? "" : "@" + context.getName());

            System.out.printf("[" + prefix + "] " + msg + "%n", args);
        }

        @Override
        public VirtualSystemMemory getMemory() {
            return internal.getMemory();
        }

        /** Helper Method
         * <p> Literally throws an error if this is used by a domain.
         */
        private void assertNoContext() {
            if (domain != null)
                throw new IllegalAccessError("Permission Denied");
        }
    }

    /**
     * Identification of Virtual Objects by the Virtual System.
     */
    public final class SystemID {

        private static int counter = 1;
        /** ID of a VirtualObject **/
        final int id;
        /** ID of a VirtualObject that requested this object**/
        final int delegatee_id;

        /**
         * Constructor
         * <p> Internal id automatically increments.
         */
        private SystemID () {
            this(counter++);
        }

        /**
         * Copy constructor
         * @param copy_id id to copy
         */
        private SystemID (int copy_id) {
            this(copy_id, 0);
        }

        /**
         * Copy constructor with delagatee.
         * @param copy_id id to copy
         * @param delegatee_id id of delegatee
         */
        private SystemID (int copy_id, int delegatee_id) {
            this.id = copy_id;
            this.delegatee_id = delegatee_id;
        }

        /**
         * Return the underlying virtual system.
         * <p> Will always return an InternalSystem.
         * @return {@link InternalSystem} that instantiated this.
         */
        VirtualSystem getSystem() {
            return VirtualSystem.this;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SystemID sid))
                return false;

            return this.id == sid.id && getSystem().equals(sid.getSystem());
        }
    }
}
