/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

/**
 *
 * @author sairam
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.plaf.basic.BasicSliderUI.ActionScroller;

import com.sun.org.apache.bcel.internal.generic.NEW;

import learners.Learner;
import opponents.StochasticOpponent;
import tools.DelayKey;

public class Engine {


	private StochasticOpponent  opponent;

	private Learner player;

	ArrayList<BlockingQueue<ArrayList>> queues; 
	
	private Double[] banditInfo;
	
	

	public Engine(Learner l, StochasticOpponent o){
		opponent = o;
		player = l;
	}

	public Learner getLearner(){
		return player;
	}

	public StochasticOpponent getStochasticOpponent(){
		return opponent;
	}
	
	public void setBanditInfo( Double[] banditInfo)
	{
		if (banditInfo == null)
			this.banditInfo = null;
		else 
		{
			this.banditInfo = new Double[banditInfo.length];
			System.arraycopy(banditInfo, 0, this.banditInfo, 0, banditInfo.length);
		}
	}

	public void newGame(){

		Integer[] possibleActions = opponent.getPossibleActions();
		player.initializePolicy(possibleActions);
		player.initializeBanditparameter(banditInfo);

		queues = new ArrayList<BlockingQueue<ArrayList>>();
		for(int i = 0 ; i < possibleActions.length; i++ )
			queues.add(new LinkedBlockingQueue<ArrayList>());
	}


	public ArrayList<double[]> play(int numberRounds, Integer[] optimalActions, Integer[] subOptimalActions, long delayPeriod){
		int i=0;
		Integer action = null;
		double reward=0;
		
		//Use to store the rewards in delayed fashion that is used by the Bandit.
		double[] cumrewards = new double [numberRounds];
		
		//Use to store the rewards with respect to current time step
		double[] rewards = new double[numberRounds];
		
		//use to store actual cost of the trip 
		double[] actualCost = new double[numberRounds];
		
		//This stores the reward and the actualCost that has to be returned
		ArrayList<double[]> returnList = new ArrayList<double[]>();
		
				
        
		Integer[]  actions = opponent.getPossibleActions();
		if(player.getName() == "ucb"  || player.getName() == "ThomsonSampling" )
		{
			


			while(i<numberRounds) {


				if(player.getName() == "ucb")
				{
					// This is to sequentially select  all the actions so that ucb can form a upper confidence bound on it. 
					if( i < opponent.getPossibleActions().length)
					{
						action =  actions[i]; 	
					}
					else
					{
						// After the actions are selected sequentially, then we choose the best available actions.
						action = player.getActionToPerform();
					}

				}
				else 
				{
					action = player.getActionToPerform();
				}
				

                if( player.getActionType())
                	 optimalActions[i] = action.intValue();
                else subOptimalActions[i] = ((Integer)action).intValue();
                	
				
				
				if( ((Integer)action).intValue() != -1 ) {
					
					opponent.getReward(action , queues, Integer.valueOf(i));
					
					player.putInDelayQueue(action, i);
					
					try {
				          Thread.sleep(delayPeriod);
				        } catch(InterruptedException e) {
				        }

					
					
					
					ArrayList<ArrayList>  feedBackList = player.updatePolicy(queues);
					
					
					//This is used to update the reward and actualCost data structure. This is to update 
					// according to the delayed reward. Using the timeStep, both the reward and the actual cost are
					// updated.
					Iterator<ArrayList> iterator = feedBackList.iterator();
					while(iterator.hasNext())
					{
						ArrayList element = iterator.next();
						Integer index = (Integer)element.get(0);
						Double val = (Double)element.get(1);
						rewards[index] = val;
						Double cost = (Double)element.get(2);
						actualCost[index] = cost;
						Integer act = (Integer)element.get(3);
			
					}
					
					
					
										

					//printStatistics(player,action,reward);
					cumrewards[i] = player.getCumulatedReward();
					i++;
				}
			}
			
			
//			try {
//		          Thread.sleep(30000);
//		        } catch(InterruptedException e) {
//		        }
			
			
			while(true){

				
				int count = 0;
				for(int j = 0 ; j < queues.size();j++ )
				{
					
//					System.out.println( (queues.get(j)).isEmpty());
//					System.out.println( player.isDelayQueueHasAction(j));
					
					if ( (queues.get(j)).isEmpty() && !player.isDelayQueueHasAction(j))
					{	
						
						    count++;	
						
						
					}
					else
					{
						ArrayList<ArrayList>  feedBackList = player.updatePolicy(queues);
						Iterator<ArrayList> iterator = feedBackList.iterator();
						while(iterator.hasNext())
						{
							ArrayList element = iterator.next();
							Integer index = (Integer)element.get(0);
							Double val = (Double)element.get(1);
							rewards[index] = val;
							Double cost = (Double)element.get(2);
							actualCost[index] = cost;
						}
					
					}
				}
				if( count == queues.size())  break;
				
			}
			
			
			
		}
		
		for( int j = 0 ; j < rewards.length ; j++){
			double sum = 0;
			for( int k = j ; k >= 0; k--)
			{
				sum = sum + rewards[k];
			}
			cumrewards[j] = sum;
		}
		returnList.add(cumrewards);
		returnList.add(actualCost);
		
		return returnList;
	}

	public ArrayList<double[][]> repeatPlays(int numberRounds, int numberOfPlays, Integer[][] optimalActions , Integer[][] subOptimalActions, long delayPeriod){

		double[][] cumrewards = new double [numberOfPlays][numberRounds];
		
		double[][] actualCosts = new double [numberOfPlays][numberRounds];
		
		ArrayList<double[][]> returnList = new ArrayList<double[][]>();
		int i=0;


		while(i<numberOfPlays) {
			
			newGame();
			
			ArrayList<double[]> retValues =   play(numberRounds , optimalActions[i], subOptimalActions[i], delayPeriod);
			cumrewards[i] = retValues.get(0);
			actualCosts[i]= retValues.get(1); 
			

			i++;
		}
		
		returnList.add(cumrewards);
		returnList.add(actualCosts);
		
		return returnList;
	}



	public void printStatistics(Learner p, Integer action, Double reward){
		System.out.println("Action performed: "+action+"; Received reward: "+reward+"; " +
				"Cumulated rewards: " + p.getCumulatedReward());

	}


}