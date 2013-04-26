

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ItemProcessor implements Runnable
{
 private BlockingQueue<Consume> jobQueue;
 
 private volatile boolean keepProcessing;
  
 public ItemProcessor(BlockingQueue<Consume> queue)
 {
  jobQueue = queue;
  keepProcessing = true;
 }
 
 public void run()
 {
  while(keepProcessing || !jobQueue.isEmpty())
  {
   try
   {
    Consume j = jobQueue.poll(10, TimeUnit.SECONDS);
    
    if(j != null)
    {
     j.process();
    }
   }
   catch(InterruptedException ie)
   {
    Thread.currentThread().interrupt();
    return;
   }
  }
 }
 
 public void cancelExecution()
 {
  this.keepProcessing = false;
 }
} 