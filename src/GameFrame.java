/*
 * John McCrummen
 * CS 3160
 * OO Project
 * 
 * -GameFrame.java-
 * main frame that game is played on/mine field is developed on
 * 
 * implements MouseListener to allow for right-clicking to flag a cell
 */
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class GameFrame implements MouseListener {

	// global variables
	public int numRows = 8; // numRows and numMines public and not static so Options can access and change them
	public int numMines = 16;
	MineCell[][] cells; // double array to hold attributes of cells in the mine field
	private JFrame frame; // refers to frame of game
	OptionsFrame optionsWindow = new OptionsFrame();
	Color originalButtonColor; // used to revert cell back to original state when un-flagging

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameFrame window = new GameFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GameFrame() {
		initialize(numRows,numMines);
	}//end constructor
	
	public GameFrame(int rows, int mines){
		initialize(rows,mines);
	}//end constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(int rows, int mines) {
		frame = new JFrame();
		frame.setBounds(frame.getX(), frame.getY(), 45*rows, 30*rows);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Minesweeper");
		//frame.setResizable(false);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenuItem newGameMenuItem = new JMenuItem("New Game");
		MenuActionListener menuListener = new MenuActionListener();
		newGameMenuItem.addActionListener(menuListener);
		menuBar.add(newGameMenuItem);
		
		JMenuItem optionsMenuItem = new JMenuItem("Options");
		optionsMenuItem.addActionListener(menuListener);
		menuBar.add(optionsMenuItem);
		
		frame.setVisible(true);
		numRows = rows;
		numMines = mines;
		
		//create grid of buttons
		makeCells();
		createGrid();
		placeMines();
		
		//showValues(); //for testing only
	}//end initialize
	
	private void makeCells(){
		cells = new MineCell[numRows][numRows+1];
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numRows+1; j++){
					cells[i][j] = new MineCell(i,j);
					frame.getContentPane().add(cells[i][j]);
			}//end for
		}//end for
		// only way I could find out how to get original background color was to save it from the beginning
		originalButtonColor = cells[0][0].getBackground();
	
		// for some inexplicable reason it always made the last button the full size of the screen
		// this was the only way I could see around it (adding one more row than necessary and removing)
		for (int x = 0; x < numRows; x++){
			cells[x][numRows].setVisible(false);
			cells[x][numRows].removeMouseListener(this);
		}//end for
	}//end makeCells

	private void createGrid(){ // this function can be called to refresh grid if in future want to make cells resize with window
		int cellwidth = frame.getWidth() / numRows - 1;
		int cellheight = frame.getHeight() / numRows - 1;
		
		//enlarge frame to ensure no cells get cut off
		int xBound = 45*numRows+cellwidth/2;
		int yBound = 30*numRows+cellheight*2;
		frame.setBounds(frame.getX(), frame.getY(), xBound, yBound);
		
		// add cells to grid
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numRows+1; j++){
				// determine position and size of new cell
				cells[i][j].setBounds(i * cellwidth, j * cellheight, cellwidth - 1, cellheight - 1);
				cells[i][j].addMouseListener(this);
			}//end for
		}//end for
		
	}//end createGrid
	
	// function to recognize when a cell has been clicked
	public void mouseClicked(MouseEvent e)
	{
		MineCell src = (MineCell) e.getSource();
		int i = src.getIndexI();
		int j = src.getIndexJ();
		
		if (SwingUtilities.isRightMouseButton(e)) // flag cell red when right-clicked to suggest mine
		{
			if (src.getBackground() == originalButtonColor && src.isEnabled())
				src.setBackground(Color.RED);
			else
				src.setBackground(originalButtonColor);
			return; // don't treat right-click as anything else
		}//end if
		
		if (src.getBackground() == Color.RED) // don't allow player to click a flagged cell
			return;
		
		// show cell clicked and test for mine
		if (src.getValue() == -1){ // hit mine
			src.setText("!");
			src.setEnabled(false);
			gameOver();
			return;
		} 
		else{ // avoided mine, show value of cell
			src.setText(String.valueOf(src.getValue()));
			src.setEnabled(false);
		}//end if/else
				
		// display values of all adjacent cells when cell clicked is zero
		if (src.getValue() == 0)		
			checkAdjacent(i,j);
		
		if (isWinner()){ // see if player has won
			JOptionPane.showMessageDialog(frame, "Nice! You're a true sweeper of mines.", "You Won!", JOptionPane.INFORMATION_MESSAGE);
			newGame();
		}//end if
	}//end mouseClicked
	
	private void placeMines(){
		int counter = numMines;
		while (counter > 0){
			int rand1 = (int) (Math.random() * numRows);
			int rand2 = (int) (Math.random() * numRows);
			if (cells[rand1][rand2].getValue() != -1){
				cells[rand1][rand2].setValue(-1);
				// alter surrounding cells accordingly
				// check above left
				if (rand1 > 0 && rand2 > 0 && cells[rand1-1][rand2-1].getValue() != -1)
					cells[rand1-1][rand2-1].setValue(cells[rand1-1][rand2-1].getValue()+1);
				// check above
				if (rand2 > 0 && cells[rand1][rand2-1].getValue() != -1)
					cells[rand1][rand2-1].setValue(cells[rand1][rand2-1].getValue()+1);
				// check above right
				if (rand1 < numRows-1 && rand2 > 0 && cells[rand1+1][rand2-1].getValue() != -1)
					cells[rand1+1][rand2-1].setValue(cells[rand1+1][rand2-1].getValue()+1);
				// check right
				if (rand1 < numRows-1 && cells[rand1+1][rand2].getValue() != -1)
					cells[rand1+1][rand2].setValue(cells[rand1+1][rand2].getValue()+1);
				// check below right
				if (rand1 < numRows-1 && rand2 < numRows-1 && cells[rand1+1][rand2+1].getValue() != -1)
					cells[rand1+1][rand2+1].setValue(cells[rand1+1][rand2+1].getValue()+1);
				// check below
				if (rand2 < numRows-1 && cells[rand1][rand2+1].getValue() != -1)
					cells[rand1][rand2+1].setValue(cells[rand1][rand2+1].getValue()+1);
				// check below left
				if (rand1 > 0 && rand2 < numRows-1 && cells[rand1-1][rand2+1].getValue() != -1)	
					cells[rand1-1][rand2+1].setValue(cells[rand1-1][rand2+1].getValue()+1);
				// check left
				if (rand1 > 0 && cells[rand1-1][rand2].getValue() != -1)
					cells[rand1-1][rand2].setValue(cells[rand1-1][rand2].getValue()+1);
				
				counter--;
			}//end if
			// when test case fails, it means cell was already a mine so counter doesn't decrement instead it tries again
		}//end while
	}//end placeMines
	
	private void checkAdjacent(int i, int j){
		int currentCellValue = cells[i][j].getValue();
		// check above left
		if (i > 0 && j > 0 && cells[i-1][j-1].getValue() != -1){
			cells[i-1][j-1].setText(String.valueOf(cells[i-1][j-1].getValue()));
			if (currentCellValue == 0 && cells[i-1][j-1].getValue() == 0 && cells[i-1][j-1].isEnabled()){ //check recursively when cell is 0
				cells[i-1][j-1].setEnabled(false); // prevents infinite loop
				checkAdjacent(i-1,j-1);
			}//end if
			cells[i-1][j-1].setEnabled(false);
		}//end if
		// check above
		if (j > 0 && cells[i][j-1].getValue() != -1){
			cells[i][j-1].setText(String.valueOf(cells[i][j-1].getValue()));
			if (currentCellValue == 0 && cells[i][j-1].getValue() == 0 && cells[i][j-1].isEnabled()){ //check recursively when cell is 0
				cells[i][j-1].setEnabled(false); // prevents infinite loop
				checkAdjacent(i,j-1);
			}//end if
			cells[i][j-1].setEnabled(false);
		}//end if
		// check above right
		if (i < numRows-1 && j > 0 && cells[i+1][j-1].getValue() != -1){
			cells[i+1][j-1].setText(String.valueOf(cells[i+1][j-1].getValue()));
			if (currentCellValue == 0 && cells[i+1][j-1].getValue() == 0 && cells[i+1][j-1].isEnabled()){ //check recursively when cell is 0
				cells[i+1][j-1].setEnabled(false); // prevents infinite loop
				checkAdjacent(i+1,j-1);
			}//end if
			cells[i+1][j-1].setEnabled(false);
		}//end if
		// check right
		if (i < numRows-1 && cells[i+1][j].getValue() != -1){
			cells[i+1][j].setText(String.valueOf(cells[i+1][j].getValue()));
			if (currentCellValue == 0 && cells[i+1][j].getValue() == 0 && cells[i+1][j].isEnabled()){ //check recursively when cell is 0
				cells[i+1][j].setEnabled(false); // prevents infinite loop
				checkAdjacent(i+1,j);
			}//end if
			cells[i+1][j].setEnabled(false);
		}//end if
		// check below right
		if (i < numRows-1 && j < numRows-1 && cells[i+1][j+1].getValue() != -1){
			cells[i+1][j+1].setText(String.valueOf(cells[i+1][j+1].getValue()));
			if (currentCellValue == 0 && cells[i+1][j+1].getValue() == 0 && cells[i+1][j+1].isEnabled()){ //check recursively when cell is 0
				cells[i+1][j+1].setEnabled(false); // prevents infinite loop
				checkAdjacent(i+1,j+1);
			}//end if
			cells[i+1][j+1].setEnabled(false);
		}//end if
		// check below
		if (j < numRows-1 && cells[i][j+1].getValue() != -1){
			cells[i][j+1].setText(String.valueOf(cells[i][j+1].getValue()));
			if (currentCellValue == 0 && cells[i][j+1].getValue() == 0 && cells[i][j+1].isEnabled()){ //check recursively when cell is 0
				cells[i][j+1].setEnabled(false); // prevents infinite loop
				checkAdjacent(i,j+1);
			}//end if
			cells[i][j+1].setEnabled(false);
		}//end if
		// check below left
		if (i > 0 && j < numRows-1 && cells[i-1][j+1].getValue() != -1){
			cells[i-1][j+1].setText(String.valueOf(cells[i-1][j+1].getValue()));
			if (currentCellValue == 0 && cells[i-1][j+1].getValue() == 0 && cells[i-1][j+1].isEnabled()){ //check recursively when cell is 0
				cells[i-1][j+1].setEnabled(false); // prevents infinite loop
				checkAdjacent(i-1,j+1);
			}//end if
			cells[i-1][j+1].setEnabled(false);
		}//end if
		// check left
		if (i > 0 && cells[i-1][j].getValue() != -1){
			cells[i-1][j].setText(String.valueOf(cells[i-1][j].getValue()));
			if (currentCellValue == 0 && cells[i-1][j].getValue() == 0 && cells[i-1][j].isEnabled()){ //check recursively when cell is 0
				cells[i-1][j].setEnabled(false); // prevents infinite loop
				checkAdjacent(i-1,j);
			}//end if
			cells[i-1][j].setEnabled(false);
		}//end if
	}//end checkAdjacent
	
	private void gameOver(){
		showMines(); // let player see where they screwed up
		JOptionPane.showMessageDialog(frame, "Whoops! Looks like you hit a mine.", "Game Over", JOptionPane.ERROR_MESSAGE);
		newGame(); // reset grid
	}//end gameOver
	
	private void showMines(){
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numRows; j++){
				if (cells[i][j].getValue() == -1)
					cells[i][j].setText("!");
			}//end for
		}//end for
	}//end showMines
	
	public void newGame(){
		frame.dispose();
		initialize(numRows,numMines);
	}//end newGame
	
	private boolean isWinner(){
		// check all cells to see if they have all been uncovered
		for (int i = 0; i < numRows; i++)
		{
			for (int j = 0; j < numRows; j++){
				if (cells[i][j].isEnabled() && cells[i][j].getValue() != -1) // cell that hasn't been discovered and is not a mine
					return false;
			}//end for
		}//end for
		
		// once checked all and found no undiscovered cells
		return true;
	}//end isWinner
	
	public class MenuActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JMenuItem src = (JMenuItem)e.getSource();
			switch (src.getText()){
			case "New Game":
				newGame();
				break;
			case "Options":
				optionsWindow.setVisible(true);
				optionsWindow.gameWindow = frame;
				break;
			}//end switch
		}//end actionPerformed
	}//end MenuActionListener

	// The following functions were required for a MouseListener but not needed in my case:
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
}//end class
