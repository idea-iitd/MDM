package MCMTUCT;

import java.util.ArrayList;

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
   protected GameState curState;
   private MCTSNode gameTree;
   private MCTSNode curNode;
   private ArrayList<MCTSNode> nodeTraj;
   private int depth;
   private int curDepth = 1;
   
   
   /**
    * Set the node to the root of the tree    
    */
    
    public void setRoot()
    {
 	   curNode = gameTree;
 	   curState = g.getStartingState();
    }

   
   /**
    * Gets the current state of the game.
    *
    * @return the current state of the game.
    */
   @Override
   public GameState getCurState()
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
   public MCTSPlayer(Game g, boolean player1, int depth)
   {
      this.g = g;
      this.player1 = player1;
      curState = g.getStartingState();
      gameTree = new MCTSNode(curState);
      curNode = gameTree;
      curNode.expand(g.getPossibleMoves(curNode.getState()));
      nodeTraj = new ArrayList<MCTSNode>();
      this.depth = depth;

   }

   @Override
   public void updateGameState(GameState s)
   {
      curState = s;
      if (curNode.isLeaf()) {
         curNode.expand(g.getPossibleMoves(curNode.getState()));
      }
      //curNode = curNode.findChildNode(s);
   }

   /**
    * Simulates possible games one step at a time and returns true if the depth 
    * is within the limits.
    */
   @Override
   public Game.status MakeMove()
   {
    	  return runTrial(curNode);  // sending current depth for trial  
    }

   /**
    * Plays a single simulated game, and encompasses the four stages of an MCTS
    * simulation (selection, expansion, simulation, and backpropagation).
    * Selection: Pick a node to simulate from by recursively applying UCB.
    * Expansion: Add a new set of nodes to the link tree as children of the
    * selected node. Simulation: Pick one of those nodes and simulate a game
    * from it. Backpropagation: Rank all nodes selected during the selection
    * step based on simulation outcome.
    *
    * @param node The node to begin running the trial from.
    * @param myTurn Whether it is this players turn or not.
    * @return The status of the trial.
    */
   private Game.status runTrial(MCTSNode node)
   {
      Game.status returnStatus;
      node.visit();
      nodeTraj.add(node);
      curDepth++;
      if( curDepth <= depth && !node.isLeaf())
      {
        
           //expansion
           //node.expand(g.getPossibleMoves(node.getState()));
           //System.out.println("--" + node.getState().segment.toString());
          // if(!node.isLeaf())
           //{
              MCTSNode bestChild  = node.bestSelection();
            //System.out.println("--" + bestChild.getState().segment.toString());
            returnStatus = simulateFrom(bestChild.getState());
            curNode = bestChild;
            curState = curNode.getState();
           //}
           //else
           //{
           //returnStatus = g.getLosegGameStatus(); 
           //}
         
      }
      else
      {
    	  returnStatus = g.getLosegGameStatus();
    	  nodeTraj.clear();
    	  curDepth = 0;
      }
     
      //backpropagation
      if (IWin(returnStatus)) {
    	  for (MCTSNode x : nodeTraj) 
    	  {
    		  x.setScore(x.getScore() + 1);
    	  }
    	  nodeTraj.clear();
    	  curDepth = 0;
      }

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
      return (s == Game.status.WIN );
              
   }

   /**
    * Returns true if this player loses, false otherwise.
    *
    * @param s the status of the game to be checked.
    * @return true if this player loses, false otherwise.
    */
//   protected boolean ILose(Game.status s)
//   {
//      return (s == Game.status.PLAYER1WIN && !player1)
//              || (s == Game.status.PLAYER2WIN && player1);
//   }

   /**
    * Performs a simulation or "rollout" for the "simulation" phase of the
    * runTrial function. This can be written to contain game-specific heuristics
    * or "finishing move" detection if desired.
    *
    * @param state the state to be simulated from.
    * @return the resulting status of the simulation.
    */
   protected abstract Game.status simulateFrom(GameState state);
   
   
}
