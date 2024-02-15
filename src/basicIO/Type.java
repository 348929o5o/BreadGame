package basicIO;
/**
 * An object that holds 4 different states and has a list of 4 different bond values for a 2D grid
 * @author 348929o5o
 * @version 1.0
 * @since 1/21/2024
 */

public class Type {
	/**
	 * The integer value used to identify the type of object and assign behaviors 
	 */
	int type;
	/**
	 * Constructs a new type object
	 * @param x, an integer between 1 and 4, where water is 1, air is 2, flour is 3, and soaked flour is 4
	 */
	public Type(int x) {
		type=x;
	}
	/**
	 * Array containing bonds representing attraction to other type objects in a 2D grid
	 * 0=right,1=left,2=front,3=back
	 */
	double[] bonds={0,0,0,0};
	public void setType(int x) {
		type=x;
	}
	/**
	 * Sets all bonds of the object to zero
	 */
	public void breakBonds() {
		for(int i=0;i<4;i++) {
			bonds[i]=0;
		}
	}
	/**
	 * returns the type value of the object
	 * @return An integer
	 */
	public int getType() {
		return type;
	}
	/**
	 * returns true if it is flour or soaked flour (type 3 or 4)
	 * @return boolean
	 */
	public boolean isFlour() {
		if(type==3||type==4) {
			return true;
		}
		return false;
	}
	/**
	 * returns the bonds array
	 * @return The bonds array
	 */
	public double[] getBond() {
		return bonds;
	}
	/**
	 * creates a bond of specified strength on the specified side
	 * @param side A value between 1 and 4 where 0=right,1=left,2=front,3=back
	 * @param strength The strength of the bond, acting as a multiplier for force transfer
	 */
	public void bond(int side,double strength){
		bonds[side-1]=strength;
	}
	/**
	 * Makes a copy of the object with the same bond values
	 * @return a copy of the original Type object
	 */
	public Type copy() {
		Type nType = new Type(type);
		for(int i=1;i<=4;i++) {
			nType.bond(i,bonds[i-1]);
		}
		return nType;
	}
	//toString for easier viewing of values in debug mode
//	public String toString() {
//		switch(type) {
//		case 1:
//			return "Water"+", "+bonds;
//		case 2:
//			return "Air"+", "+bonds;
//		case 3:
//			return "Flour"+", "+bonds;
//		case 4:
//			return "SoakedFlour"+", "+bonds;
//		default:
//			return "ERROR"+", "+bonds;
//		}
//	}
}
