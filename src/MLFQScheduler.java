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
        avgRT = avgTRT = avgWT = CPUUtilization = 0.0;
    }

    public void scheduleCPU() {
        while (!queueOne.isEmpty() || !queueTwo.isEmpty() || !queueThree.isEmpty() || !IOList.isEmpty() || runningProcess != null) {
            if (runningProcess == null && (!queueOne.isEmpty() || !queueTwo.isEmpty() || !queueThree.isEmpty())) {
                moveProcessOntoCPU();
            }
            if (runningProcess != null && runningProcess.getCurrentEvent() == 0) {
                if (runningProcess.eventAtEnd()) {
                    // TODO: Update Process stats.
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
            }
            timeQuantum++;
            CPUQuantum--;
            // printCPUStatus();
        }
        computeAvgs();
        // TODO: Report averages
    }

    public void moveProcessOntoCPU () {
        if (!queueOne.isEmpty()) {
            runningProcess = queueOne.getFirst();
            CPUQuantum = 5;
        } else if (!queueTwo.isEmpty()) {
            runningProcess = queueTwo.getFirst();
            CPUQuantum = 10;
        } else if (!queueThree.isEmpty()) {
            runningProcess = queueThree.getFirst();
            CPUQuantum = 10000000;
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
        // TODO: Fill out preempt method
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

    public void computeAvgs() {
        // TODO: Add CPU Utilization Calculation
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
    }

}
