package net.cmps455.unamed.project2;

import net.cmps455.unamed.project2.simulators.AccessListSimulator;
import net.cmps455.unamed.project2.simulators.AccessMatrixSimulator;
import net.cmps455.unamed.project2.simulators.CapabilityListSimulator;
import net.cmps455.unamed.project2.simulators.Simulator;

public class Main {

    public static void main(String[] args) {

        /* Argument Checking */
        System.out.println();

        if (args.length == 0) { // return on no arguments
            System.out.println("ERROR: No Arguments Specified");
            return;
        }

        if (!args[0].equals("-S")) { // return on argument no '-S'
            System.out.println("ERROR: Invalid Argument '" + args[0] + "'; Only '-S' is permitted.");
            return;
        }

        if (args.length == 1) { // return on no argument value
            System.out.println("Error: Missing Argument Value for '-S <VALUE>'");
            return;
        }

        if (args.length > 2) { // return on too many arguments
            System.out.println("Error: Too Many Arguments detected!");
        }

        int simulationIndex;

        try {
            simulationIndex = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) { // return on argument value not ia number
            System.out.println("Error: Argument Value for '-S <VALUE>' is not a number.");
            return;
        }
        /* -------- */

        /* Simulation Index Checking */
        Simulator simulator = switch (simulationIndex) {
            case 1 -> new AccessMatrixSimulator();
            case 2 -> new AccessListSimulator();
            case 3 -> new CapabilityListSimulator();
            default -> null;
        };

        if (simulator == null) {
            System.out.println("ERROR: Invalid Simulator (SIMULATION " + simulationIndex + ")");
            return;
        }

        /* -------- */

        System.out.println("Simulator " + simulationIndex + " selected!");
        System.out.println();

        simulator.start();

        System.out.println();
        System.out.println("Simulation has ended!");
        System.out.println("Goodbye!");
    }

}
