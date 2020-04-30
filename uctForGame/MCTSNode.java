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
 * MCTSNode contains tracks scoring information for each gamestate in a link
 * tree for a Monte Carlo Tree Search
 *
 * @author Kyle
 */
import java.util.ArrayList;
import java.util.Random;

import recomendation.SegmentProcess;

public class MCTSNode
{

   private ArrayList<MCTSNode> nextMoves;
   private SegmentProcess nodeGameState;
   private float score;
   private int timesVisisted;
   private Random r;

   /**
    * Instantiates an MCTSNode and initializes ranking variables.
    *
    * @param nodeGameState The GameState this node will keep score for.
    */
   public MCTSNode(SegmentProcess nodeGameState)
   {
      this.nodeGameState = nodeGameState;
      timesVisisted = 0;
      score = 0;
      nextMoves = null;
      r = new Random(System.currentTimeMillis());
   }

   /**
    * Expands the tree by adding a set of child nodes to this node.
    *
    * @param possibleMoves The list of move to be added as child nodes to this
    * node.
    */
   public void expand(ArrayList<SegmentProcess> possibleMoves)
   {
      nextMoves = new ArrayList<MCTSNode>();
      for (SegmentProcess s : possibleMoves) {
         nextMoves.add(new MCTSNode(s));
      }
   }

   /**
    * Uses an Upper Confidence Bounds formula to select the best node for the
    * "selecction" phase of a single MCTS game simulation. The UCB formula is
    * used to balance the value of exploring relatively unexplored nodes against
    * the value of exploring nodes that are highly ranked thus far. Function
    * assumes alternating turns between two opposing players in a \ zero-sum
    * game.
    *
    * @param myTurn Whether or not it is the turn of the MCTSPlayer that
    * contains this node.
    * @return The best seletion from this node.
    */
   public MCTSNode bestSelection()
   {
      //the randomizer is a tiny random number added for tie-breaking
      float bias, randomizer;
      float max = -Float.MAX_VALUE;
      int maxIndex = 0;
      float C = 1;
      for (int i = 0; i < nextMoves.size(); i++) {
         MCTSNode node = nextMoves.get(i);
         float nodeScore = (float) node.getScore() / ((float) (node.getTimesVisited() + Float.MIN_VALUE));
         bias = 2 * C * (float) (Math.sqrt(Math.log((float) this.getTimesVisited()) / ((float) node.getTimesVisited() + Float.MIN_VALUE)));
         randomizer = Float.MIN_VALUE * r.nextInt(nextMoves.size() * nextMoves.size());
         float biasedScore = nodeScore + randomizer + (bias);
         if (biasedScore > max ) {
            max = biasedScore;
            maxIndex = i;
         }
      }
      return nextMoves.get(maxIndex);
   }

   /**
    * Returns the number of times this node has been visited.
    *
    * @return The number of times this node has been visited.
    */
   public int getTimesVisited()
   {
      return timesVisisted;
   }

   /**
    * Chooses the best available move (node) following this node.
    *
    * @return the best available move (node) following this node.
    */
   public MCTSNode bestMove()
   {
	   float max = -Float.MAX_VALUE;
	   int maxIndex = r.nextInt(nextMoves.size());
	   //      float randomizer;
	   for (int i = 0; i < nextMoves.size(); i++) {
		   MCTSNode node = nextMoves.get(i);
		   float nodeScore = (float) node.getScore() / (float) node.getTimesVisited();
		   ////         randomizer = Float.MIN_VALUE * r.nextInt(nextMoves.size() * nextMoves.size());
		   //         if (nodeScore + randomizer > max) {
		   //            max = nodeScore + randomizer;
		   //            maxIndex = i;
		   //         }
		   if (nodeScore  > max) {
			   max = nodeScore;
			   maxIndex = i;
		   }
	   }
	   return nextMoves.get(maxIndex);
   }

   /**
    * Gets the child nodes of this node.
    *
    * @return the child nodes of this node.
    */
   public ArrayList<MCTSNode> getNextMoves()
   {
      return nextMoves;
   }

   /**
    * Gets the GameState for this node.
    *
    * @return the GameState for this node.
    */
   public SegmentProcess getState()
   {
      return nodeGameState;
   }

   /**
    * Returns the string representation of the GameState of this node.
    *
    * @return the string representation of the GameState of this node.
    */
   @Override
   public String toString()
   {
      return nodeGameState.toString();
   }

   /**
    * Sets the score of this node.
    *
    * @param score the new score to be set for this node.
    */
   public void setScore(float score)
   {
      this.score = score;
   }

   /**
    * Increases the number of visits recorded to this node by one.
    */
   public void visit()
   {
      timesVisisted++;
   }

   /**
    * Gets the score for this node.
    *
    * @return the score for this node.
    */
   public float getScore()
   {
      return score;
   }

   /**
    * Finds a child of the current Node that has a given GameState.
    *
    * @param s is the state to be searched for.
    * @return matching node if found, null otherwise.
    */
   public MCTSNode findChildNode(SegmentProcess s)
   {
      for (MCTSNode x : nextMoves) {
         if (x.getState().equals(s)) {
            return x;
         }
      }
      return null;
   }

   /**
    * Returns whether this node is a leaf (has no child nodes).
    *
    * @return whether this node is a leaf (has no child nodes).
    */
   public boolean isLeaf()
   {
	   //TODO: figure it out.
      return nextMoves == null || nextMoves.isEmpty();
   }

   /**
    * Returns a random child node of this node.
    *
    * @return a random child node of this node.
    */
   public MCTSNode getRandomChild()
   {
      int rand = r.nextInt(nextMoves.size());
      return nextMoves.get(rand);
   }
}
