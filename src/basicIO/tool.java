package basicIO;


public enum tool {
	Left,
	Right,
	Air;
	boolean pintched=false;
	public void pintch(){
		pintched=!pintched;
	}
}
