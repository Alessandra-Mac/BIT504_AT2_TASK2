import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class GameMain extends JPanel implements MouseListener{
	//Constants for game 
	// number of ROWS by COLS cell constants 
	public static final int ROWS = 3;
	public static final int COLS = 3;
	public static final String TITLE = "Tic Tac Toe";

	//constants for dimensions used for drawing
	//cell width and height
	public static final int CELL_SIZE = 100;
	//drawing canvas
	public static final int CANVAS_WIDTH = CELL_SIZE * COLS;
	public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
	//Noughts and Crosses are displayed inside a cell, with padding from border
	public static final int CELL_PADDING = CELL_SIZE / 6;
	public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
	public static final int SYMBOL_STROKE_WIDTH = 8;

	/*declare game object variables*/
	// the game board 
	private Board board;
	private JPanel boardPanel;
	private GameState currentState;
	// the current player
	private Player currentPlayer;
	// for displaying game status message
	private JLabel statusBar;

	//Scores for Players
	private int scoreX = 0;
	private int scoreO = 0;

	//Labels for scores
	private JLabel scoreLabelX;
	private JLabel scoreLabelO;


	/** Constructor to set up the UI and game components on the panel */
	public GameMain() {
		addMouseListener(this);


		// Set up the status bar (JLabel) to display status message
		statusBar = new JLabel("         ");
		statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));
		statusBar.setOpaque(true);
		statusBar.setBackground(Color.LIGHT_GRAY);

		// Restart Button
		JButton restartButton = new JButton("Restart");
		restartButton.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initGame();
				repaint();
			}
		});

		//Set Board Panel for canvas
		boardPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				//fill background and set colour to white
				super.paintComponent(g);
				setBackground(Color.WHITE);
				//ask the game board to paint itself
				board.paint(g);
				//set status bar message
				if (currentState == GameState.Playing) {
					statusBar.setForeground(Color.BLACK);
					if (currentPlayer == Player.Cross) {
						statusBar.setText("X's Turn");
					} else {
						statusBar.setText("O's Turn");
					}
				} else if (currentState == GameState.Draw) {
					statusBar.setForeground(Color.RED);
					statusBar.setText("It's a Draw! Click to play again.");
				} else if (currentState == GameState.Cross_won) {
					statusBar.setForeground(Color.RED);
					statusBar.setText("'X' Won! Click to play again.");
				} else if (currentState == GameState.Nought_won) {
					statusBar.setForeground(Color.RED);
					statusBar.setText("'O' Won! Click to play again.");
				}
			}
		};
		//North Panel
		scoreLabelX = new JLabel("X: 0");
		scoreLabelX.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));

		scoreLabelO = new JLabel("O: 0");
		scoreLabelO.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setPreferredSize(new Dimension(CANVAS_WIDTH, 50));

		northPanel.add(scoreLabelX,BorderLayout.EAST);
		northPanel.add(restartButton,BorderLayout.CENTER);
		northPanel.add(scoreLabelO,BorderLayout.WEST);

		//layout of the panel is in border layout
		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(boardPanel, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);

		// account for statusBar height in overall height
		setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT + 80));
		board = new Board();
		initGame();
	}

	public static void main(String[] args) {
		// Run GUI code in Event Dispatch thread for thread safety.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//create a main window to contain the panel
				JFrame frame = new JFrame(TITLE);
				GameMain gamePanel = new GameMain();
				frame.add(gamePanel);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}


	/** Initialise the game-board contents and the current status of GameState and Player */
	public void initGame() {
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				// all cells empty
				board.cells[row][col].content = Player.Empty;
			}
		}
		currentState = GameState.Playing;
		currentPlayer = Player.Cross;
	}

	/**After each turn check to see if the current player hasWon by putting their symbol in that position,
	 * If they have the GameState is set to won for that player
	 * If no winner then isDraw is called to see if deadlocked, if not GameState stays as PLAYING
	 *
	 */
	public void updateGame(Player thePlayer, int row, int col) {
		//check for win after play
		if(board.hasWon(thePlayer, row, col)) {
			currentState = (thePlayer == Player.Cross) ? GameState.Cross_won : GameState.Nought_won;
			if (thePlayer == Player.Cross) {
				scoreX++;
				scoreLabelX.setText("X: " + scoreX);
			}
			else{
				scoreO++;
				scoreLabelO.setText("O:" + scoreO);
			}
		} else if (board.isDraw ()) {
			currentState = GameState.Draw;
		}
		//otherwise no change to current state of playing
	}



	/** Event handler for the mouse click on the JPanel. If selected cell is valid and Empty then current player is added to cell content.
	 *  UpdateGame is called which will call the methods to check for winner or Draw. if none then GameState remains playing.
	 *  If win or Draw then call is made to method that resets the game board,  Finally a call is made to refresh the canvas so that new symbol appears*/

	public void mouseClicked(MouseEvent e) {
		// Get the coordinates of where the click event happened
		int mouseX = e.getX();
		int mouseY = e.getY();

		// Calculate the height of the north panel (score labels + status bar)
		int northPanelHeight = boardPanel.getLocation().y; // Get exact position of boardPanel

		// Adjust the mouseY coordinate relative to the board panel
		int mouseYAdjusted = mouseY - northPanelHeight;

		// Check if the click is within the grid bounds
		if (mouseX >= 0 && mouseX < CANVAS_WIDTH && mouseYAdjusted >= 0 && mouseYAdjusted < CANVAS_HEIGHT) {
			// Get the row and column clicked
			int rowSelected = mouseYAdjusted / CELL_SIZE;
			int colSelected = mouseX / CELL_SIZE;

			if (currentState == GameState.Playing) {
				if (board.cells[rowSelected][colSelected].content == Player.Empty) {
					// Make the move
					board.cells[rowSelected][colSelected].content = currentPlayer;
					// Update the currentState
					updateGame(currentPlayer, rowSelected, colSelected);
					// Switch player
					currentPlayer = (currentPlayer == Player.Cross) ? Player.Nought : Player.Cross;
				}
			} else {
				// Game over; restart the game
				initGame();
			}
			repaint();
		}
	}




	@Override
	public void mousePressed(MouseEvent e) {
		//  Auto-generated, event not used

	}
	@Override
	public void mouseReleased(MouseEvent e) {
		//  Auto-generated, event not used

	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// Auto-generated,event not used

	}
	@Override
	public void mouseExited(MouseEvent e) {
		// Auto-generated, event not used

	}

}