package edu.iastate.cs472.proj2;
/**
 * @author Josh Seeley
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch {

    private static final double UCB_C = Math.sqrt(2.0);
    
    private static final int ITERATIONS = 400;
    private final Random rand = new Random();

    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is an 2D array of the following integers defined below:
    	// 
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        System.out.println(board);
        System.out.println();

        if (legalMoves == null || legalMoves.length == 0) return null;

        CheckersData rootState = duplicate(this.board);
        ArrayList<CheckersMove> rootUntried = new ArrayList<>();
        for (CheckersMove m : legalMoves) rootUntried.add(m.clone());
        MCNode<CheckersData> root = new MCNode<>(rootState, null, null, rootUntried);

        root.visits = 0;

        for (int it = 0; it < ITERATIONS; it++) {

            MCNode<CheckersData> node = select(root);
            CheckersData simState = duplicate(node.state);

            if (!node.untriedMoves.isEmpty()) {
                node = expand(node, simState);
            }

            double result = simulate(simState);
            backpropagate(node, result);
    
        }

        MCNode<CheckersData> child2 = null;
        int bestVisits = -1;
        for (MCNode<CheckersData> ch : root.children) {
            if (ch.visits > bestVisits) {
                bestVisits = ch.visits;
                child2 = ch;
            }
        }

        if (child2 == null) return legalMoves[0];
        return child2.moveFromParent;
    }
/**
 * 
 * @param node the node to start selection from
 * @return the deepest node reached before expansion
 */
   private MCNode<CheckersData> select(MCNode<CheckersData> node) {
        while (node.untriedMoves.isEmpty() && !node.children.isEmpty()) {
            node = node.bestChildUCB(UCB_C);
        }
        return node;
    }
   /**
    * 
    * @param node the node to expand
    * @param simState a copy of game state associated with the node
    * @return the newly created child node
    */
    private MCNode<CheckersData> expand(MCNode<CheckersData> node, CheckersData simState) {
        int idx = rand.nextInt(node.untriedMoves.size());
        CheckersMove m = node.untriedMoves.remove(idx);

        simState.makeMove(m);
        int player = CheckersData.RED; 

        CheckersMove[] nextMoves = simState.getLegalMoves(player);
        ArrayList<CheckersMove> childUntried = new ArrayList<>();
        if (nextMoves != null)
            for (CheckersMove mm : nextMoves) childUntried.add(mm.clone());

        return node.addChild(duplicate(simState), m.clone(), childUntried);
    }
    /**
     * 
     * @param state the starting state to simulate from
     * @return the result of the simulation (1 for win, 0 for loss, 0.5 for draw)
     */
    private double simulate(CheckersData state) {
        CheckersData sim = duplicate(state);
        boolean blackToMove = false;
        int maxSteps = 100;

        for (int step = 0; step < maxSteps; step++) {
            int player = blackToMove ? CheckersData.BLACK : CheckersData.RED;
            CheckersMove[] moves = sim.getLegalMoves(player);

            if (moves == null) {
                if (player == CheckersData.BLACK) return 0.0; 
                else return 1.0; 
            }

            CheckersMove m = moves[rand.nextInt(moves.length)];
            sim.makeMove(m);

            blackToMove = !blackToMove;
        }

        return 0.5; 
    }
  /**
   * 
   * @param node the node where backpropagation starts
   * @param result result of the simulation to propogate
   */
    private void backpropagate(MCNode<CheckersData> node, double result) {
        while (node != null) {
            node.updateStats(result);
            node = node.parent;
        }
    }
/**
 * 
 * @param b the board state to duplicate or copy
 * @return the duplicated board state
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
