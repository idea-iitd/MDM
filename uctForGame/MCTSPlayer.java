package uctForGame;

import java.util.LinkedHashSet;

import recomendation.Location;
import recomendation.SegmentProcess;

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
 * MCTSPlayer is an implementation of Player that makes moves using UCT (A Monte
 * Carlo Tree Search that uses an Upper Confidence Bounds formula).
 *
 * @author Kyle
 */
public abstract class MCTSPlayer implements Player
{

   protected Game g;
   protected boolean player1;
   protected SegmentProcess curState;
   private MCTSNode gameTree;
   private MCTSNode curNode;
   private LinkedHashSet<Location> tabuLocations;
   
   //TODO: remove thinkTime (time within which a move must be made)
  
   protected boolean passengerFound;

   /**
    * Gets the current segment.
    *
    * @return the current segment.
    */
   @Override
   public SegmentProcess getCurState()
   {
      return curState;
   }

   /**
    * Instantiates the player.
    *
    * @param g The Game being played.
    * @param player1 Whether or not this player is player 1.
    * @param thinkTime How many milliseconds this player is allowed to think per
    * turn (Longer think time yields better simulations.
    */
   public MCTSPlayer(Game g, boolean player1)
   {
      this.g = g;
      this.player1 = player1;
      curState = g.getStartingState();
      gameTree = new MCTSNode(curState);
      curNode = gameTree;
      
      tabuLocations = new LinkedHashSet<Location>();
      tabuLocations.add(curState.startLocation);
      tabuLocations.add(curState.endLocation);
      curNode.expand(g.getPossibleMoves(curNode.getState(), tabuLocations));
      
      this.passengerFound = false;

   }

   @Override
   public void updateGameState(SegmentProcess s)
   {
      curState = s;
      tabuLocations.add(s.endLocation);
      if (curNode.isLeaf()) {
         curNode.expand(g.getPossibleMoves(curNode.getState(), tabuLocations));
      }
      curNode = curNode.findChildNode(s);
   }

   /**
    * Simulates possible games until allowed think time runs out, and thn makes
    * a move.
    */
   @Override
   public void MakeMove(int segmentIndex)
   {
      //long endTime = System.currentTimeMillis() + thinkTime;
	  
      runTrial(curNode, segmentIndex, this.tabuLocations);
      
      if (g.gameStatus(this.passengerFound) == Game.status.ONGOING) {
         MCTSNode best = curNode.bestMove();
         curState = best.getState();
         curNode = best;
      }
   }

   /**
    * Plays a single simulated game, and encompasses the four stages of an MCTS
    * simulation (selection, expansion, simulation, and backpropogation).
    * Selection: Pick a node to simulate from by recursively applying UCB.
    * Expansion: Add a new set of nodes to the link tree as children of the
    * selected node. Simulation: Pick one of those nodes and simulate a game
    * from it. Backpropogation: Rank all nodes selected during the selection
    * step based on simulation outcome.
    *
    * @param node The node to begin running the trial from.
    * @param myTurn Whether it is this players turn or not.
    * @return The status of the trial.
    */
   private Game.status runTrial(MCTSNode node, int segmentIndex, LinkedHashSet<Location> tabuLocations)
   {
      Game.status returnStatus;
      node.visit();
      /* no need to bother about passenger here since it is a simulation */
      if (!node.isLeaf() && segmentIndex < Parameters.recommendationLength) {
         //selection
    	  MCTSNode bestSelectionNode = node.bestSelection();
    	  tabuLocations.add(bestSelectionNode.getState().endLocation);
         returnStatus = runTrial(bestSelectionNode, segmentIndex + 1, tabuLocations);
      } else {
         //expansion
         node.expand(g.getPossibleMoves(node.getState(), tabuLocations));
         if (!node.isLeaf()) {
            node = node.getRandomChild();
            node.visit();
         }
         //simulation
         returnStatus = simulateFrom(node.getState());
      }
      //backpropogation
      if (IWin(returnStatus)) {
         node.setScore(node.getScore() + 1);
      }
//      if (ILose(returnStatus)) {
//         node.setScore(node.getScore() - 1);
//      }
      return returnStatus;


   }

   /**
    * Returns true if this player wins, false otherwise.
    *
    * @param s the status of the game to be checked.
    * @return true if this player wins, false otherwise.
    */
   protected boolean IWin(Game.status s)
   {
	   
      return (s == Game.status.PLAYER1WIN );
             
   }

   /**
    * Returns true if this player loses, false otherwise.
    *
    * @param s the status of the game to be checked.
    * @return true if this player loses, false otherwise.
    */
//   protected boolean ILose(Game.status s)
//   {
//      return ;
//   }

   /**
    * Performs a simulation or "rollout" for the "simulation" phase of the
    * runTrial function. This can be written to contain game-specific heuristics
    * or "finishing move" detection if desired.
    *
    * @param state the state to be simulated from.
    * @return the resulting status of the simulation.
    */
   protected abstract Game.status simulateFrom(SegmentProcess segment);
   
   
}
