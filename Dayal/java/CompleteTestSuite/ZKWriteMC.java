
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

public class ZKWriteMC implements Watcher, Runnable{

    ZooKeeper zk;
    String Node = "/" + "event1";
    String znode;
    public static String server;
    public String data = null;

    {
        StringBuilder dataBuilder = new StringBuilder();
        for (int i = 0; i <= 100; i++) {
            dataBuilder.append("x");
        }
        data = dataBuilder.toString();
    }

    public void connect(String Server) throws Exception {
        zk = new ZooKeeper(Server, 20000, this);
    }

   public void createandset() throws Exception {
        znode = zk.create(Node,data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
       // zk.setData(znode, data.getBytes(), -1);

    }
 public void setWatch() throws Exception
    {
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
    @Override
    public void run() 
{
 long exponentBase = 10, i, k, start = 0, end = 0, difference = 0, interim, LoopDelimiter = 0;
        double diffInSeconds = 0.0, j = 0.0;

        try
{

           
            ZKReadPerf test = new ZKReadPerf();
  
                test.connect(ZKWriteMC.server);
            
                for (k = 0; k < 10000; k++) {
                    test.createandset();
                }
                test.close();
             
                j++;
                Runtime.getRuntime().exec("rm -rf /var/zookeeper1/version-2/*.*");
           

}
catch(Exception e)
{
System.out.println(e);
}
}
    public static void main(String[] args) throws Exception {
        BufferedWriter csv_out=new BufferedWriter(new FileWriter("PerformanceTestResults-MC.csv"));
       csv_out.append("Test Name,Transaction Log Location,SnapCount Value,Machine,Server Version,Operation,Number Of ZK Instances in Ensemble,Number of Znodes Request Per Client,Number Of Clients,TotalNumber of Nodes Request,Execution Time(s),Rate(Znodes/s)");
       //csv_out.append("Number of Threads, Number of Node request / Thread, Total Number of Nodes Request(Create), Total Time taken(s), Throughput(nodes / second)");
      csv_out.append("\n");
       int option;
	if(args.length!=7)
	{
	System.out.println("java ZKWriteMC <ServerIP:Port> <Maximum number of Nodes> <Log Location(Disk,RamDisk,AWS EBS> <SnapCount(Default,0)> <Machine(LocalMachine, AWS)> <ZookeeperVersion(Zookeeper-x.y.z)> <Number of Instances in Ensemble>");
	System.exit(0);
	}
        String LogLocation, LogLocConfig, SnapCount = "", Machine, ServerVersion, NOI = "1";
        String Server=args[0];
	System.out.println(args[0]);	
	int LoopDelimiter=Integer.parseInt(args[1]);
	LogLocation=args[2];
	SnapCount=args[3];
	Machine=args[4];
	ServerVersion=args[5];
	NOI=args[6];
        ZKWriteMC.server=Server;
       System.out.println("nodes Request,Time, Throughput");
       int l=1;
       for(int k=5;k<=LoopDelimiter;k=k+5)
       {
          if(l==1)
              k=1;
        Runnable[] run=new Runnable[k];
       Thread[] test=new Thread[k];
       for(int i=0;i<k;i++)
           run[i]=new ZKWriteMC();
       for(int i=0;i<k;i++)
        test[i]=new Thread(run[i]);
       
      long start=System.currentTimeMillis();
       for(int i=0;i<k;i++)
           test[i].start();
        for (int i = 0; i < k; i++) {
    try {
       test[i].join();
    } catch (InterruptedException ignore) {}
}
      
        long nodes=10000*k;
       long diff=System.currentTimeMillis()-start;
       double ins=(double)diff/1000;
       double throughput=Math.ceil((double)nodes/ins);
       csv_out.append("ZK Performance Test-Write," + LogLocation + "," + SnapCount + "," + Machine + "," + ServerVersion + ",Create," + NOI + ",10000" + "," + k + "," + nodes + "," + ins + "," + throughput);
      // csv_out.append(k+","+"10000,"+nodes+","+ins+","+throughput);
       csv_out.append("\n");
       System.out.println(nodes+","+ins+","+throughput);
       System.out.println("Exit");
        if(l==1)
        {
            k=0;l=0;} 
       }
    csv_out.close();
    }

    public void process(WatchedEvent event) {
 //   System.out.println(event.getType());
    
    }
}


