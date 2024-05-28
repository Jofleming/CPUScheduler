package src;

import java.util.PriorityQueue;

public class ReadyQueuePriority extends ReadyQueueRR {
    ReadyQueuePriority () {
        eventsQ = new PriorityQueue<Event>((a, b ) -> a.p.reversePriority - b.p.reversePriority);

    }
}
