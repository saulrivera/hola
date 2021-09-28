package outland.emr.tracking.managers;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ThreadManager {
    private List<Thread> listOfThreads = new ArrayList<>();

    public void addToThread(Thread thread) {
        listOfThreads.add(thread);
    }

    public void flushThreads() {
        for (int i = 0; i < (long) listOfThreads.size(); i++) {
            listOfThreads.get(i).interrupt();
        }
        listOfThreads.clear();
    }
}
