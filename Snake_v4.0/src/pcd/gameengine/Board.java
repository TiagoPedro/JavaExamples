package pcd.gameengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pcd.gameengine.feature.Feature;
import pcd.gameengine.feature.FeatureType;


public class Board {
	private Cell[][] gridOfCells;
	private List<Feature> listOfFeatures;
	private int activeNormalSnakes;
	private boolean turboBoostPresent;

	public Board(int numberOfCells, int numberOfSnakes, int numberOfFeatures){
		activeNormalSnakes = 0;
		turboBoostPresent = false;
		
		gridOfCells = new Cell[numberOfCells][numberOfCells];
		for(int x = 0; x < numberOfCells; x++){
			for(int y = 0; y < numberOfCells; y++){
				gridOfCells[x][y] = new Cell(x, y);
			}
		}
		
		listOfFeatures = new ArrayList<Feature>();
		for(int i = 0; i < numberOfFeatures; i++){
			int[] randomPos;
			do{
				randomPos = createRandomPosition();
			} while(!getCell(randomPos).getEmptyState());
			int r = new Random().nextInt(4);
			listOfFeatures.add(new Feature(randomPos, this, FeatureType.values()[r]));
			if(listOfFeatures.get(i).getType().equals(FeatureType.BARRIER)){
				listOfFeatures.get(i).getCell().setEmpty(false);
			}
			System.out.println(listOfFeatures.get(i).getType().toString() + " x: " + listOfFeatures.get(i).getCell().getX() + " y: " + listOfFeatures.get(i).getCell().getY());
		}
	}
	
	/*
	 * Turbo-boost (reader-writer problem) implementation
	 */
	
	/**
	 * If this condition is satisfied, the requesting TurboBoosted Snake can access
	 * the board.
	 * @return true If there are no active normal snakes or TurboBoosted snakes
	 * accessing the board.
	 */
	public boolean turboBoostCondition() {
		return activeNormalSnakes == 0 && !turboBoostPresent;
	}
	
	/**
	 * If this condition is satisfied, the requesting active normal snake can access
	 * the board.
	 * @return true If there are no TurboBoosted snakes accessing the board.
	 */
	public boolean normalCondition() {
		return !turboBoostPresent;
	}

	/**
	 * The requesting snake waits while the normal condition isn't satisfied. When
	 * the condition is satisfied, the number of active normal snakes is incremented,
	 * the method ends and the snake can request the next cell.
	 */
	public synchronized void startNormalRequest() {
		while(!normalCondition()){
			try{
				wait();
			} catch(InterruptedException e) {
				System.out.println("Interrupted while waiting for normal request.");
			}
		}
		++activeNormalSnakes;
	}
	
	/**
	 * The requesting snake has its next cell and leaves the board access, decrementing
	 * the number of active normal snakes and notifying any waiting threads.
	 */
	public synchronized void stopNormalRequest() {
		--activeNormalSnakes;
		notifyAll();
	}
	
	/**
	 * The requesting TurboBoosted snake waits while there any other TurboBoosted or
	 * active normal snakes accessing the board until it gains exclusive access.
	 */
	public synchronized void startTurboBoostRequest() {
		while(!turboBoostCondition()){
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("Interrupted while waiting for TurboBoost request.");
			}
		}
		turboBoostPresent = true;
	}
	
	/**
	 * 
	 */
	public synchronized void stopTurboBoostRequest() {
		turboBoostPresent = false;
		notifyAll();
	}
	
	/**
	 * Checks if the given cell has a TurboBoost feature.
	 * @param nextCellInfo X and Y coordinates on board.
	 * @return true If the input cell has a TurboBoost feature.
	 */
	public synchronized boolean hasTurboBoost(int[] nextCellInfo) {
		for(Feature f : listOfFeatures){
			if(f.getType() == FeatureType.TURBO_BOOST && f.getCell().getX() == nextCellInfo[0] && f.getCell().getY() == nextCellInfo[1]){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * A snake consumes a TurboBoost feature and it is removed from the list of
	 * features and from the board.
	 * @param pos X and Y coordinates on board.
	 */
	public synchronized void consumeTurboBoostFeature(int[] pos){
		for(Feature f : listOfFeatures){
			if(f.getCell().getX() == pos[0] && f.getCell().getY() == pos[1]){
				if(f.getType().equals(FeatureType.TURBO_BOOST)){
					listOfFeatures.remove(f);
					System.out.println("TurboBoost consumed");
					break;
				}
			}
		}
	}

	/*
	 * Turbo-boost implementation end
	 */
	
	/**
	 * Checks if the given cell has a Food feature.
	 * @param nextCellInfo X and Y coordinates on board.
	 * @return true If the input cell has a TurboBoost feature.
	 */
	public synchronized boolean hasFood(int[] nextCellInfo) {
		for(Feature f : listOfFeatures){
			if(f.getType() == FeatureType.FOOD && f.getCell().getX() == nextCellInfo[0] && f.getCell().getY() == nextCellInfo[1]){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * A snake consumes a Food feature and that is removed from the list of
	 * features and from the board.
	 * @param pos X and Y coordinates on board.
	 */
	public synchronized void consumeFoodFeature(int[] pos){
		for(Feature f : listOfFeatures){
			if(f.getCell().getX() == pos[0] && f.getCell().getY() == pos[1]){
				if(f.getType().equals(FeatureType.FOOD)){
					listOfFeatures.remove(f);
					System.out.println("Food consumed");
					break;
				}
			}
		}
	}

	public int getLength(){
		return gridOfCells.length;
	}

	public Cell getCell(int[] cellInfo){
		try{
			return gridOfCells[cellInfo[0]][cellInfo[1]];
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Tried to get cell with x = " + cellInfo[0] + " and y = " + cellInfo[1] + " that is out of bounds.");
		}
		return null;
	}

	public synchronized boolean hasJumpFeature(int[] nextCellInfo) {
		for(Feature f : listOfFeatures){
			if(f.getType() == FeatureType.JUMP && f.getCell().getX() == nextCellInfo[0] && f.getCell().getY() == nextCellInfo[1]){
				return true;
			}
		}
		return false;
	}
	
	public synchronized void consumeJumpFeature(int[] pos){
		for(Feature f : listOfFeatures){
			if(f.getCell().getX() == pos[0] && f.getCell().getY() == pos[1]){
				if(f.getType().equals(FeatureType.JUMP)){
					listOfFeatures.remove(f);
					System.out.println("Jump consumed");
					break;
				}
			}
		}
	}
	
	public List<Feature> getListOfBarriers() {
		List<Feature> listOfBarriers = new ArrayList<Feature>();
		for(Feature f : listOfFeatures){
			if(f.getType().equals(FeatureType.BARRIER)){
				listOfBarriers.add(f);
			}
		}
		return listOfBarriers;
	}
	
	public List<Feature> getListOfJumps(){
		List<Feature> listOfJumps = new ArrayList<Feature>();
		for(Feature f : listOfFeatures){
			if(f.getType().equals(FeatureType.JUMP)){
				listOfJumps.add(f);
			}
		}
		return listOfJumps;
	}
	
	public List<Feature> getListOfTurboBoosts(){
		List<Feature> listOfTurboBoosts = new ArrayList<Feature>();
		for(Feature f : listOfFeatures){
			if(f.getType().equals(FeatureType.TURBO_BOOST)){
				listOfTurboBoosts.add(f);
			}
		}
		return listOfTurboBoosts;
	}
	
	public List<Feature> getListOfFoods(){
		List<Feature> listOfFoods = new ArrayList<Feature>();
		for(Feature f : listOfFeatures){
			if(f.getType().equals(FeatureType.FOOD)){
				listOfFoods.add(f);
			}
		}
		return listOfFoods;
	}
	
	private int[] createRandomPosition(){
		int[] pos = new int[2];
		Random r = new Random();
		pos[0] = r.nextInt(getLength());
		pos[1] = r.nextInt(getLength());
		return pos;
	}
}
