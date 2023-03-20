package net.cmps455.unamed.project2.simulators.task3;

import net.cmps455.unamed.project2.api.file.VirtualFileReader;
import net.cmps455.unamed.project2.api.VirtualDomain;
import net.cmps455.unamed.project2.api.file.VirtualFile;
import net.cmps455.unamed.project2.api.file.VirtualFileWriter;
import net.cmps455.unamed.project2.api.system.VirtualSystem;

import javax.naming.NoPermissionException;
import java.util.Random;

public class DomainThread extends Thread {

    private final VirtualDomain domain;
    private final VirtualSystem system;

    public DomainThread(VirtualDomain domain, VirtualSystem system) {
        this.domain = domain;
        this.system = system;
    }

    @Override
    public void run() {
        Random random = new Random();
        VirtualFile[] files = system.getFiles();
        VirtualDomain[] domains = system.getDomains();

        for (int r = 0; r < 5; r++) { // Select random file/domain
            int o;
            do {
                o = random.nextInt(files.length + domains.length);
            } while (o >= files.length && domains[o-files.length].equals(domain));

            if (o >= files.length) { // If domain then attempt to switch
                VirtualDomain domain = domains[o - files.length];

                system.log("Attempting to switch from %s to %s", this.domain.getName(), domain.getName());
                try {
                    system.Switch(this.domain, domain);
                    system.log("Operation Completed | Switched domains to %s", domain.getName());
                } catch (NoPermissionException e) {
                    system.log("Operation Failed | %s", e.getMessage().replace("\r\n", ""));
                }

            } else { // If file then try to
                VirtualFile file = files[o];

                if (random.nextInt(2) == 0) { // Read
                    system.log("Attempting to read from %s", file.getName());
                    VirtualFileReader reader = null;
                    try {
                        reader = new VirtualFileReader(file);

                        String txt = reader.read();

                        system.log("Read complete | Resource %s contains \"%s\"", file.getName(), txt);
                    } catch (IllegalAccessException e) {
                        system.log("Operation Failed | %s", e.getMessage().replace("\r\n", ""));
                    } finally {
                        if (reader != null) reader.close();
                    }

                } else { // Write
                    system.log("Attempting to write to %s", file.getName());
                    VirtualFileWriter writer = null;
                    try {
                        writer = new VirtualFileWriter(file);

                        writer.write(domain.getName());

                        system.log("Write complete | \"%s\" written to %s", domain.getName(), file.getName());
                    } catch (IllegalAccessException e) {
                        system.log("Operation Failed | %s", e.getMessage().replace("\r\n", ""));
                    } finally {
                        if (writer != null) writer.close();
                    }
                }
            }

            // Yield for 3 to 7 cycles.
            int yieldCount = random.nextInt(5) + 3; // [3,7]
            system.log("Yielding %d times.", yieldCount);
            for (int i = 0; i < yieldCount; i++) {
                Thread.yield();
            }
        }
    }
}
