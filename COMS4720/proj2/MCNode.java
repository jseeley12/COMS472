package edu.iastate.cs472.proj2;
/**
 * @author Josh Seeley
 */
import java.util.ArrayList;
import java.util.List;

/**
 * Node type for the Monte Carlo search tree.
 */
public class MCNode<E>
{
  E state;
  CheckersMove moveFromParent;
  MCNode<E> parent;
  List<MCNode<E>> children = new ArrayList<>();
 
  ArrayList<CheckersMove> untriedMoves;
  int visits;
  double wins;
/**
 * 
 * @param state Th state asscoiated with this node
 * @param moveFromParent the move that led to this node from its parent
 * @param parent the parent node
 * @param untriedMoves list of legal moves that have not been tried yet
 */
  public MCNode(E state, CheckersMove moveFromParent, MCNode<E> parent, ArrayList<CheckersMove> untriedMoves) {
    this.state = state;
    this.moveFromParent = moveFromParent;
    this.parent = parent;
    this.untriedMoves = untriedMoves == null ? new ArrayList<>() : untriedMoves;
    this.visits = 0;
    this.wins = 0.0;
  }
/**
 * 
 * @return true if the node had no children, false otherwise
 */
  public boolean isLeaf() { return children.isEmpty(); }
/**
 * 
 * @param childState the game state asscoiated with the child state
 * @param move the move taken from this node to reach the child state
 * @param childUntried The list of untried moces for the child node
 * @return the newly created child node
 */
  public MCNode<E> addChild(E childState, CheckersMove move, ArrayList<CheckersMove> childUntried) {
    MCNode<E> child = new MCNode<>(childState, move, this, childUntried);
    this.children.add(child);
    return child;
  }
/**
 * 
 * @param result the result of the simulation to update current wins and visits.
 */
  public void updateStats(double result) {
    this.visits++;
    this.wins += result;
  }
/**
 * 
 * @return true if all possible moves have been tried, false otherwise
 */
  public boolean isFullyExpanded() {
        return untriedMoves.isEmpty();
    }
  /**
   * 
   * @param c the exploration constant
   * @return the child node with the best UCB score
   */  
  public MCNode<E> bestChildUCB(double c) {
    MCNode<E> best = null;
    double bestVal = Double.NEGATIVE_INFINITY;
    for (MCNode<E> ch : children) {
      if (ch.visits == 0) return ch; 
      double exploitation = ch.wins / (double) ch.visits;
      double exploration = c * Math.sqrt(Math.log(Math.max(1, this.visits)) / (double) ch.visits);
      double val = exploitation + exploration;
      if (val > bestVal) {
        bestVal = val;
        best = ch;
      }
    }
    return best;
  }
}

