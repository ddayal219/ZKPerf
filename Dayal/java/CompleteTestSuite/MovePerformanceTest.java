
import java.io.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import java.util.*;

public class MovePerformanceTest implements Watcher {

    ZooKeeper zk;
    String Node = "/sourcenode";
    String dest = "/destnode";
    public String data = null;

    {
        StringBuilder dataBuilder = new StringBuilder();
        for (int i = 0; i <= 280; i++) {
            dataBuilder.append("x");
        }
        data = dataBuilder.toString();
    }

    public void connect() throws Exception {
        zk = new ZooKeeper("127.0.0.1:2184", 20000, this);
        zk.exists(Node, this);

    }

    private String createandset() throws Exception {
        String srcznode;
        String destznode;
        srcznode = zk.create(Node, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);

        return srcznode;
    }

    private void createandmove(String src) throws Exception {
        String srcznode = src;
        String destznode = zk.create(dest, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        tmove(src, destznode);
    }

    public void tmove(String src, String dest) {
        try {

            zk.move(src, dest, true, null);

        } catch (Exception e) {
        }

    }

    public void close() throws Exception {
        zk.close();
    }

    public void delete() throws Exception {
    }

    public void setWatch() throws Exception {
        zk.getChildren("/zookeeper", this);
    }

    public static void main(String[] args) throws Exception {
        ConsumerImpl consumer = new ConsumerImpl(1);
        BufferedWriter out_csv = new BufferedWriter(new FileWriter("PerformanceTestResults.csv",true));
        MovePerformanceTest t = new MovePerformanceTest();

        t.connect();
       // out_csv.append("Test Name,Transaction Log Location,SnapCount Value,Machine,Server Version,Operation,Number Of ZK Instances in Ensemble,Number of Znodes Request Per Client,Number Of Clients,TotalNumber of Nodes Request,Execution Time(s),Rate(Znodes/s)");
        // out_csv.append("Test Name,Configuration Name,Number Of ZK Instances in Ensemble,Type of Node,Number of Znodes,Execution Time(s),Rate(Znodes Operation/s)");
        //out_csv.append("\n");
	String LogLocation, LogLocConfig, SnapCount = "", Machine, ServerVersion, NOI = "1";
        String line = "";
        long LoopDelimiter = 1000;
	String Server=args[0];
	LoopDelimiter=Integer.parseInt(args[1]);
	LogLocation=args[2];
	SnapCount=args[3];
	Machine=args[4];
	ServerVersion=args[5];
	NOI=args[6];
        ArrayList<String> srcNodes = new ArrayList<String>();
        long exponentBase = 10;
        double j = 0;
        t.setWatch();
        System.out.println("Number Of Nodes,Time(s),Operations/s");
        for (long i = 1; i <= LoopDelimiter; i = (long) (Math.pow(exponentBase, j / 2))) {
            t.connect();

            long k = 0;

            while (k < i) {
                line = t.createandset();

                srcNodes.add((int) k, line);
                k++;
            }
            long start = System.currentTimeMillis();
            for (int m = 0; m < srcNodes.size(); m++) {
                t.createandmove(srcNodes.get(m));
            }

            long diff = System.currentTimeMillis() - start;
            double diffInSeconds = (double) diff / 1000;

            System.out.println(i + "\t\t" + (diffInSeconds) + "\t\t" + Math.ceil((double) i / (diffInSeconds)));
            // out_csv.append("ZK Performance Test-move,On Disk (256GiO SSD) Localmachine,1,Persistent Sequential," + i + "," + (diffInSeconds) + "," + Math.ceil((double) i / (diffInSeconds)));
            out_csv.append("ZK Performance Test-ZKmove," + LogLocation + "," + SnapCount + "," + Machine + "," + ServerVersion + ",move," + NOI + "," + i + ",1," + i + "," + (diffInSeconds) + "," + Math.ceil((double) i / (diffInSeconds)));
            out_csv.append("\n");
            j++;
            t.close();
        }
        consumer.finishConsumption();
        out_csv.close();
    }

    @Override
    public void process(WatchedEvent event) {
        //  System.out.println(event.getType());
    }
}
