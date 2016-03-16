package pcd.gameengine;

/**
 * @author tpedro
 * Represents the shared resource of the table. All snakes occupy this object which
 * acts as positions on a board.
 */
public class Cell {
	private boolean isEmpty;
	private final int x;
	private final int y;

	/**
	 * Simple constructor that receives x and y coordinates and is initialised empty.
	 * 
	 * @param x Represents the X coordinate on the board.
	 * @param y Represents the Y coordinate on the board.
	 */
	public Cell(int x, int y){
		this.x = x;
		this.y = y;
		isEmpty = true;
	}
	
	/**
	 * @return true if the cell is empty
	 */
	public synchronized boolean isCellEmpty() {
		while(!isEmpty){
			try {
				System.out.println("Snake waiting on cell " + x + " " + y);
				wait();
			} catch (InterruptedException e) {
				System.out.println("The snake was interrupted");
				return false;
			}
		}
		isEmpty = false;
		return true;
	}

	/**
	 * @param state True to set the cell empty, false to set it full.
	 */
	public void setEmpty(boolean state) {
		isEmpty = state;
	}

	/**
	 * @return The Y coordinate of the cell.
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return The X coordinate of the cell.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Checks if the cell is empty without trying to acquire its lock.
	 * @return The empty state of the cell.
	 */
	public boolean getEmptyState(){
		return isEmpty;
	}
}