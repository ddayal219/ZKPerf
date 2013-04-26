
import java.io.*;

public class FileWritePerformance {

    public static void main(String args[]) {
        if (args.length == 0) {
            System.out.println("java FileWritePerformance <Upper Bound on Loop Delimiter>");
        }
	String currentDir=System.getProperty("user.dir");
        double NumberOfBytes;
        double LoopDelimiter, expCount = 10;
        double k = 0;
        int n = Integer.parseInt(args[0]);
        StringBuilder dataBuilder = new StringBuilder();
        String data;
        try {
            FileWriter fstreamcsv = new FileWriter(currentDir+"/fileWrite.csv");
            BufferedWriter csv = new BufferedWriter(fstreamcsv);
            csv.append("Number Of Bytes, Time(ns)");
            csv.append("\n");
            FileWriter fstream = new FileWriter(currentDir+"/out.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            long start, end, sum = 0;
            do {
                NumberOfBytes = (double) Math.pow(expCount, (double) k / 2);
                LoopDelimiter = (double) Math.pow(expCount, (double) k / 2);
                for (int i = 0; i < (int) LoopDelimiter; i++) {
                    for (int j = 0; j <= (int) NumberOfBytes * i; j++) {
                        dataBuilder.append("x");
                    }
                    data = dataBuilder.toString();
                    start = System.currentTimeMillis() * 1000000;
                    out.append(data);
                    end = System.currentTimeMillis() * 1000000;
                    sum = sum + (end - start);
                }
                csv.append(Math.round(NumberOfBytes * LoopDelimiter) + "," + sum);
                System.out.println(Math.round(NumberOfBytes * LoopDelimiter) + "," + sum);
                csv.append("\n");
                k++;
            } while (Math.round(LoopDelimiter * NumberOfBytes) < n);
            csv.close();
            fstreamcsv.close();
            out.close();
            fstream.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

