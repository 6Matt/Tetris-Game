package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game implements KeyListener{
	JFrame window;
	JPanel panel;
	JLabel[][] blocks = new JLabel[10][18];
	BufferedImage[] imgSquares = new BufferedImage[8];
	int[][] colours = new int[10][18];
	ArrayList<Integer> xMoving = new ArrayList<Integer>();
	ArrayList<Integer> yMoving = new ArrayList<Integer>();
	int currentColour = 0;
	int blockOrientation = 0;
	int timerInterval = 600;
	Timer move;
	boolean lost = false;
	boolean faster = false;
	JLabel level1;
	JLabel score1;
	JLabel linesCleared1;
	int level = 1;
	int score = 0;
	int linesCleared = 0;
	int levelCounter = 0;
	
	public Game(){
		WelcomeScreen.close();
		
		window = new JFrame("TETRIS");								// Creates the JFrame
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// Exits game when close is clicked
		window.addKeyListener(this);								// Adds key listener to window
		
		panel = new JPanel(new GridBagLayout());					// Creates the JPanel
		panel.setBackground(Color.black);							// Changes background colour
		
		// Retrieves images and prints errors if they are encountered
		try {imgSquares[0] = ImageIO.read(new File("Resources/background.png"));} catch (IOException e) {e.printStackTrace();}
		try {imgSquares[1] = ImageIO.read(new File("Resources/blue.png"));} catch (IOException e) {e.printStackTrace();}
		try {imgSquares[2] = ImageIO.read(new File("Resources/green.png"));} catch (IOException e) {e.printStackTrace();}
		try {imgSquares[3] = ImageIO.read(new File("Resources/lightBlue.png"));} catch (IOException e) {e.printStackTrace();}
		try {imgSquares[4] = ImageIO.read(new File("Resources/orange.png"));} catch (IOException e) {e.printStackTrace();}
		try {imgSquares[5] = ImageIO.read(new File("Resources/purple.png"));} catch (IOException e) {e.printStackTrace();}
		try {imgSquares[6] = ImageIO.read(new File("Resources/red.png"));} catch (IOException e) {e.printStackTrace();}
		try {imgSquares[7] = ImageIO.read(new File("Resources/yellow.png"));} catch (IOException e) {e.printStackTrace();}
		
		
		GridBagConstraints b = new GridBagConstraints();			// Creates constraints for the JPanel
		for(int row = 0; row <= 17; row++){
			for(int column = 0; column <= 9; column++){
				blocks[column][row] = new JLabel(new ImageIcon(imgSquares[0]));	// Creates a grey square
				colours[column][row] = 0;							// Changes integer to 0, representing grey
				b.gridx = column;									// Sets the position
				b.gridy = row;										// Sets the position
				b.weightx = 0;										// Keeps the grid together when resized
				b.weighty = 0;										// Keeps the grid together when resized
				panel.add(blocks[column][row], b);					// Adds label to panel
			}
		}
		
		// Creates and adds label for level
		JLabel level0 = new JLabel("Level");
		level0.setForeground(Color.lightGray);
		level0.setFont(new Font("", Font.PLAIN, 16));
		level0.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		GridBagConstraints l = new GridBagConstraints();
		l.gridx = 10;
		l.gridy = 3;
		l.gridheight = 1;
		l.gridwidth = 3;
		l.weightx = 1;
		l.weighty = 1;
		l.anchor = GridBagConstraints.CENTER;
		l.insets = new Insets(0, 40, 0, 40);
		panel.add(level0, l);
		
		// Creates and adds label for level
		level1 = new JLabel("1");
		level1.setForeground(Color.lightGray);
		level1.setFont(new Font("", Font.BOLD, 16));
		level1.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		l.gridy = 4;
		panel.add(level1, l);
		
		// Creates and adds label for score
		JLabel score0 = new JLabel("Score");
		score0.setForeground(Color.lightGray);
		score0.setFont(new Font("", Font.PLAIN, 16));
		score0.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		l.gridy = 8;
		panel.add(score0, l);
		
		// Creates and adds label for score
		score1 = new JLabel("0");
		score1.setForeground(Color.lightGray);
		score1.setFont(new Font("", Font.BOLD, 16));
		score1.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		l.gridy = 9;
		panel.add(score1, l);
		
		// Creates and adds label for lines clears
		JLabel linesCleared0 = new JLabel("Lines Cleared");
		linesCleared0.setForeground(Color.lightGray);
		linesCleared0.setFont(new Font("", Font.PLAIN, 16));
		linesCleared0.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		l.gridy = 13;
		panel.add(linesCleared0, l);
		
		// Creates and adds label for lines cleared
		linesCleared1 = new JLabel("0");
		linesCleared1.setForeground(Color.lightGray);
		linesCleared1.setFont(new Font("", Font.BOLD, 16));
		linesCleared1.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		l.gridy = 14;
		panel.add(linesCleared1, l);
		
		// Calls the method handling block movement
		move();
		
		// Finalizes and displays window
		window.setContentPane(panel);
		window.pack();
		window.setResizable(false);
		window.setVisible(true);
	}
	
	public void move(){
		// Creates a new block if there is no block currently moving
		if(xMoving.isEmpty() == true){newBlock();}
		
		// Checks for blocks that have reached the bottom
		if(yMoving.get(0) == 17 || yMoving.get(1) == 17 || yMoving.get(2) == 17 || yMoving.get(3) == 17){
			for(int x = 0; x <= 3; x++){colours[xMoving.get(x)][yMoving.get(x)] = currentColour;}
			xMoving.clear();
			yMoving.clear();
			score = score + 10;
			score1.setText(Integer.toString(score));
		}
		// Moves the block down
		else if(((yMoving.get(0) + 1 >= 0 && colours[xMoving.get(0)][yMoving.get(0) + 1] == 0) || yMoving.get(0) + 1 < 0) && ((yMoving.get(1) + 1 >= 0 && colours[xMoving.get(1)][yMoving.get(1) + 1] == 0) || yMoving.get(1) + 1 < 0) && ((yMoving.get(2) + 1 >= 0 && colours[xMoving.get(2)][yMoving.get(2) + 1] == 0) || yMoving.get(2) + 1 < 0) && ((yMoving.get(3) + 1 >= 0 && colours[xMoving.get(3)][yMoving.get(3) + 1] == 0) || yMoving.get(3) + 1 < 0)){
			for(int x = 0; x <= 3; x++){
				yMoving.add(yMoving.get(x) + 1);
			}
			for(int x = 0; x <= 3; x++){
				if(yMoving.get(0) >= 0){blocks[xMoving.get(x)][yMoving.get(0)].setIcon(new ImageIcon(imgSquares[0]));}
				yMoving.remove(0);
			}
			for(int x = 0; x <= 3; x++){if(yMoving.get(x) >= 0){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}}
		}
		// Checks if the game has been lost
		else if((yMoving.get(0) + 1 == 0 && colours[xMoving.get(0)][yMoving.get(0) + 1] != 0) || (yMoving.get(1) + 1 == 0 && colours[xMoving.get(1)][yMoving.get(1) + 1] != 0) || (yMoving.get(2) + 1 == 0 && colours[xMoving.get(2)][yMoving.get(2) + 1] != 0) || (yMoving.get(3) + 1 == 0 && colours[xMoving.get(3)][yMoving.get(3) + 1] != 0)){
			lost = true;
			int playAgain = JOptionPane.showConfirmDialog(null, "You lost the game. Would you like to play again?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(playAgain == 0){
				window.dispose();
				new Game();
			}
			else{System.exit(0);}
		}
		// Checks if the block cannot move
		else{
			for(int x = 0; x <= 3; x++){if(yMoving.get(x) >= 0){colours[xMoving.get(x)][yMoving.get(x)] = currentColour;}}
			xMoving.clear();
			yMoving.clear();
			score = score + 10;
			score1.setText(Integer.toString(score));
		}
		
		ArrayList<Integer> rowsToClear = new ArrayList<Integer>();
		
		// Checks for any complete rows
		for(int r = 0; r <= 17; r++){
			if(colours[0][r] != 0 && colours[1][r] != 0 && colours[2][r] != 0 && colours[3][r] != 0 && colours[4][r] != 0 && colours[5][r] != 0 && colours[6][r] != 0 && colours[7][r] != 0 && colours[8][r] != 0 && colours[9][r] != 0){
				rowsToClear.add(r);
			}
		}
		
		// Clears the rows that were found
		if(rowsToClear.isEmpty() == false){	
			score = score + (100 * rowsToClear.size()) + (100 * (rowsToClear.size() - 1));
			score1.setText(Integer.toString(score));
			
			linesCleared = linesCleared + rowsToClear.size();
			linesCleared1.setText(Integer.toString(linesCleared));
			
			levelCounter = levelCounter + rowsToClear.size();
			if(levelCounter >= 10){
				levelCounter = 0;
				level++;
				level1.setText(Integer.toString(level));
				if(timerInterval > 20){timerInterval = timerInterval - 25;}
			}
			
			do{
				for(int row = rowsToClear.get(0); row >= 1; row--){
					for(int column = 0; column <= 9; column++){
						colours[column][row] = colours[column][row - 1];
						blocks[column][row].setIcon(new ImageIcon(imgSquares[colours[column][row]]));
					}
				}
				for(int column = 0; column <= 9; column++){
					colours[column][0] = 0;
					blocks[column][0].setIcon(new ImageIcon(imgSquares[0]));
				}
				rowsToClear.remove(0);
			}while(rowsToClear.isEmpty() == false);
		}
		
		// Calls the method again after specified time has passed
		if(lost == false){
			move = new Timer();										// Creates the timer
			if(faster == false){move.schedule(new TimerTask() {public void run(){move();}}, timerInterval);}
			else{move.schedule(new TimerTask() {public void run(){move();}}, (timerInterval / 5));}
		}	
	}
	
	public void newBlock(){
		// Chooses a random block
		int type = 0;
		do{type = new Random().nextInt(7) + 0;}while(type + 1 == currentColour);
		
		// Places the new block
		blockOrientation = 0;
		switch(type){
		case 0:	currentColour = 1;
				xMoving.add(4);	yMoving.add(0 - 1);
				xMoving.add(3);	yMoving.add(0 - 1);
				xMoving.add(5);	yMoving.add(0 - 1);
				xMoving.add(6);	yMoving.add(0 - 1);
				break;
		case 1:	currentColour = 2;
				xMoving.add(4);	yMoving.add(0 - 2);		
				xMoving.add(5);	yMoving.add(0 - 1);
				xMoving.add(5);	yMoving.add(0 - 2);
				xMoving.add(3);	yMoving.add(0 - 2);
				break;
		case 2:	currentColour = 3;
				xMoving.add(4);	yMoving.add(0 - 2);		
				xMoving.add(3);	yMoving.add(0 - 1);
				xMoving.add(3);	yMoving.add(0 - 2);
				xMoving.add(5);	yMoving.add(0 - 2);
				break;
		case 3:	currentColour = 4;
				xMoving.add(4);	yMoving.add(0 - 1);
				xMoving.add(5);	yMoving.add(0 - 1);
				xMoving.add(4);	yMoving.add(0 - 2);
				xMoving.add(5);	yMoving.add(0 - 2);
				break;
		case 4:	currentColour = 5;
				xMoving.add(4);	yMoving.add(0 - 1);		
				xMoving.add(3);	yMoving.add(0 - 1);
				xMoving.add(4);	yMoving.add(0 - 2);
				xMoving.add(5);	yMoving.add(0 - 2);
				break;
		case 5:	currentColour = 6;
				xMoving.add(4);	yMoving.add(0 - 1);		
				xMoving.add(3);	yMoving.add(0 - 1);
				xMoving.add(5);	yMoving.add(0 - 1);
				xMoving.add(4);	yMoving.add(0 - 2);
				break;
		case 6:	currentColour = 7;
				xMoving.add(4);	yMoving.add(0 - 1);
				xMoving.add(5);	yMoving.add(0 - 1);
				xMoving.add(3);	yMoving.add(0 - 2);
				xMoving.add(4);	yMoving.add(0 - 2);
				break;
		}
		
	}

	public void keyPressed(KeyEvent e) {
		if(lost == false){
		if(e.getKeyCode() == 37 && xMoving.isEmpty() == false && xMoving.get(0) != 0 && xMoving.get(1) != 0 && xMoving.get(2) != 0 && xMoving.get(3) != 0){
			if(((yMoving.get(0) >= 0 && colours[xMoving.get(0) - 1][yMoving.get(0)] == 0) || yMoving.get(0) < 0) && ((yMoving.get(1) >= 0 && colours[xMoving.get(1) - 1][yMoving.get(1)] == 0) || yMoving.get(1) < 0) && ((yMoving.get(2) >= 0 && colours[xMoving.get(2) - 1][yMoving.get(2)] == 0) || yMoving.get(2) < 0) && ((yMoving.get(3) >= 0 && colours[xMoving.get(3) - 1][yMoving.get(3)] == 0) || yMoving.get(3) < 0)){
				for(int x = 0; x <= 3; x++){
					xMoving.add(xMoving.get(x) - 1);
				}
				for(int x = 0; x <= 3; x++){
					if(yMoving.get(x) >= 0){blocks[xMoving.get(0)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[0]));}
					xMoving.remove(0);
				}
				for(int x = 0; x <= 3; x++){if(yMoving.get(x) >= 0){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}}
			}
		}
		else if(e.getKeyCode() == 39 && xMoving.isEmpty() == false && xMoving.get(0) != 9 && xMoving.get(1) != 9 && xMoving.get(2) != 9 && xMoving.get(3) != 9){
			if(((yMoving.get(0) >= 0 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0) || yMoving.get(0) < 0) && ((yMoving.get(1) >= 0 && colours[xMoving.get(1) + 1][yMoving.get(1)] == 0) || yMoving.get(1) < 0) && ((yMoving.get(2) >= 0 && colours[xMoving.get(2) + 1][yMoving.get(2)] == 0) || yMoving.get(2) < 0) && ((yMoving.get(3) >= 0 && colours[xMoving.get(3) + 1][yMoving.get(3)] == 0) || yMoving.get(3) < 0)){
				for(int x = 0; x <= 3; x++){
					xMoving.add(xMoving.get(x) + 1);
				}
				for(int x = 0; x <= 3; x++){
					if(yMoving.get(x) >= 0){blocks[xMoving.get(0)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[0]));}
					xMoving.remove(0);
				}
				for(int x = 0; x <= 3; x++){if(yMoving.get(x) >= 0){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}}
			}
		}
		else if(e.getKeyCode() == 38 && xMoving.isEmpty() == false){
			if(yMoving.get(0) > 0 && yMoving.get(0) <= 15 && currentColour == 1 && blockOrientation == 0 && colours[xMoving.get(0)][yMoving.get(0) - 1] == 0 && colours[xMoving.get(0)][yMoving.get(0) + 1] == 0 && colours[xMoving.get(0)][yMoving.get(0) + 2] == 0){
				blockOrientation = 1;
				xMoving.add(xMoving.get(0));	yMoving.add(yMoving.get(0) - 1);
				xMoving.add(xMoving.get(0));	yMoving.add(yMoving.get(0) + 1);
				xMoving.add(xMoving.get(0));	yMoving.add(yMoving.get(0) + 2);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(xMoving.get(0) != 0 && xMoving.get(0) <= 7 && currentColour == 1 && blockOrientation == 1 && colours[xMoving.get(0) - 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) + 2][yMoving.get(0)] == 0){
				blockOrientation = 0;
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) + 2);	yMoving.add(yMoving.get(0));
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(yMoving.get(0) > 0 && currentColour == 2 && blockOrientation == 0 && colours[xMoving.get(0)][yMoving.get(0) - 1] == 0 && colours[xMoving.get(0)][yMoving.get(0) + 1] == 0 && colours[xMoving.get(0) - 1][yMoving.get(0) + 1] == 0){
				blockOrientation = 1;
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) - 1);
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) + 1);
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0) + 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(xMoving.get(0) != 9 && currentColour == 2 && blockOrientation == 1 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) - 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) - 1][yMoving.get(0) - 1] == 0){
				blockOrientation = 2;
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0) - 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(yMoving.get(0) != 17 && currentColour == 2 && blockOrientation == 2 && colours[xMoving.get(0)][yMoving.get(0) + 1] == 0 && colours[xMoving.get(0)][yMoving.get(0) - 1] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0) - 1] == 0){
				blockOrientation = 3;
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) + 1);
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) - 1);
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0) - 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(xMoving.get(0) != 0 && currentColour == 2 && blockOrientation == 3 && colours[xMoving.get(0) - 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0) + 1] == 0){
				blockOrientation = 0;
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0) + 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(yMoving.get(0) > 0 && currentColour == 3 && blockOrientation == 0 && colours[xMoving.get(0)][yMoving.get(0) - 1] == 0 && colours[xMoving.get(0)][yMoving.get(0) + 1] == 0 && colours[xMoving.get(0) - 1][yMoving.get(0) - 1] == 0){
				blockOrientation = 1;
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) - 1);
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) + 1);
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0) - 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(yMoving.get(0) > 0 && currentColour == 3 && blockOrientation == 1 && colours[xMoving.get(0) - 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0) - 1] == 0){
				blockOrientation = 2;
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0) - 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(yMoving.get(0) != 17 && currentColour == 3 && blockOrientation == 2 && colours[xMoving.get(0)][yMoving.get(0) - 1] == 0 && colours[xMoving.get(0)][yMoving.get(0) + 1] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0) + 1] == 0){
				blockOrientation = 3;
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) - 1);
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) + 1);
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0) + 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(xMoving.get(0) != 0 && currentColour == 3 && blockOrientation == 3 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) - 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) - 1][yMoving.get(0) + 1] == 0){
				blockOrientation = 0;
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0) + 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(yMoving.get(0) > 0 && yMoving.get(0) != 17 && currentColour == 5 && blockOrientation == 0 && colours[xMoving.get(0)][yMoving.get(0) - 1] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0) + 1] == 0){
				blockOrientation = 1;
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) - 1);
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0) + 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(yMoving.get(0) > 0 && xMoving.get(0) != 0 && currentColour == 5 && blockOrientation == 1 && colours[xMoving.get(0)][yMoving.get(0) - 1] == 0 && colours[xMoving.get(0) - 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0) - 1] == 0){
				blockOrientation = 0;
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) - 1);
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0) - 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(yMoving.get(0) > 0 && yMoving.get(0) != 17 && currentColour == 6 && blockOrientation == 0 && colours[xMoving.get(0)][yMoving.get(0) + 1] == 0 && colours[xMoving.get(0)][yMoving.get(0) - 1] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0){
				blockOrientation = 1;
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) + 1);
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) - 1);
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0));
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(xMoving.get(0) != 0 && currentColour == 6 && blockOrientation == 1 && colours[xMoving.get(0) - 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0 && colours[xMoving.get(0)][yMoving.get(0) + 1] == 0){
				blockOrientation = 2;
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) + 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(currentColour == 6 && blockOrientation == 2 && colours[xMoving.get(0)][yMoving.get(0) - 1] == 0 && colours[xMoving.get(0)][yMoving.get(0) + 1] == 0 && colours[xMoving.get(0) - 1][yMoving.get(0)] == 0){
				blockOrientation = 3;
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) - 1);
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) + 1);
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0));
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(xMoving.get(0) != 9 && currentColour == 6 && blockOrientation == 3 && colours[xMoving.get(0) - 1][yMoving.get(0)] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0 && colours[xMoving.get(0)][yMoving.get(0) - 1] == 0){
				blockOrientation = 0;
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) - 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(yMoving.get(0) > 0 && yMoving.get(0) != 17 && currentColour == 7 && blockOrientation == 0 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0 && colours[xMoving.get(0)][yMoving.get(0) + 1] == 0 && colours[xMoving.get(0) + 1][yMoving.get(0) - 1] == 0){
				blockOrientation = 1;
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) + 1);
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0) - 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
			else if(yMoving.get(0) > 0 && xMoving.get(0) != 0 && currentColour == 7 && blockOrientation == 1 && colours[xMoving.get(0) + 1][yMoving.get(0)] == 0 && colours[xMoving.get(0)][yMoving.get(0) - 1] == 0 && colours[xMoving.get(0) - 1][yMoving.get(0) - 1] == 0){
				blockOrientation = 0;
				xMoving.add(xMoving.get(0) + 1);	yMoving.add(yMoving.get(0));
				xMoving.add(xMoving.get(0));		yMoving.add(yMoving.get(0) - 1);
				xMoving.add(xMoving.get(0) - 1);	yMoving.add(yMoving.get(0) - 1);
				for(int x = 1; x <= 3; x++){
					blocks[xMoving.get(1)][yMoving.get(1)].setIcon(new ImageIcon(imgSquares[0]));
					xMoving.remove(1);	yMoving.remove(1);
				}
				for(int x = 0; x <= 3; x++){blocks[xMoving.get(x)][yMoving.get(x)].setIcon(new ImageIcon(imgSquares[currentColour]));}
			}
		}
		else if(e.getKeyCode() == 40){faster = true;}
		}
	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == 40){faster = false;}
	}

	public void keyTyped(KeyEvent arg0) {}
}
