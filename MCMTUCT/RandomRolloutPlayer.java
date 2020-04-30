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
 * An MCTSPlayer with a purely random rollout for hte simulation phase. This
 * player employs no game-specific heuritics, nor does it take special action
 * for moves that are garaunteed to win or lose.
 *
 * @author Kyle
 */
import java.util.ArrayList;
import java.util.Random;

public class RandomRolloutPlayer extends MCTSPlayer
{

   public RandomRolloutPlayer(Game g, boolean player1, int thinkTime)
   {
      super(g, player1, thinkTime);
   }

   /**
    * Simulates a random play-through from a given state and returns the result.
    *
    * @param state The state to be simulated from.
    * @return the game status at the end of the simulation.
    */
   @Override
   protected Game.status simulateFrom(GameState state)
   {
      
	  Game.status s = g.gameStatus(state);
      return s;
//      if (s != Game.status.ONGOING) {
//         return s;
//      } else {
//         return simulateFrom(getRandomMoveFrom(state));
//      }
   }

   /**
    * Gets a random move from a given state.
    *
    * @param gameState a game state from which a random child state is desired.
    * @return a random child state of the passed state.
    */
   private GameState getRandomMoveFrom(GameState gameState)
   {
      ArrayList<? extends GameState> moves = g.getPossibleMoves(gameState);
      Random rand = new Random();
      int r = rand.nextInt(moves.size());
      return moves.get(r);
   }



}
