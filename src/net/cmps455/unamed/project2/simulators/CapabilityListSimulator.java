package net.cmps455.unamed.project2.simulators;

import net.cmps455.unamed.project2.api.Capability;
import net.cmps455.unamed.project2.api.VirtualDomain;
import net.cmps455.unamed.project2.api.file.VirtualFile;
import net.cmps455.unamed.project2.api.system.VirtualSystem;
import net.cmps455.unamed.project2.simulators.task3.DomainThread;

import java.util.Random;

/*
 * 100% Over-engineered and complex
 * On top of the main purpose of simulating access permissions via CapabilityLists,
 * simulates a primitive system that restricts access to domains.
 * References to the System is encapsulated through private subclasses, one for
 * the internal system and one for the system entry. New Objects can only be
 * created through a Virtual System, as they are given "secret" identification
 * numbers.
 * Any time the "system.createFile(name)" method is used, if the file already exists,
 * then a reference to the file is returned.
 * As well, files are just storage objects for a SystemID to be passed to another
 * object. The SystemID is used to get the data from the "memory" on the Virtual System.
 * To read/write, you have to create a VirtualFileReader/Writer so that it can access
 * the streams from the System.
 */
public class CapabilityListSimulator extends Simulator {

    @Override
    public void start() {
        Random random = new Random();

        int domainCount = 3 + random.nextInt(5); // [3,7]
        int objectCount = 3 + random.nextInt(5); // [3,7]

        // Generate Domains & Objects
        System.out.println("Access control scheme: Capability List");
        System.out.println("Creating: ");
        System.out.printf(" | %d Domains\n", domainCount);
        System.out.printf(" | %d Objects\n", objectCount);
        System.out.println();

        // Create Virtual System
        VirtualSystem system = VirtualSystem.New(domainCount + objectCount);

        // Store domains & files to set permissions and start threads.
        VirtualDomain[] domains = new VirtualDomain[domainCount];
        VirtualFile[] files = new VirtualFile[objectCount];

        // Generate Domains
        for (int i = 0; i < domainCount; i++) {
            domains[i] = system.newDomain("D" + (i+1));
        }

        // Generate Objects
        for (int i = 0; i < objectCount; i++) {
            VirtualFile file = system.newFile("F" + (i+1));
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
                system.setPermission(domains[i], domains[j], random.nextInt(2) * Capability.SWITCH);
            }
            // Print out CapabilityLists
            System.out.println(system.permissionsToString(domains[i]));
        }

        System.out.println();

        // Instantiate Domain Threads
        DomainThread[] domainThreads = new DomainThread[domainCount];
        for (int i = 0; i < domainCount; i++) {
            domainThreads[i] = new DomainThread(domains[i], VirtualSystem.For(domains[i]));
            domainThreads[i].start();
        }

        for (int i = 0; i < domainCount; i++) {
            try {
                domainThreads[i].join();
            } catch (InterruptedException e) {
                System.out.println("Error: " + e);
            }
        }
        System.out.println();
        System.out.println("All threads have completed execution.");
        System.out.println("Simulator \"Access control scheme: Capability List\" has completed execution.");
    }
}
