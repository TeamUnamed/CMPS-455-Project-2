package net.cmps455.unamed.project2.api.system;

import java.util.concurrent.Semaphore;

public class File extends VirtualObject {

    private String buffer = "";

    private int readers = 0;
    private Semaphore mutex;
    private Semaphore write;

    public File(String name, VirtualSystem system) {
        super(name, system);
        this.mutex = new Semaphore(1);
        this.write = new Semaphore(1);
    }

    public static class FileReader {

        private final File file;

        public FileReader(File file) throws InterruptedException {
            // TODO Check Permissions from System
            this.file = file;

            this.file.mutex.acquire();
            this.file.readers++;
            if (this.file.readers == 1) this.file.write.acquire();
            this.file.mutex.release();
        }

        public void close() throws InterruptedException {
            this.file.mutex.acquire();
            this.file.readers--;
            if (this.file.readers == 0) this.file.write.release();
            this.file.mutex.release();
        }
    }

    public static class FileWriter {

        private final File file;
        private final boolean open = false;

        public FileWriter(File file) throws InterruptedException {
            // TODO Check Permissions from System
            this.file = file;

            this.file.write.acquire();
        }

        public void write(String s) {
            file.buffer = s;
        }

        public void close() {
            this.file.write.release();
        }
    }
}
