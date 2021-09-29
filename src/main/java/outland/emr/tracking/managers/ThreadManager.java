package outland.emr.tracking.managers;

import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class ThreadManager {
    private final Queue<Thread> listOfThreads = new ConcurrentLinkedQueue<>();

    public void addToThread(Thread thread) {
        listOfThreads.add(thread);
    }

    public void flushThreads() {
        for (int i = 0; i < (long) listOfThreads.size(); i++) {
            listOfThreads.peek().interrupt();
        }
        listOfThreads.clear();
    }
}
