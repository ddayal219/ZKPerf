
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

public class ZKPerformanceTest implements Watcher {

    ZooKeeper zk;
    String Node = "/" + "event_p1";
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

    public void createandset() throws Exception {
        znode = zk.create(Node, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        // zk.setData(znode, data.getBytes(), -1);

    }

    public void setWatch() throws Exception {
        zk.getChildren("/zookeeper", this);
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
        LoopDelimiter = 1000;
        Runtime.getRuntime().exec("rm -rf /var/version-2/*.*");
        FileWriter fstream;
        int option;
        String LogLocation, LogLocConfig, SnapCount = "", Machine, ServerVersion, NOI = "1";
	String Server=args[0];
	System.out.println(args[0]);	
	LoopDelimiter=Integer.parseInt(args[1]);
	LogLocation=args[2];
	SnapCount=args[3];
	Machine=args[4];
	ServerVersion=args[5];
	NOI=args[6];
        Scanner in = new Scanner(System.in);
        fstream = new FileWriter("PerformanceTestResults.csv");
        BufferedWriter csv = new BufferedWriter(fstream);
        ZKPerformanceTest test = new ZKPerformanceTest();
        csv.append("Test Name,Transaction Log Location,SnapCount Value,Machine,Server Version,Operation,Number Of ZK Instances in Ensemble,Number of Znodes Request Per Client,Number Of Clients,TotalNumber of Nodes Request,Execution Time(s),Rate(Znodes/s)");
        csv.append("\n");
        for (i = 1; i <= LoopDelimiter; i = (long) (Math.pow(exponentBase, j / 2))) {
            test.connect(Server);
            test.setWatch();
            if (i == 1) {
               
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
            csv.append("ZK Performance Test-Write," + LogLocation + "," + SnapCount + "," + Machine + "," + ServerVersion + ",Create," + NOI + "," + i + ",1," + i + "," + (diffInSeconds) + "," + Math.ceil((double) i / (diffInSeconds)));
            System.out.println(i + "\t\t    " + (diffInSeconds) + "\t\t" + Math.ceil((double) i / (diffInSeconds)));
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
