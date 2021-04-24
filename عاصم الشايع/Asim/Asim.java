package students.MyTeam;

import java.util.Date;
import java.util.HashSet;

import edu.ksu.csc.ai.othello.GameState;
import edu.ksu.csc.ai.othello.OthelloPlayer;
import edu.ksu.csc.ai.othello.Square;
import edu.ksu.csc.ai.othello.GameState.Player;


public class Asim extends OthelloPlayer {
	public Asim(String name) {
		super(name);
	}
	
	public Square getMove(GameState currentState, Date deadline){
//		try {
//			  Thread.sleep( 900);
//			} catch (InterruptedException ex) {
//			  ex.printStackTrace();
//			}
/*******************************************************************************************/
								 /****** First Situation ******/
								/****** Corners available ******/
		
		/*Short cut if there is possible Powerful moves*/
		/*The Idea is to put the priority for the corners*/
		HashSet<Square> PowerMoves = new HashSet<Square>();
		int PM = 0;
		Player p = currentState.getCurrentPlayer();
		for(int i=0; i<8; i=i+7) {
			for(int j=0; j<8; j=j+7) {
				Square m = new Square(i,j);
				if(currentState.isLegalMove(m, p)) {
					PowerMoves.add(m);
					PM++;
				}
			}
		}
		if (PM != 0) {Square PMmoves[] = PowerMoves.toArray(new Square[0]); return PMmoves[0];}
/*******************************************************************************************/
		else {
								 	/****** Second Situation ******/
						/****** Between Corners AND beside the walls available ******/
			
			/*making priority to specific spots in the board*/
			/*The Idea is to put the priority for the spots between corners that can not be flipped again*/
			/* First I create new GetValidMoves list*/
			
			HashSet<Square> PowerMoves2 = new HashSet<Square>();
			int PM2 = 0;
			Player p2 = currentState.getCurrentPlayer();
			for(int i=0; i<8; i++) {
				if ((i == 1) || (i == 6)){
					continue;
				}
				else if ((i == 0) || (i == 7)){
					for(int j=2; j<6; j++) {
						Square m2 = new Square(i,j);
						if(currentState.isLegalMove(m2, p2)) {
							PowerMoves2.add(m2);
							PM2++;
						}
					}
				}
				else {
					for(int j=0; j<8; j=j+7) {
						Square m2 = new Square(i,j);
						if(currentState.isLegalMove(m2, p2)) {
							PowerMoves2.add(m2);
							PM2++;
						}
					}
				}
			}/*The end of the new GetValidMoves*/
			
			/*Second I used my BASIC evaluation with the new list*/
			if (PM2 != 0) {
				Square PMmoves2[] = PowerMoves2.toArray(new Square[0]);
				Square ChosenMove = null;
				int MIN = 0;
				int Alpha = 0;
				int Beta = 64;
				for(int i = 0 ; i < PMmoves2.length; i++) {
					GameState temp = (GameState)currentState.clone();
					temp = temp.applyMove(PMmoves2[i]);
					Square movesMIN[] = temp.getValidMoves().toArray(new Square[0]);
					for(int j = 0 ; j < movesMIN.length; j++) {
						GameState temp2 = (GameState)temp.clone();
						temp2.applyMove(movesMIN[j]);
						MIN = temp2.getScore(temp2.getCurrentPlayer());
						if (Beta > MIN) {Beta = MIN;}
						if (Beta < Alpha) {break;}
					}
					if (Alpha < Beta) {ChosenMove = PMmoves2[i]; Alpha = Beta;}
				}
				this.registerCurrentBestMove(ChosenMove);
				return ChosenMove;
			}
/*******************************************************************************************/	
									/****** Third Situation ******/
								/****** EveryThing else but the mention above ******/
			
			Square movesMAX[] = currentState.getValidMoves().toArray(new Square[0]);
			Square noWeakMoves[] = null;
			Square ChosenMove = null;
			int MIN = 0;
			int Alpha = 0;
			int Beta = 64;
			
					/*the agent will create new GetValidMoves without the moves >>   */
					/* that will allow the opponent to have a corner. */
			HashSet<Square> preWeakMoves = new HashSet<Square>();
			int PM3 = 0;
			Player p3 = currentState.getCurrentPlayer();
			for(int x=0; x<8; x++) {
				for(int y=0; y<8; y++) {
					if (
							((x==0)&&(y==1))||((x==0)&&(y==6))||((x==1)&&(y==0))||((x==1)&&(y==1))||
							((x==1)&&(y==6))||((x==1)&&(y==7))||((x==6)&&(y==0))||((x==6)&&(y==1))||
							((x==6)&&(y==6))||((x==6)&&(y==7))||((x==7)&&(y==1))||((x==7)&&(y==6))	
						) {continue;}
					Square m3 = new Square(x,y);
					if(currentState.isLegalMove(m3, p3)) {System.out.println("564");preWeakMoves.add(m3); PM3++;}
				}
			}
			if (PM3 != 0) {noWeakMoves = preWeakMoves.toArray(new Square[0]);}
			else {noWeakMoves = movesMAX;}/*The end of the new GetValidMoves*/
			
			/*Now I use my BASIC evaluation with the new list if it's different from the original*/
			for(int i = 0 ; i < noWeakMoves.length; i++) {
				GameState temp = (GameState)currentState.clone();
				temp = temp.applyMove(noWeakMoves[i]);
				Square movesMIN[] = temp.getValidMoves().toArray(new Square[0]);
				for(int j = 0 ; j < movesMIN.length; j++) {
					GameState temp2 = (GameState)temp.clone();
					temp2.applyMove(movesMIN[j]);
					MIN = temp2.getScore(temp2.getCurrentPlayer());
					if (Beta > MIN) {Beta = MIN;}
					if (Beta < Alpha) {break;}
				}
				if (Alpha < Beta) {ChosenMove = noWeakMoves[i]; Alpha = Beta;}
			}
			this.registerCurrentBestMove(ChosenMove);
			return ChosenMove;
		}
	}
}