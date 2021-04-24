package students.mohandalrasheed;
import java.util.*;

import edu.ksu.csc.ai.othello.GameState;
import edu.ksu.csc.ai.othello.OthelloPlayer;
import edu.ksu.csc.ai.othello.Square;
import edu.ksu.csc.ai.othello.GameState.Player;

public class MinMaxerBattler extends OthelloPlayer {
    final boolean USE_ALPHA_BETA = true;
    final boolean USE_TOURNAMENT = true;
    final int MAX_CUTOFF = 5; // Used if USE_TOURNAMENT == true
    int cutoff = 6; // Used if USE_TOURNAMENT == false

    Player myPlayer, otherPlayer;
    HashMap<String, Double> weights;

    public MinMaxerBattler(String name) {
        super(name);

        weights = new HashMap<String, Double>();
        weights.put("Moves", 15.);
        weights.put("Corner", 50.);
        weights.put("Score", 5.);
        weights.put("Winner", 99999.);

    }

    public Square getMove(GameState currentState, Date deadline) {
        myPlayer = currentState.getCurrentPlayer();
        otherPlayer = (myPlayer == Player.PLAYER1) ? Player.PLAYER2 : Player.PLAYER1;

        if(this.USE_TOURNAMENT){
            this.cutoff = 1;
            while(this.MAX_CUTOFF-this.cutoff != 0) {
                search(currentState, USE_ALPHA_BETA);
                this.cutoff++;
            }
        }
        else
            search(currentState, this.USE_ALPHA_BETA);

        Square bestMove = this.getCurrentBestMove();
        if(bestMove == null)
            return currentState.getValidMoves().toArray(new Square[0])[0];
        return bestMove;
    }

    void search(GameState currentState, boolean USE_ALPHA_BETA){
        if(USE_ALPHA_BETA)
            alphaBetaSearch(currentState);
        else
            minMaxSearch(currentState);
    }

    private double minValue(GameState state, int depth) {
        if(depth == this.cutoff || state.getStatus() != GameState.GameStatus.PLAYING)
            return evaluateMove(state);

        HashSet<Square> moves = (HashSet<Square>) state.getValidMoves();
        double v = Double.POSITIVE_INFINITY;
        for (Square move : moves)
            v = Math.min(v, maxValue(state.applyMove(move), depth + 1));
        return v;
    }

    private double maxValue(GameState state, int depth) {
        if(depth == this.cutoff || state.getStatus() != GameState.GameStatus.PLAYING)
            return evaluateMove(state);

        HashSet<Square> moves = (HashSet<Square>) state.getValidMoves();
        double v = Double.NEGATIVE_INFINITY;
        for (Square move : moves)
            v = Math.max(v, minValue(state.applyMove(move), depth + 1));
        return v;
    }

    void minMaxSearch(GameState currentState) {
        double bestScore = Double.NEGATIVE_INFINITY;
        HashSet<Square> moves = (HashSet<Square>) currentState.getValidMoves();
        for (Square move : moves) {
            double v = maxValue(currentState.applyMove(move), 0);
            if (v > bestScore) {
                bestScore = v;
                this.registerCurrentBestMove(move);
            }
        }
    }

    // Alpha Beta Pruning
    private double minValueAB(GameState state, double alpha, double beta, int depth) {
        if(depth == this.cutoff || state.getStatus() != GameState.GameStatus.PLAYING)
            return evaluateMove(state);

        HashSet<Square> moves = (HashSet<Square>) state.getValidMoves();

        double v = Double.POSITIVE_INFINITY;
        for (Square move : moves) {
            v = maxValueAB(state.applyMove(move), alpha, beta, depth + 1);
            if(v <= alpha)
                return v;
            beta = Math.min(beta, v);
        }
        return v;
    }

    private double maxValueAB(GameState state, double alpha, double beta, int depth) {
        if(depth == this.cutoff || state.getStatus() != GameState.GameStatus.PLAYING)
            return evaluateMove(state);
        HashSet<Square> moves = (HashSet<Square>) state.getValidMoves();
        double v = Double.NEGATIVE_INFINITY;
        for (Square move : moves) {
            v = minValueAB(state.applyMove(move), alpha, beta, depth + 1);
            if(v >= beta)
                return v;
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    void alphaBetaSearch(GameState currentState){
        double bestScore = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        HashSet<Square> moves = (HashSet<Square>) currentState.getValidMoves();
        for (Square move : moves) {
            double v = maxValueAB(currentState.applyMove(move), bestScore, beta, 0);
            if(v > bestScore){
                bestScore = v;
                this.registerCurrentBestMove(move);
            }
        }
    }

    double normalizeDiff(double d1, double d2){
        return (d1 - d2) / (d1 + d2);
    }

    double evaluateMove(GameState newState){
        GameState currentState = newState.getPreviousState();
        Square move = newState.getPreviousMove();
        int row = move.getRow(), col = move.getCol();

        double evaluation = 0;


        // Moves
        int myMovesCnt = newState.getValidMoves(this.myPlayer).size();
        int otherMovesCnt = newState.getValidMoves(this.otherPlayer).size();
        evaluation += weights.get("Moves") * normalizeDiff(myMovesCnt, otherMovesCnt);

        // Corner
        if((row == 0 || row == 7) && (col == 0 || col == 7)) // If corner
            evaluation += weights.get("Corner"); // * 1

        // Score
        evaluation += weights.get("Score") * normalizeDiff(newState.getScore(this.myPlayer),
                newState.getScore(this.otherPlayer));


        // Winner
        Player winner = newState.getWinner();
        if(winner == this.myPlayer)
            evaluation += weights.get("Winner");
        else if(winner == this.otherPlayer)
            evaluation -= weights.get("Winner");

        return evaluation;
    }


}

/*
    a    b    c    d    e    f    g    h
  -----------------------------------------
0 | a0 | b0 | c0 | d0 | e0 | f0 | g0 | h0 |
  -----------------------------------------
1 | a1 | b1 | c1 | d1 | e1 | f1 | g1 | h1 |
  -----------------------------------------
2 | a2 | b2 | c2 | d2 | e2 | f2 | g2 | h2 |
  -----------------------------------------
3 | a3 | b3 | c3 | d3 | e3 | f3 | g3 | h3 |
  -----------------------------------------
4 | a4 | b4 | c4 | d4 | e4 | f4 | g4 | h4 |
  -----------------------------------------
5 | a5 | b5 | c5 | d5 | e5 | f5 | g5 | h5 |
  -----------------------------------------
6 | a6 | b6 | c6 | d6 | e6 | f6 | g6 | h6 |
  -----------------------------------------
7 | a7 | b7 | c7 | d7 | e7 | f7 | g7 | h7 |
  -----------------------------------------

*/
