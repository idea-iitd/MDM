package MCMTUCT;
/**
 * Copyright (c) 2012 Kyle Hughart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Credit for algorithm goes to:
 *
 * Kocsis L. & Szepesvari C. (September 2006). Bandit based Monte-Carlo
 * Planning. Unpublished paper presented European Conference on Machine
 * Learning, Berlin, Germany.
 *
 * Chaslot, Guillaume et al. (October, 2008). Monte-Carlo Tree Search: A New
 * Framework for Game AI. Unpublished paper presented at the Fourth Artificial
 * Intelligence and Interactive Digital Entertainment Conference, Maastricht,
 * The Netherlands.
 */
/**
 * A Connect-Four implementation of Game
 *
 * @author Kyle
 */
import java.util.ArrayList;
import java.util.StringTokenizer;

import recomendation.RoadNetwork;
import recomendation.SegmentProcess;


public class ConnectFour implements Game
{

   SegmentProcess segment;
   
   int timeSlot;
   
   public ConnectFour( SegmentProcess segment, int timeSlot)
   {
	   this.segment = segment;
	   this.timeSlot = timeSlot;
   }
   @Override
   public ArrayList<? extends GameState> getPossibleMoves(GameState gameState)
   {
      ArrayList<SegmentState> posMoves = new ArrayList<SegmentState>();
      if (gameStatus(gameState) == Game.status.ONGOING) {
    	  // get neighbor list 
          ArrayList<SegmentProcess> neighborList = TestRun.roadNetwork.neighborSegment(gameState.segment);
          for(SegmentProcess nbh : neighborList)
        	  posMoves.add(new SegmentState(nbh));
      }
      return posMoves;
   }

   @Override
   public status gameStatus(GameState gameState)
   {
	   int simulationSpeed = 1;
	   double pickupProbability = gameState.segment.findPickupProbability(timeSlot);
		long maxDelay = (long) (gameState.segment.timeToCrossSegment / simulationSpeed);
		
		/* path is being simulated.. */
		double randomNumber = Math.random();
		
		//TODO  
		//Removed the delay part for the time being
//		long delay;
//		if(randomNumber < pickupProbability)
//			delay = (long) (Math.random() * (double) maxDelay);
//		else
//			delay = maxDelay;
//		try
//			{
//			Thread.sleep(delay);
//			}
//			catch(InterruptedException e)
//				{
//				e.printStackTrace();
//				}
		
		
		// TODO Check whether the success frequency is updating in original or copy.
		/* updating appropriate frequencies */
		if(randomNumber < pickupProbability)
			{
			++segment.freqSuccess[timeSlot];
			return Game.status.WIN;
			}
		else
			{
			++segment.freqFailure[timeSlot];
			return Game.status.ONGOING;
			} 
   }

   @Override
   public GameState getStartingState()
   {
      return new SegmentState(segment);
   }

   @Override
   public void printState(GameState state)
   {
      String s = state.segment.toString();
      System.out.println(s);
      
      
      
   }
   
@Override
public status getLosegGameStatus() {
	
	return Game.status.LOSE;
}
   
   
  
}