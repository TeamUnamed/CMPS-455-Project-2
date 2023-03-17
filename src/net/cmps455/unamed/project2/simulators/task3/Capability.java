package net.cmps455.unamed.project2.simulators.task3;

public final class Capability {

    public final VirtualObject object;
    public final int flag;

    private Capability(VirtualObject object, int flag) {
        this.object = object;
        this.flag = flag;
    }

    public boolean has(int flag) {
        return (this.flag & flag) == flag;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", object, Integer.toString(flag,2));
    }

    public static final int NONE        = 0b0000;
    public static final int READ        = 0b0001;
    public static final int WRITE       = 0b0010;
    public static final int READ_WRITE  = 0b0011;
    public static final int SWITCH      = 0b0100;

    public static Capability create(VirtualObject object, int ... flags) {
        if (flags.length == 0) return new Capability(object, 0);
        if (flags.length == 1) return new Capability(object, flags[0]);

        int flag = 0;
        for (int f : flags) {
            flag ^= f;
        }

        return new Capability(object, flag);
    }
}
