
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

import src.Action.ACTION;

public class MyAI extends AI {
	private class TwoTuple {
		public int x;
		public int y;

		public TwoTuple(int x, int y) {
			this.x = x;
			this.y = y;
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
		rowNum = rowDimension;
		colNum = colDimension;
		this.currX = startX-1;
		this.currY = startY-1;
		this.totalMines = totalMines;
		board = new int[rowNum][colNum];
		visited = new boolean[rowNum][colNum];
		safeToVisit = new ArrayList<>(rowDimension * colDimension);
		safeToVisitCounter = 0;

	}

	// ################## Implement getAction(), (required) #####################
	public Action getAction(int number) {

		board[currX][currY] = number;
		visited[currX][currY] = true;
		if (number == 0) {
			markNeighboursSafe(currX, currY);
		}
		if(safeToVisitCounter != safeToVisit.size() - 1 ){
			lastVisited = safeToVisit.get(safeToVisitCounter);
			return new Action(ACTION.UNCOVER, lastVisited.x+1, lastVisited.y+1);
		}
		else {
			int qindex = safeToVisitCounter;
			for(int i=0; i<rowNum ; i++) {
				for (int j= 0; j< colNum; j++) {
					boolean hasAtleastOneSafeNeighbour = false;
					if(!visited[i][j]) {
						if (i-1>=0 && j-1>=0 && safeToVisit.contains(new TwoTuple(i-1,j-1)))
							{
							safeToVisit.add(qindex++, (new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
							}
						if (i-1>=0 && j>=0 && safeToVisit.contains(new TwoTuple(i-1, j)))
							{
							safeToVisit.add(qindex++, (new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
							}
						if (i-1>=0 && j+1< colNum && safeToVisit.contains(new TwoTuple(i-1, j+1)))
							{
							safeToVisit.add(qindex++, (new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
							}
						if (i>=0 && j+1<colNum && safeToVisit.contains(new TwoTuple(i, j+1)))
							{
							safeToVisit.add(qindex++, (new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
							}
						if (i+1 < rowNum && j+1<colNum && safeToVisit.contains(new TwoTuple(i+1, j+1)))
							{
							safeToVisit.add(qindex++, (new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
							}
						if (i+1 < rowNum && j<colNum && safeToVisit.contains(new TwoTuple(i+1, j)))
							{
							safeToVisit.add(qindex++, (new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
							}
						if (i+1 < rowNum && j-1 >= 0 && safeToVisit.contains(new TwoTuple(i+1, j-1)))
							{
							safeToVisit.add(qindex++, (new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
							}
						if (i < rowNum && j-1>=0 && safeToVisit.contains(new TwoTuple(i, j-1)))
							{
							safeToVisit.add(qindex++, (new TwoTuple(i, j)));
							hasAtleastOneSafeNeighbour = true;
							continue;
							}

						
					}
					if(!hasAtleastOneSafeNeighbour) {
						return new Action(ACTION.FLAG, i+1, j+1);
					}
				}
			}
		}
		if (safeToVisitCounter < safeToVisit.size()) {
			lastVisited = safeToVisit.get(safeToVisitCounter);
			return new Action(ACTION.UNCOVER, lastVisited.x+1, lastVisited.y+1);
		}
		return new Action(ACTION.LEAVE);
	}

	private void markNeighboursSafe(int currX2, int currY2) {
		safeToVisit.add(new TwoTuple(currX, currY)); safeToVisitCounter++;
		if (currX - 1 < rowNum && currY - 1 > 0 && !visited[currX - 1][currY - 1])
			safeToVisit.add(new TwoTuple(currX - 1, currY - 1));

		if (currX - 1 > 0 && !visited[currX - 1][currY])
			safeToVisit.add(new TwoTuple(currX - 1, currY));

		if (currX - 1 < rowNum && currY + 1 < colNum && !visited[currX - 1][currY + 1])
			safeToVisit.add(new TwoTuple(currX - 1, currY + 1));

		if (currY + 1 < colNum && !visited[currX][currY + 1]) {
			safeToVisit.add(new TwoTuple(currX, currY + 1));
		}

		if (currX + 1 < rowNum && currY + 1 < colNum && !visited[currX + 1][currY + 1])
			safeToVisit.add(new TwoTuple(currX + 1, currY + 1));

		if (currX + 1 < rowNum && !visited[currX + 1][currY])
			safeToVisit.add(new TwoTuple(currX + 1, currY));

		if (currX + 1 < rowNum && currY - 1 < colNum && !visited[currX + 1][currY - 1])
			safeToVisit.add(new TwoTuple(currX + 1, currY - 1));

		if (currY - 1 < colNum && !visited[currX][currY - 1]) {
			safeToVisit.add(new TwoTuple(currX, currY - 1));
		}

	}

// ################### Helper Functions Go Here (optional) ##################
// ...

}