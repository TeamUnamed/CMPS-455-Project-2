package net.cmps455.unamed.project2.simulators;

import net.cmps455.unamed.project2.api.system.*;
import net.cmps455.unamed.project2.simulators.task3.DomainThread;
import net.cmps455.unamed.project2.api.system.VirtualSystem;

import java.util.Random;

public class CapabilityListSimulator extends Simulator {

    @Override
    public void start() {
        Random random = new Random();

        int domainCount = 3 + random.nextInt(5); // [3,7]
        int objectCount = 3 + random.nextInt(5); // [3,7]

        // Generate Domains & Objects
        System.out.println("Access control scheme: Capability List");
        System.out.println("Creating: ");
        System.out.printf("| %d Domains\n", domainCount);
        System.out.printf("| %d Objects\n", objectCount);
        System.out.println();

        /* Setup */

        // Create Array of LinkedLists
        // CapabilityList inherits from LinkedList
        CapabilityList[] capabilityLists = new CapabilityList[domainCount];

        // Create array to store Domains & Files
        VirtualObject[] objects = new VirtualObject[domainCount + objectCount];

        // Create Arbitrator and give a pointer to Capabilities
        Arbitrator arbitrator = new Arbitrator(capabilityLists);


        // Generate Objects
        for (int i = 0; i < objectCount; i++) {
            objects[i] = new DummyObject(i+1);
        }

        // Generate Domains
        for (int i = 0; i < domainCount; i++) {
            objects[i + objectCount] = new Domain(i+1, objects, arbitrator);
        }

        // Generate and fill Capability Lists
        for (int i = 0; i < domainCount; i++) {
            capabilityLists[i] = new CapabilityList((VirtualDomain) objects[i + objectCount]);

            for (int j = 0; j < domainCount + objectCount; j++) {
                int flag = random.nextInt(4); // [0,3]

                VirtualObject object = objects[j];
                if (object.isDomain())
                    flag = (flag / 2) * 4; // [0,3] -> [0,1] -> 0 or 4

                capabilityLists[i].add(Capability.create(objects[i],flag));
            }

            System.out.println(capabilityLists[i]);
        }

        /* Simulation */

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
