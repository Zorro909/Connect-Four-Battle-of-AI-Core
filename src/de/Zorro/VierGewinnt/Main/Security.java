package de.Zorro.VierGewinnt.Main;

import java.util.Set;

public class Security {

    public static void shutdownThreads() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        for(Thread t : threadArray){
            if(Thread.currentThread().getId()!=t.getId()){
                if(t.isAlive()){
                    t.stop();
                }
            }
        }
    }

}
