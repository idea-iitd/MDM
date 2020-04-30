package learners;



import ilo.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import recomendation.PoissonParameterCalculator;
import recomendation.SegmentProcess;
import tools.BernoulliDistribution;
import tools.DelayKey;
import tools.DelayObject;
import tools.DelayProducer;
import tools.Sampler;



public class ThomsonSampling implements Learner{

	private Integer[] actions; 
	private Double[] posteriors;
	private Integer[] numberOfObservations;
	private Double[] cumulatedRewards;
	private boolean isOptimalAction  = true;
	private Double[] psedocumulatedRewards;

	private HashMap<DelayKey, DelayObject> delayList;

	public void initializePolicy(Integer[] possibleActions){
		actions = possibleActions;
		numberOfObservations = new Integer[actions.length];
		cumulatedRewards = new Double[actions.length];
		psedocumulatedRewards  = new Double[actions.length];
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
		
	private int argminList(Double[] ub){
			//System.out.println(ub);
			int i=0,argmin=0;
			double maxv = ub[0];
			while (i<ub.length) {
				if (ub[i] < maxv) {
					maxv = ub[i];
					argmin = i;        	  
				}
				i++;
			}
			return argmin;
		
		
	}
	
	
	public void initializeBanditparameter( Double[] banditInfo)
	{
		posteriors = new Double[actions.length];
		 int i =0;
		 while (i<actions.length) {
			  numberOfObservations[i] = 0;
			  cumulatedRewards[i] = 0.0;
			  psedocumulatedRewards[i]= 0.0; 
			  i++;
			} 
		 if (banditInfo == null)
		 {	 
			 i = 0;
			 while (i<actions.length) {
				 posteriors[i] = 0.0;
				  i++;
				} 
		 }
		 else
		 {
			 
			 try
			    {
			       if( banditInfo.length != posteriors.length);
			    }
			    catch (Exception e)
			    {
			       System.out.println("Mismatch in banddit info and the upperbounds");
			    }
			  System.arraycopy(banditInfo, 0, posteriors, 0, actions.length);
		 }
	}
	
	//	
	//	public Integer getActionToPerform(){
	//		
	//		return argmaxList(posterior);
	//	}

	public Integer getActionToPerform(){
		 isOptimalAction = true;
       if( delayList.isEmpty() ) {
       	//for the reward
           //return argmaxList(upperBounds);
       	// for loss
       	return argmaxList(posteriors);
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
           			tub[i] = posteriors[i];
           			}
           		else
           			{
						tub[i]= 0.0;
           			}
           		}
           	else
           		{
					tub[i] = posteriors[i];
           		}
             	}
             
             if(delayList.size() == actions.length)
           	  return -1;
             // for the reward
             //return argmaxList(tub);
             // for loss
             if (argmaxList(posteriors)!= argmaxList(tub)) 
             {	  
                 for( int i = 0 ; i < tub.length ; i++)
               	  System.out.println( tub[i]+ " " + posteriors[i] );
           	  
           	  isOptimalAction = false;
             }
             return argmaxList(tub);
       }
           
}

	public void putInDelayQueue( Integer a, Integer timeStep ){
    	//TODO: write a procedure to learn lambda parameter of poisson distribution
    	
    	SegmentProcess route = main.routes.get(a);
    	ArrayList<SegmentProcess> segmentSequence = new ArrayList<SegmentProcess>();
    	segmentSequence.add(route);
    	double lambda = PoissonParameterCalculator.calculateLambda(segmentSequence, main.TIME_SLOT);
        long timeDelay = Sampler.nextPoisson(lambda);
//        System.out.println( "lambda" + lambda + " " + "timesDelay" + timeDelay);
        Thread t = new Thread( new DelayProducer(delayList , a , timeDelay, timeStep));
        t.start();
    }


	public void updatePolicy(Integer a, Double r){
		numberOfObservations[a] = numberOfObservations[a]+1;
		cumulatedRewards[a] = cumulatedRewards[a]+r; //r \in {0,1}
		for(int i = 0;i<actions.length;i++){
			posteriors[actions[i]] = Sampler.sampleBeta(1+cumulatedRewards[actions[i]], 1+numberOfObservations[actions[i]]-cumulatedRewards[actions[i]]);
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
                  
                  // Since r is loss. convert it into 1- r so that  lower loss is near 1 and higher loss is near 0
                  //double rDash = 1-r;
                  //Sample from the bernoulli distribution with the prob r
  				  BernoulliDistribution bDistribution = new BernoulliDistribution(r);
  				  double val = bDistribution.sampleFromDistribution();
  				  if(val == 1)
  					cumulatedRewards[i] = cumulatedRewards[i] + 1;
                  
                  // If the action has been removed from the Blocking queue
                  // then there is no use of the action to be in the DelayList
                  // So we remove the (action,timeStep) from the delayList.
                  DelayKey  delaykey = new DelayKey(i, timeStep);
                  if(delayList.containsKey(delaykey))
	                  delayList.remove(delaykey);
                 
                  numberOfObservations[i] = numberOfObservations[i]+1;
                  psedocumulatedRewards[i] = psedocumulatedRewards[i]+r.doubleValue();
                 
                  isUpdated = true;
          }
         
          
        }
        if( isUpdated ) {
            for(int i = 0;i<actions.length;i++){
            	posteriors[actions[i]] =Sampler.sampleBeta(1+cumulatedRewards[actions[i]], 1+numberOfObservations[actions[i]]-cumulatedRewards[actions[i]]);
            }
        }
 return feedBacKList;
 
}





	public Double getCumulatedReward(Integer a) {
		return psedocumulatedRewards[a];
	}

	public Double getCumulatedReward() {
		double cum = 0;
		for(int i=0;i<psedocumulatedRewards.length;i++){
			cum+=psedocumulatedRewards[i];
		}
		return cum;
	}

	public boolean getActionType()
	{
		return  isOptimalAction;
	}
	
	public boolean isDelayQueueHasAction(Integer i)
	{
		return delayList.containsKey(i);
	}

	public String getName(){
		return "ThomsonSampling";
	}

	
	public Double[] getBanditInfo() {
		
		return posteriors;
	}
	
	
}