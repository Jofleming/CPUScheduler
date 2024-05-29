package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Scheduler class to emulate the shortest remaining time first scheduling algorithm.
 * @author Jordan Fleming
 * @version 1.0
 * @since 5/22/2024
 */
public class SRTFScheduler {
    Process[] processList;
    ArrayList<Process> IOList;
    Process runningProcess;
    PriorityQueue<Process> processQueue;
    int timeQuantum;
    boolean printOutput = true;
    double avgWT;
    double avgRT;
    double avgTRT;
    double CPUUtilization;

    double totalTimeActive;

    /**
     * Lone constructor for SRTFScheduler class. Full constructor.
     * @param processes     Process array with processes to be scheduled.
     */
    SRTFScheduler(Process[] processes) {
        processList = processes;
        IOList = new ArrayList<Process>();
        runningProcess = null;
        processQueue = new PriorityQueue<>(new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                return o1.getCurrentEvent() - o2.getCurrentEvent();
            }
        });
        for (int i = 0; i < processes.length; i++) {
            processQueue.add(processes[i]);
        }
        timeQuantum = 0;
        avgRT = avgTRT = avgWT = CPUUtilization = totalTimeActive = 0.0;
    }

    /**
     * Runs the simulation with the given array of processes.
     */
    public double[] scheduleCPU() {
        while (!processQueue.isEmpty() || !IOList.isEmpty() || runningProcess != null) {
            if (runningProcess == null && !processQueue.isEmpty()) {
                moveProcessOntoCPU();
            }
            if (runningProcess != null && runningProcess.getCurrentEvent() == 0) {
                if (runningProcess.eventAtEnd()) {
                    runningProcess.updateProcessStats(timeQuantum);
                    runningProcess = null;
                } else {
                    processToIO();
                    moveProcessOntoCPU();
                }
            }
            if (!processQueue.isEmpty() && runningProcess != null && processQueue.peek().getCurrentEvent() < runningProcess.getCurrentEvent()) {
                preempt();
            }
            decrementIO();
            if (runningProcess != null) {
                runningProcess.updateCurrentEvent(-1);
                totalTimeActive++;
            }
            timeQuantum++;
            printCPUStatus();
        }
        computeAvgs();
        System.out.printf("Average Response Time: %d%n", avgRT);
        System.out.printf("Average Waiting Time: %d%n", avgWT);
        System.out.printf("Average Turnaround Time: %d%n", avgTRT);
        System.out.printf("CPU Percent Utilization: %d%%%n", avgRT);
        return new double[]{avgRT, avgWT, avgTRT, CPUUtilization};
    }

    /**
     * Takes the top of the priority queue out and sets to running process is queue is not empty.
     */
    public void moveProcessOntoCPU () {
        if (!processQueue.isEmpty()) {
            runningProcess = processQueue.poll();
            checkFirstLoad(runningProcess);
        }
    }

    public void checkFirstLoad(Process process) {
        if (process.getResponseTime() == -1) {
            process.setResponseTime(timeQuantum);
        }
    }

    /**
     * Adds the runningProcess to the IO ArrayList and sets runningProcess to null.
     */
    public void processToIO() {
        if (runningProcess != null) {
            runningProcess.setEventsIndex(runningProcess.getEventsIndex() + 1);
            IOList.add(runningProcess);
            runningProcess = null;
        }
    }

    /**
     * Increments the event index of a process at a given index then
     * removes a given index from the IO ArrayList and adds it to the priority queue to be scheduled on CPU.
     * @param index     the index of the process
     */
    public void IO_to_Queue(int index) {
        Process processToMove = IOList.get(index);
        processToMove.setEventsIndex(processToMove.getEventsIndex() + 1);
        processQueue.add(IOList.remove(index));
    }

    /**
     * Decrements the IO time left on all processes in the IO ArrayList. Then adds them to queue if IO left is zero.
     */
    public void decrementIO() {
        for (int i = 0; i < IOList.size(); i++) {
            Process currProcess = IOList.get(i);
            currProcess.updateCurrentEvent(currProcess.getCurrentEvent() - 1);
            if (checkZeroTime(currProcess)) {
                IO_to_Queue(i);
            }
        }
    }

    /**
     * Checks to see if the current event for a given Process is 0.
     * @param currProcess       Process to check event of
     * @return                  boolean for if current event of process being 0 is true or false
     */
    public boolean checkZeroTime(Process currProcess) {
        if (currProcess.getCurrentEvent() == 0) {
            return true;
        }
        return false;
    }

    public void preempt() {
        processQueue.add(runningProcess);
        runningProcess = null;
        moveProcessOntoCPU();
    }

    public void computeAvgs() {
        double sumRT = 0.0;
        double sumTRT = 0.0;
        double sumWT = 0.0;
        for (int i = 0; i < processList.length; i++) {
            sumTRT += processList[i].getTurnAroundTime();
            sumRT += processList[i].getResponseTime();
            sumWT += processList[i].getWaitTime();
        }
        avgRT = sumRT / processList.length;
        avgTRT = sumTRT / processList.length;
        avgWT = sumWT / processList.length;
        CPUUtilization = (totalTimeActive / timeQuantum) * 100;
    }


    /**
     * Prints out all the current processes and their states.
     */
    public void printCPUStatus() {
        if (printOutput) {
            System.out.printf("Current Time: %d%n", timeQuantum);
            if (runningProcess != null) {
                System.out.printf("Next process on the CPU: %s%n", runningProcess.getID());
            } else {
                System.out.println("CPU currently awaiting process");
            }
            System.out.println("........................................................");
            System.out.println("List of processes in the ready queue:");
            if (processQueue.isEmpty()) {
                System.out.println("[ EMPTY ]");
            } else {
                PriorityQueue<Process> printQueue = new PriorityQueue<>(processQueue);
                System.out.println("    Process    Burst");
                while (!printQueue.isEmpty()) {
                    Process currProcess = printQueue.poll();
                    System.out.printf("     %s      %d%n", currProcess.getID(), currProcess.getCurrentEvent());
                }
            }
            System.out.println("........................................................");
            System.out.println("List of processes in I/O:");
            if (IOList.isEmpty()) {
                System.out.println("[ EMPTY ]");
            } else {
                for (int i = 0; i < IOList.size(); i++) {
                    Process currProcess = IOList.get(i);
                    System.out.printf("     %s      %d%n", currProcess.getID(), currProcess.getCurrentEvent());
                }
            }
            System.out.println("........................................................");
            System.out.println("........................................................");
        }
    }
}
