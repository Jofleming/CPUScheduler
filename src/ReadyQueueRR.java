package src;
import java.util.LinkedList;
import java.util.Queue;
public class ReadyQueueRR {
    Queue<Event> eventsQ;
    ReadyQueueRR() {
        eventsQ = new LinkedList<Event>();
    }
    public void addProcess(Event event, int currentTime) {
        eventsQ.add(event);
        event.p.setReadyQAddTime(currentTime);
    }
    public Event getNextEvent() {
        Event e = eventsQ.poll();
        if (e == null){
            return null;
        }
        return e;
    }

}
