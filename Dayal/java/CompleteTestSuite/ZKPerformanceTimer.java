
/**
 *
 * @author dayal
 */
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.PurgeTxnLog;

public class ZKPerformanceTimer implements Watcher, Runnable {

    ZooKeeper zk;
    String Node = "/" + "event_p1";
    String znode;
    long time = 0;
    int failedNodes = 0;
    final Object lock = new Object();
    public static String data = null;

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

    public void run() {
        while (true) {
            try {
                List<String> list = zk.getChildren("/", this);
                while (list != null) {
                    while (list.size() != 0) {
                        System.out.println("deleting");
                        zk.delete("/" + list.get(0), -1);
                        list.remove(0);
                    }
                }
            } catch (KeeperException ex) {
                System.exit(0);
                Logger.getLogger(ZKRestorePerfTest.class.getName()).log(Level.SEVERE, null, ex);

            } catch (InterruptedException ex) {
                System.exit(0);
                Logger.getLogger(ZKRestorePerfTest.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    public void createandset() {
        try {
            znode = zk.create(Node, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            // zk.setData(znode, data.getBytes(), -1);
        } catch (Exception e) {

            System.out.println("exception: " + e);
            failedNodes++;
            //Note the number of failed creations.
            long start = System.currentTimeMillis();
            //  System.out.println(e);
            try {
                Thread.sleep(2000);
                System.out.println("time to restore-top:" + time);

                zk = new ZooKeeper("127.0.0.1:2186,127.0.0.1:2187,127.0.0.1:2188", 20000, this);

                System.out.println("time to restore:" + time);
                time = 0;

                //System.out.println("Connection after Failure");
            } catch (Exception ex) {
                // System.out.println("Connect Exception:"+ex);
            }
            //time=time+(System.currentTimeMillis()-start);
        }
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
        long exponentBase = 10, i = 0, k, start = 0, end = 0, start1 = 0, end1 = 0, difference = 0, interim, LoopDelimiter = 0, difference1 = 0;
        double diffInSeconds = 0.0, j = 0.0, diffInSeconds1 = 0;
        LoopDelimiter = 1000;
        Runtime.getRuntime().exec("rm -rf /var/version-2/*.*");
        FileWriter fstream;
        int option;
        String LogLocation, LogLocConfig, SnapCount = "", Machine, ServerVersion, NOI = "1";
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the following details:");
        System.out.println("Log Location:\n1.Disk\n2.AWS EBS\n3.RamDisk\n4.None");
        option = in.nextInt();

        switch (option) {
            case 1:
                LogLocation = "Disk";
                break;
            case 2:
                LogLocation = "AWS EBS";
                break;
            case 3:
                LogLocation = "RamDisk";
                break;
            case 4:
                LogLocation = "None";
                break;
            default:
                LogLocation = "None";
                break;
        }
        System.out.println("SnapCount:\n1.Default\n2.0");
        option = in.nextInt();
        switch (option) {
            case 1:
                SnapCount = "10000";
                break;
            case 2:
                SnapCount = "0";
                break;
            default:
                SnapCount = "10000";
                break;
        }
        System.out.println("Machine:\n1.LocalMachine\n2.AWS");
        option = in.nextInt();
        switch (option) {
            case 1:
                Machine = "Local Machine";
                break;
            case 2:
                Machine = "AWS";
                break;
            default:
                Machine = "Local Machine";
                break;
        }
        System.out.println("Zookeeper Version: (format: Zookeeper-x.y.z)");
        ServerVersion = in.next();
        System.out.println("Number of Instances in Zookeeper Ensemble");
        NOI = in.next();
        System.out.println("Enter the server address, format:<IP:Port>");
        String Server = in.next();
        System.out.println("Enter the Maximum Number of Nodes");
        LoopDelimiter = in.nextInt();
        fstream = new FileWriter("PerformanceTestResults.csv");
        BufferedWriter csv = new BufferedWriter(fstream);
        ZKPerformanceTimer test = new ZKPerformanceTimer();

        csv.append("Test Name,Transaction Log Location,SnapCount Value,Machine,Server Version,Operation,Number Of ZK Instances in Ensemble,Execution Time(s),Number of Successful Requests,Sync Rate(Znodes/s),Async Rate(Znodes/s");
        csv.append("\n");
        //for (i = 1; i <= LoopDelimiter; i = (long) (Math.pow(exponentBase, j / 2))) {

        test.connect(Server);
        test.createandset();
        // Thread delThread=new Thread(test);
        // delThread.start();
        for (k = 0; k < LoopDelimiter; k++) {
            i = 0;
            start = System.currentTimeMillis();
            while (((System.currentTimeMillis() - start) < (k * 1000))) {
                i++;
                test.createandset();
            }
            end = System.currentTimeMillis();
            difference = k * 1000;
            diffInSeconds = ((double) difference) / 1000;

            System.out.println("i:" + i);
            System.out.println("Actual difference in Time: " + (end - start));
            System.out.println(k + "\t\t" + (diffInSeconds) + "\t\t" + Math.ceil((double) i / (diffInSeconds)));
            csv.append("ZK Performance Test-Write," + LogLocation + "," + SnapCount + "," + Machine + "," + ServerVersion + ",Create," + NOI + "," + (diffInSeconds) + "," + i + "," + Math.ceil((double) i / (diffInSeconds)) + "," + Math.ceil((double) i / (diffInSeconds)));
            //  test.close();
            csv.append("\n");
            System.out.println("time to restore:" + test.time);
        }
        Runtime.getRuntime().exec("rm -rf /var/zookeeper1/version-2/*.*");

        csv.close();
        fstream.close();
        System.out.println(test.failedNodes);
        System.out.println("Performance Test Successful");
        //  }
    }

    public void process(WatchedEvent event) {
        if (event.equals("SyncConnected")) {
            System.out.println("Connected");
        }
    }
}
