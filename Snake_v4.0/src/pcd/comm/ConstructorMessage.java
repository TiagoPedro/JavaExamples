package pcd.comm;


public class ConstructorMessage extends Message {
	private final int snakeID;
	private final int numberOfCells;

	public ConstructorMessage(int snakeID, int numberOfCells) {
		this.snakeID = snakeID;
		this.numberOfCells = numberOfCells;
	}

	public int getID(){
		return snakeID;
	}
	
	public int getNumberOfCells(){
		return numberOfCells;
	}
}
