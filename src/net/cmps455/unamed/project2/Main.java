package net.cmps455.unamed.project2;

public class Main {

    public static void main(String[] args) {

        /* Argument Checking */
        System.out.println();

        if (args.length == 0) {
            System.out.println("ERROR: No Arguments Specified");
            return;
        }

        if (!args[0].equals("-S")) {
            System.out.println("ERROR: Invalid Argument '" + args[0] + "'; Only '-S' is permitted.");
            return;
        }

        if (args.length == 1) {
            System.out.println("Error: Missing Argument Value for '-S <VALUE>'");
            return;
        }

        int simulationIndex;

        try {
            simulationIndex = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Error: Argument Value for '-S <VALUE>' is not a number.");
            return;
        }
        /* -------- */

        /* Simulation Index Checking */
        switch (simulationIndex) {
            case 1 -> System.out.println("TEMP -> STARTING SIMULATION 1");
            case 2 -> System.out.println("TEMP -> STARTING SIMULATION 2");
            case 3 -> System.out.println("TEMP -> STARTING SIMULATION 3");
            default -> {
                System.out.println("ERROR: Invalid Simulation (SIMULATION " + simulationIndex + ")");
                return;
            }
        }
        /* -------- */

        System.out.println("Simulation " + simulationIndex + " selected!");
        System.out.println();

        // TODO: Start Simulation

        System.out.println();
        System.out.println("Simulation has ended!");
        System.out.println("Goodbye!");
    }

}
