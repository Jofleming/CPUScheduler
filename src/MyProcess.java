package src;
import java.util.ArrayList;
public class MyProcess {
    String ID;
    int priority;
    int reversePriority;
    public ArrayList<Integer> events;
    public int eventsIndex;
    int waitTime;
    int turnAroundTime;
    int responseTime;
    int ReadyQAddTime;
    int arrivalTime;
    boolean isFirstCpuCycle;

    public void setPriority(int priority) {
        this.priority = priority;
        this.reversePriority = priority;
    }
    public ArrayList<Integer> getEvents() {
        return events;
    }
    public int getNextEventDuration() {
        if (eventsIndex < events.size()) {
            return events.get(eventsIndex);

        }
        return -1;
    }
    public void moveIndex() {
        eventsIndex++;
    }

    public void setReadyQAddTime(int QAddTime) {
        this.ReadyQAddTime = QAddTime;
    }
    MyProcess(String ID, ArrayList<Integer> events) {
        this.events = events;
        eventsIndex = 0;
        waitTime = 0;
        turnAroundTime = 0;
        responseTime = 0;
        ReadyQAddTime = 0;
        priority = 0;
        this.ID = ID;
        this.isFirstCpuCycle = true;
    }
    public void moveOutOfReadyQ() {
        moveIndex();
    }

    public void saveCPUStartStats(int currentTime) {
        if(isFirstCpuCycle) {
            responseTime = currentTime;
            isFirstCpuCycle = false;
        }
        waitTime += currentTime - ReadyQAddTime;
    }

    public void finishStatistics(int currentTime) {
        turnAroundTime = currentTime - arrivalTime;
    }
}