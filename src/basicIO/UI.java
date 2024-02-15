package basicIO;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**  Board GUI for implementation with various games
 *   Modified by 348929o5o
 */
 
// =============================MUST IMPLEMENT KEYLISTENER ====================
public class UI extends JPanel implements KeyListener
{
  private static final int X_DIM = 12;
  private static final int Y_DIM = 12;
  private static final int X_OFFSET = 30;
  private static final int Y_OFFSET = 30;
  private static final double MIN_SCALE = 0.25;
  private static final int GAP = 10;
  private static final int FONT_SIZE = 16;
   // THIS VARIABLE STORES THE UNICODE VALUE OF THE KEY THAT WAS PRESSED =======
  
  // Grid colours
  private static final Color GRID_COLOR_A = new Color(255,255,255);
  private static final Color SoakedFlour = new Color(220,180,0);
  private static final Color FlourColor = new Color(240,200,0);
  private static final Color WaterColor = new Color(180,120,0);
  
  // Preset colours for pieces
  private static final Color[] COLOURS = 
    {Color.YELLOW, Color.BLUE, Color.CYAN, Color.GREEN, 
     Color.PINK, Color.WHITE, Color.RED, Color.ORANGE };
  
  // String used to indicate each colour
  private static final String[] COLOUR_NAMES = 
  {"yellow", "blue", "cyan", "green", "pink", "white", "red", "orange"};
  
  // Colour to use if a match is not found
  private static Color default_color = Color.BLACK;
  
  private Color[][] grid;
                                 // to the board where the last click occurred
  private String message = "";
  private int numLines = 2;
  private int[][] line = new int[4][numLines];  // maximum number of lines is 100
  private int columns, rows;
  private boolean first = true;
  private int originalWidth;
  private int originalHeight;
  private double scale;
  
  /** A constructor to build a 2D board.
   */
  public UI (int rows, int cols)
  {
    super( true );
    // RENAME THE WINDOW HERE =================================================
    JFrame f = new JFrame( "BreadGame" );
    
    this.columns = cols;
    this.rows = rows;
    originalWidth = 2*X_OFFSET+X_DIM*cols;
    originalHeight = 2*Y_OFFSET+Y_DIM*rows+GAP+FONT_SIZE;
    
    this.setPreferredSize( new Dimension( originalWidth, originalHeight ) );
                                          
    f.setResizable(true);

    this.grid = new Color[cols][rows];
    this.setFocusable(true);
    // MUST ADD THE KEYLISTENER TO THE BOARD (JPANEL) =========================
    addKeyListener(this);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setContentPane( this );
    f.pack();
    f.setVisible(true);
    
  }
	public void keyPressed(KeyEvent e) {

	}
	/**
	 * Moves the left hand around the grid with WASD, and right with IJKL, O and P are for toggling dragging
	 */
	public void keyTyped(KeyEvent e) {
		switch(e.getKeyChar()) {
		case 'a':
			Flat.moveHand(true, 1);
			break;
		case 'd':
			Flat.moveHand(true, 2);
			break;
		case 'w':
			Flat.moveHand(true, 3);
			break;
		case 's':
			Flat.moveHand(true, 4);
			break;
		case 'j':
			Flat.moveHand(false, 1);
			break;
		case 'l':
			Flat.moveHand(false, 2);
			break;
		case 'i':
			Flat.moveHand(false, 3);
			break;
		case 'k':
			Flat.moveHand(false, 4);
			break;
		case 'q':
			Flat.grabLeft=!Flat.grabLeft;
			Flat.lastlx=Flat.leftX;
			Flat.lastly=Flat.leftY;
			break;
		case 'o':
			Flat.grabRight=!Flat.grabRight;
			Flat.lastrx=Flat.rightX;
			Flat.lastry=Flat.rightY;
			break;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		
	}
  private void paintText(Graphics g)
  {
    g.setColor( this.getBackground() );
    g.setFont(new Font(g.getFont().getFontName(), Font.ITALIC+Font.BOLD, (int)(Math.round(FONT_SIZE*scale))));
    
    int x = (int)Math.round(X_OFFSET*scale);
    int y = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale + GAP  ) ;
    
    g.fillRect(x,y, this.getSize().width, (int)Math.round(GAP+FONT_SIZE*scale) );
    g.setColor( Color.black );
    g.drawString(message, x, y + (int)Math.round(FONT_SIZE*scale));
  }
//Creates a popup of the instructions for how to use the game
  public static void Popup() {
	  JOptionPane.showMessageDialog(null, "Welcome to bread streatching simulator. The two dots represent your left and right hands."
              + "\nUse WASD to move your blue left hand, and toggle drag with key q."
              + "\nFor right use key o to drag and IJKL to move"
              + "\nThe lines in drag mode represent the distance the original point moves. Other points will be shifted based on bonds");
  }
  /**
   * paints the gridspaces accoriding to the type of object at that gridspace
   * Needs the Flat class to have a type array grid
   */
  private void paintGrid(Graphics g)
  {
	super.paintComponent(g);
	//goes through every index in the grid
    for (int i = 0; i < Flat.grid.length; i++)
    {
      for (int j = 0; j < Flat.grid[i].length; j++)
      {    
    	//Sets the color to FlourColor if it is flour, SoakedFlour if it is soaked flour WaterColor if water
    	//Otherwise, (can only be air), it is set to the default
        if (Flat.grid[i][j].getType()==3) {
        	g.setColor(FlourColor);
        }
        else if(Flat.grid[i][j].getType()==4)
    		g.setColor(SoakedFlour);
        else if(Flat.grid[i][j].getType()==1)
        	g.setColor(WaterColor);
        else
          g.setColor(GRID_COLOR_A);
        //I didn't change this
        int curX = (int)Math.round((X_OFFSET+X_DIM*i)*scale);
        int curY = (int)Math.round((Y_OFFSET+Y_DIM*j)*scale);
        int nextX = (int)Math.round((X_OFFSET+X_DIM*(i+1))*scale);
        int nextY = (int)Math.round((Y_OFFSET+Y_DIM*(j+1))*scale);
        int deltaX = nextX-curX; 
        int deltaY = nextY-curY;
                                   
        g.fillRect( curX, curY, deltaX, deltaY );
        Color curColour = this.grid[i][j];
        if (curColour != null) // Draw pegs if they exist
        {
          g.setColor(curColour);
          g.fillRect(curX + 7, curY + 7, deltaX, deltaY);
        }
      }
    }
    ((Graphics2D) g).setStroke( new BasicStroke(0.5f) );
    //Sets the color back to black after painting the grid
    g.setColor(Color.BLACK);
    int curX = (int)Math.round(X_OFFSET*scale);
    int curY = (int)Math.round(Y_OFFSET*scale);
    int nextX = (int)Math.round((X_OFFSET+X_DIM*grid.length)*scale);
    int nextY = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale);
    g.drawRect(curX, curY, nextX-curX, nextY-curY);
  }
  
  /**
   * draws a line, the line list stores two lines, the first one is is drawn as red, the other green
   * 
   */
  private void drawLine(Graphics g)
  {
	//i is either 0 for the first line or 1 for the second
    for (int i=0; i < numLines; i++ ) 
    {
    	//change drawing to red color if its the first, otherwise change it to green
    	if(i%2==0) {
    		g.setColor(Color.RED);
    	}
    	else {
    		g.setColor(Color.GREEN);
    	}
    	//I didin't make this
      ((Graphics2D) g).setStroke( new BasicStroke( 5.0f*(float)scale) );
      if(line[0][i]!=line[1][i]||line[2][i]!=line[3][i])
    	  g.drawLine( (int)Math.round((X_OFFSET+X_DIM/2.0+line[0][i]*X_DIM)*scale), 
    			  	(int)Math.round((Y_OFFSET+Y_DIM/2.0+line[1][i]*Y_DIM)*scale), 
    			  	(int)Math.round((X_OFFSET+X_DIM/2.0+line[2][i]*X_DIM)*scale), 
    			  	(int)Math.round((Y_OFFSET+Y_DIM/2.0+line[3][i]*Y_DIM)*scale) );
    }
    //Change the color back to black
    g.setColor(Color.BLACK);
  }

  
  /**
   * Convert a String to the corresponding Color defaulting to Black 
   * with an invald input
   */
  private Color convertColour( String theColour )
  {
    for( int i=0; i<COLOUR_NAMES.length; i++ )
    {
      if( COLOUR_NAMES[i].equalsIgnoreCase( theColour ) )
        return COLOURS[i];
    }
    
    return default_color;
  }
 
  
  /** The method that draws everything
   */
  public void paintComponent( Graphics g ) 
  {
    this.setScale();
    this.paintGrid(g);
    this.drawLine(g);
    this.paintText(g);
  }
  
  public void setScale()
  {
    double width = (0.0+this.getSize().width) / this.originalWidth;
    double height = (0.0+this.getSize().height) / this.originalHeight;
    this.scale = Math.max( Math.min(width,height), MIN_SCALE ); 
  }
  
  /** Sets the message to be displayed under the board
   */
  public void displayMessage(String theMessage)
  {
    message = theMessage;
  }
  
  
  /** This method will save the value of the colour of the peg in a specific 
    * spot.  theColour is restricted to 
    *   "yellow", "blue", "cyan", "green", "pink", "white", "red", "orange"  
    * Otherwise the colour black will be used. 
    */
  public void putPeg(String theColour, int row, int col)
  {
    this.grid[col][row] = this.convertColour( theColour );
  }
  
  /** Same as putPeg above but for 1D boards
   */
  public void putPeg(String theColour, int col)
  {
    this.putPeg( theColour, 0, col );
  }
  
  /** Remove a peg from the gameboard.
   */
  public void removePeg(int row, int col)
  {
    this.grid[col][row] = null;
  }
  
  /** Same as removePeg above but for 1D boards
   */
  public void removePeg(int col)
  {
    this.grid[col][0] = null;
  }
   
  /** Draws a line on the board using the given co-ordinates as endpoints
   */
  public void drawLine(int row1, int col1, int row2, int col2,int index)
  {
    this.line[0][index]=col1;
    this.line[1][index]=row1;
    this.line[2][index]=col2;
    this.line[3][index]=row2;
    
  }
  public void removeLines() {
	  for(int i=0;i<4;i++) {
		  for(int j=0;j<numLines;j++) {
			  this.line[i][j]=0;
		  }
	  }
  }
  public int getColumns()
  {
    return this.grid.length;
  }
    
  public int getRows()
  {
    return this.grid[0].length;
  }
}
