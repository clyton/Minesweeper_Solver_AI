
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import src.Action.ACTION;

public class MyAI extends AI {

	public static Comparator<TwoTuple> voteComparator = new Comparator<TwoTuple>() {
		@Override
		public int compare(TwoTuple t1, TwoTuple t2) {
			return (int) Math.ceil((t1.votes - t2.votes));
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
	// 2) You MUST implement the getAction() method which has a single
	// parameter,
	// number. If your most recent move is an Action.UNCOVER action, this value
	// will
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
	HashSet<TwoTuple> flaggedMines = new HashSet<>();

	private int coveredTiles = 0;

	public MyAI(int rowDimension, int colDimension, int totalMines, int startX,
			int startY) {
		// ################### Implement Constructor (required)
		// ####################
		rowNum = colDimension + 1;
		colNum = rowDimension + 1;
		this.currX = startX;
		this.currY = startY;
		this.totalMines = totalMines;
		board = new TwoTuple[rowNum][colNum];
		// visited = new boolean[rowNum][colNum];
		safeToVisit = new TreeSet<TwoTuple>();
		// safeToVisitCounter = 1;
		coveredTiles = (rowNum - 1) * (colNum - 1) - 1;

		for (int i = 0; i < rowNum; i++) {
			for (int j = 0; j < colNum; j++) {
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

	// ################## Implement getAction(), (required)
	// #####################
	public Action getAction(int number) {

		Queue<TwoTuple> votePq = new PriorityQueue<>(10, voteComparator);
//		minePq.removeIf(cell -> cell.noOfNeighboringMines==0 && cell.visited);
		setNumberOfMines(lastVisited, number);
		lastVisited.visited = true;
//		printboard(board);
		// visited[lastVisited.x][lastVisited.y] = true;
		number = lastVisited.noOfNeighboringMines;

//		if (totalMines == 0) {
//			for (int i = 1; i < rowNum; i++) {
//				for (int j = 1; j < colNum; j++) {
//					if (!board[i][j].visited && !board[i][j].flagged)
//						markSafe(i, j);
//				}
//			}
//		}
//
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
			return uncover(lastVisited);
		} else {
//			System.out.println("Safe to visit is empty");
			HashSet<TwoTuple> tilesToFlag = new HashSet<>();
			for (TwoTuple cell : minePq) { // minePq operations

//					long countOfCoveredCells = cell.getNeighbors().stream().
//							filter(celli -> celli.visited == false)
//							.count();

				int countOfCoveredCells = 0;
				ArrayList<TwoTuple> cellsNeighbors = cell.getNeighbors();
				for (TwoTuple celli : cellsNeighbors) {
					if ((celli.visited == false) && (celli.flagged == false)) {
						countOfCoveredCells++;
					}
				}

				if (cell.noOfNeighboringMines == countOfCoveredCells) {
					tilesToFlag.addAll(cell.getNeighbors().stream()
							.filter(icell -> !icell.visited && !icell.flagged)
							.collect(Collectors.toList()));
				}

			}
			flagTiles(tilesToFlag);
		}

		findPatterns();
		if (!safeToVisit.isEmpty()) {
			Iterator<TwoTuple> setIterator = safeToVisit.iterator();
			lastVisited = setIterator.next();
			safeToVisit.remove(lastVisited);
			return uncover(lastVisited);
		}

//		if (isThereAFreeCorner() != null) {
//			lastVisited = isThereAFreeCorner();
//			return uncover(lastVisited);
//		}

		for (int i = 1; i < rowNum; i++) {
			for (int j = 1; j < colNum; j++) {
				TwoTuple cell = board[i][j];
				if (cell.visited == false && cell.flagged == false) {
					List<TwoTuple> nonFlaggedVisitedNeighbors = cell
							.getNeighbors().stream()
							.filter(icell -> icell.visited && !icell.flagged)
							.collect(Collectors.toList());
					if (nonFlaggedVisitedNeighbors.isEmpty())
						continue;

					double sum = 0.0;
					for (TwoTuple twoTuple : nonFlaggedVisitedNeighbors) {
						sum += twoTuple.noOfNeighboringMines;
					}
					cell.votes = sum / nonFlaggedVisitedNeighbors.size();
					votePq.add(cell);
				}
			}
		}

		if (!votePq.isEmpty()) {
			lastVisited = votePq.poll();
			for (TwoTuple twoTuple : votePq) {
				twoTuple.votes = 0.0;
			}
			votePq.clear();
			return uncover(lastVisited);
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

	private void findPatterns() {

		/**
		 * -1 i 1 -1 * * * 1 2 1 O O O
		 * 
		 * * * * * 1 2 2 1 0 0 0 0
		 * 
		 */

		HashSet<TwoTuple> tilesToFlag = new HashSet();

		// 3 grid patterns
		for (int i = 2; i < rowNum - 1; i++) {
			for (int j = 2; j < colNum - 1; j++) {
				// 121
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i - 1][j].noOfNeighboringMines == 1
						&& board[i + 1][j].noOfNeighboringMines == 1
						&& !board[i][j - 1].visited
						&& !board[i - 1][j - 1].visited
						&& !board[i + 1][j - 1].visited
						&& board[i][j + 1].visited
						&& board[i - 1][j + 1].visited
						&& board[i + 1][j + 1].visited) {
					tilesToFlag.add(board[i - 1][j - 1]);
					tilesToFlag.add(board[i + 1][j - 1]);
				}
				// 121
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i - 1][j].noOfNeighboringMines == 1
						&& board[i + 1][j].noOfNeighboringMines == 1
						&& board[i][j - 1].visited
						&& board[i - 1][j - 1].visited
						&& board[i + 1][j - 1].visited
						&& !board[i][j + 1].visited
						&& !board[i - 1][j + 1].visited
						&& !board[i + 1][j + 1].visited) {
					tilesToFlag.add(board[i - 1][j + 1]);
					tilesToFlag.add(board[i + 1][j + 1]);
				}

				// 121
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i][j - 1].noOfNeighboringMines == 1
						&& board[i][j + 1].noOfNeighboringMines == 1
						&& board[i - 1][j].visited
						&& board[i - 1][j - 1].visited
						&& board[i - 1][j + 1].visited
						&& !board[i + 1][j].visited
						&& !board[i + 1][j - 1].visited
						&& !board[i + 1][j + 1].visited) {
					tilesToFlag.add(board[i + 1][j - 1]);
					tilesToFlag.add(board[i + 1][j + 1]);
				}
				// 121
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i][j - 1].noOfNeighboringMines == 1
						&& board[i][j + 1].noOfNeighboringMines == 1
						&& !board[i - 1][j].visited
						&& !board[i - 1][j - 1].visited
						&& !board[i - 1][j + 1].visited
						&& board[i + 1][j].visited
						&& board[i + 1][j - 1].visited
						&& board[i + 1][j + 1].visited) {
					tilesToFlag.add(board[i - 1][j - 1]);
					tilesToFlag.add(board[i - 1][j + 1]);
				}

				// 12*
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i - 1][j].noOfNeighboringMines == 1
						&& board[i + 1][j].visited && !board[i][j - 1].visited
						&& !board[i - 1][j - 1].visited
						&& !board[i + 1][j - 1].visited
						&& board[i][j + 1].visited
						&& board[i - 1][j + 1].visited
						&& board[i + 1][j + 1].visited) {
					tilesToFlag.add(board[i + 1][j - 1]);
				}

				// 12*
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i - 1][j].noOfNeighboringMines == 1
						&& board[i + 1][j].visited && board[i][j - 1].visited
						&& board[i - 1][j - 1].visited
						&& board[i + 1][j - 1].visited
						&& !board[i][j + 1].visited
						&& !board[i - 1][j + 1].visited
						&& !board[i + 1][j + 1].visited) {
					tilesToFlag.add(board[i + 1][j + 1]);
				}

				// 12*
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i + 1][j].noOfNeighboringMines == 1
						&& board[i - 1][j].visited
						&& board[i - 1][j - 1].visited
						&& board[i][j - 1].visited
						&& board[i + 1][j - 1].visited
						&& !board[i - 1][j + 1].visited
						&& !board[i][j + 1].visited
						&& !board[i + 1][j + 1].visited) {
					tilesToFlag.add(board[i - 1][j + 1]);
				}
				// 12*
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i + 1][j].noOfNeighboringMines == 1
						&& board[i - 1][j].visited
						&& !board[i - 1][j - 1].visited
						&& !board[i][j - 1].visited
						&& !board[i + 1][j - 1].visited
						&& board[i - 1][j + 1].visited
						&& board[i][j + 1].visited
						&& board[i + 1][j + 1].visited) {
					tilesToFlag.add(board[i - 1][j - 1]);
				}

				// 12*
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i][j - 1].noOfNeighboringMines == 1
						&& board[i][j + 1].visited
						&& board[i + 1][j - 1].visited
						&& board[i + 1][j].visited
						&& board[i + 1][j + 1].visited
						&& !board[i - 1][j - 1].visited
						&& !board[i - 1][j].visited
						&& !board[i - 1][j + 1].visited) {

					tilesToFlag.add(board[i - 1][j + 1]);
				}

				// 12*
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i][j - 1].noOfNeighboringMines == 1
						&& board[i][j + 1].visited
						&& !board[i + 1][j - 1].visited
						&& !board[i + 1][j].visited
						&& !board[i + 1][j + 1].visited
						&& board[i - 1][j - 1].visited
						&& board[i - 1][j].visited
						&& board[i - 1][j + 1].visited) {

					tilesToFlag.add(board[i + 1][j + 1]);
				}

				// 12*
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i][j + 1].noOfNeighboringMines == 1
						&& board[i][j - 1].visited
						&& !board[i + 1][j - 1].visited
						&& !board[i + 1][j].visited
						&& !board[i + 1][j + 1].visited
						&& board[i - 1][j - 1].visited
						&& board[i - 1][j].visited
						&& board[i - 1][j + 1].visited) {

					tilesToFlag.add(board[i + 1][j - 1]);
				}

				// 12*
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i][j + 1].noOfNeighboringMines == 1
						&& board[i][j - 1].visited
						&& board[i + 1][j - 1].visited
						&& board[i + 1][j].visited
						&& board[i + 1][j + 1].visited
						&& !board[i - 1][j - 1].visited
						&& !board[i - 1][j].visited
						&& !board[i - 1][j + 1].visited) {

					tilesToFlag.add(board[i - 1][j - 1]);
				}
			}
		} /**
			 * visited 1 2 2 1 horizontally unvisited
			 */
		for (int i = 2; i < rowNum - 2; i++) {
			for (int j = 2; j < colNum - 1; j++) {

				// 12 horizontal
				if (board[i][j].noOfNeighboringMines == 1
						&& board[i + 1][j].noOfNeighboringMines == 2
						&& board[i - 1][j].visited && board[i + 2][j].visited
						&& board[i - 1][j + 1].visited
						&& board[i][j + 1].visited
						&& board[i + 1][j + 1].visited
						&& board[i + 2][j + 1].visited
						&& !board[i - 1][j - 1].visited
						&& !board[i][j - 1].visited
						&& !board[i + 1][j - 1].visited
						&& !board[i + 2][j - 1].visited) {
					tilesToFlag.add(board[i + 2][j - 1]);
					markSafe(i - 1, j - 1);
				}

				// 12 horizontal
				if (board[i][j].noOfNeighboringMines == 1
						&& board[i + 1][j].noOfNeighboringMines == 2
						&& board[i - 1][j].visited && board[i + 2][j].visited
						&& board[i - 1][j - 1].visited
						&& board[i][j - 1].visited
						&& board[i + 1][j - 1].visited
						&& board[i + 2][j - 1].visited
						&& !board[i - 1][j + 1].visited
						&& !board[i][j + 1].visited
						&& !board[i + 1][j + 1].visited
						&& !board[i + 2][j + 1].visited) {
					tilesToFlag.add(board[i + 2][j + 1]);
					markSafe(i - 1, j + 1);
				}

//				horizontal 21
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i + 1][j].noOfNeighboringMines == 1
						&& board[i - 1][j].visited && board[i + 2][j].visited
						&& board[i - 1][j + 1].visited
						&& board[i][j + 1].visited
						&& board[i + 1][j + 1].visited
						&& board[i + 2][j + 1].visited
						&& !board[i - 1][j - 1].visited
						&& !board[i][j - 1].visited
						&& !board[i + 1][j - 1].visited
						&& !board[i + 2][j - 1].visited) {
					tilesToFlag.add(board[i - 1][j - 1]);
					markSafe(i + 2, j - 1);
				}

				// horizontal 21
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i + 1][j].noOfNeighboringMines == 1
						&& board[i - 1][j].visited && board[i + 2][j].visited
						&& board[i - 1][j - 1].visited
						&& board[i][j - 1].visited
						&& board[i + 1][j - 1].visited
						&& board[i + 2][j - 1].visited
						&& !board[i - 1][j + 1].visited
						&& !board[i][j + 1].visited
						&& !board[i + 1][j + 1].visited
						&& !board[i + 2][j + 1].visited) {
					tilesToFlag.add(board[i - 1][j + 1]);
					markSafe(i + 2, j + 1);
				}

				// horizontal 11
				if (board[i][j].noOfNeighboringMines == 1
						&& board[i + 1][j].noOfNeighboringMines == 1
						&& board[i - 1][j - 1].visited
						&& board[i - 1][j].visited
						&& board[i - 1][j + 1].visited
						&& board[i][j - 1].visited && board[i][j + 1].visited
						&& !board[i + 1][j - 1].visited
						&& !board[i + 1][j + 1].visited
						&& !board[i + 2][j].visited
						&& !board[i + 2][j - 1].visited
						&& !board[i + 2][j + 1].visited) {
					markSafe(i + 2, j - 1);
					markSafe(i + 2, j);
					markSafe(i + 2, j + 1);
				}

				// horizontal 11
				if (board[i][j].noOfNeighboringMines == 1
						&& board[i + 1][j].noOfNeighboringMines == 1
						&& !board[i - 1][j - 1].visited
						&& !board[i - 1][j].visited
						&& !board[i - 1][j + 1].visited
						&& !board[i][j - 1].visited && !board[i][j + 1].visited
						&& board[i + 1][j - 1].visited
						&& board[i + 1][j + 1].visited
						&& board[i + 2][j].visited
						&& board[i + 2][j - 1].visited
						&& board[i + 2][j + 1].visited) {
					markSafe(i - 1, j - 1);
					markSafe(i - 1, j);
					markSafe(i - 1, j + 1);
				}

//		  visited 1 2 2 1 horizontally unvisited

				if (board[i][j].noOfNeighboringMines == 2
						&& board[i - 1][j].noOfNeighboringMines == 1
						&& board[i + 1][j].noOfNeighboringMines == 2
						&& board[i + 2][j].noOfNeighboringMines == 1
						&& !board[i][j - 1].visited
						&& !board[i - 1][j - 1].visited
						&& !board[i + 1][j - 1].visited
						&& !board[i + 2][j - 1].visited
						&& board[i][j + 1].visited
						&& board[i - 1][j + 1].visited
						&& board[i + 1][j + 1].visited
						&& board[i + 2][j + 1].visited) {
					tilesToFlag.add(board[i][j - 1]);
					tilesToFlag.add(board[i + 1][j - 1]);
					markSafe(i - 1, j - 1);
					markSafe(i + 2, j - 1);
				}

				if (board[i][j].noOfNeighboringMines == 2
						&& board[i - 1][j].noOfNeighboringMines == 1
						&& board[i + 1][j].noOfNeighboringMines == 2
						&& board[i + 2][j].noOfNeighboringMines == 1
						&& board[i][j - 1].visited
						&& board[i - 1][j - 1].visited
						&& board[i + 1][j - 1].visited
						&& board[i + 2][j - 1].visited
						&& !board[i][j + 1].visited
						&& !board[i - 1][j + 1].visited
						&& !board[i + 1][j + 1].visited
						&& !board[i + 2][j + 1].visited) {
					tilesToFlag.add(board[i][j + 1]);
					tilesToFlag.add(board[i + 1][j + 1]);
					markSafe(i - 1, j + 1);
					markSafe(i + 2, j + 1);
				}
			}
		}

		for (int i = 2; i < rowNum - 1; i++) {
			for (int j = 2; j < colNum - 2; j++) {

				// 11 vertical
				if (board[i][j].noOfNeighboringMines == 1
						&& board[i][j + 1].noOfNeighboringMines == 1
						&& board[i][j - 1].visited
						&& board[i - 1][j - 1].visited
						&& board[i - 1][j].visited
						&& board[i + 1][j - 1].visited
						&& board[i + 1][j].visited
						&& !board[i - 1][j + 1].visited
						&& !board[i - 1][j + 2].visited
						&& !board[i][j + 2].visited
						&& !board[i + 1][j + 1].visited
						&& !board[i + 1][j + 2].visited) {
					markSafe(i - 1, j + 2);
					markSafe(i, j + 2);
					markSafe(i + 1, j + 2);
				}

				// 11 vertical change j->j+1 and j+2 -> j -1
				if (board[i][j + 1].noOfNeighboringMines == 1
						&& board[i][j].noOfNeighboringMines == 1
						&& board[i][j + 2].visited
						&& board[i - 1][j + 2].visited
						&& board[i - 1][j + 1].visited
						&& board[i + 1][j + 2].visited
						&& board[i + 1][j + 1].visited
						&& !board[i - 1][j].visited
						&& !board[i - 1][j - 1].visited
						&& !board[i][j - 1].visited && !board[i + 1][j].visited
						&& !board[i + 1][j - 1].visited) {
					markSafe(i - 1, j - 1);
					markSafe(i, j - 1);
					markSafe(i + 1, j - 1);
				}

//				// 12 vertical
//				if (board[i][j].noOfNeighboringMines == 1
//						&& board[i][j + 1].noOfNeighboringMines == 2
//						&& board[i][j - 1].visited && board[i][j + 2].visited
//						&& board[i + 1][j - 1].visited
//						&& board[i + 1][j].visited
//						&& board[i + 1][j + 1].visited
//						&& board[i + 1][j + 2].visited
//						&& !board[i - 1][j - 1].visited
//						&& !board[i - 1][j].visited
//						&& !board[i - 1][j + 1].visited
//						&& !board[i - 1][j + 2].visited) {
//					tilesToFlag.add(board[i - 1][j + 2]);
//					markSafe(i - 1, j - 1);
//				}
//
//				// 12 vertical
//				if (board[i][j].noOfNeighboringMines == 1
//						&& board[i][j + 1].noOfNeighboringMines == 2
//						&& board[i][j - 1].visited // && board[i + 2][j].visited
//						&& board[i][j + 2].visited
//						&& board[i - 1][j - 1].visited
//						&& board[i - 1][j].visited
//						&& board[i - 1][j + 1].visited
//						&& board[i - 1][j + 2].visited
//						&& !board[i + 1][j - 1].visited
//						&& !board[i + 1][j].visited
//						&& !board[i + 1][j + 1].visited
//						&& !board[i + 1][j + 2].visited) {
//					tilesToFlag.add(board[i + 1][j + 2]);
//					markSafe(i + 1, j - 1);
//				}
//
//				// 21 vertical
//				if (board[i][j].noOfNeighboringMines == 2
//						&& board[i][j + 1].noOfNeighboringMines == 1
//						&& board[i][j - 1].visited && board[i][j + 2].visited
//						&& board[i + 1][j - 1].visited
//						&& board[i + 1][j].visited
//						&& board[i + 1][j + 1].visited
//						&& board[i + 1][j + 2].visited
//						&& !board[i - 1][j - 1].visited
//						&& !board[i - 1][j].visited
//						&& !board[i - 1][j + 1].visited
//						&& !board[i - 1][j + 2].visited) {
//					markSafe(i - 1, j + 2);
//					tilesToFlag.add(board[i - 1][j - 1]);
//				}
//
//				// 21 vertical
//				if (board[i][j].noOfNeighboringMines == 2
//						&& board[i][j + 1].noOfNeighboringMines == 1
//						&& board[i][j - 1].visited // && board[i + 2][j].visited
//						&& board[i][j + 2].visited
//						&& board[i - 1][j - 1].visited
//						&& board[i - 1][j].visited
//						&& board[i - 1][j + 1].visited
//						&& board[i - 1][j + 2].visited
//						&& !board[i + 1][j - 1].visited
//						&& !board[i + 1][j].visited
//						&& !board[i + 1][j + 1].visited
//						&& !board[i + 1][j + 2].visited) {
//					tilesToFlag.add(board[i + 1][j - 1]);
//					markSafe(i + 1, j + 2);
//				}

				// 1221 vertically
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i][j - 1].noOfNeighboringMines == 1
						&& board[i][j + 1].noOfNeighboringMines == 2
						&& board[i][j + 2].noOfNeighboringMines == 1
						&& !board[i - 1][j].visited
						&& !board[i - 1][j - 1].visited
						&& !board[i - 1][j + 1].visited
						&& !board[i - 1][j + 2].visited
						&& board[i + 1][j].visited
						&& board[i + 1][j - 1].visited
						&& board[i + 1][j + 1].visited
						&& board[i + 1][j + 2].visited) {
					tilesToFlag.add(board[i - 1][j]);
					tilesToFlag.add(board[i - 1][j + 1]);
					markSafe(i - 1, j - 1);
					markSafe(i - 1, j + 2);
				}

				/** 1 2 2 1 vertically **/
				if (board[i][j].noOfNeighboringMines == 2
						&& board[i][j - 1].noOfNeighboringMines == 1
						&& board[i][j + 1].noOfNeighboringMines == 2
						&& board[i][j + 2].noOfNeighboringMines == 1
						&& board[i - 1][j].visited
						&& board[i - 1][j - 1].visited
						&& board[i - 1][j + 1].visited
						&& board[i - 1][j + 2].visited
						&& !board[i + 1][j].visited
						&& !board[i + 1][j - 1].visited
						&& !board[i + 1][j + 1].visited
						&& !board[i + 1][j + 2].visited) {
					tilesToFlag.add(board[i + 1][j]);
					tilesToFlag.add(board[i + 1][j + 1]);
					markSafe(i + 1, j - 1);
					markSafe(i + 1, j + 2);
				}

			}
		}

		if (!tilesToFlag.isEmpty()) {
			System.out.println(tilesToFlag);
			printboard(board);
		}

		flagTiles(tilesToFlag);
	}

	private void flagTiles(HashSet<TwoTuple> tilesToFlag) {
		for (TwoTuple eachTile : tilesToFlag) {
			if (flaggedMines.contains(eachTile))
				continue; // if already flagged ignore
			eachTile.flagged = true;
			totalMines--;
			eachTile.getNeighbors().forEach(n -> setNumberOfMines(n, -1));
			flaggedMines.add(eachTile);
		}

		for (int i = 1; i < rowNum; i++) {
			for (int j = 1; j < colNum; j++) {
				TwoTuple twoTuple = board[i][j];
				if (twoTuple.noOfNeighboringMines == 0) {
					markNeighboursSafe(twoTuple);
				}
			}
		}
	}

	private void flagTile(TwoTuple tileToFlag) {
		if (flaggedMines.contains(tileToFlag))
			return; // if already flagged ignore
		tileToFlag.flagged = true;
		tileToFlag.getNeighbors().forEach(n -> setNumberOfMines(n, -1));
		flaggedMines.add(tileToFlag);
		for (int i = 1; i < rowNum; i++) {
			for (int j = 1; j < colNum; j++) {
				TwoTuple twoTuple = board[i][j];
				if (twoTuple.noOfNeighboringMines == 0) {
					markNeighboursSafe(twoTuple);
				}
			}
		}
		totalMines--;
	}

	private Action flag(TwoTuple lastVisited2) {
		// TODO Auto-generated method stub
//		coveredTiles--;
		flaggedMines.add(lastVisited2);
		return new Action(ACTION.FLAG, lastVisited2.x, lastVisited2.y);
	}

	private Action uncover(TwoTuple lastVisited) {
		coveredTiles--;
		return new Action(ACTION.UNCOVER, lastVisited.x, lastVisited.y);
	}

	private TwoTuple isThereAFreeCorner() {
		TwoTuple[] corner = { board[1][1], board[1][colNum - 1],
				board[rowNum - 1][1], board[rowNum - 1][colNum - 1] };
		for (TwoTuple twoTuple : corner) {
			if (!twoTuple.visited && !twoTuple.flagged) {
				return twoTuple;
			}
		}
		return null;
	}

	private void setNumberOfMines(TwoTuple lastVisited, int number) {
		if (board[lastVisited.x][lastVisited.y].noOfNeighboringMines == Integer.MAX_VALUE) {
			board[lastVisited.x][lastVisited.y].noOfNeighboringMines = number;

		} else {
			board[lastVisited.x][lastVisited.y].noOfNeighboringMines += number;
		}
	}

	private void printboard(TwoTuple[][] board2) {
		for (int i = 0; i < rowNum; i++) {
			for (int j = 0; j < colNum; j++) {
				String no = "";
				int noToPrint = board[i][j].noOfNeighboringMines;
				if (noToPrint == Integer.MAX_VALUE)
					no = "#";
				if (board2[i][j].flagged)
					no = "F";
				if (board2[i][j].visited)
					no = String.valueOf(noToPrint);
				if (!board2[i][j].visited && !board[i][j].flagged
						&& noToPrint != Integer.MAX_VALUE)
					no = String.valueOf(noToPrint);

				System.out.printf("%-5s", no);
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
		if (x < rowNum && y >= 1 && x >= 1 && y < colNum
				&& !safeToVisit.contains(board[x][y]) && !board[x][y].visited
				&& !board[x][y].flagged)
			safeToVisit.add(board[x][y]);
	}

	// ################### Helper Functions Go Here (optional)
	// ##################
	// ...

}
