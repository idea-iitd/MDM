package MCMTUCT;

import recomendation.Main;
import recomendation.RoadNetwork;
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
 * A test class for pitting different players against each other in different
 * games.
 *
 * @author Kyle
 */
public class TestRun
{
	public static RoadNetwork roadNetwork = null;

	/**
	 * A simple main function that allows different player types to play games
	 * against each other. Different permutations can be tried by adjusting
	 * the hardcoded think times or by swapping out different games and players
	 * as g, p1, and p2, respectively.
	 *
	 * @param args
	 */
	public static void main(String args[])
	{
		//Game g = new TicTacToe();
		roadNetwork = new RoadNetwork();
		roadNetwork.populateSegmentProcessIdMap(Main.noOfTimeSlot);
		SegmentProcess segment = roadNetwork.currRoadSegment(Main.start	, Main.end);
		Game g = new ConnectFour(segment,Main.timeSlot);
		Player p1;

		/**
		 * Game status will show the current status of the game whether ongoing or win or lose
		 */
		Game.status status = null; 
		p1 = new RandomRolloutPlayer(g, true, Main.depth);
		for (int i = 0; i < 100; i++) 
		{
			//p1 = new RandomRolloutPlayer(g, true, 500);
			//p1 = new HumanPlayer(g, true);
			//p1 = new RandomPlayer(g, true);
			//p2 = new RandomRolloutPlayer(g, false, 500);
			p1.setRoot();
			status = Game.status.ONGOING;
			while ( status == Game.status.ONGOING) 
			{
				//p1 goes
				System.out.println("Player 1's turn:");
				g.printState(p1.getCurState());
				status = p1.MakeMove();
				p1.updateGameState(p1.getCurState());
				g.printState(p1.getCurState());
				System.out.println("Status is " + status);


				//p2 goes
				//            System.out.println("Player 2's turn:");
				//            p2.MakeMove();
				//            p1.updateGameState(p2.getCurState());
				//            g.printState(p1.getCurState());
				//            System.out.println("Status is " + g.gameStatus(p1.getCurState()));
			}


			//         if (g.gameStatus(p2.getCurState()) == Game.status.DRAW) {
			//            draws++;
			//         } else if (g.gameStatus(p2.getCurState()) == Game.status.PLAYER1WIN) {
			//            p1Wins++;
			//         } else if (g.gameStatus(p2.getCurState()) == Game.status.PLAYER2WIN) {
			//            p2Wins++;
			//         }
			//
			//         System.out.println("P1 wins: " + p1Wins + ", P2 wins: "
			//                 + p2Wins + ", Draws: " + draws);
		}
	}
}
