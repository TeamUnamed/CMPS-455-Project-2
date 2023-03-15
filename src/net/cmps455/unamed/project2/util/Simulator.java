package net.cmps455.unamed.project2.util;

/**
 *  <p>Parent class for all Simulators.
 *  <p>Use {@link Simulator#run()} to start the simulation.
 */
public abstract class Simulator extends Thread {

    /**
     * Start the simulation.
     */
    public abstract void start();
}
