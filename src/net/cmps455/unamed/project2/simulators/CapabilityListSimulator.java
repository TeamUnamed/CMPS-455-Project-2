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

        // Create Array of LinkedLists
        // CapabilityList inherits from LinkedList
        CapabilityList[] capabilityLists = new CapabilityList[domainCount];


        VirtualSystem system = VirtualSystem.NewInternal();

        Domain[] domains = new Domain[domainCount];
        File[] files = new File[objectCount];

        // Generate Domains
        for (int i = 0; i < domainCount; i++) {
            domains[i] = system.newDomain("D" + (i+1));
        }

        // Generate Objects
        for (int i = 0; i < objectCount; i++) {
            File file = system.newFile("F" + (i+1));
            files[i] = file;
        }

        // Generate Capabilities
        for (int i = 0; i < domainCount; i++) {
            // For Files
            for (int j = 0; j < objectCount; j++) {
                system.setPermission(domains[i], files[j], random.nextInt(4));
            }
            // For other Domains
            for (int j = 0; j < domainCount; j++) {
                system.setPermission(domains[i], domains[j], random.nextInt(2) * 3);
            }
            // Print out CapabilityLists
            System.out.println(system.getPermissions(domains[i]));
        }

        // Instantiate Domain Threads
        DomainThread[] domainThreads = new DomainThread[domainCount];
        for (int i = 0; i < domainCount; i++) {
            domainThreads[i] = new DomainThread(domains[i], system.For(domains[i]));
            domainThreads[i].start();
        }

        for (int i = 0; i < domainCount; i++) {
            try {
                domainThreads[i].join();
            } catch (InterruptedException e) {
                System.out.println("Error: " + e);
            }
        }
    }
}
