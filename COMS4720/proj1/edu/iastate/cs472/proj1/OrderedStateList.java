package edu.iastate.cs472.proj1;

import java.util.Iterator;
import java.util.NoSuchElementException;

//import javax.smartcardio.CardTerminals.State;

/**
 *  
 * @author Josh Seeley
 * 
 * I added or modified the functions OrderStateList, addState, findState, removeState, remove, compareStates
 *
 */

/**
 * This class describes a circular doubly-linked list of states to represent both the OPEN and CLOSED lists
 * used by the A* algorithm.  The states on the list are sorted in the  
 * 
 *     a) order of non-decreasing cost estimate for the state if the list is OPEN, or 
 *     b) lexicographic order of the state if the list is CLOSED.  
 * 
 */
public class OrderedStateList 
{

	/**
	 * Implementation of a circular doubly-linked list with a dummy head node.
	 */
	  private State head;           // dummy node as the head of the sorted linked list 
	  private int size = 0;
	  
	  private boolean isOPEN;       // true if this OrderedStateList object is the list OPEN and false 
	                                // if the list CLOSED.

	  /**
	   *  Default constructor constructs an empty list. Initialize heuristic. Set the fields next and 
	   *  previous of head to the node itself. Initialize instance variables size and heuristic. 
	   * 
	   * @param h 
	   * @param isOpen   
	   */
	  public OrderedStateList(Heuristic h, boolean isOpen)
	  {
		  //	TODO
		  State.heu = h;   // initialize heuristic used for evaluating all State objects. 
		  isOPEN = isOpen;
		  int[][] x = {{0,0,0},{0,0,0},{0,0,0}};
		  head = new State(x);
		  head.next = head;
		  head.previous = head;
		  size = 0;
	  }

	  
	  public int size()
	  {
		  return size; 
	  }
	  
	  
	  /**
	   * A new state is added to the sorted list.  Traverse the list starting at head.  Stop 
	   * right before the first state t such that compareStates(s, t) <= 0, and add s before t.  
	   * If no such state exists, simply add s to the end of the list. 
	   * 
	   * Precondition: s does not appear on the sorted list. 
	   * 
	   * @param s
	   */
	  public void addState(State s)
	  {
		  // TODO 
		  State currentState = head.next;

		  while(currentState != head && compareStates(s, currentState) > 0){
			currentState = currentState.next;
		  }
		  s.next = currentState;
		  s.previous = currentState.previous;
		  currentState.previous.next = s;
		  currentState.previous = s;
		  size++;
	  }
	  
	  
	  /**
	   * Conduct a sequential search on the list for a state that has the same board configuration 
	   * as the argument state s.  
	   * 
	   * Calls equals() from the State class. 
	   * 
	   * @param s
	   * @return the state on the list if found
	   *         null if not found 
	   */
	  public State findState(State s)
	  {
		  // TODO 
			State currentState = head.next;

			while(currentState != head){
				if(currentState.equals(s)){
					return currentState;
				}
				currentState = currentState.next;
			}

		  return null; 
	  }
	  
	  
	  /**
	   * Remove the argument state s from the list.  It is used by the A* algorithm in maintaining 
	   * both the OPEN and CLOSED lists. 
	   * 
	   * @param s
	   * @throws IllegalStateException if s is not on the list 
	   */
	  public void removeState(State s) throws IllegalStateException
	  {
		  // TODO 
		  State removeS = findState(s);

		  if(removeS == null) {
			throw new IllegalStateException("State is not found in the list");
		  }
		  removeS.previous.next = removeS.next;
		  removeS.next.previous = removeS.previous;

		  removeS.next = null;
		  removeS.previous = null;
		  size--;
	  }
	  
	  
	  /**
	   * Remove the first state on the list and return it.  This is used by the A* algorithm in maintaining
	   * the OPEN list. 
	   * 
	   * @return  
	   */
	  public State remove()
	  {
		  // TODO
		  State f;
		  if(size == 0){
			throw new IllegalStateException("No states in the list");
		  }else{
			f = head.next;
			head.next = f.next;
			f.next.previous = head;
			f.next = null;
			f.previous = null;
			size--;

		  }
		  return f; 
	  }
	  
	  
	  /**
	   * Compare two states depending on whether this OrderedStateList object is the list OPEN 
	   * or the list CLOSE used by the A* algorithm.  More specifically,  
	   * 
	   *     a) call the method compareTo() of the State if isOPEN == true, or 
	   *     b) create a StateComparator object to call its compare() method if isOPEN == false. 
	   * 
	   * @param s1
	   * @param s2
	   * @return -1 if s1 is less than s2 as determined by the corresponding comparison method
	   *         0  if they are equal 
	   *         1  if s1 is greater than s2
	   */
	  private int compareStates(State s1, State s2)
	  {
		  // TODO 
		  if(isOPEN){
			return s1.compareTo(s2);
		  }else if(!isOPEN){
			StateComparator c = new StateComparator();
			return c.compare(s1, s2);
		  }else{
		  return 0;
		  } 
	  }
}
