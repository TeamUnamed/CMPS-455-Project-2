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


    }

}
