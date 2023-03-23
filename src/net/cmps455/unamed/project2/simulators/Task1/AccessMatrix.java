import java.util.Random;
import java.util.concurrent.Semaphore;

public class AccessMatrix {
    static Random rand = new Random();
    static int Domain = rand.nextInt(5) + 3;        // Initializes Domain(N) as a random number between 3 and 7
    static int Object = rand.nextInt(5) + 3;        // Initializes Object(M) as a random number between 3 and 7
    String[][] accessMatrix = new String[Domain][Object + Domain]; // 2D string array initializes for the accessMatrix

    public AccessMatrix() {                                // Assign access rights for objects and Domains
        for (int j = 0; j < Object; j++) {                 // A for loop that loops through the objects
            for (int i = 0; i < Domain; i++) {             // A for loop that loops through the Domains
                int r = rand.nextInt(4);            // A new random value that is between 0 and 3
                if (r == 0) {                              // If the random value is 0
                    accessMatrix[i][j] = "R";              // Then the assigned object has the permissions of Read only or R
                } else if (r == 1) {                       // If the random value is 1
                    accessMatrix[i][j] = "W";              // Then the assigned object has the permissions of Write only or W
                } else if (r == 2) {                       // If the random value is 2
                    accessMatrix[i][j] = "R/W";            // Then the assigned object has the permissions of Read or Write, or labeled as R/W
                } else {                                   // If the random value is 3 or any other number
                    accessMatrix[i][j] = " ";              // Then the assigned object has no permissions
                }
            }
        }

        for (int i = 0; i < Domain; i++) {                      // A for loop that loops through the Domains
            for (int j = Object; j < Object + Domain; j++) {    // A for loop that loops through the Objects and domains to give the switching permissions
                if (i == j - Object) {
                    accessMatrix[i][j] = " ";
                } else {
                    int r = rand.nextInt(2);              // A new random value that is between 0 and 1
                    if (r == 0) {                               // If the random value is 0
                        accessMatrix[i][j] = " ";               // Then the switching permission is set to nothing or blank in this case
                    } else {                                    // If the random value is 1 or any other number
                        accessMatrix[i][j] = "A";               // Then the switching permission is set to Allowed or A in this case
                    }
                }
            }
        }
    }

    public boolean hasReadAccess(int domain, int object) {       // Arbitrator that is in charge of determining if the object/domain has reading privileges
        String access = accessMatrix[domain][object];            // Gets the string or permission of the domain/object
        return access.equals("R") || access.equals("R/W");       // return true if the permission is Read or Read/Write
    }

    public boolean hasWriteAccess(int domain, int object) {      // Arbitrator that is in charge of determining if the object/domain has Writing privileges
        String access = accessMatrix[domain][object];            // Gets the string or permission of the domain/object
        return access.equals("W") || access.equals("R/W");       // return true if the permission is Write or Read/Write
    }

    public boolean canSwitch(int fromDomain, int toDomain) {       // Arbitrator that is in charge of determining if the domain can switch
        if (fromDomain == toDomain) {                                  // If it is its own domain
            return false;                                              // returns false
        }
        String access = accessMatrix[fromDomain][Object + toDomain];   // Gets the string or permission of the domain
        return access.equals("A");                                     // return true if the switch is allowed
    }

    public void displayMatrix() {               // Function in charge of displaying the matrix to the user
        System.out.print("Obj/Dom ");           // print statement for the table
        for (int j = 0; j < Object; j++) {      // For loop that loops for as long as Objects
            System.out.printf("%-5s", "F" + j); // Prints the Object rows
        }
        for (int j = 0; j < Domain; j++) {      // For loop that loops for as long as Domains
            System.out.printf("%-5s", "D" + j); // Prints the Domain rows
        }
        System.out.println();
        for (int i = 0; i < Domain; i++) {              // for loop for domains
            System.out.printf("%-8s", "  D" + i + ":"); // prints the domain number
            for (int j = 0; j < Object; j++) {          // for loop for objects
                String access = accessMatrix[i][j];     // gets the permissions for the AccessMatrix
                switch (access) {
                    case "R/W" -> System.out.printf("%-5s", "R/W");  // if the permission is read and write then it will display as R/W
                    case "R" -> System.out.printf("%-5s", "R");      // if the permission is read then it will display as R
                    case "W" -> System.out.printf("%-5s", "W");      // if the permission is Write then it will display as W
                    default -> System.out.printf("%-5s", " ");       // if there are no permissions then it will display empty
                }
            }
            for (int j = Object; j < Object + Domain; j++) {        // for loop in charge of printing switch access
                String access = accessMatrix[i][j];                 // Gets the permissions for the matrix
                if (access.equals("A")) {                           // If it is allowed to switch
                    System.out.printf("%-5s", access + " ");        // print to the table
                } else {                                            // Else
                    System.out.printf("%-5s", " ");                 // Set to nothing
                }
            }
            System.out.println();
        }
    }

    public int getDomain() {                    // Function in charge of getting the domain
        return Domain;                          // Returns the Domain
    }

    public int getObject() {                    // Function in charge of getting the object
        return Object;                          // Returns the Object
    }

    public static void main(String[] args) {
        AccessMatrix matrix = new AccessMatrix();                       // Initializes an accessMatrix from the AccessMatrix class
        Semaphore semaphore = new Semaphore(1);                  // semaphore initializer initialized to 1
        System.out.println("Domain count: " + matrix.getDomain());      // Prints the amount of domains
        System.out.println("Object count: " + matrix.getObject());      // Prints the amount of Objects

        matrix.displayMatrix();                                         // Displays the table of permissions for the matrix
        Thread[] Threads = new Thread[matrix.getDomain()];              // Create multiple threads to access the matrix
        for (int i = 0; i < matrix.getDomain(); i++) {                  // for each domain
            Threads[i] = new Thread(new MatrixThread(i, matrix, semaphore));       // Each thread runs the thread function
            Threads[i].start();                                         // Each thread starts running
        }
    }

    static class MatrixThread implements Runnable {         // Threads that run the program
        private final int ID;                               // Thread id initializer
        private final AccessMatrix Matrix;                  // Matrix initializer
        private final Semaphore semaphore;

        public MatrixThread(int id, AccessMatrix matrix, Semaphore semaphore) {              // contractor for the threads
            this.Matrix = matrix;                                       // sets the matrix for the thread
            this.ID = id;                                               // sets the id for the thread
            this.semaphore = semaphore;
        }

        public void run() {
            for (int i = 0; i < 5; i++) {                                                   // Each thread generates at least 5 requests.
                Random Rand = new Random();                                                 // Random number initializer
                int Domain = Rand.nextInt(Matrix.getDomain());                              // gets a random domain
                int Object = Rand.nextInt(Matrix.getObject() + Matrix.getDomain());  // gets a random object
                if (Object < Matrix.getObject()) {                                          // if the object is less than the max amount of objects it will attempt to read or write
                    int ReadOrWrite = Rand.nextInt(2);                                // Generates a number between 0 and 1
                    if (ReadOrWrite == 0) {                                                 // If the random number is zero then it will attempt to read from the objects
                        System.out.println("[Thread " + ID + "(D" + Domain + ")] Attempting to read resource: F" + Object);     // prints the thread id and domain and the object it is attempting to read
                        try {
                            if (Matrix.hasReadAccess(Domain, Object)) {                                                                     // runs the function that determines if the object can be read from
                                System.out.println("[Thread " + ID + "(D" + Domain + ")] Successfully read from resource: F" + Object);     // If the object has reading privilege
                                semaphore.acquire();                                                                        // Acquires the semaphore
                                int TimesToYield = (int)(Math.random() * 5) + 3;                                                        // Generates a random number between 3 and 7.
                                System.out.println("[Thread " + ID + "(D" + Domain + ")] Yielding " + TimesToYield + " times.");        // Displays how many times the thread yields
                                for (int k = 0; k < TimesToYield; k++) {
                                    Thread.yield();                                                                                     // yields the thread
                                }
                            } else {
                                System.out.println("[Thread " + ID + "(D" + Domain + ")] Operation failed, permission denied.");            // If the object doesn't have reading privilege
                                int TimesToYield = (int)(Math.random() * 5) + 3;                                                        // Generates a random number between 3 and 7.
                                for (int k = 0; k < TimesToYield; k++) {
                                    Thread.yield();                                                                                     // yields the thread
                                }
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {                                                          // If the random number is anything other than zero then it will attempt to write to the objects
                        System.out.println("[Thread " + ID + "(D" + Domain + ")] Attempting to write resource: F" + Object);    // prints the thread id and domain and the object it is attempting to write to
                        try {
                            if (Matrix.hasWriteAccess(Domain, Object)) {                                                                // runs the function that determines if the object can be written to
                                System.out.println("[Thread " + ID + "(D" + Domain + ")] Writing word to Resource: F" + Object);    // If the object has writing privilege
                                semaphore.acquire();                                                                        // Acquires the semaphore
                                int TimesToYield = (int)(Math.random() * 5) + 3;                                                        // Generates a random number between 3 and 7.
                                System.out.println("[Thread " + ID + "(D" + Domain + ")] Yielding " + TimesToYield + " times.");        // Displays how many times the thread yields
                                for (int k = 0; k < TimesToYield; k++) {
                                    Thread.yield();                                                                                     // yields the thread
                                }
                            } else {
                                System.out.println("[Thread " + ID + "(D" + Domain + ")] Operation failed, permission denied.");        // If the object doesn't have writing privilege
                                int TimesToYield = (int)(Math.random() * 5) + 3;                                                        // Generates a random number between 3 and 7.
                                for (int k = 0; k < TimesToYield; k++) {
                                    Thread.yield();                                                                                     // yields the thread
                                }                                 // continues
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {                                                            // if the object is more than the max amount of objects it will attempt to switch domains
                    int toDomain = Object - Matrix.getObject();                     // gets the domain that it will attempt to switch to
                    System.out.println("[Thread " + ID + "(D" + Domain + ")] Attempting to switch to D" + toDomain);      // prints the thread id and domain and the target domain it is attempting to switch to
                    try {
                        if (Matrix.canSwitch(Domain, toDomain)) {                                                       // runs the function that determines if the domain can be switched to
                            System.out.println("[Thread " + ID + "(D" + Domain + ")] Switched to D" + toDomain);        // Switches the domains
                            semaphore.acquire();                                                                        // Acquires the semaphore
                            int TimesToYield = (int)(Math.random() * 5) + 3;                                                        // Generates a random number between 3 and 7.
                            System.out.println("[Thread " + ID + "(D" + Domain + ")] Yielding " + TimesToYield + " times.");        // Displays how many times the thread yields
                            for (int k = 0; k < TimesToYield; k++) {
                                Thread.yield();                                                                                     // yields the thread
                            }
                        } else {
                            System.out.println("[Thread " + ID + "(D" + Domain + ")] Operation failed, permission denied.");        // If the Domain doesn't have switching privilege
                            int TimesToYield = (int)(Math.random() * 5) + 3;                                                        // Generates a random number between 3 and 7.
                            for (int k = 0; k < TimesToYield; k++) {
                                Thread.yield();                                                                                     // yields the thread
                            }                             // continues
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                semaphore.release();
            }
        }
    }
}



