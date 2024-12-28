import java.awt.*;

public class Cell {
    //content of this cell (empty, cross, nought)
	Player content;
	//row and column of this cell
	int row, col;

	//Constants for Colours
	Color playerOnePrimary = Color.decode("#2196f3"); //Blue
	Color playerTwoPrimary = Color.decode("#f50057"); //Red


	/** Constructor to initialise this cell with the specified row and col */
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		clear();//Set Content to Empty
	}
	

	/** Paint itself on the graphics canvas, given the Graphics context g */ 
	public void paint(Graphics g) {
		//Graphics2D allows setting of pen's stroke size
		Graphics2D graphic2D = (Graphics2D) g;
		graphic2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphic2D.setStroke(new BasicStroke(GameMain.SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		//draw the symbol in the position
		int x1 = col * GameMain.CELL_SIZE + GameMain.CELL_PADDING;
		int y1 = row * GameMain.CELL_SIZE + GameMain.CELL_PADDING;
		if (content == Player.Cross) {
			graphic2D.setColor(playerOnePrimary);
			int x2 = (col + 1) * GameMain.CELL_SIZE - GameMain.CELL_PADDING;
			int y2 = (row + 1) * GameMain.CELL_SIZE - GameMain.CELL_PADDING;
			graphic2D.drawLine(x1, y1, x2, y2);
			graphic2D.drawLine(x2, y1, x1, y2);
		} else if (content == Player.Nought) {
			graphic2D.setColor(playerTwoPrimary);
			graphic2D.drawOval(x1, y1, GameMain.SYMBOL_SIZE, GameMain.SYMBOL_SIZE);
		}
	}
	
	/** Set this cell's content to EMPTY */
	public void clear() {
		this.content = Player.Empty;
	}
		
}
