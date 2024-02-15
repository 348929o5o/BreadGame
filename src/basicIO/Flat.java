package basicIO;
import java.util.*;
/**
 * Creates and modifies a 2D array of Type objects using two player controlled hands
 * @author Daniel Amin
 * @version 1.0
 * @since 1/21/2024
 */
public class Flat {
	/**
	 * Size of the board
	 */
	public static final int SIZE=50;
	/**
	 * Starting time
	 */
	static long startTime = System.currentTimeMillis();
	/**
	 * Time sine start
	 */
	static long elapsedTime = System.currentTimeMillis() - startTime;
	/**
	 * Seconds since start rounded down
	 */
	static int elapsedSeconds = (int)elapsedTime / 1000;
	/**
	 * minutes since start rounded down
	 */
	static int elapsedMinutes = elapsedSeconds / 60;
	/**
	 * Type array fed to the UI, all methods will modify this
	 */
	public static Type[][] grid=new Type[SIZE][SIZE];
	/**
	 * State of the right hand, drag mode or not
	 */
	static boolean grabRight=false;
	/**
	 * x coordinate of right hand
	 */
	static int rightX=SIZE-2;
	/**
	 * y coordinate of right hand
	 */
	static int rightY=0;
	/**
	 * x coordinate of left hand
	 */
	static int leftX=0;
	/**
	 * y coordinate of left hand
	 */
	static int leftY=0;
	/**
	 * State of the left hand, drag mode or not
	 */
	static boolean grabLeft=false;
	//The last coordinates of right and left pins, calculated every second
	/**
	 * Last x index of the right hand
	 */
	static int lastrx=SIZE-2;
	/**
	 * Last y index of the right hand
	 */
	static int lastry=0;
	/**
	 * Last x index of the left hand
	 */
	static int lastlx=0;
	/**
	 * Last y index of the left hand
	 */
	static int lastly=0;
	/**
	 * GUI for display
	 */
	static UI gui=new UI(SIZE,SIZE);
	//Different bond strengths (amount of force transfered)
	/**
	 * maximum strength bond
	 */
	static final double BOND1=1;
	/**
	 * lower strength bond
	 */
	static final double BOND2=0.95;
	/**
	 * malus for forces traveling in a direction perpendicular to the force applied
	 */
	final static double sideF=0.95;
	/**
	 * Buffer added to force values for stability
	 */
	final static double BUFFER=0.5;
	/**
	 * Initializes a board of Type objects, and fills it with a heap of dough, as well as initializing a UI to display
	 * Rebonds and shifts particles based on force every second
	 */
	public static void main(String[]args) {
		//initializes all elements to Type Air
		for(int i=0;i<SIZE;i++) {
			for(int j=0;j<SIZE;j++) {
				grid[i][j]=new Type(2);
			}
		}
		//Creates a block of flour and water
		for(int i=SIZE/5;i<=SIZE/2;i++) {
			for(int j=SIZE/5;j<=SIZE/2;j++) {
				grid[i][j].setType(3);
				if(i%2!=0&&j%2!=0) {
					grid[i][j].setType(1);
				}
			}
		}
		//
		bond();
		//Last value of seconds for timer
		int lastSecond=elapsedSeconds;
		int lastTenSec=(int)elapsedSeconds/10;
		gui.displayMessage("WASD for left hand, IJKL to left hand. Toggle q to drag left and o to drag right");
		gui.Popup();
		//1 minute runtime, game loop
		while(elapsedMinutes<1) {
			//updates the time values
			elapsedTime = System.currentTimeMillis() - startTime;
			elapsedSeconds = (int)elapsedTime / 1000;
			elapsedMinutes = elapsedSeconds / 60;
			//every tenth of a second do...
			if(lastTenSec<(int)elapsedTime/10) {
				//Resets timer
				lastTenSec=(int)elapsedTime/10;
				//Repaints the gui
				gui.repaint();
			}
			//Every second do...
			if(lastSecond<elapsedSeconds) {
				if(grabRight) {
					drag(false);
				}
				//Same for left
				if(grabLeft) {
					drag(true);
				}
				bond();
				//clears the peg for the last location, and sets the last coordinate to the current location of left and right
				gui.removePeg(lastry, lastrx);
				gui.removePeg(lastly, lastlx);
				lastry=rightY;
				lastrx=rightX;
				lastly=leftY;
				lastlx=leftX;
				//puts down new pegs for the updated location
				gui.putPeg("red", lastry,lastrx);
				gui.putPeg("green", lastly, lastlx);
				//updates the last second to be equal to elapsed seconds, reseting the timer
				lastSecond=elapsedSeconds;
				//clears the lines on the board
				gui.removeLines();
			}
		}
		//quits after a minute
		System.exit(0);
	}
	/**
	 * Creates bonds between all particles based on the following rules
	 * 1. A water particle may bond to any flour particle adjacent to it with highest strength
	 * 2. A soaked flour particle may bond to any flour particle with the second highest strength
	 * Additionally any flour particle next to water will become a soaked flour particle before bonding
	 * Soaked flour and dry flour will both be refered to as flour collectivally throughout, even though dry flour
	 * has Type.Flour
	 */
	public static void bond() {
		//breaks all bonds on the board
		for(int i=0;i<SIZE;i++) {
			for(int j=0;j<SIZE;j++) {
				grid[i][j].breakBonds();
			}
		}
		//for every particle on the board
		for(int i=0;i<SIZE;i++) {
			for(int j=0;j<SIZE;j++) {
				//if it is water
				if(grid[i][j].getType()==1) {
					//check the gridspace left of it and see if it is flour
					if(i>0&&i<=SIZE&&j>=0&&j<SIZE&&grid[i-1][j].isFlour()) {
						//change its Type to soaked flour
						grid[i-1][j].setType(4);
						//create a bond on the left side of the water particle with highest strength
						grid[i][j].bond(1,BOND1);
						//create a bond on the right side of the flour partile with highest strength
						grid[i-1][j].bond(2,BOND1);
					}
					//Repeat for all sides
					if(i<SIZE-1&&i>=-1&&j>=0&&j<SIZE&&grid[i+1][j].isFlour()) {
						grid[i+1][j].setType(4);
						grid[i][j].bond(2,BOND1);
						grid[i+1][j].bond(1,BOND1);
					}
					if(j>0&&j<SIZE&&j>0&&i>=0&&grid[i][j-1].isFlour()) {
						grid[i][j-1].setType(4);
						grid[i][j].bond(4,BOND1);
						grid[i][j-1].bond(3,BOND1);
					}
					if(j<SIZE-1&&i<SIZE&&j+1>=0&&i>=0&&grid[i][j+1].isFlour()) {
						grid[i][j+1].setType(4);
						grid[i][j].bond(3,BOND1);
						grid[i][j+1].bond(4,BOND1);
					}
					
				}
			}
		}
		//for every point on the board
		//Have to do it twice for newly soaked particles
		for(int i=0;i<SIZE;i++) {
			for(int j=0;j<SIZE;j++) {
				//if its soaked flour
				if(grid[i][j].getType()==4) {
					//check if it is next to a flour particle
					if(i>0&&i<=SIZE&&j>=0&&j<SIZE&&grid[i-1][j].isFlour()) {
						//create a weaker bond on the left side of the original particle, and one on the right side of the receiver
						grid[i][j].bond(1,BOND2);
						grid[i-1][j].bond(2,BOND2);
					}
					if(i<SIZE-1&&i>=-1&&j>=0&&j<SIZE&&grid[i+1][j].isFlour()) {
						grid[i][j].bond(2,BOND2);
						grid[i+1][j].bond(1,BOND2);
					}
					if(j>0&&j<SIZE&&j>0&&i>=0&&grid[i][j-1].isFlour()) {
						grid[i][j].bond(3,BOND2);
						grid[i][j-1].bond(4,BOND2);
					}
					if(j<SIZE-1&&i<SIZE&&j+1>=0&&i>=0&&grid[i][j+1].isFlour()) {
						grid[i][j].bond(4,BOND2);
						grid[i][j+1].bond(3,BOND2);
					}
				}
			}
		}
	}
	/**
	 * Moves the hand on the board, and draws a shape to indicate its position and current state
	 * Draws a line from the old point to the new in drag mode
	 * Otherwise moves a peg around the board
	 * @param hand left or right, right is false
	 * @param direction, left 1, right 2, up 3, down 4
	 */
	public static void moveHand(boolean hand, int direction) {
		//coordinates
		int x;
		int y;
		//sets coordinates to which hand is being used
		if(hand) {
			x=leftX;
			y=leftY;
		}
		else {
			x=rightX;
			y=rightY;
		}
		//Changes the coordinates based on the direction specified
		switch(direction) {
			case 1:
				if(x>0) {
					x-=1;
				}
				break;
			case 2:
				if(x<SIZE-2) {
					x+=1;
				}
				break;
			case 3:
				if(y>0) {
					y-=1;
				}
				break;
			case 4:
				if(y<SIZE-2) {
					y+=1;
				}
				break;
		}
		//If its the left side
		if(hand) {			
			//if its grabbing
			if(grabLeft) {
				//draw a green line from the last coordinate to the current one
				gui.drawLine(lastly, lastlx, leftY, leftX,1);
			}
			else {
				//shifted the green peg over to the current coordinate
				gui.removePeg(leftY, leftX);
				gui.putPeg("green", y, x);
			}
			//update the current x and y coordinates
			leftX=x;
			leftY=y;
		}
		//same thing for the right
		else {	
			if(grabRight) {
				gui.drawLine(lastry, lastrx, rightY, rightX,0);
			}
			else {
				gui.removePeg(rightY, rightX);
				gui.putPeg("red", y, x);
			}
			rightX=x;
			rightY=y;
		}
	}
	/**
	 * Moves the existing particle at a point to make space for the new particle at that location
	 * @param x The x coordinate the Type object is moving towards
	 * @param y The y coordinate the Type object is moving towards
	 * @param direction : the direction that the point comes from
	 */
	public static void space(int x,int y, int direction) {
		//the type of the existing particle
		Type same;
		//generates a random number between 1-4 for the direction the particle is displaced in 
		Random r=new Random();
		int push=r.nextInt(4)+1;
		//initializes same
		if(grid[x][y].getType()==3) {
			same=new Type(3);
		}
		else if(grid[x][y].getType()==4) {
			same=new Type(4);
		}
		else {
			same=new Type(1);
		}
		//1 is left, 2 is right, 3 is up, 4 is down
		switch(direction) {
			case 1:
				//if there is space in the grid to the left
				if(!(x-1<0)) {
					//if its already occupied, call space again with a random direction for the particles displacement
					if(grid[x-1][y].getType()!=2) {
						space(x-1,y,push);
					}
					//set the original gridspace to the incoming particle
					grid[x-1][y]=same;
				}
				break;
			//repeat for other particles
			case 2:
				if(!(x>SIZE)) {
					if(grid[x+1][y].getType()!=2) {
						space(x+1,y,push);
					}
					grid[x+1][y]=same;
				}
				break;
			case 3:
				if(!(y-1<0)) {
					if(grid[x][y-1].getType()!=2) {
						space(x,y-1,push);
					}
					grid[x][y-1]=same;
				}
				break;
			case 4:
				if(!(y>SIZE)) {
					if(grid[x][y+1].getType()!=2) {
						space(x,y+1,push);
					}
					grid[x][y+1]=same;
				}
				break;
		}
	}
	/**
	 * Changes the position of particles on the board based on the following rules
	 * 1. Force applied is starting from a single non air particle
	 * 2. Each force may be broken down into two forces each in the x and y direction respectively, and each description hereafter will refer to one of these directional fores
	 * 3. For any particle in front of the origin particle in the same direction of the force, it will move at the same rate
	 * 4. For any other particle, the amount of distance it moves will be multiplied by the strength of the bond adjacent to it
	 * 5. Each particle may only be moved once. The order of the directions to be checked will left right up down
	 * 6. For any particle perpendicular to the applied force a negative multiplier equivalent to sideF will be applied to the force transfered
	 * @param hand left is true, right is false
	 */
	public static void drag(boolean hand){
		//shift of x and y coordinates from starting point
		double sx;
		double sy;
		//the last coordinates every second
		int lx;
		int ly;
		//active points
		Queue<Integer> shifted= new LinkedList<>();
		//distance to move
		Queue<Double> shift= new LinkedList<>();
		//visited points
		boolean[][] visited=new boolean[SIZE][SIZE];
		//choose left or right point
		if(hand) {
			lx=lastlx;
			ly=lastly;
			sx=leftX-lx;
			sy=leftY-ly;
		}
		else {
			lx=lastrx;
			ly=lastry;
			sx=rightX-lx;
			sy=rightY-ly;
		}
		//buffer to prevent early breaks
		if(sx>0) {
			sx+=BUFFER;
		}
		else if(sx<0){
			sx-=BUFFER;
		}
		if(sy>0) {
			sy+=BUFFER;
		}
		else if(sy<0){
			sy-=BUFFER;
		}
		System.out.println(sx+", "+sy);
		if(grid[lx][ly].getType()!=2) {
			Type[][] gridC=new Type[SIZE][SIZE];
			for(int i=0;i<SIZE;i++) {
				for(int j=0;j<SIZE;j++) {
					gridC[i][j]=grid[i][j].copy();
				}
			}
			//bfs for all connected points
			//adds starting point, x then y point
			shifted.add(lx);
			shifted.add(ly);
			System.out.println(lx+" "+ly);
			//keeps going while there is any point in shifted list
			while(shifted.size()>0) {
				int x=shifted.poll();
				int y=shifted.poll();
				//sets active point to air
				grid[x][y].setType(2);
				//checks every point next to it and adds it to shifted 2 if it is not air
				if(x>0&&visited[x-1][y]==false) {
					//saves in array to avoid duplicates
					visited[x-1][y]=true;
					if(grid[x-1][y].getType()!=2) {
						shifted.add(x-1);
						shifted.add(y);
					}
				}
				if(x<SIZE-1&&visited[x+1][y]==false) {
					visited[x+1][y]=true;
					if(grid[x+1][y].getType()!=2) {
						shifted.add(x+1);
						shifted.add(y);
					}
				}
				if(y>0&&visited[x][y-1]==false) {
					visited[x][y-1]=true;
					if(grid[x][y-1].getType()!=2) {
						shifted.add(x);
						shifted.add(y-1);
					}
				}
				if(y<SIZE-1&&visited[x][y+1]==false) {
					visited[x][y+1]=true;
					if(grid[x][y+1].getType()!=2) {
						shifted.add(x);
						shifted.add(y+1);
					}
				}
			}
			//resets visited array
			for(int i=0;i<SIZE;i++) {
				for(int j=0;j<SIZE;j++) {
					visited[i][j]=false;
				}
			}
			//clears shifted query, this will be reused later
			shifted.clear();
			//the same Type as the initiating particle
			Type same;
			//sets the saved particle to the particle at lx,ly
			if(gridC[lx][ly].getType()==3) {
				same=new Type(3);
			}
			else if(gridC[lx][ly].getType()==4) {
				same=new Type(4);
			}
			else {
				same=new Type(1);
			}
			//Gets a new point translated sx in the x diretion and sy in the y direction to be the same Type as the origin
			//This is how dragging is represented
			grid[(int)(lx+sx)][(int)(ly+sy)]=same;
			//adds this point to the shifted list because it was shifted
			shifted.add(lx);
			shifted.add(ly);
			//Boolean for the side it is moving towards, right is true
			boolean side=false;
			//Boolean for the up down direction, down is true
			boolean up=false;
			//initializes the variables
			if(sx<0) {
				side=true;
			}
			if(sy<0) {
				up=true;
			}
			//sets the coordinate to visited
			visited[lx][ly]=true;
			//adds the distance the coordinate shifted by into the shift array
			shift.add(sx);
			shift.add(sy);
			//shift(i),shift(i+1) is distance traveled by point shifted(i),shifted(i+1)
			//while there are points that have been shifted where all surrounding points have not been checked
			while(shifted.size()>0) {
				//pull out the coordinates
				int x=shifted.poll();
				int y=shifted.poll();
				//previous shifted values for reference
				double psx=shift.poll();
				double psy=shift.poll();
				//get the bonds of the coordinates
				double[] bonds=gridC[x][y].getBond();
				//check for boundaries so it dosen't error
				//this will check the left side, or x-1,y
				if(x>0) {
					//set same to the Type of the point to the left
					if(gridC[x-1][y].getType()==3) {
						same=new Type(3);
					}
					else if(gridC[x-1][y].getType()==4) {
						same=new Type(4);
					}
					else {
						same=new Type(1);
					}
					//if this coordinate is not air in the original, and not visited
					if(gridC[x-1][y].getType()!=2&&!(visited[x-1][y])) {
						//add this coordinate to the query
						shifted.add(x-1);
						shifted.add(y);
						//The upward shift would be transmitted in a direction perpendicular to the force, so it will be affeted by the multiplier
						sy=psy*bonds[0]*sideF;
						//if its moving left, and being pushed don't change the force
						if(side) {
							sx=psx;
						}
						//if its moving right, adjust it to the bond strength
						else {
							sx=psx*bonds[0];
						}
						//draw the updated coordinate on the grid, skip if it is drawn off the board
						if((int)(x-1+sx)>=0&&(int)(x+sx)>0&&(int)(y+sy)>=0&&(int)(y+sy)<SIZE) {
							//if the space that the particle is moving to already has a particle, call space function
							if(grid[(int)(x-1+sx)][(int)(y+sy)].getType()!=2) {
								space((int)(x-1+sx),(int)(y+sy),1);
							}
							grid[(int)(x-1+sx)][(int)(y+sy)]=same;
						}
						//add the new forces to the location in the shift query corresponding to this coordinate
						shift.add(sx);
						shift.add(sy);
					}
					visited[x-1][y]=true;
				}
				//do the same thing for the other directions
				if(x<SIZE-1) {
					if(gridC[x+1][y].getType()==3) {
						same=new Type(3);
					}
					else if(gridC[x+1][y].getType()==4) {
						same=new Type(4);
					}
					else {
						same=new Type(1);
					}
					if(gridC[x+1][y].getType()!=2&&!(visited[x+1][y])) {
						shifted.add(x+1);
						shifted.add(y);
						sy=psy*bonds[1]*sideF;
						if(side) {
							sx=psx*bonds[1];
						}
						else {
							sx=psx;
						}
						if((int)(x+1+sx)<=SIZE-1&&(int)(x+sx)>0&&(int)(y+sy)>=0&&(int)(y+sy)<SIZE) {
							if(grid[(int)(x+1+sx)][(int)(y+sy)].getType()!=2) {
								space((int)(x+1+sx),(int)(y+sy),2);
							}
							grid[(int)(x+1+sx)][(int)(y+sy)]=same;
						}
						shift.add(sx);
						shift.add(sy);
					}
					visited[x+1][y]=true;
				}
				if(y>0) {
					if(gridC[x][y-1].getType()==3) {
						same=new Type(3);
					}
					else if(gridC[x][y-1].getType()==4) {
						same=new Type(4);
					}
					else {
						same=new Type(1);
					}
					if(gridC[x][y-1].getType()!=2&&!(visited[x][y-1])) {
						shifted.add(x);
						shifted.add(y-1);
						sx=psx*bonds[2]*sideF;
						if(up) {
							sy=psy;
						}
						else {
							sy=psy*bonds[2];
						}
						if((int)(y+sy)>0&&(int)(y+sy)<SIZE&&(int)(x+sx)>=0&&(int)(x+sx)<SIZE) {
							if(grid[(int)(x+sx)][(int)(y-1+sy)].getType()!=2) {
								space((int)(x+sx),(int)(y-1+sy),3);
							}
							grid[(int)(x+sx)][(int)(y-1+sy)]=same;
						}
						shift.add(sx);
						shift.add(sy);
					}
					visited[x][y-1]=true;
				}
				if(y<SIZE-1) {
					if(gridC[x][y+1].getType()==3) {
					same=new Type(3);
					}
					else if(gridC[x][y+1].getType()==4) {
						same=new Type(4);
					}
					else {
						same=new Type(1);
					}
					if(gridC[x][y+1].getType()!=2&&!(visited[x][y+1])) {
						shifted.add(x);
						shifted.add(y+1);
						sx=psx*bonds[2]*sideF;
						if(up) {
							sy=psy*bonds[2];
						}
						else {
							sy=psy;
						}
						if((int)(y+1+sy)<=SIZE-1&&(int)(y+sy)>0&&(int)(x+sx)>=0&&(int)(x+sx)<SIZE) {
							if(grid[(int)(x+sx)][(int)(y+1+sy)].getType()!=2) {
								space((int)(x+sx),(int)(y+1+sy),4);
							}
							grid[(int)(x+sx)][(int)(y+1+sy)]=same;
						}
						shift.add(sx);
						shift.add(sy);
					}
					visited[x][y+1]=true;
				}
			}
		}
	}
}
