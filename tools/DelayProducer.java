/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author sairam
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

	 
public class DelayProducer implements Runnable {
	    
	    // Creates an instance of blocking queue using the DelayQueue.
	    private HashMap<DelayKey, DelayObject> list;
	    private final Random random = new Random(); 
	    private int action;
        private long delay;
        private int timeStep;
	    public DelayProducer(HashMap<DelayKey, DelayObject> list , int action , long delay , int timeStep){ 
	         this.list = list;
                 this.action = action;
                 this.delay = delay;
                 this.timeStep = timeStep;
	    }
	     
           
	    @Override
	    public void run() {
	           // while (true) {
	                try {
	                   
	                    // Put some Delayed object into the DelayQueue.
	                    //int delay = random.nextInt(10000);
	                    DelayObject object = new DelayObject(
	                            UUID.randomUUID().toString(), delay);
	 
//	                    System.out.printf("Put object = %s%n", object);
	                    synchronized(list) { list.put(new DelayKey(action, timeStep), object);}
                           
	                    Thread.sleep(500);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	           // }
	        }
	    
	     
	    
	 
	}