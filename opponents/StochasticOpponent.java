/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package opponents;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author sairam
 */
public interface StochasticOpponent {

	
	public Integer[] getPossibleActions();
	
	public void getReward(Integer a , ArrayList<BlockingQueue<ArrayList>>  queues, Integer timeStep);
	
	public String getName();
	
	public double getMaxMean();
	
	 public double getMinMean();
	 
	 

}
