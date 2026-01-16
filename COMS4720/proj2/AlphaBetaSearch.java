package edu.iastate.cs472.proj2;
/**
 * @author Josh Seeley
 */


/**
 * This class implements the Alpha-Beta pruning algorithm to find the best 
 * move at current state.
*/
public class AlphaBetaSearch extends AdversarialSearch {
private static final double NORM_VALUE = 1.0;
private static final double KING_VALUE = 1.8;
    /**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     * 
     * Each legalMove in the input now contains a single move
     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
     * (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is a int 2D array. The numbers in the `board` are
        // defined as
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        System.out.println(board);
        System.out.println();

        if (legalMoves == null || legalMoves.length == 0) return null;
        

        int maxDepth = 6; 
        CheckersMove bestMove = legalMoves[0];
        double bestVal = Double.NEGATIVE_INFINITY;

        for (CheckersMove m : legalMoves) {
            CheckersData copy = duplicate(this.board);
            copy.makeMove(m);
            double val = alphaBeta(copy, maxDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
            if (val > bestVal) {
                bestVal = val;
                bestMove = m;
            }
        }

        return bestMove;
    }
    
    // TODO
    // Implement your helper methods here.
/**
 * 
 * @param state the board state
 * @param depth the depth to search
 * @param alpha the value of alpha
 * @param beta the value of beta
 * @param blackToMove indicates whether black is to move
 * @return value
 */
    private double alphaBeta(CheckersData state, int depth, double alpha, double beta, boolean blackToMove) {
       
        int player = blackToMove ? CheckersData.BLACK : CheckersData.RED;
        CheckersMove[] moves = state.getLegalMoves(player);
        if (moves == null) {
           
            if (blackToMove) return -1.0; 
            else return 1.0; 
        }

        if (depth == 0) return evaluate(state);

        if (blackToMove) {
            double value = Double.NEGATIVE_INFINITY;
            for (CheckersMove m : moves) {
                CheckersData copy = duplicate(state);
                copy.makeMove(m);
                value = Math.max(value, alphaBeta(copy, depth - 1, alpha, beta, false));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break;
            }
            return value;
        } else {
            double value = Double.POSITIVE_INFINITY;
            for (CheckersMove m : moves) {
                CheckersData copy = duplicate(state);
                copy.makeMove(m);
                value = Math.min(value, alphaBeta(copy, depth - 1, alpha, beta, true));
                beta = Math.min(beta, value);
                if (alpha >= beta) break;
            }
            return value;
        }
    }
/**
 * 
 * @param state the board state
 * @return the evaluation score
 */
    private double evaluate(CheckersData state) {
        double red = 0, black = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int p = state.pieceAt(r, c);
             if (p == CheckersData.RED) {
                double adv = (7 - r) * 0.05;    
                double center = (c >= 2 && c <= 5) ? 0.05 : 0.0;
                red += NORM_VALUE + adv + center;
            } else if (p == CheckersData.RED_KING) {
                double center = (c >= 2 && c <= 5) ? 0.05 : 0.0;
                red += KING_VALUE + center;
            } else if (p == CheckersData.BLACK) {
                double adv = r * 0.05;
                double center = (c >= 2 && c <= 5) ? 0.05 : 0.0;
                black += NORM_VALUE + adv + center;
            } else if (p == CheckersData.BLACK_KING) {
                double center = (c >= 2 && c <= 5) ? 0.05 : 0.0;
                black += KING_VALUE + center;
            }
        }
    }
        double score = black - red;
       
        return Math.max(-1.0, Math.min(1.0, score / 12.0));
    }

/**
 * 
 * @param b the board to copy or duplicate
 * @return the copied or duplicated board
 */
    private CheckersData duplicate(CheckersData b) {
        CheckersData nb = new CheckersData();
        for (int i = 0; i < 8; i++){
             for (int j = 0; j < 8; j++){
                 nb.board[i][j] = b.pieceAt(i, j);
             }
            }
        return nb;
    }

}
