package src;
import java.util.PriorityQueue;

public class IOEventQueue {

    PriorityQueue <Event> eventsQ;

    IOEventQueue() {
        eventsQ = new PriorityQueue <Event>((a, b ) -> a.completionTime - b.completionTime);
    }

    public void addProcess(Event event){
        eventsQ.add(event);

    }

    public Event getNextEvent(){

        Event e = eventsQ.poll();
        if (e == null){
            return null;
        }
        e.p.moveIndex();
        return e;

    }

    public void advanceTime(int t) {
        for (Event e : eventsQ) {
            e.completionTime = Math.max(e.completionTime -t, 0) ;
        }

    }

    public Event peakNextEvent(){
        return eventsQ.peek();

    }


    public boolean isEmpty() {
        return eventsQ.isEmpty();
    }

}