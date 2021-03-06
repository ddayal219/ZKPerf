

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsumerImpl 
{
 private BlockingQueue< Consume > itemQueue = 
  new LinkedBlockingQueue< Consume >();
 
 private ExecutorService executorService = 
  Executors.newCachedThreadPool();
 
 private List< ItemProcessor > jobList = 
  new LinkedList< ItemProcessor >();
 
 private volatile boolean shutdownCalled = false;
  
 public ConsumerImpl(int poolSize)
 {
  for(int i = 0; i < poolSize; i++)
  {
   ItemProcessor jobThread = 
    new ItemProcessor(itemQueue);
   
   jobList.add(jobThread);
   executorService.submit(jobThread);
  }
 }
 
 public boolean consume(Consume j)
 {
  if(!shutdownCalled)
  {
   try
   {
    itemQueue.put(j);
   }
   catch(InterruptedException ie)
   {
    Thread.currentThread().interrupt();
    return false;
   }
   return true;
  }
  else
  {
   return false;
  }
 }
 
 public void finishConsumption()
 {
  for(ItemProcessor j : jobList)
  {
   j.cancelExecution();
  }
  
  executorService.shutdown();
 }
}
