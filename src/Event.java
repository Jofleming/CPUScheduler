package src;

public class Event {
    public MyProcess p;

    public int completionTime;
    public EventName event;
    public int remainingTime;
    Event(MyProcess p, int completionTime, EventName event){
        this.p = p;
        this.completionTime = completionTime;
        this.remainingTime = completionTime;
        this.event = event;
    }
}