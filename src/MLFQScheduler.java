package src;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Scheduler class to emulate the multi level feedback queue scheduling algorithm.
 * @author Jordan Fleming
 * @version 1.0
 * @since 5/22/2024
 */
public class MLFQScheduler {
    Process[] processList;
    ArrayList<Process> IOList;
    Process runningProcess;
    LinkedList<Process> queueOne;
    LinkedList<Process> queueTwo;
    LinkedList<Process> queueThree;
    int CPUQuantum;
    int timeQuantum;
    int quantumCounter;
    boolean printOutput = true;
    double avgWT;
    double avgRT;
    double avgTRT;
    double CPUUtilization;
    double totalTimeActive;

    MLFQScheduler(Process[] processes) {
        processList = processes;
        IOList = new ArrayList<Process>();
        runningProcess = null;
        queueOne = new LinkedList<Process>();
        queueTwo = new LinkedList<Process>();
        queueThree = new LinkedList<Process>();
        for (int i = 0; i < processes.length; i++) {
            queueOne.add(processes[i]);
        }
        timeQuantum = 0;
        quantumCounter = 0;
        avgRT = avgTRT = avgWT = CPUUtilization = totalTimeActive = 0.0;
    }

    public double[] scheduleCPU() {
        while (!queueOne.isEmpty() || !queueTwo.isEmpty() || !queueThree.isEmpty() || !IOList.isEmpty() || runningProcess != null) {
            if (runningProcess == null && (!queueOne.isEmpty() || !queueTwo.isEmpty() || !queueThree.isEmpty())) {
                moveProcessOntoCPU();
            }
            if (runningProcess != null && runningProcess.getCurrentEvent() == 0) {
                if (runningProcess.eventAtEnd()) {
                    runningProcess.updateProcessStats(timeQuantum);
                    runningProcess = null;
                    moveProcessOntoCPU();
                } else {
                    processToIO();
                    moveProcessOntoCPU();
                }
            }
            if (CPUQuantum == 0) {
                preempt();
            }
            decrementIO();
            if (runningProcess != null) {
                runningProcess.updateCurrentEvent(-1);
                totalTimeActive++;
            }
            timeQuantum++;
            CPUQuantum--;
             printCPUStatus();
        }
        computeAvgs();
        // TODO: Report averages
        System.out.printf("Average Response Time: %d%n", avgRT);
        System.out.printf("Average Waiting Time: %d%n", avgWT);
        System.out.printf("Average Turnaround Time: %d%n", avgTRT);
        System.out.printf("CPU Percent Utilization: %d%%%n", avgRT);
        return new double[]{avgRT, avgWT, avgTRT, CPUUtilization};
    }

    public void moveProcessOntoCPU () {
        if (!queueOne.isEmpty()) {
            runningProcess = queueOne.getFirst();
            CPUQuantum = 5;
            checkFirstLoad(runningProcess);
        } else if (!queueTwo.isEmpty()) {
            runningProcess = queueTwo.getFirst();
            CPUQuantum = 10;
            checkFirstLoad(runningProcess);
        } else if (!queueThree.isEmpty()) {
            runningProcess = queueThree.getFirst();
            CPUQuantum = 10000000;
            checkFirstLoad(runningProcess);
        }
    }

    public void processToIO() {
        if (runningProcess != null) {
            runningProcess.setEventsIndex(runningProcess.getEventsIndex() + 1);
            IOList.add(runningProcess);
            runningProcess = null;
        }
    }

    public void IO_to_Queue(int index) {
        Process processToMove = IOList.get(index);
        processToMove.setEventsIndex(processToMove.getEventsIndex() + 1);
        if (processToMove.getPriority() == 1) {
            queueOne.add(IOList.remove(index));
        } else if (processToMove.getPriority() == 2) {
            queueTwo.add(IOList.remove(index));
        } else {
            queueThree.add(IOList.remove(index));
        }
    }

    public void decrementIO() {
        for (int i = 0; i < IOList.size(); i++) {
            Process currProcess = IOList.get(i);
            currProcess.updateCurrentEvent(currProcess.getCurrentEvent() - 1);
            if (checkZeroTime(currProcess)) {
                IO_to_Queue(i);
            }
        }
    }

    public boolean checkZeroTime(Process currProcess) {
        if (currProcess.getCurrentEvent() == 0) {
            return true;
        }
        return false;
    }

    public void preempt() {
        runningProcess.setPriority(runningProcess.getPriority() + 1);
        if (runningProcess.getPriority() == 2) {
            queueTwo.add(runningProcess);
            runningProcess = null;
        } else if (runningProcess.getPriority() >= 3) {
            queueThree.add(runningProcess);
            runningProcess = null;
        }
        moveProcessOntoCPU();
    }

    public void checkFirstLoad(Process process) {
        if (process.getResponseTime() == -1) {
            process.setResponseTime(timeQuantum);
        }
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

    public void printCPUStatus() {
        if (printOutput) {
            System.out.printf("Current Time: %d%n", timeQuantum);
            if (runningProcess != null) {
                System.out.printf("Next process on the CPU: %s%n", runningProcess.getID());
            } else {
                System.out.println("CPU currently awaiting process");
            }
            System.out.println("........................................................");
            System.out.println("List of processes in queue 1:");
            if (queueOne.isEmpty()) {
                System.out.println("[ EMPTY ]");
            } else {
                LinkedList<Process> printQueue = (LinkedList<Process>)queueOne.clone();
                System.out.println("    Process    Burst");
                while (!printQueue.isEmpty()) {
                    Process currProcess = printQueue.poll();
                    System.out.printf("     %s      %d%n", currProcess.getID(), currProcess.getCurrentEvent());
                }
            }
            System.out.println("........................................................");
            System.out.println("List of processes in queue 2:");
            if (queueTwo.isEmpty()) {
                System.out.println("[ EMPTY ]");
            } else {
                LinkedList<Process> printQueue = (LinkedList<Process>)queueTwo.clone();
                System.out.println("    Process    Burst");
                while (!printQueue.isEmpty()) {
                    Process currProcess = printQueue.poll();
                    System.out.printf("     %s      %d%n", currProcess.getID(), currProcess.getCurrentEvent());
                }
            }
            System.out.println("........................................................");
            System.out.println("List of processes in queue 3:");
            if (queueThree.isEmpty()) {
                System.out.println("[ EMPTY ]");
            } else {
                LinkedList<Process> printQueue = (LinkedList<Process>)queueThree.clone();
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
