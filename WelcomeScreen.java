package game;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class WelcomeScreen{
	// Declaration of GUI components
	static JFrame window;
	JPanel panel;
	JButton newGame;
	JButton howToPlay;
	BufferedImage titleImage;
	JLabel title;
	
	public WelcomeScreen(){
		window = new JFrame();												// Created JFrame
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);				// Exits the application when close is clicked
		panel = new JPanel(new GridLayout(0, 1, 0, 20));					// Created the JPanel
		panel.setBackground(Color.black);									// Makes the window background black
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));	// Adds spacing below the JPanel
		
		try{titleImage = ImageIO.read(new File("Resources/title.png"));		// Gets the title image
		}catch(IOException e){System.err.println("Error loading image.");}	// Produces an error if the image cannot be retrieved
		title = new JLabel(new ImageIcon(titleImage));						// Creates a JLabel with the image
		panel.add(title);													// Adds the label to the JPanel
		
		newGame = new JButton("START");										// Creates a button to start an easy game
		newGame.setFont(new Font("", Font.BOLD, 25));
		newGame.setBackground(Color.lightGray);
		newGame.addActionListener(new newGame());							// Executes the class when the button is clicked
		panel.add(newGame);													// Adds the button to the JPanel
		
		howToPlay = new JButton("How to Play".toUpperCase());				// Creates a button for instructions
		howToPlay.setFont(new Font("", Font.BOLD, 25));
		howToPlay.setBackground(Color.lightGray);
		howToPlay.addActionListener(new howToPlay());						// Executes the class when the button is clicked
		panel.add(howToPlay);												// Adds the button to the JPanel
		
		window.setContentPane(panel);										// Adds the JPanel to the window
		window.setSize(300, 350);											// Sets the size of the window
		window.setResizable(false);											// Does not allow the window to be resized
		window.setVisible(true);											// Makes the window visible
	}

	public static void main(String args[]){
		JFrame.setDefaultLookAndFeelDecorated(true);						// Gives the GUI java characteristics
		new WelcomeScreen();												// Creates the GUI
	}
	
	public static void close(){window.dispose();}							// Closes the window
	
	class newGame implements ActionListener{
		public void actionPerformed(ActionEvent e){
			newGame.setEnabled(true);										// Disables the buttons
			JFrame.setDefaultLookAndFeelDecorated(true);					// Gives the GUI java characteristics
			new Game();														// Creates an object of the game class
		}
	}
	
	class howToPlay implements ActionListener{
		public void actionPerformed(ActionEvent e){
			// Displays a message dialog with instructions for playing the game
			JOptionPane.showMessageDialog(null, "Stack the blocks to form solid rows.\nLeft and right arrow keys shift blocks, up arrow key rotates, and holding down arrow key increases speed.\nPoints are given after each block and for clearing rows. Multiple rows at a time produce more points.\nThe speed of the blocks increase as the game progresses.\nWhen a stack of blocks reaches the top, the game is lost.", "How to Play", JOptionPane.PLAIN_MESSAGE);
		}
	}
}
