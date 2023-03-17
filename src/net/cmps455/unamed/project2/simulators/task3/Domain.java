package net.cmps455.unamed.project2.simulators.task3;

import java.util.Random;

public class Domain extends Thread implements VirtualDomain {

    private final VirtualObject[] objects;
    private final int id;
    private final Arbitrator arbitrator;

    public Domain(int id, VirtualObject[] objects, Arbitrator arbitrator) {
        this.id = id;
        this.objects = objects;
        this.arbitrator = arbitrator;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return "D" + getID();
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int r = 0; r < 5; r++) {
            VirtualObject choice;
            do {
                choice = objects[random.nextInt(objects.length)];
            } while (choice.equals(this));

            if (choice.isDomain()) {
                System.out.printf("[%1$s] Attempting to switch from %1$s to %2$s%n", this, choice.getID());
            } else {
                int flag = random.nextInt(2) + 1;
                System.out.printf("[%1$s] Attempting to %3$s %2$s%n", this, choice.getID(), Integer.toString(flag,2));
            }

            int yieldCount = random.nextInt(5) + 3; // [3,7]
            System.out.printf("[%s] Yielding %d times.%n", this, yieldCount);
            for (int i = 0; i < yieldCount; i++) {
                Thread.yield();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Domain))
            return false;

        return this.id == ((Domain) o).id;
    }
}
