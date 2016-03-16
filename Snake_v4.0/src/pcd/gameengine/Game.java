package pcd.gameengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

import pcd.gameengine.feature.Feature;

public class Game extends Observable {
	private Board board;
	private static boolean DONE = false;
	private List<Snake> listOfSnakes;
	private boolean isStarted;

	public Game(int numberOfCells, int numberOfSnakes, int numberOfFeatures) {
		isStarted = false;
		if(numberOfSnakes < 2) {
			throw new IllegalArgumentException("Invalind number os snakes, must be at least two.");
		}
		board = new Board(numberOfCells, numberOfSnakes, numberOfFeatures);
		System.out.println("Board created...");
		
		/*
		 * creating snakes at random positions
		 */
		Random r = new Random();
		int random;
		Snake s;
		
		listOfSnakes = new ArrayList<Snake>();
		for(int i = 0; i < numberOfSnakes; i++){
			int initialX;
			int initialY;
			
			do{
				random = r.nextInt(numberOfCells - 6);
			} while(random < 4);
			initialX = random;
			
			do{
				random = r.nextInt(numberOfCells - 6);
			} while(random < 4);
			initialY = random;
			
			s = new Snake(initialX, initialY, board, this);
			listOfSnakes.add(s);
		}
		System.out.println("Snakes created...");
	}

	public int getNumberOfCells(){
		return board.getLength();
	}
	
	public boolean isStarted(){
		return isStarted;
	}
	
	public void setStarted(boolean started){
		isStarted = started;
	}
	
	public Map<Integer, ArrayList<int[]>> getListOfUnselectedSnakesPos(){
		Map<Integer, ArrayList<int[]>> mapOfSnakes = new HashMap<Integer, ArrayList<int[]>>();
		for(Snake s : listOfSnakes){
			if(!s.isSelected())
				mapOfSnakes.put(s.getID(), convertSnakeCellsToArrayOfInt(s));
		}
		return mapOfSnakes;
	}
	
	public Map<Integer, ArrayList<int[]>> getListOfSelectedSnakesPos(){
		Map<Integer, ArrayList<int[]>> mapOfSnakes = new HashMap<Integer, ArrayList<int[]>>();
		for(Snake s : listOfSnakes){
			if(s.isSelected())
				mapOfSnakes.put(s.getID(), convertSnakeCellsToArrayOfInt(s));
		}
		return mapOfSnakes;	
	}

	public List<int[]> getListOfBarriersPos() {
		List<int[]> listOfBarriersPos = new ArrayList<int[]>();
		for(Feature f : board.getListOfBarriers()){
			int[] pos = new int[2];
			pos[0] = f.getCell().getX();
			pos[1] = f.getCell().getY();
			listOfBarriersPos.add(pos);
		}
		return listOfBarriersPos;
	}
	
	public List<int[]> getListOfJumpsPos() {
		List<int[]> listOfJumpsPos = new ArrayList<int[]>();
		for(Feature f : board.getListOfJumps()){
			int[] pos = new int[2];
			pos[0] = f.getCell().getX();
			pos[1] = f.getCell().getY();
			listOfJumpsPos.add(pos);
		}
		return listOfJumpsPos;
	}
	
	public List<int[]> getListOfTurboBoostsPos() {
		List<int[]> listOfTurboBoostsPos = new ArrayList<int[]>();
		for(Feature f : board.getListOfTurboBoosts()){
			int[] pos = new int[2];
			pos[0] = f.getCell().getX();
			pos[1] = f.getCell().getY();
			listOfTurboBoostsPos.add(pos);
		}
		return listOfTurboBoostsPos;
	}
	
	public List<int[]> getListOfFoodsPos() {
		List<int[]> listOfFoodsPos = new ArrayList<int[]>();
		for(Feature f : board.getListOfFoods()){
			int[] pos = new int[2];
			pos[0] = f.getCell().getX();
			pos[1] = f.getCell().getY();
			listOfFoodsPos.add(pos);
		}
		return listOfFoodsPos;
	}

	public List<Snake> getListOfSnakes() {
		return listOfSnakes;
	}

	private ArrayList<int[]> convertSnakeCellsToArrayOfInt(Snake s){
		ArrayList<int[]> snakeCells = new ArrayList<int[]>();
		for(int i = 0; i < s.getOccupiedCells().size(); i++){
			int[]cell = new int[2];
			cell[0] = s.getOccupiedCells().get(i).getX();
			cell[1] = s.getOccupiedCells().get(i).getY();
			snakeCells.add(cell); 
		}
		return snakeCells;
	}

	public static boolean isDone() {
		return DONE;
	}

	public static void setDone(boolean done) {
		DONE = done;
	}

	public synchronized void notifyGame(){
		setChanged();
		notifyObservers();
	}
	
	public void setTargetCell(int[] pos, int snakeID) {
		for(Snake s : listOfSnakes){
			if(s.getID() == snakeID){
				s.setTargetCell(pos);
				s.setPursuitState(true);
				s.interrupt();
			}
		}
		removeFromListOfSelectedSnakes(snakeID);
	}

	public void addToListOfSelectedSnakes(int clickedSnakeID) {
		listOfSnakes.get(clickedSnakeID).setSelected(true);
	}
	

	public void removeFromListOfSelectedSnakes(int clickedSnakeID) {
		listOfSnakes.get(clickedSnakeID).setSelected(false);
	}
	
	public List<int[]> getSnakePos(int snakeID) {
		List<int[]> snakePos = new ArrayList<int[]>();
		for(Snake s : listOfSnakes) {
			if(s.getID() == snakeID){
				snakePos = convertSnakeCellsToArrayOfInt(s);
			}
		}
		return snakePos;
	}

	public void execute(){
		for(int i = 0; i < listOfSnakes.size(); i++){
			listOfSnakes.get(i).start();
			System.out.println("Snake " + listOfSnakes.get(i).getID() + " thread started.");
		}
		System.out.println("Game thread ended.");
	}
}
