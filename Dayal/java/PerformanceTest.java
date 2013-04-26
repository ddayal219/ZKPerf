
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

public class PerformanceTest implements Watcher {

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

    public void connect() throws Exception {
        zk = new ZooKeeper("127.0.0.1:2181", 20000, this);
    }

    private void createandset() throws Exception {
        znode = zk.create(Node, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

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

    public static void main(String[] args) throws Exception {
        long exponentBase = 10, i, k, start = 0, end = 0, difference = 0, interim, LoopDelimiter = 0;
        double diffInSeconds = 0.0, j = 0.0;
        if (args.length == 0) {
            System.out.println("java FileWritePerformance <Maximum Bound on the Zookeeper Nodes> <Number Of Tests> <Zookeeper Logs Directory>");
        } else {
	    String fileName="rm -rf "+args[2];
            LoopDelimiter = Integer.parseInt(args[0]);
            Runtime.getRuntime().exec(fileName);
            FileWriter fstream;
	    String currentdir = System.getProperty("user.dir");
        if(args[1].equals("1"))
            fstream = new FileWriter(currentdir+"/PerformanceTestResults.csv");
	else
	    fstream = new FileWriter(currentdir+"/PerformanceTestResults.csv",true);
            BufferedWriter csv = new BufferedWriter(fstream);
            PerformanceTest test = new PerformanceTest();
            csv.append("Test Name,Configuration Name,Server Version,Client API version,Number Of ZK Instances in Ensemble,Number of Znodes,Execution Time(s),Rate(Znodes/s)");
            csv.append("\n");
            for (i = 1; i <= LoopDelimiter; i = (long) (Math.pow(exponentBase, j / 2))) {
                test.connect();
                if (i == 1) {
                    System.out.println("Performance test on Creation and setting of Zknodes with log Directory configured on the Ram Disk.");
                    System.out.println("Number of Znodes---Time(s)---Throughput(Number of nodes per s)");
                    System.out.println("-------------------------------------------------------------------");
                }

                start = System.currentTimeMillis();
                for (k = 0; k < i; k++) {
                    test.createandset();
                }
                test.close();
                end = System.currentTimeMillis();
                difference = (end - start);
                diffInSeconds = ((double) difference) / 1000;
                csv.append("ZK Performance Test-Standalone,In memory (16 GiO Ramdisk) Localmachine,Zookeeper 3.4.4,Zookeeper JAVA API 3.4.5,1," + i + "," + (diffInSeconds) + "," + Math.ceil((double) i / (diffInSeconds)));
                System.out.println(i + "\t\t    " + (diffInSeconds) + "\t\t" + Math.ceil((double) i / (diffInSeconds)));
                csv.append("\n");

                j++;
                Runtime.getRuntime().exec("rm -rf /var/zookeeper1/version-2/*.*");
            }
            csv.close();
            fstream.close();
            System.out.println("Performance Test Successful");
        }
    }

    public void process(WatchedEvent event) {
    }
}
