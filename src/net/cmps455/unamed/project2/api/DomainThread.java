package net.cmps455.unamed.project2.api;

import java.util.Random;

public class DomainThread extends Thread {

    protected final int id;
    private final AccessManager accessManager;
    private final DomainManager domainManager;
    private final FileManager fileManager;

    public DomainThread (int id, AccessManager accessManager, DomainManager domainManager, FileManager fileManager) {
        this.id = id;
        this.accessManager = accessManager;
        this.domainManager = domainManager;
        this.fileManager = fileManager;
    }

    public void yieldRandom(Random random, int lower, int upper) {
        if (upper < 2) {
            logMessage("Yielding.");
            Thread.yield();
            return;
        }

        int yieldCount = random.nextInt(upper - lower + 1) + lower;
        logMessage("Yielding %d times.", yieldCount);
        for (int i = 0; i < yieldCount; i++) {
            Thread.yield();
        }
    }

    @Override
    public void run() {
        Random random = new Random();
        int domainCount = accessManager.getDomainCount();
        int objectCount = accessManager.getFileCount();

        for (int r = 0; r < 5; r++) {
            int select = random.nextInt(domainCount + objectCount);

            if (select < objectCount) { // Objects
                if (random.nextInt(2) == 0) { // Read
                    logMessage("Attempting to read from resource F%d", select);
                    if (accessManager.canRead(id, select)) {
                        fileManager.open(select, FileManager.Access.READ);
                        try {
                            logMessage("Reading from resource F%d",select);

                            yieldRandom(random, 3, 7);

                            logMessage("Resource F%d contains '%s'", select, fileManager.read(select));
                        } finally {
                            fileManager.close(select, FileManager.Access.READ);
                            logMessage("Operation Complete");
                        }
                    } else {
                        logMessage("Operation failed, permission denied");
                    }
                } else { // Write
                    logMessage("Attempting to write to resource F%d", select);
                    if (accessManager.canWrite(id, select)) {
                        fileManager.open(select, FileManager.Access.WRITE);
                        try {
                            String data = TEXT[random.nextInt(TEXT.length)];
                            logMessage("Writing '%s' to resource F%d", data, select);

                            yieldRandom(random, 3, 7);

                            fileManager.write(select, data);
                        } finally {
                            fileManager.close(select, FileManager.Access.WRITE);
                            logMessage("Operation Complete");
                        }
                    } else {
                        logMessage("Operation failed, permission denied");
                    }
                }
            } else { // Domains
                int context = domainManager.getContext(id);
                while (context == select - objectCount) {
                    select = objectCount + random.nextInt(domainCount);
                }

                logMessage("Attempting to switch to domain D%d", select - objectCount + 1);
                if (accessManager.canSwitch(id, select - objectCount)) {
                    logMessage("Switching to domain D%d", select - objectCount + 1);

                    yieldRandom(random, 3, 7);

                    domainManager.setContext(id, select - objectCount);

                    logMessage("Operation Complete");
                } else {
                    logMessage("Operation failed, permission denied");
                }
            }

            Thread.yield();
        }
    }

    private void logMessage(String s, Object ... args) {
        int context = domainManager.getContext(id);
        String tag = ("[D"+(id+1)) + (id != context ? ("@D"+(context+1)) : "") + "]";
        System.out.printf(tag + " " + s + "%n", args);
    }

    private static final String[] TEXT = {
            "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT",
            "NINE", "RED", "BLUE", "GREEN", "YELLOW", "CYAN", "INDIGO", "PINK",
            "APPLE", "BANANA", "ORANGE", "GRAPE", "CHERRY", "LEMON", "SPICY",
            "SWEET", "HELP", "SAVE", "LOAD", "BLANK", "NOT BLANK", "FULLY BLANK",
            "DONT BLINK", "HELP x2", "THIS SENTENCE IS FALSE", "ERROR 404 - DATA NOT FOUND"
    };
}
