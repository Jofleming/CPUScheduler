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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getPriority() {
        return priority;
    }

    public int getCurrentEvent() {return events.get(eventsIndex);}

    public void updateCurrentEvent(int update) {
        Integer changedEvent = events.get(0).intValue() + update;
        events.set(eventsIndex, changedEvent);
    }

    public void updateProcessStats(int endTime) {
        turnAroundTime = endTime;
        waitTime = turnAroundTime - burstTime;
    }

    public boolean eventAtEnd() {
        if (eventsIndex == events.size() - 1) {
            return true;
        } else {
            return false;
        }
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ArrayList<Integer> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Integer> events) {
        this.events = events;
        int eventsBurst = 0;
        for (int i = 0; i < events.size(); i++) {
            eventsBurst += events.get(i);
        }
        this.burstTime = eventsBurst;
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



    public void updateCurrentEvent(Integer update) {
        events.set(eventsIndex, update);
    }


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
}

