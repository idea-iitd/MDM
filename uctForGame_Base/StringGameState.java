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

/*
 * String game states can be used if you want to encode all gamestate
 * information into a single string. For example, a tic-tac-toe state
 * might be X_X_O____O where the first 9 chars indicate values for
 * spaces and the 10th indicates whose turn it is.
 */
public class StringGameState implements GameState
{

   private String state;

   /**
    * Creates a new StringGameState
    *
    * @param state is the String to be wrapped in this gamestate.
    */
   public StringGameState(String state)
   {
      this.state = state;
   }

   @Override
   public boolean equals(GameState g)
   {
      if (g instanceof StringGameState) {
         return state.equals(((StringGameState) g).state);
      }
      return false;
   }

   /**
    * Returns the state as a string.
    *
    * @return The string that represents this GameState
    */
   @Override
   public String toString()
   {
      return state;
   }
}
