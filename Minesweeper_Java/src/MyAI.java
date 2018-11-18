
/*

AUTHOR:      John Lu

DESCRIPTION: This file contains your agent class, which you will
             implement.

NOTES:       - If you are having trouble understanding how the shell
               works, look at the other parts of the code, as well as
               the documentation.

             - You are only allowed to make changes to this portion of
               the code. Any changes to other portions of the code will
               be lost when the tournament runs your code.
*/

package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import src.Action.ACTION;

public class MyAI extends AI {

	public static Comparator<TwoTuple> voteComparator = new Comparator<TwoTuple>() {
		@Override
		public int compare(TwoTuple t1, TwoTuple t2) {
			return (int)Math.ceil((t1.votes - t2.votes));
		}
	};

	public static Comparator<TwoTuple> mineComparator = new Comparator<TwoTuple>() {
		@Override
		public int compare(TwoTuple t1, TwoTuple t2) {
			return t2.noOfNeighboringMines - t1.noOfNeighboringMines;
		}
	};

	private class TwoTuple implements Comparable<TwoTuple> {
		public int x;
		public int y;
		public double votes = 0;
		public int noOfNeighboringMines = Integer.MAX_VALUE;
		public boolean flagged = false;
		public boolean visited = false;

		public TwoTuple(int x, int y) {
			this.x = x;
			this.y = y;

		}

		public TwoTuple(int x, int y, int noOfNeighborMines) {
			this.x = x;
			this.y = y;
			this.noOfNeighboringMines = noOfNeighborMines;
		}

		@Override
		public boolean equals(Object e) {
			if (e instanceof TwoTuple) {
				TwoTuple e1 = (TwoTuple) e;
				if (e1.x == this.x && e1.y == this.y)
					return true;
			}

			return false;
		}

		public String toString() {
			return "(" + x + "," + y + ")";
		}

		public ArrayList<TwoTuple> getNeighbors() {

			ArrayList<TwoTuple> neighbors = new ArrayList<>();
			if (isBoardIndexInBounds(this.x - 1, this.y - 1))
				neighbors.add(board[this.x - 1][this.y - 1]);
			if (isBoardIndexInBounds(this.x - 1, this.y))
				neighbors.add(board[this.x - 1][this.y]);
			if (isBoardIndexInBounds(this.x - 1, this.y + 1))
				neighbors.add(board[this.x - 1][this.y + 1]);
			if (isBoardIndexInBounds(this.x, this.y + 1))
				neighbors.add(board[this.x][this.y + 1]);
			if (isBoardIndexInBounds(this.x + 1, this.y + 1))
				neighbors.add(board[this.x + 1][this.y + 1]);
			if (isBoardIndexInBounds(this.x + 1, this.y))
				neighbors.add(board[this.x + 1][this.y]);
			if (isBoardIndexInBounds(this.x + 1, this.y - 1))
				neighbors.add(board[this.x + 1][this.y - 1]);
			if (isBoardIndexInBounds(this.x, this.y - 1))
				neighbors.add(board[this.x][this.y - 1]);

			return neighbors;
		}

//		public int getCountOfCoveredCells() {
//			int countOfCovered = 0;
//			countOfCovered += visitStatus(this.x - 1, this.y - 1) + visitStatus(this.x - 1, this.y)
//					+ visitStatus(this.x - 1, this.y + 1) + visitStatus(this.x, this.y + 1)
//					+ visitStatus(this.x + 1, this.y + 1) + visitStatus(this.x + 1, this.y)
//					+ visitStatus(this.x + 1, this.y - 1) + visitStatus(this.x, this.y - 1);
//			return countOfCovered;
//		}
//
//		private int visitStatus(int x, int y) {
//			if (x < rowNum && y >= 1 && x >= 1 && y < colNum) {
//				return board[x][y].visited ? 0 : 1;
//			} else
//				return 0;
//		}

		private boolean isBoardIndexInBounds(int x, int y) {
			if (x < rowNum && y >= 1 && x >= 1 && y < colNum)
				return true;
			else
				return false;
		}

		@Override
		public int compareTo(TwoTuple o) {
			if (this.x != o.x)
				return this.x - o.x;
			else
				return this.y - o.y;
		}

	}

//	private static final int SAFE = -1;
	// ########################## INSTRUCTIONS ##########################
	// 1) The Minesweeper Shell will pass in the board size, number of mines
	// and first move coordinates to your agent. Create any instance variables
	// necessary to store these variables.
	//
	// 2) You MUST implement the getAction() method which has a single parameter,
	// number. If your most recent move is an Action.UNCOVER action, this value will
	// be the number of the tile just uncovered. If your most recent move is
	// not Action.UNCOVER, then the value will be -1.
	//
	// 3) Feel free to implement any helper functions.
	//
	// ###################### END OF INSTURCTIONS #######################

	// This line is to remove compiler warnings related to using Java generics
	// if you decide to do so in your implementation.
//	@SuppressWarnings("unchecked")

	int rowNum = 0;
	int colNum = 0;
	int currX = 0;
	int currY = 0;
	int totalMines = 0;
	TwoTuple[][] board;
	Set<TwoTuple> safeToVisit;
	TwoTuple lastVisited;
	boolean oneEncountered = false;
	int safeToVisitCounter;
	Queue<TwoTuple> minePq = new PriorityQueue<>(2, mineComparator);

	public MyAI(int rowDimension, int colDimension, int totalMines, int startX, int startY) {
		// ################### Implement Constructor (required) ####################
		rowNum = rowDimension + 1;
		colNum = colDimension + 1;
		this.currX = startX;
		this.currY = startY;
		this.totalMines = totalMines;
		board = new TwoTuple[rowNum][colNum];
		// visited = new boolean[rowNum][colNum];
		safeToVisit = new TreeSet<TwoTuple>();
		// safeToVisitCounter = 1;

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (i == 0 || j == 0) {
					if (i == 0) {
						board[i][j] = new TwoTuple(i, j, j);
					}

					if (j == 0) {
						board[i][j] = new TwoTuple(i, j, i);
					}

				} else {
					board[i][j] = new TwoTuple(i, j, Integer.MAX_VALUE);
				}
			}
		}
		lastVisited = board[currX][currY];
		lastVisited.visited = true;
	}

	// ################## Implement getAction(), (required) #####################
	public Action getAction(int number) {

		Queue<TwoTuple> votePq = new PriorityQueue<>(10,voteComparator);
		setNumberOfMines(lastVisited, number);
		lastVisited.visited = true;
		printboard(board);
		// visited[lastVisited.x][lastVisited.y] = true;
		number = lastVisited.noOfNeighboringMines;

		if (number < 1) {
			markNeighboursSafe(board[lastVisited.x][lastVisited.y]);
		} else {
//				TwoTuple cell = new TwoTuple(lastVisited.x, lastVisited.y) ;

			lastVisited.noOfNeighboringMines = number;
			minePq.add(lastVisited);
		}

		if (!safeToVisit.isEmpty()) {
			Iterator<TwoTuple> setIterator = safeToVisit.iterator();
			lastVisited = setIterator.next();
			safeToVisit.remove(lastVisited);
			return new Action(ACTION.UNCOVER, lastVisited.x, lastVisited.y);
		} else {
			System.out.println("Safe to visit is empty");
			for (TwoTuple cell : minePq) { // minePq operations

//					long countOfCoveredCells = cell.getNeighbors().stream().
//							filter(celli -> celli.visited == false)
//							.count();

				int countOfCoveredCells = 0;
				ArrayList<TwoTuple> cellsNeighbors = cell.getNeighbors();
				for (TwoTuple celli : cellsNeighbors) {
					if (celli.visited == false) {
						countOfCoveredCells++;
					}
				}

				for (TwoTuple celli : cellsNeighbors) {
					if (celli.visited == false) {
						celli.votes += cell.noOfNeighboringMines / countOfCoveredCells;
						votePq.add(celli);
					}
				}

				if (cell.noOfNeighboringMines == countOfCoveredCells) {
					for (TwoTuple neighbor : cell.getNeighbors()) {
						if (neighbor.visited == false) { // flag closed/covered cells
							neighbor.flagged = true;
							ArrayList<TwoTuple> neighborsOfKnownMines = neighbor.getNeighbors();
							for (TwoTuple n : neighborsOfKnownMines) { // for each closed cell's neighbor
								setNumberOfMines(n, -1); // decrement count of number of mines
								if (n.visited == true && n.noOfNeighboringMines == 0) {
// if the count becomes zero for a neighbor that was visited then mark its neighbors safe
									markNeighboursSafe(n);
								}

							}
						}
					}
				}

			}
		}

		// if the operations on minepq have given some safe cells then open them
		if (!safeToVisit.isEmpty()) {
			Iterator<TwoTuple> setIterator = safeToVisit.iterator();
			lastVisited = setIterator.next();
			safeToVisit.remove(lastVisited);
			return new Action(ACTION.UNCOVER, lastVisited.x, lastVisited.y);
		}

		if (!votePq.isEmpty()) {
			lastVisited = votePq.poll();
			return new Action(ACTION.UNCOVER, lastVisited.x, lastVisited.y);
		}
		// Queue<TwoTuple> votePq = new PriorityQueue<>(2,voteComparator);
		// if (votePq.size() == 1 && safeToVisitCounter >= safeToVisit.size()) {
		// lastVisited = votePq.poll();
		// return new Action(ACTION.FLAG, lastVisited.x, lastVisited.y);
		// }
		// if (votePq.size() > 1 && safeToVisitCounter >= safeToVisit.size()) {
		// lastVisited = votePq.poll();
		// return new Action(ACTION.UNCOVER, lastVisited.x, lastVisited.y);
		// }
		// }
		// if (safeToVisitCounter < safeToVisit.size()) {
		// lastVisited = safeToVisit.get(safeToVisitCounter++);
		// return new Action(ACTION.UNCOVER, lastVisited.x, lastVisited.y);
		// }
		return new Action(ACTION.LEAVE);
	}

	private void setNumberOfMines(TwoTuple lastVisited, int number) {
		if (board[lastVisited.x][lastVisited.y].noOfNeighboringMines == Integer.MAX_VALUE) {
			board[lastVisited.x][lastVisited.y].noOfNeighboringMines = number;

		} else {
			board[lastVisited.x][lastVisited.y].noOfNeighboringMines += number;
		}
	}

	private void printboard(TwoTuple[][] board2) {
		for (int i = 0; i < board2.length; i++) {
			for (int j = 0; j < board2.length; j++) {
				int noToPrint = board[i][j].noOfNeighboringMines;
				if (noToPrint == Integer.MAX_VALUE)
					noToPrint = -1;
				System.out.printf("%-5d", noToPrint);
			}
			System.out.println();
		}

	}

	private void markNeighboursSafe(TwoTuple cell) {
		int currX2 = cell.x;
		int currY2 = cell.y;
		markSafe(currX2, currY2);
		markSafe(currX2 - 1, currY2 - 1);
		markSafe(currX2 - 1, currY2);
		markSafe(currX2 - 1, currY2 + 1);
		markSafe(currX2, currY2 + 1);
		markSafe(currX2 + 1, currY2 + 1);
		markSafe(currX2 + 1, currY2);
		markSafe(currX2 + 1, currY2 - 1);
		markSafe(currX2, currY2 - 1);

	}

	private void markSafe(int x, int y) {
		if (x < rowNum && y >= 1 && x >= 1 && y < colNum && !safeToVisit.contains(board[x][y]) && !board[x][y].visited
				&& !board[x][y].flagged)
			safeToVisit.add(board[x][y]);
	}

	// ################### Helper Functions Go Here (optional) ##################
	// ...

}
