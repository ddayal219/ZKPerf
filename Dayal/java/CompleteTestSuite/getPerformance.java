
/**
 *
 * @author dayal
 */
import java.io.*;
import java.util.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

public class getPerformance implements Watcher {

    ZooKeeper zk;
    String Node = "/" + "event1";
    String znode;
    public String data = null;

    {
        StringBuilder dataBuilder = new StringBuilder();
        for (int i = 0; i <= 280; i++) {
            dataBuilder.append("x");
        }
        data = dataBuilder.toString();
    }

    public void connect(String Server) throws Exception {
        zk = new ZooKeeper(Server, 20000, this);
    }

    private String createandset() throws Exception {
        znode = zk.create(Node, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        return znode;
    }

    public void close() throws Exception {
        zk.close();
    }

    public void delete_contents(File file) throws IOException {
        if (file.isDirectory()) {
            String files[] = file.list();
            for (String temp : files) {
                File fileDelete = new File(file, temp);
                delete_contents(fileDelete);
            }
        } else {
            file.delete();
        }
    }

    public void delete(String path) throws Exception {
        zk.delete(path, -1);
    }

    public void get(String path) throws Exception {
        zk.getData(path, this, null);
    }

    public static void main(String[] args) throws Exception {
        long exponentBase = 10, i, k, start = 0, end = 0, dstart = 0, dend = 0, difference = 0, ddifference = 0, interim, LoopDelimiter = 0;
        double diffInSeconds = 0.0, ddiffInSeconds, j = 0.0;

//        if (args.length == 0) {
        //          System.out.println("java FileWritePerformance <Maximum Bound on the Zookeeper Nodes>");
        //    } else {
        LoopDelimiter = 10000;
        File LogDir = new File("/tmp/ramdisk/zookeeper1/version-2/");
        Runtime.getRuntime().exec("rm -rf /var/version-2/*.*");
        FileWriter fstream;
        ArrayList<String> nodes = new ArrayList<String>();
        fstream = new FileWriter("PerformanceTestResults.csv", true);
        Scanner in = new Scanner(System.in);
        int option;
        BufferedWriter csv = new BufferedWriter(fstream);
        getPerformance test = new getPerformance();
        //  csv.append("Test Name,Transaction Log Location,SnapCount Value,Machine,Server Version,Operation,Number Of ZK Instances in Ensemble,Number of Znodes Request Per Client,Number Of Clients,TotalNumber of Nodes Request,Execution Time(s),Rate(Znodes/s)");
        String LogLocation, LogLocConfig, SnapCount = "", Machine, ServerVersion, NOI = "1";
	String Server=args[0];
	System.out.println(args[0]);	
	LoopDelimiter=Integer.parseInt(args[1]);
	LogLocation=args[2];
	SnapCount=args[3];
	Machine=args[4];
	ServerVersion=args[5];
	NOI=args[6];
        //csv.append("\n");
        for (i = 1; i <= LoopDelimiter; i = (long) (Math.pow(exponentBase, j / 2))) {
            test.connect(Server);
            if (i == 1) {
                
                System.out.println("Number of Znodes-Insert Time(s)-Insert Throughput(Number of nodes per s)-Get Time(s)-Get Throughput(Number of nodes per s),Overall Time, Overall Throughput,Time Percent for Writes, Time Percent for Reads");
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            }

            start = System.currentTimeMillis();
            for (k = 0; k < i; k++) {
                nodes.add((int) k, test.createandset());
            }
            end = System.currentTimeMillis();
            dstart = System.currentTimeMillis();
            for (k = 0; k < i; k++) {
                test.get(nodes.get((int) k));
            }
            dend = System.currentTimeMillis();
            test.close();
            ddifference = (dend - dstart);
            ddiffInSeconds = ((double) ddifference) / 1000;
            difference = (end - start);
            diffInSeconds = ((double) difference) / 1000;
            long overallTime = ddifference + difference;
            double overallThroughput = Math.ceil((double) i / (diffInSeconds)) + Math.ceil((double) i / (ddiffInSeconds));
            double writepercent = (100 * difference) / overallTime;
            double readpercent = 100 - writepercent;
            //   csv.append("ZK Performance Test-Replicated,In Memory (16GiO ramdisk) Localmachine,3," + i + "," + (diffInSeconds) + "," + Math.ceil((double) i / (diffInSeconds))+"," + (ddiffInSeconds) + "," + Math.ceil((double) i / (ddiffInSeconds)));
            csv.append("ZK Performance Test-Read," + LogLocation + "," + SnapCount + "," + Machine + "," + ServerVersion + ",Get," + NOI + "," + i + ",1," + i + "," + (ddiffInSeconds) + "," + Math.ceil((double) i / (ddiffInSeconds)));
            System.out.println(i + "\t\t    " + (diffInSeconds) + "\t\t\t" + Math.ceil((double) i / (diffInSeconds)) + "\t\t\t    " + (ddiffInSeconds) + "\t\t\t" + Math.ceil((double) i / (ddiffInSeconds)) + "\t\t\t" + overallTime + "\t\t\t" + overallThroughput + "\t\t\t" + writepercent + "\t\t\t" + readpercent);
            csv.append("\n");
            j++;
            Runtime.getRuntime().exec("rm -rf /var/zookeeper1/version-2/*.*");
        }
        csv.close();
        fstream.close();
        System.out.println("Performance Test Successful");
        //  }
    }

    public void process(WatchedEvent event) {
    }
}
