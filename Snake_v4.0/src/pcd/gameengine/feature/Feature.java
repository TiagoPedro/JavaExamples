package pcd.gameengine.feature;

import pcd.gameengine.Board;
import pcd.gameengine.Cell;

public class Feature {
	private Cell cell;
	private Board board;
	private FeatureType type;

	public Feature(int[] pos, Board board, FeatureType type) {
		this.board = board;
		cell = board.getCell(pos);
		this.type = type;
	}
	
	public Cell getCell() {
		return cell;
	}
	
	public FeatureType getType() {
		return type;
	}
}
