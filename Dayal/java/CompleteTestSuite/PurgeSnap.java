/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dayal
 */
import java.io.File;
import java.io.IOException;
import org.apache.zookeeper.server.*;
public class PurgeSnap {
    public static void main(String[] args) throws IOException
    {
     if(args.length<3 || args.length>4)
	{    
        System.out.println("usage: java PurgeLogs snapDir dataLogDir -n num");
	System.exit(0);
	}        
	int i = 0;
        File dataDir=new File(args[0]);
        File snapDir=dataDir;
        if(args.length==4){
            i++;
            snapDir=new File(args[i]);
        }
        i++; i++;
        int num = Integer.parseInt(args[i]);
        PurgeTxnLog.purge(dataDir, snapDir, 3);
    }
 }

