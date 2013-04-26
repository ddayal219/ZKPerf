public class Consume
{
 private String line;
 private Test t;
 public Consume(String s, Test te)
 {
  line = s;
  t=te;
 }
 
 public void process() 
 {
     try
     {
   t.getData(line);
 
     }
         catch(Exception e)
     
                 {}
 //System.out.println(Thread.currentThread().getName() + " consuming :" + line);
 }
}