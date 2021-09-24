package outland.emr.tracking.schedulers;

import outland.emr.tracking.websockets.PersonnelStatusSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PersonnelScheduler {
    @Autowired
    private final PersonnelStatusSocket personnelStatusSocket;

    public PersonnelScheduler(PersonnelStatusSocket personnelStatusSocket) {
        this.personnelStatusSocket = personnelStatusSocket;
    }

    @Scheduled(fixedRate = 1000)
    public void personalEmmit() {
        try {
            personnelStatusSocket.broadcastTracking();
        } catch (IOException error) {
            System.out.println(error);
        }
    }
}
