package src;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scheduler rrSched = new Scheduler(new ReadyQueueRR(), 5);
        readProcessedData(rrSched);
        rrSched.SimulateScheduler(false);
        rrSched.generateReport();

        System.out.println("***Priority Scheduler***");
        Scheduler pSched = new Scheduler(new ReadyQueuePriority(), Integer.MAX_VALUE);
        readProcessedData(pSched);
        pSched.SimulateScheduler(false);
        pSched.generateReport();
    }
    public static void readProcessedData (Scheduler sc) {
        File processedFile = new File("/Users/emiguel/opSys/CPU_Scheduler/Data/EMProcessData");
        try {
            Scanner pF = new Scanner(processedFile);
            String sourceFile = "";
            int countProcesses = pF.nextInt();
            pF.nextLine();
            String prioritystr = pF.nextLine();

            String[] priorities = prioritystr.split(",");
            int counter = 0;
            while (pF.hasNextLine() && countProcesses > 0) {
                sourceFile = pF.nextLine();
                String[] strArray = sourceFile.split("[{]");
                strArray[1] = strArray[1].substring(0,strArray[1].length()-1);
//System.out.println(strArray[0] + ".." + strArray[1]);
                String[] d = strArray[1].split(",");
                ArrayList<Integer> duration = new ArrayList<>();
                for(int i = 0; i<d.length; i++){
                    duration.add(Integer.parseInt(d[i]));
                }
                MyProcess p = new MyProcess(strArray[0], duration);
                p.setPriority(Integer.parseInt(priorities[counter ++]));
                p.arrivalTime = 0;
                p.ReadyQAddTime = 0;
                sc.addProcess(p);

                countProcesses--;
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }
}