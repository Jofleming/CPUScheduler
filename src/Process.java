package src;

import java.util.ArrayList;

public class Process {
    private String ID;
    private int priority;
    private ArrayList<Integer> events;
    private int eventsIndex;
    private int waitTime;
    private int turnAroundTime;
    private int responseTime;
    private int QAddTime;
    private int burstTime;

    public Process(String ID) {
        this.ID = ID;
        this.events = new ArrayList<>();
        this.eventsIndex = 0;
        this.waitTime = 0;
        this.turnAroundTime = 0;
        this.responseTime = -1;
        this.QAddTime = 0;
        this.priority = 1;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ArrayList<Integer> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Integer> events) {
        this.events = events;
        this.burstTime = events.stream().mapToInt(Integer::intValue).sum();
    }

    public int getCurrentEvent() {
        return events.get(eventsIndex);
    }

    public void updateCurrentEvent(int update) {
        int updatedEvent = events.get(eventsIndex) + update;
        events.set(eventsIndex, updatedEvent);
    }

    public void updateProcessStats(int endTime) {
        turnAroundTime = endTime;
        waitTime = Math.max(0, turnAroundTime - burstTime);
    }

    public boolean eventAtEnd() {
        return eventsIndex == events.size() - 1;
    }

    public int getEventsIndex() {
        return eventsIndex;
    }

    public void setEventsIndex(int eventsIndex) {
        this.eventsIndex = eventsIndex;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public int getTurnAroundTime() {
        return turnAroundTime;
    }

    public void setTurnAroundTime(int turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public int getQAddTime() {
        return QAddTime;
    }

    public void setQAddTime(int QAddTime) {
        this.QAddTime = QAddTime;
    }

    public String reportString() {
        return String.format("Process: %s , Response Time: %d , Wait Time: %d , Turnaround Time: %d", ID, responseTime, waitTime, turnAroundTime);
    }
}
