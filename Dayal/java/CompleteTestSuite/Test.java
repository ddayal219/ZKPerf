
import java.io.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import java.util.*;

public class Test implements Watcher {

    ZooKeeper zk;
    String Node = "/event";
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
        zk.exists(Node, this);

    }

    private String createandset() throws Exception {
        String znode;
        znode = zk.create(Node, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
     zk.getData(znode, this, null);
        return znode;
    }

    public void getData(String node) {
        try {
            /*   List<String> children=zk.getChildren(Node,true);
             System.out.println(children.size());
             for(String child: children)
             {
             System.out.println(child);*/
         //   zk.getData(node, this, null);
         zk.setData(node, data.getBytes(), -1);
            zk.delete(node, -1);
            //} 
            //   System.out.println("here");
        } catch (Exception e) {
        }

    }

    public void close() throws Exception {
        zk.close();
    }

    public void delete() throws Exception {
    }
    public void setWatch() throws Exception
    {
  //  zk.getChildren("/zookeeper", this);
    zk.getData("/event", this, null);
    }

    public static void main(String[] args) throws Exception {
        ConsumerImpl consumer = new ConsumerImpl(1);
        BufferedWriter out_csv = new BufferedWriter(new FileWriter("PerformanceTestResults.csv",true));
        Test t = new Test();
       
     //    out_csv.append("Test Name,Transaction Log Location,SnapCount Value,Machine,Server Version,Operation,Number Of ZK Instances in Ensemble,Number of Znodes Request Per Client,Number Of Clients,TotalNumber of Nodes Request,Execution Time(s),Rate(Znodes/s)");
       // out_csv.append("Test Name,Configuration Name,Number Of ZK Instances in Ensemble,Number of Znodes,Execution Time(s),Rate(Znodes Operation/s)");
      //  out_csv.append("\n");
        String line = "";
        int option;
        String LogLocation, LogLocConfig, SnapCount = "", Machine, ServerVersion, NOI = "1";
	String Server=args[0];
	System.out.println(args[0]);	
	long LoopDelimiter=Integer.parseInt(args[1]);
	LogLocation=args[2];
	SnapCount=args[3];
	Machine=args[4];
	ServerVersion=args[5];
	NOI=args[6];
        t.connect(Server);
        long exponentBase = 10;
        double j = 0;
       // t.setWatch();
        
        System.out.println("Number Of Nodes,Time(s),Operations/s");
        for (long i = 1; i <= LoopDelimiter; i = (long) (Math.pow(exponentBase, j / 2))) {
            
            long k = 0;
            long start = System.currentTimeMillis();
            while (k < i) {
                line = t.createandset();
                consumer.consume(new Consume(line, t));
                k++;
            }
           
            long diff = System.currentTimeMillis() - start;
            double diffInSeconds = (double) diff / 1000;
            System.out.println(i + "\t\t" + (diffInSeconds) + "\t\t" + Math.ceil((double) i / (diffInSeconds)));
           out_csv.append("ZK Performance Test-Producer/Consumer," + LogLocation + "," + SnapCount + "," + Machine + "," + ServerVersion + ",Create+Get+Delete," + NOI + "," + i + ",1," + i + "," + (diffInSeconds) + "," + Math.ceil((double) i / (diffInSeconds)));
            //out_csv.append("ZK Performance Test-Producer/Consumer,On Disk (256GiO SSD) Localmachine,3," + i + "," + (diffInSeconds) + "," + Math.ceil((double) i / (diffInSeconds)));
            out_csv.append("\n");
            j++;
        }
        consumer.finishConsumption();
        out_csv.close();
    }

    @Override
    public void process(WatchedEvent event) {
     //System.out.println(event.getType());
        if(event.getType().equals(Watcher.Event.EventType.NodeDataChanged))
          {   //System.out.println("Got new Event");
          try
          {
             // System.out.println("Event node Path:"+event.getPath());
        //zk.getData(event.getPath(),this,null);
        zk.delete(event.getPath(),-1);
          }
          catch(Exception e)
          {}
          }
           }
}
