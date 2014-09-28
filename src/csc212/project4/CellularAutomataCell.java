package csc212.project4;

import android.graphics.Color;

// Cellular Automata are comprised of individual cells on a graph that create a complex image. This class will be used as a
// data structure to store a single CA cell, which is comprised of a (graph) row, a (graph) column, and a color (white or black).

public class CellularAutomataCell {

	private int cellRow;
	private int cellColumn;
	private int cellColor;
	
	// Constructor--initialize our class variables
	public CellularAutomataCell()
	{

		cellRow = 0;
		cellColumn = 0;
		cellColor = Color.WHITE;

	}

	// Getters and setters for our class variables.

	/**
	 * Return the value of cellRow.
	 */
	public int getCellRow() {
		return cellRow;
	}

	/**
	 * Set cellRow to the value passed into the method.
	 */

	public void setCellRow(int cellRow) {
		this.cellRow = cellRow;
	}

	/**
	 * return the value of cellColumn
	 */
	public int getCellColumn() {
		return cellColumn;
	}

	/**
	 * Set cellColumn to the value passed into the method.
	 */

	public void setCellColumn(int cellColumn) {
		this.cellColumn = cellColumn;
	}

	/**
	 * Return the value of cellColor.
	 */

	public int getCellColor() {
		return cellColor;
	}

	/**
	 * Set cellColor to the value passed into the method.
	 */

	public void setCellColor(int cellColor) {
		this.cellColor = cellColor;
	}
}