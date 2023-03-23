package net.cmps455.unamed.project2.simulators;

import net.cmps455.unamed.project2.api.*;
import net.cmps455.unamed.project2.simulators.Task1.AccessMatrixManager;

import java.util.Random;

public class AccessMatrixSimulator extends Simulator {

    @Override
    public void start() {
        Random random = new Random();

        int domainCount = random.nextInt(5) + 3; // between 3 and 7
        int fileCount = random.nextInt(5) + 3; // between 3 and 7

        System.out.println("Access control scheme: Access Matrix");
        System.out.println("Creating: ");
        System.out.printf(" | %d Domains\n", domainCount);
        System.out.printf(" | %d Objects\n", fileCount);
        System.out.println();

        DomainManager domainManager = new DomainManager(domainCount);
        FileManager fileManager = new FileManager(fileCount);
        AccessManager accessManager = new AccessMatrixManager(domainManager, fileCount);


        // Generate Access Matrix
        for (int i = 0; i < domainCount; i++) {
            for (int j = 0; j < fileCount; j++) {
                // In range 0 to 3
                accessManager.set(i, j, random.nextInt(4));
            }
            for (int j = 0; j < domainCount; j++) {
                // Either 0 or 4
                accessManager.set(i, fileCount + j, random.nextInt(2) * 4);
            }
        }

        // Print Access Matrix
        System.out.println(accessManager);

        System.out.println();

        DomainThread[] domainThreads = new DomainThread[domainCount];
        for (int i = 0; i < domainCount; i++) {
            domainThreads[i] = new DomainThread(i, accessManager, domainManager, fileManager);
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
        System.out.println("Simulator \"Access control scheme: Access Matrix\" has completed execution.");
    }
}