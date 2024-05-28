package src;

import java.util.ArrayList;
public class Scheduler {
    ArrayList<MyProcess> processes;
    ReadyQueueRR readyQ;
    IOEventQueue IOQ;
    //private boolean isPreemptive;
    int currentTime;
    int cpuBurstTime = 5;
    int totalCPUTime = 0;
    Event runningProcess = null;
    Scheduler(ReadyQueueRR readyQ, int cpuBurstTime) {
        this.cpuBurstTime = cpuBurstTime;
        processes = new ArrayList<MyProcess>();
        this.readyQ = readyQ;
        this.IOQ = new IOEventQueue();
        currentTime = 0;
    }

    public void addProcess(MyProcess p) {
        this.processes.add(p);
        for(int i = 0; i < p.events.size(); i+=2) {
            totalCPUTime += p.events.get(i);
        }
        readyQ.addProcess(new Event(p,p.getNextEventDuration(),EventName.READY), currentTime);
    }
    public void SimulateScheduler(boolean printSteps) {
        startNextProcess(0);
        Event e = this.getNextEvent ();
        while (e != null) {
            if (e.event == EventName.IO) {
                Event e1 = IOQ.getNextEvent();
                MyProcess p = e1.p;
                currentTime += e1.completionTime;
                IOQ.advanceTime(e1.completionTime);
                int nextDuration = p.getNextEventDuration();
                if(printSteps) {
                    System.out.printf("CurrentTime: %d, ProcessID: %s, IO Finished next CPU time: %d\n", currentTime, p.ID, nextDuration);
                }
                if(runningProcess != null ){
                    runningProcess.remainingTime -= e1.completionTime;
                }

                if (nextDuration >= 0) {
                    readyQ.addProcess(new Event(p, p.getNextEventDuration(), EventName.READY),
                            currentTime);
                }
                startNextProcess(currentTime);

            } else {
                int cpuTime = runningProcess.remainingTime;
                if (runningProcess.p.events.get(runningProcess.p.eventsIndex) > 0) {
                    // This process still have some more CPU time to consume
                    IOQ.advanceTime(cpuTime);
                    currentTime += cpuTime;
                    if(printSteps) {
                        System.out.printf("CurrentTime: %d, ProcessID: %s, CPU burst Finished for %d, next CPU cycle: %d\n", currentTime,
                                runningProcess.p.ID, runningProcess.completionTime, runningProcess.p.getNextEventDuration());
                    }
                    // Add this process back in ready queue. Remaining CPU time is already been updated and this cant be last CPU burst time
                    readyQ.addProcess(new Event(runningProcess.p, runningProcess.p.getNextEventDuration(), EventName.READY), currentTime);
                    runningProcess = null;
                    startNextProcess(currentTime);
                } else {
                    currentTime += cpuTime;
                    IOQ.advanceTime(cpuTime);
                    runningProcess.p.moveOutOfReadyQ();
                    int nextDuration = runningProcess.p.getNextEventDuration();
                    if(printSteps) {
                        System.out.printf("CurrentTime: %d, ProcessID: %s, CPU burst Finished for %d, next IO time: %d\n", currentTime, runningProcess.p.ID,
                                runningProcess.completionTime, nextDuration);
                    }
                    if (nextDuration >= 0) {
                        MyProcess p = runningProcess.p;
                        IOQ.addProcess(new Event(p, p.getNextEventDuration(), EventName.IO));
                    }else{
                        // Last CPU burst
                        runningProcess.p.finishStatistics(currentTime);
                    }
                    runningProcess = null;
                    startNextProcess(currentTime);
                }
            }
            //  System.out.printf ("CurrentTime: %d , ProcessID: %s , EventName: %s, Index: %d, Burst: %d\n",
            //currentTime, e.p.ID, e.event, e.p.eventsIndex, e.completionTime);
            e = getNextEvent();
        }
    }

    public void generateReport() {

        int count = 0;
        float totalWaitTime = 0;
        float totalResponseTime = 0;
        float totalTurnAroundTime = 0;
        for(MyProcess p : processes) {
            System.out.printf("Process ID: %s, Wait time: %d, Response time: %d, Turnaround time: %d, \n", p.ID, p.waitTime, p.responseTime, p.turnAroundTime);
            totalWaitTime += p.waitTime;
            totalResponseTime += p.responseTime;
            totalTurnAroundTime += p.turnAroundTime;
            count++;
        }
        System.out.printf("Total CPU Usage: %.2f%% \n", (totalCPUTime *100.0)/(currentTime));
        System.out.printf("Average Wait Time: %.2f\n", totalWaitTime/count);
        System.out.printf("Average Response Time: %.2f\n", totalResponseTime/count);
        System.out.printf("Average Turnaround Time: %.2f\n", totalTurnAroundTime/count);
    }

    private void startNextProcess(int currentTime) {
        if(runningProcess == null){
            Event e = readyQ.getNextEvent();
            if(e == null)
                return;
            if(e.completionTime > cpuBurstTime){
                e.completionTime = cpuBurstTime;
                e.remainingTime = cpuBurstTime;
                // reduce time from CPU burst time
                e.p.events.set(e.p.eventsIndex, e.p.events.get(e.p.eventsIndex) - cpuBurstTime);
            }else{
                e.p.events.set(e.p.eventsIndex, 0);

            }
            e.p.saveCPUStartStats(currentTime);
            runningProcess = e;
        }
    }

    private Event getNextEvent() {
        if (runningProcess == null && IOQ.isEmpty()) {
            return null;
        }
        int nextIO = Integer.MAX_VALUE;
        int nextCPU = Integer.MAX_VALUE;
        if(runningProcess!= null) {
            nextCPU = runningProcess.remainingTime;
        }
        if (!IOQ.isEmpty()){
            nextIO = IOQ.peakNextEvent().completionTime;
        }

        if(nextCPU <= nextIO) {
            return runningProcess;
        } else {
            return IOQ.peakNextEvent();
        }
    }

}