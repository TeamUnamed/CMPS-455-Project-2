package net.cmps455.unamed.project2.simulators;

import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AccessMatrixSimulator extends Simulator {

    @Override
    public void start() {
        Random random = new Random();

        int domainCount = random.nextInt(5) + 3; // between 3 and 7
        int objectCount = random.nextInt(5) + 3; // between 3 and 7

        DomainManager domainManager = new DomainManager(domainCount);
        FileManager fileManager = new FileManager(objectCount);
        AccessMatrix accessMatrix = new AccessMatrix(domainManager, objectCount);

        System.out.println("Domain count: " + domainCount);
        System.out.println("Object count: " + objectCount);

        // Generate Access Matrix
        for (int i = 0; i < domainCount; i++) {
            for (int j = 0; j < objectCount; j++) {
                // In range 0 to 3
                accessMatrix.set(i, j, random.nextInt(4));
            }
            for (int j = 0; j < domainCount; j++) {
                // Either 0 or 4
                accessMatrix.set(i, objectCount + j, random.nextInt(2) * 4);
            }
        }

        // Print Access Matrix
        System.out.println(accessMatrix);

        System.out.println();

        DomainThread[] domainThreads = new DomainThread[domainCount];
        for (int i = 0; i < domainCount; i++) {
            domainThreads[i] = new DomainThread(i, accessMatrix, fileManager, domainManager);
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

    private static class DomainThread extends Thread {

        final int id;
        final AccessMatrix accessMatrix;
        final FileManager fileManager;
        final DomainManager domainManager;

        DomainThread (int id, AccessMatrix accessMatrix, FileManager fileManager, DomainManager domainManager) {
            this.id = id;
            this.accessMatrix = accessMatrix;
            this.fileManager = fileManager;
            this.domainManager = domainManager;
        }

        @Override
        public void run() {
            Random random = new Random();
            int domainCount = accessMatrix.getDomainCount();
            int objectCount = accessMatrix.getObjectCount();

            for (int r = 0; r < 5; r++) {
                int select = random.nextInt(domainCount + objectCount);

                if (select < objectCount) { // Objects
                    if (random.nextInt(2) == 0) { // Read
                        System.out.printf("[D%d] Attempting to read from resource F%d%n", id+1, select);
                        if (accessMatrix.hasReadAccess(id, select)) {
                            fileManager.open(select, FileManager.Access.READ);
                            try {
                                System.out.printf("[D%d] Reading from resource F%d%n", id + 1, select);

                                yieldRandom(random);

                                System.out.printf("[D%d] Resource F%d contains '%s'%n", id + 1, select, fileManager.read(select));
                            } finally {
                                fileManager.close(select, FileManager.Access.READ);
                            }
                        } else {
                            System.out.printf("[D%d] Operation failed, permission denied%n", id+1);
                        }
                    } else { // Write
                        System.out.printf("[D%d] Attempting to write to resource F%d%n", id+1, select);
                        if (accessMatrix.hasWriteAccess(id, select)) {
                            fileManager.open(select, FileManager.Access.WRITE);
                            try {
                                String data = "test_write";
                                System.out.printf("[D%d] Writing '%s' to resource F%d%n", id + 1, data, select);

                                yieldRandom(random);

                                fileManager.write(select, data);
                            }finally{
                                fileManager.close(select, FileManager.Access.WRITE);
                            }
                        } else {
                            System.out.printf("[D%d] Operation failed, permission denied%n", id+1);
                        }
                    }
                } else { // Domains
                    System.out.printf("[D%d] Attempting to switch to domain D%d%n", id+1, select-objectCount+1);
                    if (accessMatrix.canSwitch(id, select - objectCount)) {
                        System.out.printf("[D%d] Switching to domain D%d%n", id+1, select-objectCount+1);

                        yieldRandom(random);

                        domainManager.setContext(id, select - objectCount);
                    } else {
                        System.out.printf("[D%d] Operation failed, permission denied%n", id+1);
                    }
                }

                System.out.printf("[D%d] Operation Complete%n",id+1);
            }
        }

        void yieldRandom(Random random) {
            // Yield for 3 to 7 cycles.
            int yieldCount = random.nextInt(5) + 3; // [3,7]
            System.out.printf("[D%d] Yielding %d times.%n", id+1, yieldCount);
            for (int i = 0; i < yieldCount; i++) {
                Thread.yield();
            }
        }
    }

    private static class AccessMatrix {

        private final int[][] matrix;
        private final DomainManager domainManager;
        private final int domainCount;
        private final int objectCount;

        AccessMatrix(DomainManager domainManager, int objectCount) {
            this.domainManager = domainManager;
            this.domainCount = domainManager.getDomainCount();
            this.objectCount = objectCount;

            this.matrix = new int[domainCount][domainCount + objectCount];
        }

        int getDomainCount() {
            return domainCount;
        }

        int getObjectCount() {
            return objectCount;
        }

        boolean hasReadAccess(int domain, int object) {
            return (matrix[domainManager.getContext(domain)][object] & 0b001) == 0b001;
        }

        boolean hasWriteAccess(int domain, int object) {
            return (matrix[domainManager.getContext(domain)][object] & 0b010) == 0b010;
        }

        boolean canSwitch(int domainSource, int domainTarget) {
            return (matrix[domainManager.getContext(domainSource)][domainTarget + objectCount] & 0b100) == 0b100;
        }

        void set(int domain, int object, int value) {
            matrix[domain][object] = value;
        }

        @Override
        public String toString() {
            StringBuilder output = new StringBuilder();
            output.append("    ");
            for (int i = -1; i < domainCount; i++) {
                if (i < 0) {
                    for (int j = 0; j < objectCount; j++) {
                        output.append(" F").append(j + 1).append("  ");
                    }
                    for (int j = 0; j < domainCount; j++) {
                        output.append(" D").append(j + 1).append("  ");
                    }
                    output.append("\n");
                    System.out.println();
                    continue;
                }

                output.append("  D").append(i+1);
                for (int j = 0; j < objectCount; j++) {
                    int v = matrix[i][j];
                    output.append(String.format(" %-3s ", v == 1 ? "R" : v == 2 ? "W" : v == 3 ? "R/W" : ""));
                }
                for (int j = 0; j < domainCount; j++) {
                    output.append(String.format(" %-3s ", matrix[i][objectCount+j] == 4 ? "A" : ""));
                }
                output.append("\n");
            }

            return output.toString();
        }
    }

    private static class FileManager {

        private final String[] fileData;
        private final ReadWriteLock[] locks;

        FileManager (int fileCount) {
            this.fileData = new String[fileCount];
            this.locks = new ReadWriteLock[fileCount];
            for (int i = 0; i < fileCount; i++) {
                locks[i] = new ReentrantReadWriteLock();
            }
        }

        void open(int file, Access access) {
            if (access == Access.READ)
                locks[file].readLock().lock();
            else
                locks[file].writeLock().lock();
        }

        void close(int file, Access access) {
            if (access == Access.READ)
                locks[file].readLock().unlock();
            else
                locks[file].writeLock().unlock();
        }

        String read(int file) {
            return fileData[file];
        }

        void write(int file, String data) {
            fileData[file] = data;
        }

        enum Access {
            READ, WRITE
        }
    }

    private static class DomainManager {

        private final int[] context;

        DomainManager (int domainCount) {
            this.context = new int[domainCount];
            for (int i = 0; i < domainCount; i++) {
                context[i] = i;
            }
        }

        int getDomainCount() {
            return context.length;
        }

        int getContext(int domain) {
            return context[domain];
        }

        void setContext(int domainSource, int domainTarget) {
            context[domainSource] = domainTarget;
        }
    }
}
