package com.company;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    /**
     * All xml/xsl operation done without importing libraries - for millions and billions of rows it can be memory-addictive. Better using some JAXP for this, SOX mb
     * The only library imported - jdbc driver
     * All logic run in new thread, while in parent we checking time
     * The only way to stop child thread is - System.exit(1) - all others methods don't stop it
     */
    private static final int minutesToStopCalculation = 1;
    private static final long maximumOfN = 1000000;

    public static void main(String[] args) {
        System.out.println("___stage with asking N an erase/fulfill table");
        Scanner in = new Scanner(System.in);
        System.out.println("Enter N:");
        long N = in.nextInt();
        //ExecutorService service = Executors.newCachedThreadPool();
        ExecutorService service = Executors.newFixedThreadPool(1);
        Future future = service.submit((Runnable) new allBLogic(N));
        if (N > maximumOfN) {
            System.out.println("careful N is more than " + maximumOfN);
            long start = System.currentTimeMillis();
            long end = start + minutesToStopCalculation * 10 * 1000;
            while (System.currentTimeMillis() < end) {}
            System.out.println("TOO MUCH TIME!\nProgram will close now, please wait");
            // to cancel an individual task
            future.cancel(true);
            service.shutdown();
            service.shutdownNow();
            //System.exit(0);
        }
    }
}