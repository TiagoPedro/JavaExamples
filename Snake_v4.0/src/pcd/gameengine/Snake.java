package pcd.gameengine;


import java.util.LinkedList;
import java.util.Random;

/**
 * @author tpedro
 *
 */
public class Snake extends Thread {
	private final static int INITIAL_LENGTH = 6;
	private static int ID_INCREMENTER = 0;
	private static int BOARD_SIZE;
	private Board board;
	private Game game;
	private LinkedList<Cell> occupiedCells;
	private int id;
	private boolean isPursuing;
	private Cell targetCell;
	private boolean jumpMode;
	private boolean turboBoostMode;
	private boolean fedMode;
	private boolean isSelected;

	/**
	 * Constructor that receives an x, an y and a board that acts as the resource manager.
	 * 
	 * @param initialX Initial X coordinate of the snake.
	 * @param initialY Initial Y coordinate of the snake.
	 * @param board Resource manager.
	 */
	public Snake(int initialX, int initialY, Board board, Game game){
		id = ID_INCREMENTER++;
		this.board = board;
		BOARD_SIZE = board.getLength();
		targetCell = null;
		this.game = game;
		jumpMode = false;
		turboBoostMode = false;
		fedMode = false;
		isSelected = false;

		occupiedCells = new LinkedList<Cell>();
		int[] initialPosition = {initialX, initialY};
		occupiedCells.addLast(board.getCell(initialPosition));

		while(occupiedCells.size() < INITIAL_LENGTH){
			board.getCell(initialPosition).setEmpty(false);
			occupiedCells.addLast(board.getCell(initialPosition));
			initialY++;
		}
	}

	/**
	 * @return the list of cells occupied by the snake.
	 */
	public synchronized LinkedList<Cell> getOccupiedCells() {
		return occupiedCells;
	}

	/**
	 * @return the ID of the snake.
	 */
	public int getID() {
		return id;
	}
	
	public boolean getPursuingState(){
		return isPursuing;
	}
	
	public void setPursuitState(boolean state){
		isPursuing = state;
	}
	
	public void setTargetCell(int[] pos){
		targetCell = board.getCell(pos);
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	/**
	 * @return a random cell next to the head of the snake.
	 */
	private int[] randomCellInfo(){
		int[] nextCellInfo;
		int r;
		
		do {
			r = new Random().nextInt(4);
			switch(r){
			case 0:
				nextCellInfo = upCellInfo();
				break;
			case 1:
				nextCellInfo = rightCellInfo();
				break;
			case 2:
				nextCellInfo = leftCellInfo();
				break;
			case 3:
				nextCellInfo = downCellInfo();
				break;
			default:
				nextCellInfo = rightCellInfo();
				break;
			}
		} while(nextCellInfo[0] < 0 || nextCellInfo[0] > board.getLength() - 1 ||
				nextCellInfo[1] < 0 || nextCellInfo[1] > board.getLength() - 1 ||
				overlappingOwnCells(nextCellInfo));
		
		return nextCellInfo;
	}
	
	private boolean overlappingOwnCells(int[] pos){
		for(Cell cell : occupiedCells){
			if(pos[0] == cell.getX() && pos[1] == cell.getY())
				return true;
		}
		return false;
	}
	
	/**
	 * @return a unidimensional array of integers which contain the closer upper cell of the
	 * head of the snake.
	 */
	private int[] upCellInfo(){
		int[] upCellInfo = {(occupiedCells.getLast().getX()),(occupiedCells.getLast().getY() - 1)};
		return upCellInfo;
	}
	
	/**
	 * @return a unidimensional array of integers which contain the closer right cell of the
	 * head of the snake.
	 */
	private int[] rightCellInfo(){
		int[] rightCellInfo = {(occupiedCells.getLast().getX() + 1), (occupiedCells.getLast().getY())};
		return rightCellInfo;
	}
	
	/**
	 * @return a unidimensional array of integers which contain the closer left cell of the
	 * head of the snake.
	 */
	private int[] leftCellInfo(){
		int[] leftCellInfo = {(occupiedCells.getLast().getX() - 1), (occupiedCells.getLast().getY())};
		
		return leftCellInfo;
	}
	
	/**
	 * @return a unidimensional array of integers which contain the closer down cell of the
	 * head of the snake.
	 */
	private int[] downCellInfo(){
		int[] downCellInfo = {(occupiedCells.getLast().getX()), (occupiedCells.getLast().getY() + 1)};
		return downCellInfo;
	}
	
	/**
	 * Asks the resource manager for a cell
	 * 
	 * @param cellInfo
	 * @return a Cell
	 */
	private Cell requestNextCell(){
		int[] nextCellInfo;
		
		if(isPursuing){
			if(occupiedCells.getLast().getX() == targetCell.getX() && occupiedCells.getLast().getY() == targetCell.getY()){
				isPursuing = false;
				do{
					int[] pos = randomCellInfo();
					if(!overlappingOwnCells(pos)){
						nextCellInfo = pos;
					} else
						nextCellInfo = randomCellInfo();
				} while(!board.getCell(nextCellInfo).isCellEmpty());
			} else if(occupiedCells.getLast().getX() < targetCell.getX()){
				int[] pos = {occupiedCells.getLast().getX() + 1, occupiedCells.getLast().getY()};
				do{
					if(!overlappingOwnCells(pos)){
						nextCellInfo = pos;
					} else
						nextCellInfo = randomCellInfo();
				} while(!board.getCell(nextCellInfo).isCellEmpty());

			} else if(occupiedCells.getLast().getX() > targetCell.getX()) {
				int[] pos = {occupiedCells.getLast().getX() - 1, occupiedCells.getLast().getY()};
				do{
					if(!overlappingOwnCells(pos)){
						nextCellInfo = pos;
					} else
						nextCellInfo = randomCellInfo();
				} while(!board.getCell(nextCellInfo).isCellEmpty());
			} else if(occupiedCells.getLast().getY() < targetCell.getY()){
				int[] pos = {occupiedCells.getLast().getX(), occupiedCells.getLast().getY() + 1};
				do{
					if(!overlappingOwnCells(pos)){
						nextCellInfo = pos;
					} else
						nextCellInfo = randomCellInfo();
				} while(!board.getCell(nextCellInfo).isCellEmpty());
			} else {
				int[] pos = {occupiedCells.getLast().getX(), occupiedCells.getLast().getY() - 1};
				do{
					if(!overlappingOwnCells(pos)){
						nextCellInfo = pos;
					} else
						nextCellInfo = randomCellInfo();
				} while(!board.getCell(nextCellInfo).isCellEmpty());
			}
		} else {
			do{
				int[] pos = randomCellInfo();
				if(!overlappingOwnCells(pos)){
					nextCellInfo = pos;
				} else
					nextCellInfo = randomCellInfo();
			} while(!board.getCell(nextCellInfo).isCellEmpty());
		}

		if(board.hasJumpFeature(nextCellInfo)){
			setJumpModeOn(true);
			board.consumeJumpFeature(nextCellInfo);
		} else if(board.hasTurboBoost(nextCellInfo)) {
			setTurboBoostModeOn(true);
			board.consumeTurboBoostFeature(nextCellInfo);
		} else if(board.hasFood(nextCellInfo)){
			setFedMode(true);
			board.consumeFoodFeature(nextCellInfo);
		}
		return board.getCell(nextCellInfo);
	}
	
	/**
	 * 
	 * @return
	 */
	private Cell requestNextCellJump(){
		int[] nextCellInfo;
		
		if(isPursuing){
			if(occupiedCells.getLast().getX() == targetCell.getX() && occupiedCells.getLast().getY() == targetCell.getY()){
				isPursuing = false;
				do {
					nextCellInfo = randomCellInfo();
				} while(overlappingPreviousCell(nextCellInfo));
				if(!board.getCell(nextCellInfo).getEmptyState()){
					setJumpModeOn(false);
					System.out.println("Jump used");
				}
			} else if(occupiedCells.getLast().getX() < targetCell.getX()){
				int[] pos = {occupiedCells.getLast().getX() + 1, occupiedCells.getLast().getY()};
				if(!overlappingPreviousCell(pos)){
					nextCellInfo = pos;
				} else {
					do {
						nextCellInfo = randomCellInfo();
					} while(overlappingPreviousCell(nextCellInfo));
				}
				if(!board.getCell(nextCellInfo).getEmptyState()){
					setJumpModeOn(false);
					System.out.println("Jump used");
				}
			} else if(occupiedCells.getLast().getX() > targetCell.getX()) {
				int[] pos = {occupiedCells.getLast().getX() - 1, occupiedCells.getLast().getY()};
				if(!overlappingPreviousCell(pos)){
					nextCellInfo = pos;
				} else {
					do {
						nextCellInfo = randomCellInfo();
					} while(overlappingPreviousCell(nextCellInfo));
				}
				if(!board.getCell(nextCellInfo).getEmptyState()){
					setJumpModeOn(false);
					System.out.println("Jump used");
				}
			} else if(occupiedCells.getLast().getY() < targetCell.getY()){
				int[] pos = {occupiedCells.getLast().getX(), occupiedCells.getLast().getY() + 1};
				if(!overlappingPreviousCell(pos)){
					nextCellInfo = pos;
				} else {
					do {
						nextCellInfo = randomCellInfo();
					} while(overlappingPreviousCell(nextCellInfo));
				}
				if(!board.getCell(nextCellInfo).getEmptyState()){
					setJumpModeOn(false);
					System.out.println("Jump used");
				}
			} else {
				int[] pos = {occupiedCells.getLast().getX(), occupiedCells.getLast().getY() - 1};
				if(!overlappingPreviousCell(pos)){
					nextCellInfo = pos;
				} else {
					do {
						nextCellInfo = randomCellInfo();
					} while(overlappingPreviousCell(nextCellInfo));
				}
				if(!board.getCell(nextCellInfo).getEmptyState()){
					setJumpModeOn(false);
					System.out.println("Jump used");
				}
			}
		} else {
			do {
				nextCellInfo = randomCellInfo();
			} while(overlappingPreviousCell(nextCellInfo));
			if(!board.getCell(nextCellInfo).getEmptyState()){
				setJumpModeOn(false);
				System.out.println("Jump used");
			}
		}
		if(board.hasTurboBoost(nextCellInfo)){
			setTurboBoostModeOn(true);
			board.consumeTurboBoostFeature(nextCellInfo);
		} else if(board.hasFood(nextCellInfo)){
			setFedMode(true);
			board.consumeFoodFeature(nextCellInfo);
		}
		return board.getCell(nextCellInfo);
	}
	
	private boolean overlappingPreviousCell(int[] pos){
		if(occupiedCells.get(occupiedCells.size() - 2).getX() == pos[0] && occupiedCells.get(occupiedCells.size()-2).getY() == pos[1]){
			return true;
		}
		return false;
	}
	
	private void setJumpModeOn(boolean state) {
		jumpMode = state;
	}

	private boolean hasJumpModeOn(){
		return jumpMode;
	}
	
	private void setTurboBoostModeOn(boolean state) {
		turboBoostMode = state;
	}
	
	private boolean hasTurboBoostModeOn(){
		return turboBoostMode;
	}
	
	private void setFedMode(boolean state){
		fedMode = state;
	}
	
	private void moveToNextCell(Cell cell){
		cell.setEmpty(false);
		occupiedCells.addLast(cell);

		if(!fedMode){
			occupiedCells.getFirst().setEmpty(true);
			occupiedCells.removeFirst();
		} else
			fedMode = false;
		game.notifyGame();
	}
	
	@Override
	public void run() {
		while(!isInterrupted()){
			if(hasTurboBoostModeOn() && hasJumpModeOn()){
				board.startTurboBoostRequest();
				for(int i = 0; i < 3; i++){
					moveToNextCell(requestNextCellJump());
				}
				board.stopTurboBoostRequest();
			} else if(hasTurboBoostModeOn() && !hasJumpModeOn()){
				board.startTurboBoostRequest();
				for(int i = 0; i < 3; i++){
					moveToNextCell(requestNextCell());
				}
				board.stopTurboBoostRequest();
			} else if(hasJumpModeOn()){
				board.startNormalRequest();
				moveToNextCell(requestNextCellJump());
				board.stopNormalRequest();
			} else {
				board.startNormalRequest();
				moveToNextCell(requestNextCell());
				board.stopNormalRequest();
			}
//			System.out.println("Snake " + getID() + " head:" + occupiedCells.getLast().getX() + " " + 
//					occupiedCells.getLast().getY());

			if(occupiedCells.getLast().getX() > BOARD_SIZE - 1){
				System.out.println("Snake " + getID() + " finished");
				interrupt();
			}
			try {
				sleep(400);	
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}
}
