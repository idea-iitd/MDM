package uctForGame;
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

import recomendation.SegmentProcess;

public class ConnectFour implements Game
{

   final int COLS = 7;
   final int ROWS = 6;

   @Override
   public ArrayList<? extends GameState> getPossibleMoves(GameState gameState)
   {
      ArrayList<StringGameState> posMoves = new ArrayList<StringGameState>();
      if (gameStatus(gameState) == Game.status.ONGOING) {
         char activePlayer = gameState.toString().charAt(gameState.toString().length() - 1);
         for (int i = 0; i < COLS; i++) {
            for (int j = ROWS - 1; j >= 0; j--) {
               int pos = j * (COLS + 1) + i;
               if (gameState.toString().charAt(pos) == '_') {
                  String temp = gameState.toString().substring(0, pos)
                          + activePlayer
                          + gameState.toString().substring(pos + 1,
                          gameState.toString().length() - 1);
                  if (activePlayer == '1') {
                     temp += '2';
                  } else {
                     temp += '1';
                  }
                  posMoves.add(new StringGameState(temp));
                  j = -1;
               }
            }

         }
      }
      return posMoves;
   }

   @Override
   public status gameStatus(SegmentProcess segment, int timeSlot)
   {
	   // TODO check it
	   int simulationSpeed = 1;
	   double pickupProbability = segment.findPickupProbability(timeSlot);
		long maxDelay = (long) (segment.timeToCrossSegment / simulationSpeed);
		
		/* path is being simulated.. */
		double randomNumber = Math.random();
		long delay;
		if(randomNumber < pickupProbability)
			delay = (long) (Math.random() * (double) maxDelay);
		else
			delay = maxDelay;
		try
			{
			Thread.sleep(delay);
			}
			catch(InterruptedException e)
				{
				e.printStackTrace();
				}
		// TODO Check whether the success frequency is updating in original or copy.
		/* updating appropriate frequencies */
		if(randomNumber < pickupProbability)
			{
			++segment.freqSuccess[timeSlot];
			return Game.status.PLAYER1WIN;
			}
		else
			{
			++segment.freqFailure[timeSlot];
			return Game.status.ONGOING;
			}
		
			   
	   
	   
//      String state = gameState.toString().substring(0,
//              gameState.toString().length() - 1);
//      if (state.matches(".*1111.*")
//              || state.matches(".*(1.......){3}1.*")
//              || state.matches(".*(1[^,].......){3}1.*")
//              || state.matches(".*([^,]1.....){3}.1.*")) {
//         return Game.status.PLAYER1WIN;
//      } else if (state.matches(".*2222.*")
//              || state.matches(".*(2.......){3}2.*")
//              || state.matches(".*(2[^,].......){3}2.*")
//              || state.matches(".*([^,]2.....){3}.2.*")) {
//         return Game.status.PLAYER2WIN;
//      } else if (!state.contains(
//              "_")) {
//         return Game.status.DRAW;
//      } else {
//         return Game.status.ONGOING;
//      }
   }

   @Override
   public GameState getStartingState()
   {
      return new StringGameState("_______,_______,_______,_______,_______,_______,1");
   }

   @Override
   public void printState(GameState state)
   {
      String s = state.toString().substring(0, state.toString().length() - 1);
      s = s.replace('1', '☻');
      s = s.replace('2', '☺');
      s = s.replace('_', '.');
      StringTokenizer st = new StringTokenizer(s, ",");
      while (st.hasMoreTokens()) {
         System.out.println(st.nextToken());
      }
   }
}