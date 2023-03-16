package net.cmps455.unamed.project2.simulators;

import net.cmps455.unamed.project2.AccessLocked;
import net.cmps455.unamed.project2.simulators.task3.Capability;
import net.cmps455.unamed.project2.simulators.task3.Capability.Permission;
import net.cmps455.unamed.project2.simulators.task3.CapabilityList;
import net.cmps455.unamed.project2.simulators.task3.Domain;

import java.util.Random;

public class CapabilityListSimulator extends Simulator {

    private CapabilityList[] capabilitiesList;
    private Domain[] domainList;
    private DummyObject[] objectList;

    private int domainCount = 0;
    private int objectCount = 0;
    @Override
    public void start() {
        Random random = new Random();

        // Generate Domains & Objects
        domainCount = 3 + random.nextInt(5); // [3,7]
        objectCount = 3 + random.nextInt(5); // [3,7]

        System.out.printf("Creating capability list for %d domains and %d objects.\n", domainCount, objectCount);

        // Create Array of LinkedLists
        // CapabilityList inherits from LinkedList
        capabilitiesList = new CapabilityList[domainCount];

        // Store Domains & Objects
        domainList = new Domain[domainCount];
        objectList = new DummyObject[objectCount];

        // Generate Objects
        for (int i = 0; i < objectCount; i++) {
            objectList[i] = new DummyObject(i + 1);
        }

        // Generate Domains
        for (int i = 0; i < domainCount; i++) {
            domainList[i] = new Domain("D" + (i+1));
        }

        // Generate and fill Capability Lists
        for (int i = 0; i < domainCount; i++) {
            capabilitiesList[i] = new CapabilityList();

            // Capabilities for Objects
            for (int j = 0; j < objectCount; j++) {
                int flag = random.nextInt(4);

                if (flag == 0) continue;

                capabilitiesList[i].add(new Capability(objectList[j], Capability.Permission.fromValue(flag)));
            }

            // Capabilities for Domains
            for (int j = 0; j < domainCount; j++) {
                if (j == i) continue;

                int flag = random.nextInt(2) * Permission.SWITCH.value;

                if (flag == 0) continue;

                capabilitiesList[i].add(new Capability(domainList[j], Capability.Permission.fromValue(flag)));
            }

            System.out.printf("%s :: %s\n", domainList[i], capabilitiesList[i]);
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
        public String toString() {
            return "F" + id;
        }
    }
}
