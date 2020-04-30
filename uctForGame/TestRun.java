package uctForGame;


import recomendation.*;
public class TestRun
{

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
	   Parameters parameters = new Parameters();
	   
	   Main maintemp = new Main();
	   parameters.startPoint = Main.start;
	   parameters.endPoint = Main.end;
	   
	   RoadNetwork.populateSegmentProcessIdMap(Parameters.noOfTimeSlots);
	   SegmentProcess segment = RoadNetwork.currRoadSegment(parameters.startPoint, parameters.endPoint);
	   
      Game g = new TicTacToe(segment);
      //Game g = new ConnectFour();
      RandomRolloutPlayer p1;

      int p1Wins = 0, p2Wins = 0, draws = 0;

      for (int i = 0; i < 100; i++) {
         p1 = new RandomRolloutPlayer(g, true, 500);
         //p1 = new HumanPlayer(g, true);
         //p1 = new RandomPlayer(g, true);
//         p2 = new RandomRolloutPlayer(g, false, 500);
         
         int segmentIndex = 0;
         while (g.gameStatus(p1.passengerFound) == Game.status.ONGOING) {
            //p1 goes
            System.out.println("Player 1's turn:");
            p1.MakeMove(segmentIndex);
            g.printState(p1.getCurState());
            System.out.println("Status is " + g.gameStatus(p1.getCurState()));


            //p2 goes
            System.out.println("Player 2's turn:");
            p2.MakeMove();
            p1.updateGameState(p2.getCurState());
            g.printState(p1.getCurState());
            System.out.println("Status is " + g.gameStatus(p1.getCurState()));
            
            ++segmentIndex;
         }

         if (g.gameStatus(p2.getCurState()) == Game.status.DRAW) {
            draws++;
         } else if (g.gameStatus(p2.getCurState()) == Game.status.PLAYER1WIN) {
            p1Wins++;
         } else if (g.gameStatus(p2.getCurState()) == Game.status.PLAYER2WIN) {
            p2Wins++;
         }

         System.out.println("P1 wins: " + p1Wins + ", P2 wins: "
                 + p2Wins + ", Draws: " + draws);
      }
   }
}
