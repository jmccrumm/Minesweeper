/*
 * John McCrummen
 * CS 3160
 * OO Project
 * 
 * -MineCell.java-
 * object class that extends JButton to allow for indexes and value
 * (mine/num representing adjacent mine count) to be kept track of
 * 
 * can possibly be extended to include references of adjacent cells
 * that will more sufficiently check surroundings
 */

import javax.swing.JButton;


public class MineCell extends JButton {
	
	
	private int value; // value of -1 denotes a mine, other values will represent # of adjacent mines
	private int index_i; // indexes held to check adjacent buttons to the one clicked
	private int index_j;
	
	public int getValue(){
		return value;
	}
	
	public void setValue(int input){
		value = input;
	}
	
	public int getIndexI(){
		return index_i;
	}
	
	public int getIndexJ(){
		return index_j;
	}
	
	MineCell(int i, int j){
		index_i = i;
		index_j = j;
		value = 0;
	}

}
