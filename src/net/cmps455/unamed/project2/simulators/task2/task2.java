package com.company;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class task2 implements Runnable{

    private int ID;
    private int domain;
    private int object;
    private String[][] objectList;
    private int[][] domainList;
    private Semaphore[][] oLock;

    public int getID(){return this.ID; }
    public void setID(){this.ID = ID; }

    public int getDomain(){return this.domain; }
    public void setDomain(){this.domain = domain; }

    public int getObject(){return this.object; }
    public void setObject(){this.object = object; }

    public String[][] getObjectList(){return this.objectList; }
    public void setObjectList(){this.objectList = objectList; }

    public int[][] getDomainList(){return this.domainList; }
    public void setDomainList(){this.domainList = domainList; }

    public Semaphore[][] getoLock(){return this.oLock; }
    public void setoLock(){this.oLock = oLock; }

    task2(int ID, int domain, int object, String[][] objectList, int[][] domainList, Semaphore[][] oLock){
        this.ID = ID;
        this.domain = domain;
        this.object = object;
        this.objectList = objectList;
        this.domainList = domainList;
        this.oLock = oLock;
    }

    public void run() {
        // Array for write to randomly pick from
        char[] grade = new char[5];
        grade[0] = 'A';
        grade[1] = 'B';
        grade[2] = 'C';
        grade[3] = 'D';
        grade[4] = 'F';
        int write;

        // Variables to track indices and semaphores
        // domainNum = location of current domain (starting with Domain 1)
        // domainIndex = index of domainNum in the array
        // dSwitch = random generator for domain switching
        // currDomain = semaphore to keep track of the current domain resource
        // oPermission = random generator for read/write permissions on each object
        // yield = random yield generator
        Random random = new Random();
        int domainNum = 1;
        int domainIndex = domainNum - 1;
        int dSwitch;
        Semaphore currDomain = new Semaphore(1);
        int oPermission;
        int yield;
        Lock[][] dLock = new Lock[domain][domain];

        // Initial output
        System.out.print("[Thread: " + ID + "(D" + domainNum + ")] ");
        int firstReq = arbitration();

        // Initial request when the thread begins running
        // arbitration function == 2 then attempting to domain switch
        if (firstReq == 2) {
            dSwitch = random.nextInt(domain - 1) + 1;
            if (dSwitch == domainNum) {
                dSwitch = random.nextInt(domain - 1) + 1;
            }
            System.out.println("Attempting to switch from D" + domainNum + " to D" + dSwitch + ".");
            if (domainList[domainIndex][dSwitch] != 0) {
                System.out.println("[Thread: " + ID + "(D" + domainNum + ")] Switched to D" + dSwitch);
                domainNum = dSwitch;
                if (currDomain.availablePermits() < domainNum) {
                    for (int i = 0; i < domainNum; i++) {
                        try {
                            currDomain.acquire();
                        } catch (Exception e) {
                        }
                    }
                } else if (currDomain.availablePermits() > domainNum) {
                    for (int i = 0; i < domainNum; i++) {
                        currDomain.release();
                    }
                }
                yield = random.nextInt((7 - 3) + 1) + 3;
                System.out.println("[Thread: " + ID + "(D" + domainNum + ")] Yielding " + yield + " times");
                Thread.yield();
            } else {
                System.out.println("[Thread: " + ID + "(D" + domainNum + ")] Operation failed, permission denied");
            }
            // arbitration function = 1 then request attempting to write to a file
        }else if (firstReq == 1) {
            write = random.nextInt(5);
            oPermission = random.nextInt(object - 1) + 1;
            System.out.println("Attempting to write resource: F" + oPermission + ".");
            if (!objectList[oPermission][domainIndex].equals("R") || !objectList[oPermission][domainIndex].equals("null")) {
                System.out.println("[Thread: " + ID + "(D" + domainNum + ")] Writing '" + grade[write] + "' to resource F" + oPermission);
                if (oLock[oPermission][domainIndex].availablePermits() == 1) {
                    try {
                        oLock[oPermission][domainIndex].acquire(1);
                    } catch (Exception e) {
                    }
                }
                yield = random.nextInt((7 - 3) + 1) + 3;
                System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Yielding " + yield + " times");
                Thread.yield();
                oLock[oPermission][domainIndex].release();
            } else {
                System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Operation failed, permission denied");
            }
        } else if (firstReq == 0) {
            oPermission = random.nextInt(object - 1) + 1;
            write = random.nextInt(5);
            System.out.println("Attempting to read resource: F" + oPermission + ".");
            if (!objectList[oPermission][domainIndex].equals("W") || !objectList[oPermission][domainIndex].equals("null")) {
                System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Resource F" + oPermission + " contains " + grade[write]);
                if (oLock[oPermission][domainIndex].availablePermits() == 1) {
                    try {
                        oLock[oPermission][domainIndex].acquire(1);
                    } catch (Exception e) {
                    }
                }
                yield = random.nextInt((7 - 3) + 1) + 3;
                System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Yielding " + yield + " times");
                Thread.yield();
                oLock[oPermission][domainIndex].release();
            } else {
                System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Operation failed, permission denied");
            }
        }

        // Requests 2-5
        for (int i = 0; i < 5; i++) {
            int request = arbitration();
            if (firstReq == 2) {
                dSwitch = random.nextInt(domain - 1) + 1;
                if (dSwitch == domainNum) {
                    dSwitch = random.nextInt(domain - 1) + 1;
                }
                System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Attempting to switch from D" + domainNum + " to D" + dSwitch + ".");
                if (domainList[domainIndex][dSwitch] != 0) {
                    System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Switched to D" + dSwitch);
                    domainNum = dSwitch;
                    if (currDomain.availablePermits() < currDomain.availablePermits()) {
                        for (int j = 0; i < currDomain.availablePermits(); i++) {
                            try {
                                currDomain.acquire();
                            } catch (Exception e) {
                            }
                        }
                    } else if (currDomain.availablePermits() > domainNum) {
                        for (int j = 0; i < domainNum; i++) {
                            currDomain.release();
                        }
                    }
                    yield = random.nextInt((7 - 3) + 1) + 3;
                    System.out.println("[Thread: " + ID + "(D" + domainNum + ")] Yielding " + yield + " times");
                    Thread.yield();
                } else {
                    System.out.println("[Thread: " + ID + "(D" + domainNum + ")] Operation failed, permission denied");
                }
            } else if (firstReq == 1) {
                write = random.nextInt(5);
                oPermission = random.nextInt(object - 1) + 1;
                System.out.println("[Thread: " + ID + "(D" + domainNum + ")]Attempting to write resource: F" + oPermission + ".");
                if (!objectList[oPermission][domainIndex].equals("R") || !objectList[oPermission][domainIndex].equals("null")) {
                    System.out.println("[Thread: " + ID + "(D" + domainNum + ")] Writing '" + grade[write] + "' to resource F" + oPermission);
                    if (oLock[oPermission][domainIndex].availablePermits() == 1) {
                        try {
                            oLock[oPermission][domainIndex].acquire(1);
                        } catch (Exception e) {
                        }
                    }
                    yield = random.nextInt((7 - 3) + 1) + 3;
                    System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Yielding " + yield + " times");
                    Thread.yield();
                    oLock[oPermission][domainIndex].release();
                } else {
                    System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Operation failed, permission denied");
                }
            } else if (firstReq == 0) {
                oPermission = random.nextInt(object - 1) + 1;
                write = random.nextInt(5);
                System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Attempting to read resource: F" + oPermission + ".");
                if (!objectList[oPermission][domainIndex].equals("W") || !objectList[oPermission][domainIndex].equals("null")) {
                    System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Resource F" + oPermission + " contains " + grade[write]);
                    if (oLock[oPermission][domainIndex].availablePermits() == 1) {
                        try {
                            oLock[oPermission][domainIndex].acquire(1);
                        } catch (Exception e) {
                        }
                    }
                    yield = random.nextInt((7 - 3) + 1) + 3;
                    System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Yielding " + yield + " times");
                    Thread.yield();
                    oLock[oPermission][domainIndex].release();
                } else {
                    System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Operation failed, permission denied");
                }
            }
        }
        System.out.println("[Thread: " + ID + "(D" + currDomain.availablePermits() + ")] Operation Complete");
    }
    // Arbitration function
    public static int arbitration(){
        Random random = new Random();
        int request = random.nextInt(3);

        // If request = 2 then attempt to switch domains
        // if request = 1 then attempt to write
        // if request = 0 then attempt to read
        if(request == 2){
            return request;
        }
        else if(request == 1){
            return request;
        }
        else if(request == 0){
            return request;
        }
        return -1;
    }
}
