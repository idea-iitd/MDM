package uctForGame_Base;
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

public class TicTacToe implements Game
{

   @Override
   public ArrayList<StringGameState> getPossibleMoves(GameState state)
   {
      ArrayList<StringGameState> posMoves = new ArrayList<StringGameState>();
      if (gameStatus(state) == Game.status.ONGOING) {
         char activePlayer = state.toString().charAt(state.toString().length() - 1);
         for (int i = 0; i < state.toString().length() - 1; i++) {
            if (state.toString().charAt(i) == '_') {
               String temp = state.toString().substring(0, i) + activePlayer
                       + state.toString().substring(i + 1, state.toString().length() - 1);
               if (activePlayer == 'X') {
                  temp += 'O';
               } else {
                  temp += 'X';
               }
               posMoves.add(new StringGameState(temp));
            }

         }
      }
      return posMoves;
   }

   @Override
   public status gameStatus(GameState state)
   {
      String s = state.toString().substring(0, state.toString().length());
      if (s.matches("XXX.......")
              || s.matches("...XXX....")
              || s.matches("......XXX.")
              || s.matches("X..X..X...")
              || s.matches(".X..X..X..")
              || s.matches("..X..X..X.")
              || s.matches("X...X...X.")
              || s.matches("..X.X.X...")) {
         return Game.status.PLAYER1WIN;
      } else if (s.matches("OOO.......")
              || s.matches("...OOO....")
              || s.matches("......OOO.")
              || s.matches("O..O..O...")
              || s.matches(".O..O..O..")
              || s.matches("..O..O..O.")
              || s.matches("O...O...O.")
              || s.matches("..O.O.O...")) {
         return Game.status.PLAYER2WIN;
      } else if (!s.contains("_")) {
         return Game.status.DRAW;
      } else {
         return Game.status.ONGOING;
      }
   }

   @Override
   public StringGameState getStartingState()
   {
      return new StringGameState("_________X");
   }

   @Override
   public void printState(GameState state)
   {
      System.out.println(state.toString().substring(0, 3));
      System.out.println(state.toString().substring(3, 6));
      System.out.println(state.toString().substring(6, 9));
   }
}
