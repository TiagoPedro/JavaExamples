package pcd.comm;


public class ClientSideMessage extends Message {
	private int snakeID;
	private boolean toRemove;
	private int[] pos;
	
	public ClientSideMessage(int snakeID, boolean toRemove){
		this.snakeID = snakeID;
		this.toRemove = toRemove;
	}

	public ClientSideMessage(int[] pos, int snakeID) {
		this.pos = pos;
		this.snakeID = snakeID;
	}
	
	public ClientSideMessage(int[] pos, int snakeID, boolean toRemove){
		this.snakeID = snakeID;
		this.pos = pos;
		this.toRemove = toRemove;
	}
	
	public int getSnakeID() {
		return snakeID;
	}

	public boolean isToRemove() {
		return toRemove;
	}

	public int[] getPos() {
		return pos;
	}
}
