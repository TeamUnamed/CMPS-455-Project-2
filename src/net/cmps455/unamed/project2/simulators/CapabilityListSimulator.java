package net.cmps455.unamed.project2.simulators;

import net.cmps455.unamed.project2.AccessLocked;
import net.cmps455.unamed.project2.Capability;

import java.util.ArrayList;
import java.util.Random;

public class CapabilityListSimulator extends Simulator {

    private ArrayList<Capability>[] capabilityList;
    private DummyDomain[] domainList;
    private DummyObject[] objectList;

    private int domainCount = 0;
    private int objectCount = 0;
    @Override
    public void start() {
        Random random = new Random();

        domainCount = 3 + random.nextInt(5); // [3,7]
        objectCount = 3 + random.nextInt(5); // [3,7]

        System.out.printf("Creating capability list for %d domains and %d objects.\n", domainCount, objectCount);

        capabilityList = new ArrayList[domainCount];

        domainList = new DummyDomain[domainCount];
        objectList = new DummyObject[objectCount];


        for (int i = 0; i < objectCount; i++) {
            objectList[i] = new DummyObject(i+1);
        }

        for (int i = 0; i < domainCount; i++) {
            domainList[i] = new DummyDomain(i+1);
        }

        for (int i = 0; i < domainCount; i++) {
            capabilityList[i] = new ArrayList<>();
            for (int j = 0; j < objectCount; j++) {
                // 0b00 - None; 0b01 - Read; 0b10 - Write; 0b11 - Read/Write
                int flag = random.nextInt(4);

                if (flag == 0) continue;

                capabilityList[i].add(new Capability(objectList[j], flag));
            }

            for (int j = 0; j < domainCount; j++) {
                if (j==i) continue;

                // 0b0 - None; 0b01 - Switch
                int flag = random.nextInt(2);

                if (flag == 0) continue;

                capabilityList[i].add(new Capability(domainList[j], flag));
            }
        }

        for (int y = 0; y < domainCount; y++) {
            printCapabilityList(y);
        }

    }

    private String getPermissionString(AccessLocked object, int flag) {
        if (object.isDomain()) return (flag==1) ? "switch" : "";

        switch (flag) {
            case 1 ->  { return "read"; }
            case 2 ->  { return "write"; }
            case 3 ->  { return "read/write"; }
            default -> { return ""; }
        }
    }
    private void printCapabilityList(int i) {
        if (i < 0 || i >= domainCount) return;

        System.out.printf("<D%d> :: ", i+1);
        for (Capability c : capabilityList[i]) {
            AccessLocked object = c.object;
            String permissionString = getPermissionString(object, c.permission);
            System.out.printf("[%s%d, %s] → ", object.isDomain() ? "D" : "F", object.getUID(), permissionString);
        }
        System.out.println("NULL");
    }
    private static class DummyDomain implements AccessLocked {

        private final int id;
        DummyDomain (int id) {
            this.id = id;
        }

        @Override
        public int getUID() {
            return id;
        }

        @Override
        public boolean isDomain() {
            return true;
        }
    }
    private static class DummyObject implements AccessLocked {

        public final int id;

        DummyObject (int id) {
            this.id = id;
        }

        public void read() {

        }

        public void write() {

        }

        @Override
        public int getUID() {
            return id;
        }
    }
}
