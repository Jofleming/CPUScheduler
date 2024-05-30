package src;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

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
        IOList = new ArrayList<>();
        runningProcess = null;
        queueOne = new LinkedList<>();
        queueTwo = new LinkedList<>();
        queueThree = new LinkedList<>();
        for (Process process : processes) {
            queueOne.add(process);
        }
        timeQuantum = 0;
        quantumCounter = 0;
        avgRT = avgTRT = avgWT = CPUUtilization = totalTimeActive = 0.0;
    }

    public double[] scheduleCPU() throws IOException {
        while (!queueOne.isEmpty() || !queueTwo.isEmpty() || !queueThree.isEmpty() || !IOList.isEmpty() || runningProcess != null) {
            if (runningProcess == null && (!queueOne.isEmpty() || !queueTwo.isEmpty() || !queueThree.isEmpty())) {
                moveProcessOntoCPU();
            }
            if (runningProcess != null && runningProcess.getCurrentEvent() <= 0) {
                if (runningProcess.eventAtEnd()) {
                    runningProcess.updateProcessStats(timeQuantum);
                    runningProcess = null;
                    moveProcessOntoCPU();
                } else {
                    processToIO();
                    moveProcessOntoCPU();
                }
            }
            if (CPUQuantum <= 0 && runningProcess != null) {
                preempt();
            }
            decrementIO();
            if (runningProcess != null) {
                runningProcess.updateCurrentEvent(-1);
                totalTimeActive++;
                CPUQuantum--;
            }
            timeQuantum++;
            printCPUStatus();
        }
        computeAvgs();
        System.out.printf("Average Response Time: %f%n", avgRT);
        System.out.printf("Average Waiting Time: %f%n", avgWT);
        System.out.printf("Average Turnaround Time: %f%n", avgTRT);
        System.out.printf("CPU Percent Utilization: %f%%%n", CPUUtilization);
        writeDataToFile(createFileIfNotExists("MLFQ_data.txt"));
        return new double[]{avgRT, avgWT, avgTRT, CPUUtilization};
    }

    public void moveProcessOntoCPU() {
        if (!queueOne.isEmpty()) {
            runningProcess = queueOne.poll();
            CPUQuantum = 5;
        } else if (!queueTwo.isEmpty()) {
            runningProcess = queueTwo.poll();
            CPUQuantum = 10;
        } else if (!queueThree.isEmpty()) {
            runningProcess = queueThree.poll();
            CPUQuantum = 20;
        }
        if (runningProcess != null) {
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
        Process processToMove = IOList.remove(index);
        if (processToMove.getPriority() == 1) {
            queueOne.add(processToMove);
        } else if (processToMove.getPriority() == 2) {
            queueTwo.add(processToMove);
        } else {
            queueThree.add(processToMove);
        }
    }

    public void decrementIO() {
        for (int i = 0; i < IOList.size(); ) {
            Process currProcess = IOList.get(i);
            currProcess.updateCurrentEvent(currProcess.getCurrentEvent() - 1);
            if (checkZeroTime(currProcess)) {
                IO_to_Queue(i);
            } else {
                i++;
            }
        }
    }

    public boolean checkZeroTime(Process currProcess) {
        return currProcess.getCurrentEvent() <= 0;
    }

    public void preempt() {
        runningProcess.setPriority(runningProcess.getPriority() + 1);
        if (runningProcess.getPriority() == 2) {
            queueTwo.add(runningProcess);
        } else if (runningProcess.getPriority() >= 3) {
            queueThree.add(runningProcess);
        }
        runningProcess = null;
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
        for (Process process : processList) {
            sumTRT += process.getTurnAroundTime();
            sumRT += process.getResponseTime();
            sumWT += process.getWaitTime();
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
                System.out.printf("Running Process: %s, Current Event: %d%n", runningProcess.getID(), runningProcess.getCurrentEvent());
            } else {
                System.out.println("CPU currently awaiting process");
            }
            System.out.println("........................................................");
            System.out.println("List of processes in queue 1:");
            if (queueOne.isEmpty()) {
                System.out.println("[ EMPTY ]");
            } else {
                LinkedList<Process> printQueue = new LinkedList<>(queueOne);
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
                LinkedList<Process> printQueue = new LinkedList<>(queueTwo);
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
                LinkedList<Process> printQueue = new LinkedList<>(queueThree);
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
                for (Process currProcess : IOList) {
                    System.out.printf("     %s      %d%n", currProcess.getID(), currProcess.getCurrentEvent());
                }
            }
            System.out.println("........................................................");
            System.out.println("........................................................");
        }
    }

    public void writeDataToFile(File file) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write("MLFQ ALGORITHM STATS\n");
            for (Process process : processList) {
                bw.write(process.reportString());
                bw.newLine();
            }
            bw.write("Average Response Time: " + avgRT + "\n");
            bw.write("Average Wait Time: " + avgWT + "\n");
            bw.write("Average Turnaround Time: " + avgTRT + "\n");
            bw.write("CPU Utilization: " + CPUUtilization + "%\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createFileIfNotExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Failed to create file: " + filePath);
            }
        }
        return file;
    }
}
