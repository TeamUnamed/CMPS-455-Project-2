package net.cmps455.unamed.project2.simulators.task3;

import net.cmps455.unamed.project2.api.system.Domain;
import net.cmps455.unamed.project2.api.system.File;
import net.cmps455.unamed.project2.api.system.VirtualSystem;

import javax.naming.NoPermissionException;
import java.util.Random;

public class DomainThread extends Thread {

    private final Domain domain;
    private final VirtualSystem system;

    public DomainThread(Domain domain, VirtualSystem system) {
        this.domain = domain;
        this.system = system;
    }

    @Override
    public void run() {
        Random random = new Random();
        File[] files = system.getFiles();
        Domain[] domains = system.getDomains();

        for (int r = 0; r < 5; r++) { // Select random file/domain
            int o;
            do {
                o = random.nextInt(files.length + domains.length);
            } while (o >= files.length && domains[o-files.length].equals(domain));

            if (o >= files.length) { // If domain then attempt to switch
                Domain domain = domains[o - files.length];

                log("Attempting to switch from %1$s to %2$s", this.domain.getName(), domain.getName());
                try {
                    system.Switch(this.domain, domain);
                } catch (NoPermissionException e) {
                    log("Operation Failed | %s", e.getMessage());
                }


            } else { // If file then try to
                File file = files[o];


                if (random.nextInt(2) == 0) { // Read
                    log("Attempting to read from %s", file.getName());
                    try {
                        File.FileReader reader = new File.FileReader(file);

                        reader.close();
                        log("Read from %s complete", domain.getName());
                    } catch (InterruptedException e) {
                        log("Operation Failed | %s", e.getMessage());
                    }


                } else { // Write
                    log("Attempting to write to %s", file.getName());
                    try {
                        File.FileWriter writer = new File.FileWriter(file);

                        writer.close();
                        log("Write from %s complete", domain.getName());
                    } catch (InterruptedException e) {
                        log("Operation Failed | %s", e.getMessage());
                    }
                }
            }

            // Yield for 3 to 7 cycles.
            int yieldCount = random.nextInt(5) + 3; // [3,7]
            log("Yielding %d times.", yieldCount);
            for (int i = 0; i < yieldCount; i++) {
                Thread.yield();
            }
        }
    }

    private void log (String msg, Object ... args) {
        if (args.length == 0)
            System.out.println(msg);
        else
            System.out.printf("[" + this.domain.getName() + "] " + msg + "%n", args);
    }
}
