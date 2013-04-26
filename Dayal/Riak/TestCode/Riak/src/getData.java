
import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.raw.http.HTTPClientConfig;
import com.basho.riak.client.raw.pbc.PBClientConfig;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class getData {

    public static void main(String[] args) throws RiakException, IOException {
        long exponentBase = 10, i, k, start = 0, end = 0, start1 = 0, end1 = 0, difference = 0, interim, LoopDelimiter = 0, difference1 = 0;
        double diffInSeconds = 0.0, j = 0.0, diffInSeconds1 = 0;
        LoopDelimiter = 100000;
        HTTPClientConfig httpConfig = new HTTPClientConfig.Builder().withHost("127.0.0.1").withPort(8098).build();
        PBClientConfig pbConfig = new PBClientConfig.Builder().withHost("127.0.0.1").withPort(8087).build();
        String myData = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        IRiakClient riakClient = RiakFactory.newClient(httpConfig);//RiakFactory.httpClient();
        IRiakClient pbClient = RiakFactory.newClient(pbConfig);
        FileWriter fstream;
        fstream = new FileWriter("PerformanceTestResults2.csv");
        BufferedWriter csv = new BufferedWriter(fstream);
        Bucket myBucket = riakClient.createBucket("TestBucket").execute();
        Bucket pbBucket = pbClient.createBucket("PbBucket").execute();
        csv.append("Test Name,Backend,Number of Nodes in Cluster,Number of KV,HTTP Client Time(s),HTTP Client throughput, PB Client Time(s), PB Client throughput(s)");
        csv.append("\n");
        for (i = 1; i <= LoopDelimiter; i = (long) (Math.pow(exponentBase, j / 2))) {

            if (i == 1) {
                System.out.println("Performance test on Creation and setting of Zknodes with log Directory configured on the Hard Disk.");
                System.out.println("Number of Keys---Time(s)---HTTP Interface Throughput(Number of nodes per s)---PB Throughput(Number of nodes per s");
                System.out.println("-------------------------------------------------------------------");
            }
            start = System.currentTimeMillis();
            for (k = 0; k < i; k++) {
                //      System.out.println("creating keys");
                myBucket.fetch("Test Key").execute();
              //  myBucket.store("TestKey" + j + i, myData).execute();
            }

            end = System.currentTimeMillis();
            diffInSeconds = (double) (end - start) / 1000;
            //double throughput=100/(double)diffInSeconds;
            start1 = System.currentTimeMillis();
            for (k = 0; k < i; k++) {
                pbBucket.fetch("Test Key1").execute();
                //pbBucket.store("TestKey1" + j + i, myData).execute();
            }
            diffInSeconds1 = (double) (System.currentTimeMillis() - start1) / 1000;
            csv.append("Riak Performance Test-Read,memory,3," + i + "," + diffInSeconds + "," + Math.ceil((double) i / (diffInSeconds)) + "," + diffInSeconds1 + "," + Math.ceil((double) i / (diffInSeconds1)));
            System.out.println(i + "\t\t    " + (diffInSeconds) + "\t\t" + Math.ceil((double) i / (diffInSeconds)) + "\t\t" + diffInSeconds1 + "\t\t" + Math.ceil((double) i / (diffInSeconds1)));
            csv.append("\n");
            j++;
        }
        csv.close();
        pbClient.shutdown();
        riakClient.shutdown();
    }
}
