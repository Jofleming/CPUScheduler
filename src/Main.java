package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
//        SJFNonPreemptiveScheduler sc = new SJFNonPreemptiveScheduler();
//
//        Scheduler sc = new Scheduler(new ReadyQueueRR());

//        readProcessedData(sc);
//        sc.SimulateScheduler();
//        sc.generateReport();
        MLFQSchedule();
        SRTFSchedule();
    }

    public static void MLFQSchedule() throws IOException {
        File processedFile = new File("Data/ProcessData");
        ArrayList<Process> processList = new ArrayList<Process>();
        try (Scanner pF = new Scanner(processedFile)) {
            pF.nextLine();
            while (pF.hasNextLine()) {
                String line = pF.nextLine().trim();
                if (!line.isEmpty()) {
                    System.out.println("Reading process data: " + line);

                    String processID = line.substring(0, 2);
                    String eventsString = line.substring(line.indexOf('{') + 1, line.indexOf('}')).trim();
                    String[] eventsArray = eventsString.split(",");

                    ArrayList<Integer> events = new ArrayList<>();
                    for (String event : eventsArray) {
                        events.add(Integer.parseInt(event.trim()));
                    }

                    Process process = new Process(processID);
                    process.setEvents(events);
                    processList.add(process);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Process[] processArray = new Process[processList.size()];
        processArray = processList.toArray(processArray);
        MLFQScheduler runMLFQ = new MLFQScheduler(processArray);
        double[] reportArray = runMLFQ.scheduleCPU();
    }


    public static void SRTFSchedule() throws IOException {
        File processedFile = new File("Data/ProcessData");
        ArrayList<Process> processList = new ArrayList<Process>();
        try (Scanner pF = new Scanner(processedFile)) {
            pF.nextLine();
            while (pF.hasNextLine()) {
                String line = pF.nextLine().trim();
                if (!line.isEmpty()) {
                    System.out.println("Reading process data: " + line);

                    String processID = line.substring(0, 2);
                    String eventsString = line.substring(line.indexOf('{') + 1, line.indexOf('}')).trim();
                    String[] eventsArray = eventsString.split(",");

                    ArrayList<Integer> events = new ArrayList<>();
                    for (String event : eventsArray) {
                        events.add(Integer.parseInt(event.trim()));
                    }

                    Process process = new Process(processID);
                    process.setEvents(events);
                    processList.add(process);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Process[] processArray = new Process[processList.size()];
        processArray = processList.toArray(processArray);
        SRTFScheduler runSRTF = new SRTFScheduler(processArray);
        double[] reportArray = runSRTF.scheduleCPU();
    }


//    public static void readProcessedDataCarter(Scheduler sc) {
//        File processedFile = new File("/Users/cartergarcia/Library/CloudStorage/OneDrive-Personal/Documents/School/WWU/2023-2024/CS 447 Operating Systems/CPUScheduler/Data/ProcessData");
//
//        try (Scanner pF = new Scanner(processedFile)) {
//            while (pF.hasNextLine()) {
//                String line = pF.nextLine().trim();
//                if (!line.isEmpty()) {
//                    System.out.println("Reading process data: " + line);
//
//                    String processID = line.substring(0, line.indexOf(' ')).trim();
//                    String eventsString = line.substring(line.indexOf('{') + 1, line.indexOf('}')).trim();
//                    String[] eventsArray = eventsString.split(",");
//
//                    ArrayList<Integer> events = new ArrayList<>();
//                    for (String event : eventsArray) {
//                        events.add(Integer.parseInt(event.trim()));
//                    }
//
//                    Process p = new Process(processID);
//                    p.setEvents(events);
//                    sc.addProcess(p);
//                }
//            }
//
//    public static void readProcessedDataEdale (Scheduler sc) {
//
//        File processedFile = new File("/Users/emiguel/opSys/CPU_Scheduler/Data/ProcessData");
//
//        try {
//            Scanner pF = new Scanner(processedFile);
//            String sourceFile = "";
//            int countProcesses = pF.nextInt();
//            pF.nextLine();
//
//            while (pF.hasNextLine() && countProcesses > 0) {
//                sourceFile = pF.nextLine();
//                //P1 {5,27,3,31,5,43,4,18,6,22,4,26,3,24,4}
//                String[] strArray = sourceFile.split("[{]");
//                strArray[1] = strArray[1].substring(0,strArray[1].length()-1);
//
//                //System.out.println(strArray[0] + ".." + strArray[1]);
//                String[] d = strArray[1].split(",");
//                ArrayList<Integer> duration = new ArrayList<>();
//                for(int i = 0; i<d.length; i++){
//                    duration.add(Integer.parseInt(d[i]));
//                }
//
//                MyProcess p = new MyProcess(strArray[0], duration);
//                sc.addProcess(p);
//                //System.out.println(duration);
//                countProcesses--;
//            }
//            //System.out.println(sourceFile);
//
//        } catch (FileNotFoundException e) {
//            System.out.println("File not found: " + e.getMessage());
//        } catch (NumberFormatException e) {
//            System.out.println("Error parsing number: " + e.getMessage());
//        } catch (Exception e) {
//            System.out.println("An unexpected error occurred: " + e.getMessage());
//        }
//    }
}
