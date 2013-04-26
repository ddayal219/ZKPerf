
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import java.io.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import java.util.Collections;
public class SyncPrimitive implements Watcher {

    static ZooKeeper zk = null;
    static Integer mutex;

    String root;

    SyncPrimitive(String address) {
        if(zk == null){
            try {
                System.out.println("Starting ZK:");
                zk = new ZooKeeper(address, 3000, this);
                mutex = new Integer(-1);
                System.out.println("Finished starting ZK: " + zk);
            } catch (IOException e) {
                System.out.println(e.toString());
                zk = null;
            }
        }
        //else mutex = new Integer(-1);
    }

    synchronized public void process(WatchedEvent event) {
        synchronized (mutex) {
            //System.out.println("Process: " + event.getType());
            mutex.notify();
        }
    }

    
   
    /**
     * Producer-Consumer queue
     */
    static public class Queue extends SyncPrimitive {

        /**
         * Constructor of producer-consumer queue
         *
         * @param address
         * @param name
         */

	static List<String> list;
        Queue(String address, String name) {
            super(address);
            this.root = name;
            // Create ZK node name
            if (zk != null) {
                try {
                    Stat s = zk.exists(root, false);
                    if (s == null) {
                        zk.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE,
                                CreateMode.PERSISTENT);
                    }
                } catch (KeeperException e) {
                    System.out
                            .println("Keeper exception when instantiating queue: "
                                    + e.toString());
                } catch (InterruptedException e) {
                    System.out.println("Interrupted exception");
                }
            }
        }

        /**
         * Add element to the queue.
         *
         * @param i
         * @return
         */

        boolean produce(int i,String data) throws KeeperException, InterruptedException{
            ByteBuffer b = ByteBuffer.allocate(4);
            byte[] value;
	
            // Add child with value i
            b.putInt(i);
            value = b.array();
            String znode=zk.create(root + "/element",data.getBytes(), Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT_SEQUENTIAL);
		//System.out.println("produced"+znode);
            return true;
        }


        /**
         * Remove first element from the queue.
         *
         * @return
         * @throws KeeperException
         * @throws InterruptedException
         */
        int consume(List<String> Children) throws KeeperException, InterruptedException{
            int retvalue = -1;
            Stat stat = null;
		String element="";
            // Get the first element available
            while (true) {
                synchronized (mutex) {
                    List<String> list = Children;
                    if (list.size() == 0) {
                        System.out.println("Going to wait");
                        mutex.wait();
                    } else {
		/*	int fulllength=list.get(0).length();
                        Integer min = new Integer(list.get(0).substring(7,fulllength));
			element=list.get(0);
			//System.out.println("min"+min);
                        for(String s : list){
                            Integer tempValue = new Integer(s.substring(7,fulllength));
                            //System.out.println("Temporary value: " + tempValue);
                            if(tempValue < min) {min = tempValue; //System.out.println(min); 
				element=s; 
				//System.out.println(s); 
				} 

                        }*/
	element=Queue.list.get(0);		
	Queue.list.remove(element);
                       // System.out.println("Temporary value: " + root + "/"+element);
                        byte[] b = zk.getData(root + "/"+element,
                                    false, stat);
                        zk.delete(root + "/"+element, -1);
                        ByteBuffer buffer = ByteBuffer.wrap(b);
                       // retvalue = buffer.getInt();
			//System.out.println("get Value"+retvalue);
                        return 1;
                    }
                }
            }
        }
    }

    public static void main(String args[]) throws Exception {
        
            queueTest(args);
       
    }

    public static void queueTest(String args[]) throws Exception {
        Queue q = new Queue(args[0], "/app1");
	String data = null;

    {
        StringBuilder dataBuilder = new StringBuilder();
        for (int m = 0; m <= 1; m++) {
            dataBuilder.append("x");
        }
        data = dataBuilder.toString();
    }
        System.out.println("Input: " + args[0]);
        long k,i=0;
 String LogLocation, LogLocConfig, SnapCount = "", Machine, ServerVersion, NOI = "1";
        Integer max = new Integer(args[1]);
	long start=0,end=0,diff=0;
	double diffInSeconds=0.0,j=0.0;
	  FileWriter fstream;
	long LoopDelimiter=max,exponentBase=10;
LogLocation=args[3];
	SnapCount=args[4];
	Machine=args[5];
	ServerVersion=args[6];
	NOI=args[7];
	 fstream = new FileWriter("PerformanceTestResults-MQ.csv");
        BufferedWriter csv = new BufferedWriter(fstream);
	csv.append("Test Name,Transaction Log Location,SnapCount Value,Machine,Server Version,Operation,Number Of ZK Instances in Ensemble,Number of Znodes Request Per Client,Number Of Clients,TotalNumber of Nodes Request,Sync Execution Time(s),Sync Rate(Znodes/s),Async Execution Time(s),Async Rate(Znodes/s)");
csv.append("\n");
	System.out.println("Message Passing queue using zookeeper");	
	System.out.println("Number of Nodes---Time in Seconds---Through(ZKnodes/s)");
	for (i = 1; i <= LoopDelimiter; i = (long) (Math.pow(exponentBase, j / 2))) 
	{
	start=System.currentTimeMillis();
	//System.out.println(start);
	if (args[2].equals("p")) {
            for (k = 0; k < i; k++){
                try{
                    
                    int h=(int)k;
                    q.produce(10 + h,data);
                 // Queue.list=zk.getChildren("/app1", true);
//			q.consume(Queue.list);
                } catch (KeeperException e){

                } catch (InterruptedException e){

                }
}
Queue.list=zk.getChildren("/app1", true);
Collections.sort(Queue.list);
for(k=0;k<i;k++)
 q.consume(Queue.list);
        } else {
           
        }
	end=System.currentTimeMillis();
	//System.out.println(end);
	diff=end-start;
	diffInSeconds = ((double) diff) / 1000;
	System.out.println("Time difference"+diff);
 csv.append("ZK Performance Test-MQ," + LogLocation + "," + SnapCount + "," + Machine + "," + ServerVersion + ",Create+GetChildren+Get+Delete," + NOI + "," + i + ",1," + i + "," + (diffInSeconds) + "," + Math.ceil((double) i / (diffInSeconds)));
          //  System.out.println(i + "\t\t    " + (diffInSeconds) + "\t\t" + Math.ceil((double) i / (diffInSeconds))+"\t\t"+Math.ceil((double) i/(diffInSeconds1)));
            csv.append("\n");
	System.out.println(i+"\t\t"+diffInSeconds+"\t\t"+Math.ceil((double) i/(diffInSeconds)));	
	j++;
	}
	csv.close();
	System.out.println("end");
    }
    }
   

    
