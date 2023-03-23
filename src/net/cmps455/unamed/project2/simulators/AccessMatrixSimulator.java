package net.cmps455.unamed.project2.simulators;

import java.util.Random;

public class AccessMatrixSimulator extends Simulator {

    @Override
    public void start() {
        Random random = new Random();

        int domainCount = random.nextInt(5) + 3; // between 3 and 7
        int objectCount = random.nextInt(5) + 3; // between 3 and 7
        String[][] accessMatrix = new String[domainCount][objectCount + domainCount];

        System.out.println("Domain count: " + domainCount);
        System.out.println("Object count: " + objectCount);

        // Generate Access Matrix
        for (int i = 0; i < domainCount; i++) {
            for (int j = 0; j < objectCount; j++) {
                accessMatrix[i][j] = switch(random.nextInt(4)) {
                    case 1 -> "R";
                    case 2 -> "W";
                    case 3 -> "R/W";
                    default -> " ";
                };
            }
            for (int j = 0; j < domainCount; j++) {
                accessMatrix[i][objectCount + j] = (i == j) ? " " : (random.nextInt(2) == 1)? "A" : " ";
            }
        }

        // Print Access Matrix
        System.out.print("    ");
        for (int i = -1; i < domainCount; i++) {
            if (i < 0) {
                for (int j = 0; j < objectCount; j++) {
                    System.out.printf(" F%d  ", j+1);
                }
                for (int j = 0; j < domainCount; j++) {
                    System.out.printf(" D%d  ", j+1);
                }
                System.out.println();
                continue;
            }

            System.out.printf("  D%d", i+1);
            for (int j = 0; j < objectCount; j++) {
                System.out.printf(" %-3s ", accessMatrix[i][j]);
            }
            for (int j = 0; j < domainCount; j++) {
                System.out.printf(" %-3s ", accessMatrix[i][objectCount+j]);
            }
            System.out.println();
        }


    }

}
