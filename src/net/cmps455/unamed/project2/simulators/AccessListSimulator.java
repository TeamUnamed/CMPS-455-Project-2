package net.cmps455.unamed.project2.simulators;

import net.cmps455.unamed.project2.simulators.task2.AccessList;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class AccessListSimulator extends Simulator {

    @Override
    public void start() {
        Random random = new Random();

        // Randomly generating n (domain) and m (objects)
        int domainCount = random.nextInt(5) + 3;
        int objectCount = random.nextInt(5) + 3;
        int printIndex;

        // Variables to randomly populate the access list
        int readPop;
        int writePop;
        int switchPop;
        LinkedList<String> pop = new LinkedList<>();

        //
        String[][] object = new String[objectCount][domainCount];
        String[][] domain = new String[domainCount][domainCount];
        int[][] oIndex = new int[objectCount][domainCount];
        int[][] dIndex = new int[domainCount][domainCount];
        Semaphore[][] oLock = new Semaphore[objectCount][domainCount];
        for(int i=0; i < objectCount; i++){
            for(int j=0; j < domainCount; j++){
                oLock[i][j] = new Semaphore(1);
            }
        }

        // Access List Output
        System.out.println("Domain Count: " + domainCount);
        System.out.println("Object Count: " + objectCount);
        for(int i = 1; i <= objectCount; i++){
            pop.clear();
            System.out.print("F" + i + " --> ");
            for(int j = 0; j < domainCount; j++){
                printIndex = j + 1;
                readPop = random.nextInt(2);
                writePop = random.nextInt(2);
                if(readPop == 1 && writePop == 1){
                    pop.add("R/W");
                    object[i-1][j] = "R/W";
                    oIndex[i-1][j] = printIndex;
                }
                else if(readPop == 1){
                    pop.add("R");
                    object[i-1][j] = "R";
                    oIndex[i-1][j] = printIndex;
                }
                else if (writePop == 1){
                    pop.add("W");
                    object[i-1][j] = "W";
                    oIndex[i-1][j] = printIndex;
                }
                else{
                    pop.add("null");
                    object[i-1][j] = "null";
                }

                if(printIndex == domainCount){
                    if(pop.get(j).equals("null")){
                        continue;
                    }
                    else{
                        System.out.print("D" + printIndex + ":" + pop.get(j));
                    }
                }
                else if(pop.get(j).equals("null")){
                    continue;
                }
                else{
                    System.out.print("D" + printIndex + ":" + pop.get(j) + ", ");
                }
            }
            System.out.println();
        }

        for(int i = 1; i <= domainCount; i++){
            pop.clear();
            System.out.print("D" + i + " --> ");
            for(int j = 0; j < domainCount; j++){
                printIndex = j + 1;
                switchPop = random.nextInt(2);
                if(printIndex == i){
                    pop.add("null");
                }
                else if(switchPop == 1){
                    pop.add("allow");
                    domain[i-1][j] = "allow";
                    dIndex[i-1][j] = 1;
                }
                else{
                    pop.add("null");
                    dIndex[i-1][j] = 0;
                }
                if(printIndex == domainCount){
                    if(pop.get(j).equals("null") || printIndex == i){
                        continue;
                    }
                    else{
                        System.out.print("D" + printIndex + ":" + pop.get(j));
                    }
                }
                else if(pop.get(j).equals("null") || printIndex == i){
                    continue;
                }
                else{
                    System.out.print("D" + printIndex + ":" + pop.get(j) + ", ");
                }
                //domainIndex++;
            }
            System.out.println();
        }

        ExecutorService pool2 = Executors.newCachedThreadPool();
        for(int i=0; i < 6; i++){
            pool2.execute(new AccessList(i, domainCount, objectCount, object, dIndex, oLock));
            try{
                Thread.sleep(500);
            } catch (Exception e){}
        }
        pool2.shutdown();
    }

    private static class AccessListb {

        final LinkedList<AccessListEntry> list;

        AccessListb() {
            this.list = new LinkedList<>();
        }


    }

    private static class AccessListEntry {

    }
}
