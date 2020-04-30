/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learners;

import ilo.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sun.javafx.image.impl.IntArgb;
import com.sun.org.apache.bcel.internal.generic.I2F;

import recomendation.PoissonParameterCalculator;
import recomendation.SegmentProcess;
import tools.DelayKey;
import tools.DelayObject;
import tools.DelayProducer;
import tools.Sampler;

/**
 *
 * @author sairam
 */
public class Ucb implements Learner{

	private Integer[] actions; 
	private Double[] upperBounds;
	private Integer[] numberOfObservations;
	private Double[] cumulatedRewards;
	private int totalNumberOfObservations;
	private boolean isOptimalAction = true;;
	
	
	
        
    private HashMap<DelayKey, DelayObject> delayList;
        
    
   
        
	
	public void initializePolicy(Integer[] possibleActions){
		actions = possibleActions;
		totalNumberOfObservations = 0;
		numberOfObservations = new Integer[actions.length];
		cumulatedRewards = new Double[actions.length];
		//upperBounds = new Double[actions.length];
		
		
		
        delayList = new HashMap<DelayKey, DelayObject>();

//		int i=0;
//		while (i<actions.length) {
//		  numberOfObservations[i] = 0;
//		  cumulatedRewards[i] = 0.0;
//		  upperBounds[i] = Double.POSITIVE_INFINITY;
//		  i++;
//		}

		
	}
	
	public void initializeBanditparameter( Double[] banditInfo)
	{
		 upperBounds = new Double[actions.length];
		 int i =0;
		 while (i<actions.length) {
			  numberOfObservations[i] = 0;
			  cumulatedRewards[i] = 0.0;
			  i++;
			} 
		 if (banditInfo == null)
		 {	 
			 i = 0;
			 while (i<actions.length) {
				  upperBounds[i] = 0.0;
				  i++;
				} 
		 }
		 else
		 {
			 
			 try
			    {
			       if( banditInfo.length != upperBounds.length);
			    }
			    catch (Exception e)
			    {
			       System.out.println("Mismatch in banddit info and the upperbounds");
			    }
			  System.arraycopy(banditInfo, 0, upperBounds, 0, actions.length);
		 }
	}
	
	
	// If reward, we have to maximize it, hence we need to take the maximum,
	private int argmaxList(Double[] ub){
		//System.out.println(ub);
		int i=0,argmax=0;
		double maxv = ub[0];
		while (i<ub.length) {
                    if (ub[i] > maxv) {
        	        maxv = ub[i];
        	        argmax = i;        	  
                    }
		    i++;
		}
		return argmax;
	}
	
	
	// If loss,  we have to minimize it, hence we need to take the minimum,
		private int argminList(Double[] ub){
			//System.out.println(ub);
			int i=0,argmin=0;
			double minv = ub[0];
			while (i<ub.length) {
	                    if (ub[i] < minv) {
	        	        minv = ub[i];
	        	        argmin = i;        	  
	                    }
			    i++;
			}
			return argmin;
		}

	
//	public Integer getActionToPerform1(){
//            if( delayList.isEmpty() ) {
//                return argmaxList(upperBounds);
//            }
//            else {
//                  int count = 0;
//                  Double tub[] = new Double[actions.length];
//                  int i = 0;
//                  for( i = 0 ; i < actions.length  && !delayList.isEmpty(); i++ ) {
//                       DelayObject db = (DelayObject)delayList.get(i);
//                       long tm = db.getDelay(TimeUnit.MILLISECONDS);
//                       if (tm <= 0) { 
//                           delayList.remove(i);
//                           tub[i] = upperBounds[i];}
//                       else {
//                    	   //TODO: old code:  tub[i] = -1.0;
//                    	   tub[i] = Double.POSITIVE_INFINITY; 
//                           count++;
//                       }
//                  }
//                  for( int j = i ; j < actions.length; j++ ) tub[j] = upperBounds[j];
//                  if(count == actions.length)  return -1;
//                  else return argmaxList(tub);
//            }
//                
//	}

	
	
	public Integer getActionToPerform(){
		 isOptimalAction = true;
        if( delayList.isEmpty() ) {
        	//for the reward
            //return argmaxList(upperBounds);
        	// for loss
        	return argmaxList(upperBounds);
        }
        else {
              Double tub[] = new Double[actions.length];
              
              //checks if any action is still frozen and if it is, it is not given as an available action
              for(int i = 0 ; i < actions.length ; ++i)
              	{
            	if(delayList.containsKey(i))
            		{
            		DelayObject db = (DelayObject) delayList.get(i);
            		long tm = db.getDelay(TimeUnit.MINUTES);
            				            		
            		//checks if there is still some positive delay left
            		if(tm <= 0)
            			{
            			//no more delay; remove from delay list
            			delayList.remove(i);
            			tub[i] = upperBounds[i];
            			}
            		else
            			{
						tub[i]= 0.0;
            			}
            		}
            	else
            		{
					tub[i] = upperBounds[i];
            		}
              	}
              
              if(delayList.size() == actions.length)
            	  return -1;
              // for the reward
              //return argmaxList(tub);
              // for loss
              if (argmaxList(upperBounds)!= argmaxList(tub)) 
              {	  
                  for( int i = 0 ; i < tub.length ; i++)
                	  System.out.println( tub[i]+ " " + upperBounds[i] );
            	  
            	  isOptimalAction = false;
              }
              return argmaxList(tub);
        }
            
}

	
	
        public void putInDelayQueue( Integer a, Integer timeStep ){
        	//TODO: write a procedure to learn lambda parameter of poisson distribution
        	
        	SegmentProcess route = main.routes.get(a);
        	/* calculate lambda */
        	ArrayList<SegmentProcess> segmentSequence = new ArrayList<SegmentProcess>();
        	segmentSequence.add(route);
        	double lambda = PoissonParameterCalculator.calculateLambda(segmentSequence, main.TIME_SLOT);
            long timeDelay = Sampler.nextPoisson(lambda);
//            System.out.println( "lambda" + lambda + " " + "timesDelay" + timeDelay);
            Thread t = new Thread( new DelayProducer(delayList , a , timeDelay, timeStep));
            t.start();
        }
        
        
        

	private double ucb(int nbO, double cumR, int nbTot) {
		if (nbO == 0) {
			return Double.POSITIVE_INFINITY;
		} else {
			// for the reward
			//return cumR / nbO + Math.sqrt(2*Math.log((double) nbTot+1)/ nbO);
			// for the loss
			return cumR / nbO + Math.sqrt(2*Math.log((double) nbTot+1)/ nbO);
		}
	}
	
	public void updatePolicy(Integer a, Double r){
		totalNumberOfObservations++;
		numberOfObservations[a] = numberOfObservations[a]+1;
		cumulatedRewards[a] = cumulatedRewards[a]+r;
		for(int i = 0;i<actions.length;i++){
		upperBounds[actions[i]] = ucb(numberOfObservations[actions[i]],cumulatedRewards[actions[i]],totalNumberOfObservations);
		}
	}
        
        
        public ArrayList<ArrayList>  updatePolicy(ArrayList<BlockingQueue<ArrayList>>  queues){
        	    
        	    //feedBackList consists of timeStep, normalized cost, actual cost,action
        	    ArrayList<ArrayList> feedBacKList = new ArrayList<ArrayList>();
                boolean isUpdated = false;
                Double r = 0.0;
                int timeStep;
                for(int i = 0 ; i< queues.size(); i++ ){
                	BlockingQueue<ArrayList> innerQueues = (BlockingQueue<ArrayList>)queues.get(i);
                    while(!innerQueues.isEmpty())
                    {
                          synchronized(queues){
							  ArrayList value = innerQueues.poll();
							  value.add(i);
							  feedBacKList.add(value);
							  timeStep = (Integer)value.get(0);
							  r =(Double)value.get(1); 
						  }
                          
                          // If the action has been removed from the Blocking queue
                          // then there is no use of the action to be in the DelayList
                          // So we remove the (action,timeStep) from the delayList.
                          DelayKey  delaykey = new DelayKey(i, timeStep);
                          if(delayList.containsKey(delaykey))
        	                  delayList.remove(delaykey);
                          totalNumberOfObservations++;
		                  numberOfObservations[i] = numberOfObservations[i]+1;
		                  cumulatedRewards[i] = cumulatedRewards[i]+r.doubleValue();
		                 
		                  isUpdated = true;
		          }
                 
                  
                }
                if( isUpdated ) {
                    for(int i = 0;i<actions.length;i++){
		        upperBounds[actions[i]] = ucb(numberOfObservations[actions[i]],cumulatedRewards[actions[i]],totalNumberOfObservations);
		    }
         }
         return feedBacKList;
         
	}
	
	
	
	
	public Double getCumulatedReward(Integer a) {
		return cumulatedRewards[a];
	}
	
	public Double getCumulatedReward() {
		double cum = 0;
		for(int i=0;i<cumulatedRewards.length;i++){
			cum+=cumulatedRewards[i];
		}
		return cum;
	}
	
	public String getName(){
		return "ucb";
	}

	
	public boolean getActionType()
	{
		return  isOptimalAction;
	}
	
	public boolean isDelayQueueHasAction(Integer i)
	{
		return delayList.containsKey(i);
	}

	public Double[] getBanditInfo()
	{
		return upperBounds;
	}
	
}
