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
 * A Tic Tac Toe implementation of Game.
 *
 * @author Kyle
 */
import java.util.ArrayList;
import java.util.LinkedHashSet;

import recomendation.Location;
import recomendation.RoadNetwork;
import recomendation.SegmentProcess;

public class TicTacToe implements Game
{

   @Override
   public ArrayList<SegmentProcess> getPossibleMoves(SegmentProcess segment, LinkedHashSet<Location> tabuLocations)
   {
	   ArrayList<SegmentProcess> allNeighbourList = RoadNetwork.neighborSegment(segment);
	   ArrayList<SegmentProcess> validNeighbourList = new ArrayList<SegmentProcess>();
	   for(SegmentProcess neighbour : allNeighbourList)
		   if(!tabuLocations.contains(neighbour.endLocation))
			   validNeighbourList.add(neighbour);
	   
	   return validNeighbourList;
   }

   @Override
   public void printState(SegmentProcess segment)
   {
	   System.out.println("\n" + segment.toString());
   }
   
   /** our functions */
   public SegmentProcess startSegment;
   
   public TicTacToe(SegmentProcess segment)
   	{
	   this.startSegment = segment;
   	}
   
   public SegmentProcess getStartingState()
   	{
	   return this.startSegment;
   	}
   
   public status gameStatus(boolean passengerFound)
   	{
	   if(!passengerFound)
		   return Game.status.ONGOING;
	   return Game.status.PLAYER1WIN;
   	}
}
