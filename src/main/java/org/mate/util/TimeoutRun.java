package org.mate.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TimeoutRun {
    public static boolean timeoutRun(Callable<Void> c, long milliseconds) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<Void> future = executor.submit(c);
        boolean finishedWithoutTimeout = false;

        try {
            Log.println("Starting timeout run...");
            future.get(milliseconds, TimeUnit.MILLISECONDS);
            Log.println("Finished run before timeout.");
            finishedWithoutTimeout = true;
        } catch (TimeoutException e) {
            Log.println("Timeout. Requesting shutdown...");
            executor.shutdownNow();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Log.printError("Unexpected exception awaiting termination of timeout run: " + e.getMessage());
                e.printStackTrace();
            }
            Log.println("Finished run due to timeout.");
        } catch (InterruptedException | ExecutionException e) {
            Log.printError("Unexpected exception in timeout run: " + e.getMessage());
            e.printStackTrace();
        }

        executor.shutdownNow();
        return  finishedWithoutTimeout;
    }
}
