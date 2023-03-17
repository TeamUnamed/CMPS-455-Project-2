package net.cmps455.unamed.project2.simulators.task3;

import net.cmps455.unamed.project2.VirtualObject;

public class Capability {

    public final VirtualObject object;
    public final Permission permission;

    public Capability(VirtualObject object, Permission permission) {
        this.object = object;
        this.permission = permission;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", object, permission);
    }

    public enum Permission {

        NONE      (0b000,""),
        READ      (0b001,"read"),
        WRITE     (0b010,"write"),
        READWRITE (0b011,"read/write"),
        SWITCH    (0b100,"switch");

        public final int value;
        public final String text;
        Permission(int value, String text) {
            this.value = value;
            this.text = text;
        }

        public boolean contains (Permission other) {
            return (this.value & other.value) == other.value;
        }

        public static Permission fromValue(int value) {
            return switch (value) {
                case 1 -> READ;
                case 2 -> WRITE;
                case 3 -> READWRITE;
                case 4 -> SWITCH;
                default -> NONE;
            };
        }

        @Override
        public String toString() {
            return this.text;
        }
    }
}
