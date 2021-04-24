package students.AlShawi;

import edu.ksu.csc.ai.othello.*;
import edu.ksu.csc.ai.othello.GameState.GameStatus;
import edu.ksu.csc.ai.othello.GameState.Player;

import java.util.AbstractSet;
import java.util.Date;

public class AlShawiAgent extends OthelloPlayer {

	int CutOff = 10000, CounterMin = 1, CounterMax = 1;
	boolean CuttedOff = false;
	Player player, opponent;

	public AlShawiAgent(String name) {
		super(name);
	}

	@Override
	public Square getMove(GameState currentState, Date deadline) {
		player = currentState.getCurrentPlayer();
		opponent = currentState.getOpponent(player);
		return MiniMaxDecision(currentState);
	}

	public Square MiniMaxDecision(GameState currentState) {
		Square MaximizedMove = null;
		double MaximizedValue = Double.NEGATIVE_INFINITY;

		AbstractSet<Square> VaildMoveList = currentState.getValidMoves(player);

		for (Square action : VaildMoveList) {

			double value = MinValue(currentState.applyMove(action));
			CuttedOff = false;
			if (MaximizedValue <= value) {
				MaximizedMove = action;
				MaximizedValue = value;
			}
		}
		return MaximizedMove;

	}

	public double MinValue(GameState currentState) {

		if (player == Player.PLAYER1) {
			if (currentState.getStatus() == GameStatus.PLAYER1WON) {
				return currentState.getScore(GameState.Player.PLAYER1);
			}
			if (currentState.getStatus() == GameStatus.PLAYER2WON) {
				return -currentState.getScore(GameState.Player.PLAYER2);
			}
			if (currentState.getStatus() == GameStatus.TIE) {
				return 0;
			}
		} else {
			if (currentState.getStatus() == GameStatus.PLAYER1WON) {
				return -currentState.getScore(GameState.Player.PLAYER1);
			}
			if (currentState.getStatus() == GameStatus.PLAYER2WON) {
				return currentState.getScore(GameState.Player.PLAYER2);
			}
			if (currentState.getStatus() == GameStatus.TIE) {
				return 0;
			}
		}

		if (CounterMin % CutOff == 0) {
			CounterMin = 1;
			CuttedOff = true;
			if (currentState.getScore(player) > currentState.getScore(opponent))
				return currentState.getScore(player);
			else
				return -currentState.getScore(opponent);
		}

		double v = Double.POSITIVE_INFINITY;

		AbstractSet<GameState> successors = currentState.getSuccessors();

		++CounterMin;
		for (GameState state : successors) {
			v = Math.min(v, MaxValue(state));
			if (CuttedOff)
				return v;
		}
		return v;

	}

	private double MaxValue(GameState currentState) {

		if (player == Player.PLAYER1) {
			if (currentState.getStatus() == GameStatus.PLAYER1WON) {
				return currentState.getScore(GameState.Player.PLAYER1);
			}
			if (currentState.getStatus() == GameStatus.PLAYER2WON) {
				return -currentState.getScore(GameState.Player.PLAYER2);
			}
			if (currentState.getStatus() == GameStatus.TIE) {
				return 0;
			}
		} else {
			if (currentState.getStatus() == GameStatus.PLAYER1WON) {
				return -currentState.getScore(GameState.Player.PLAYER1);
			}
			if (currentState.getStatus() == GameStatus.PLAYER2WON) {
				return currentState.getScore(GameState.Player.PLAYER2);
			}
			if (currentState.getStatus() == GameStatus.TIE) {
				return 0;
			}
		}

		if (CounterMax % CutOff == 0) {
			CounterMax = 1;
			CuttedOff = true;
			if (currentState.getScore(player) > currentState.getScore(opponent))
				return currentState.getScore(player);
			else
				return -currentState.getScore(opponent);
		}

		double v = Double.NEGATIVE_INFINITY;

		AbstractSet<GameState> successors = currentState.getSuccessors();
		CounterMax++;
		for (GameState state : successors) {
			v = Math.max(v, MinValue(state));
			if (CuttedOff)
				return v;
		}
		return v;

	}

}
