
import java.io.*;
import java.util.zip.CheckedOutputStream;
import java.util.zip.CRC32;
import java.util.zip.Adler32;
import org.apache.jute.*;

public class FileWriteComparison {

    public static void main(String args[]) throws Exception {
	if(args.length==0)
		System.out.println("java FileWriteComparison <Maximum Data Size>");
        int LoopDelimiter = Integer.parseInt(args[0]);
        double j = 1;
        StringBuilder dataBuilder = new StringBuilder();
        String data = "";
        byte[] a = new byte[1000000000];
        int exponentBase = 10;
        String currentDir = System.getProperty("user.dir");
        FileWriter fstream_csv = new FileWriter(currentDir + "/FileWriteComparison.csv");
        BufferedWriter csv_out = new BufferedWriter(fstream_csv);
        csv_out.append("Size(Bytes)" + ",Normal File Write Time(ms),Zookeeper Checked Output Stream Write Time(ms)");
        csv_out.append("\n");
        try {
            for (long i = 1; i <= LoopDelimiter; i = (long) (Math.pow(exponentBase, j / 2))) {
                OutputStream sessOS = new BufferedOutputStream(new FileOutputStream(currentDir + "/ZKWrite.data"));
                CheckedOutputStream crcOut = new CheckedOutputStream(sessOS, new Adler32());
                OutputArchive oa = BinaryOutputArchive.getArchive(crcOut);
                FileWriter fstream = new FileWriter(currentDir + "/normalFileWrite.txt");
                BufferedWriter out = new BufferedWriter(fstream);
                for (long k = 0; k < i; k++) {
                    dataBuilder.append("x");
                }
                data = dataBuilder.toString();
                long start = System.currentTimeMillis();
                out.append(data);
                long diff_normal = System.currentTimeMillis() - start;
                long end = System.currentTimeMillis();
                start = System.currentTimeMillis();
                oa.writeString(data, "data");
                long diff_zk = System.currentTimeMillis() - start;
                csv_out.append(i + "," + diff_normal + "," + diff_zk);
                csv_out.append("\n");
                System.out.println(i + "," + diff_normal + "," + diff_zk);
                j++;
                out.close();
                fstream.close();
            }

            csv_out.close();
            fstream_csv.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }


    }
}

