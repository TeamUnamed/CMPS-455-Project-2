package net.cmps455.unamed.project2.simulators;

import net.cmps455.unamed.project2.VirtualObject;
import net.cmps455.unamed.project2.simulators.task3.Capability;
import net.cmps455.unamed.project2.simulators.task3.CapabilityList;
import net.cmps455.unamed.project2.simulators.task3.Domain;

import java.util.Random;

public class CapabilityListSimulator extends Simulator {

    private CapabilityList[] capabilityLists;
    private VirtualObject[] objects;

    private int domainCount = 0;
    private int objectCount = 0;
    @Override
    public void start() {
        Random random = new Random();

        // Generate Domains & Objects
        domainCount = 3 + random.nextInt(5); // [3,7]
        objectCount = 3 + random.nextInt(5); // [3,7]
        System.out.println("Access control scheme: Capability List");
        System.out.println("Creating: ");
        System.out.printf("| %d Domains\n", domainCount);
        System.out.printf("| %d Objects\n", objectCount);
        System.out.println();

        // Create Array of LinkedLists
        // CapabilityList inherits from LinkedList
        capabilityLists = new CapabilityList[domainCount];

        // Create Arbitrator and give a pointer to Capabilities
        Arbitrator arbitrator = new Arbitrator(capabilityLists);


        // Generate Objects
        for (int i = 0; i < objectCount; i++) {
            objects[i] = new DummyObject("F" + (i+1));
        }

        // Generate Domains
        for (int i = 0; i < domainCount; i++) {
            objects[i + objectCount] = new Domain(i+1, objects, arbitrator);
        }

        // Generate and fill Capability Lists
        for (int i = 0; i < domainCount; i++) {
            capabilityLists[i] = new CapabilityList((VirtualDomain) objects[i + objectCount]);

            for (int j = 0; j < domainCount + objectCount; j++) {
                int value = random.nextInt(4); // [0,3]

                VirtualObject object = objects[j];
                if (object.isDomain())
                    value = (value / 2) * 4; // [0,3] -> [0,1] -> 0 or 4

                capabilityLists[i].add(new Capability(objects[j], Capability.Permission.fromValue(value)));
            }

            System.out.printf("%s :: %s\n", objects[i + objectCount], capabilityLists[i]);
        }

        for (int i = 0; i < domainCount; i++) {
            ((Domain) objects[i + objectCount]).start();
        }

        for (int i = 0; i < domainCount; i++) {
            try {
                ((Domain) objects[i + objectCount]).join();
            } catch (InterruptedException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    private static class DummyObject implements VirtualObject {

        public final int id;

        DummyObject (int id) {
            this.id = id;
        }

        public void read() {

        }

        public void write() {

        }

        @Override
        public String toString() {
            return "F" + id;
        }

        @Override
        public int getID() {
            return id;
        }
    }

}
