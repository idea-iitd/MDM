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
 * An implementation of the Player class which takes stricly random moves unless
 * a winning move is available. This class assumes a game where causing oneself
 * to lose is not a legal move.
 *
 * @author Kyle
 */
import java.util.ArrayList;
import java.util.Random;

public class RandomPlayer implements Player
{

   private Game g;
   private boolean player1;
   GameState curState;

   public RandomPlayer(Game g, boolean player1)
   {
      this.g = g;
      this.player1 = player1;
      curState = g.getStartingState();

   }

   public GameState getCurState()
   {
      return curState;
   }

   public void updateGameState(GameState s)
   {
      curState = s;
   }

   public void MakeMove()
   {
      if (g.gameStatus(curState) == Game.status.ONGOING) {
         GameState finisher = getFinishingMoveFrom(curState);
         if (finisher == null) {
            curState = getRandomMoveFrom(curState);
         } else {
            curState = finisher;
         }
      }
      if (player1) {
         System.out.print("player1 : ");
      } else {
         System.out.print("player2 : ");
      }
      System.out.println(g.gameStatus(curState));
      g.printState(curState);
   }

   /**
    * Returns game winning state (for either side) if one is available, null
    * otherwise.
    *
    * @param gameState The state from which an immediate finishing move will be
    * searched for.
    * @return Game winning state (for either side) if one is available, null
    * otherwise.
    */
   private GameState getFinishingMoveFrom(GameState gameState)
   {
      ArrayList<? extends GameState> moves = g.getPossibleMoves(gameState);
      for (GameState s : moves) {
         if ((g.gameStatus(s) == Game.status.PLAYER1WIN)
                 || (g.gameStatus(s) == Game.status.PLAYER2WIN)) {
            return s;
         }
      }
      return null;
   }

   /**
    * Chooses a random move from the given GameState.
    *
    * @param gameState from which an immediate following move should be chosen
    * randomly. Function assumes it will not be called when no legal moves are
    * available.
    * @return a random move that legally follows the passed GameState.
    */
   private GameState getRandomMoveFrom(GameState gameState)
   {
      ArrayList<? extends GameState> moves = g.getPossibleMoves(gameState);
      Random rand = new Random();
      int r = rand.nextInt(moves.size());
      return moves.get(r);
   }
}
