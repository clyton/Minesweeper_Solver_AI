
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
import java.util.PriorityQueue;
import java.util.Queue;

import src.Action.ACTION;

public class MyAI extends AI {

	public static Comparator<TwoTuple> voteComparator = new Comparator<TwoTuple>() {
		@Override
		public int compare(TwoTuple t1, TwoTuple t2) {
			return t1.votes - t2.votes;
		}
	};

	private class TwoTuple {
		public int x;
		public int y;
		public int votes = 0;

		public TwoTuple(int x, int y) {
			this.x = x;
			this.y = y;

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
	}

	private static final int SAFE = -1;
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
	@SuppressWarnings("unchecked")

	int rowNum = 0;
	int colNum = 0;
	int currX = 0;
	int currY = 0;
	int totalMines = 0;
	int[][] board;
	boolean[][] visited;
	ArrayList<TwoTuple> safeToVisit;
	TwoTuple lastVisited;
	boolean oneEncountered = false;
	int safeToVisitCounter;

	public MyAI(int rowDimension, int colDimension, int totalMines, int startX, int startY) {
		// ################### Implement Constructor (required) ####################
		rowNum = rowDimension + 1;
		colNum = colDimension + 1;
		this.currX = startX;
		this.currY = startY;
		this.totalMines = totalMines;
		board = new int[rowNum][colNum];
		visited = new boolean[rowNum][colNum];
		safeToVisit = new ArrayList<>(rowDimension * colDimension);
		safeToVisitCounter = 1;
		lastVisited = new TwoTuple(currX, currY);

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (i == 0 || j == 0) {
					if (i == 0)
						board[i][j] = j;

					if (j == 0) {
						board[i][j] = i;
					}
				} else {
					board[i][j] = -1;
				}
			}
		}

	}

	// ################## Implement getAction(), (required) #####################
	public Action getAction(int number) {

		board[lastVisited.x][lastVisited.y] = number;
//		printboard(board);
		visited[lastVisited.x][lastVisited.y] = true;
		if (number == 0) {
			markNeighboursSafe(lastVisited.x, lastVisited.y);
		}
		if (safeToVisitCounter <= safeToVisit.size() - 1) {
			lastVisited = safeToVisit.get(safeToVisitCounter++);
			return new Action(ACTION.UNCOVER, lastVisited.x, lastVisited.y);
		} else {
			Queue<TwoTuple> votePq = new PriorityQueue<>(2,voteComparator);
			for (int i = 1; i < rowNum; i++) {
				for (int j = 1; j < colNum; j++) {
					if (!visited[i][j] && !safeToVisit.contains(new TwoTuple(i, j))) {
						boolean hasAtleastOneSafeNeighbour = false;
						if (i - 1 >= 1 && j - 1 >= 1 && board[i - 1][ j - 1] == 0) {
							safeToVisit.add((new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
						}
						if (i - 1 >= 1 && j >= 1 && board[i - 1][ j] == 0) {
							safeToVisit.add((new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
						}
						if (i - 1 >= 1 && j + 1 < colNum && board[i - 1][ j + 1]== 0 ) {
							safeToVisit.add((new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
						}
						if (i >= 1 && j + 1 < colNum && board[i][ j + 1] == 0){
							safeToVisit.add((new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
						}
						if (i + 1 < rowNum && j + 1 < colNum && board[i + 1][ j + 1]== 0 ) {
							safeToVisit.add((new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
						}
						if (i + 1 < rowNum && j < colNum && board[i + 1][ j]== 0 ) {
							safeToVisit.add((new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
						}
						if (i + 1 < rowNum && j - 1 >= 1 && board[i + 1][ j - 1]== 0 ) {
							safeToVisit.add((new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
						}
						if (i < rowNum && j - 1 >= 1 && board[i][ j - 1]== 0 ) {
							safeToVisit.add((new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
						}

						if (!hasAtleastOneSafeNeighbour) {
							int add = 0;
							if (i - 1 >= 1 && j - 1 >= 1 && visited[i - 1][j - 1])
								add += board[i - 1][j - 1];
							if (i - 1 >= 1 && j >= 1 && visited[i - 1][j])
								add += board[i - 1][j];
							if (i - 1 >= 1 && j + 1 < colNum && visited[i - 1][j + 1])
								add += board[i - 1][j + 1];
							if (i >= 1 && j + 1 < colNum && visited[i][j + 1])
								add += board[i][j + 1];
							if (i + 1 < rowNum && j + 1 < colNum && visited[i + 1][j + 1])
								add += board[i + 1][j + 1];
							if (i + 1 < rowNum && j < colNum && visited[i + 1][j])
								add += board[i + 1][j];
							if (i + 1 < rowNum && j - 1 >= 1 && visited[i + 1][j - 1])
								add += board[i + 1][j - 1];
							if (i < rowNum && j - 1 >= 1 && visited[i][j - 1])
								add += board[i][j - 1];
							TwoTuple unsafeTuple = new TwoTuple(i, j);
							unsafeTuple.votes = add;
							votePq.add(unsafeTuple);
						}
					}
				}
			}
			if (votePq.size() == 1 && safeToVisitCounter >= safeToVisit.size()) {
				lastVisited = votePq.poll();
				return new Action(ACTION.FLAG, lastVisited.x, lastVisited.y);
			}
			if (votePq.size() > 1 && safeToVisitCounter >= safeToVisit.size()) {
				lastVisited = votePq.poll();
				return new Action(ACTION.UNCOVER, lastVisited.x, lastVisited.y);
			}
		}
		if (safeToVisitCounter < safeToVisit.size()) {
			lastVisited = safeToVisit.get(safeToVisitCounter++);
			return new Action(ACTION.UNCOVER, lastVisited.x, lastVisited.y);
		}
		return new Action(ACTION.LEAVE);
	}

	private void printboard(int[][] board2) {
		for (int i = 0; i < board2.length; i++) {
			for (int j = 0; j < board2.length; j++) {
				System.out.printf("%-5d", board[i][j]);
			}
			System.out.println();
		}

	}

	private void markNeighboursSafe(int currX2, int currY2) {
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
		if (x < rowNum && y >= 1 && x >= 1 && y < colNum && !safeToVisit.contains(new TwoTuple(x, y)))
			safeToVisit.add(new TwoTuple(x, y));
	}

// ################### Helper Functions Go Here (optional) ##################
// ...

}
