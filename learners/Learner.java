/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learners;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author sairam
 */
public interface Learner {

	
	public void initializePolicy(Integer[] possibleActions);

	public Integer getActionToPerform();
	
	public void putInDelayQueue(Integer a , Integer timeStep);

	public void updatePolicy(Integer a,Double reward);

	public ArrayList<ArrayList> updatePolicy(ArrayList<BlockingQueue<ArrayList>>  queues);
	

	public Double getCumulatedReward(Integer a);

	public Double getCumulatedReward();	

	public String getName();
	
	public boolean getActionType();
	
	public boolean isDelayQueueHasAction(Integer i);
	
	public Double[] getBanditInfo();
	
	public void initializeBanditparameter( Double[] banditInfo);
	

}
