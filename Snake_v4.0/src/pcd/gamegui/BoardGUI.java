package pcd.gamegui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

public class BoardGUI extends JComponent {
	private final int numberOfCells;
	private final int sizeOfBoard;
	private final int sizeOfCell;
	private final int snakeID;
	private Map<Integer, ArrayList<int[]>> selectedSnakesPos;
	private Map<Integer, ArrayList<int[]>> snakesPos;
	private List<int[]> jumpsPos;
	private List<int[]> listOfBarriersPos;
	private List<int[]> listOfTurboBoostsPos;
	private List<int[]> listOfFoodsPos;
	
	public BoardGUI(int numberOfCells, int sizeOfBoard, int snakeID){
		this.numberOfCells = numberOfCells;
		this.sizeOfBoard = sizeOfBoard;
		this.snakeID = snakeID;
		sizeOfCell = sizeOfBoard/numberOfCells;
		selectedSnakesPos = new HashMap<Integer, ArrayList<int[]>>();
		snakesPos = new HashMap<Integer, ArrayList<int[]>>();
		jumpsPos = new ArrayList<int[]>();
		listOfBarriersPos = new ArrayList<int[]>();
		listOfTurboBoostsPos = new ArrayList<int[]>();
		listOfFoodsPos = new ArrayList<int[]>();
		setBackground(Color.CYAN);
	}

	public int getSizeOfBoard() {
		return sizeOfBoard;
	}

	public int getNumberOfCells() {
		return numberOfCells;
	}
	
	public int getSizeOfCell() {
		return sizeOfCell;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int xi;
		int yi;
		int xf;
		int yf;
		
		/*
		 * fill snakes background
		 */
		for(Map.Entry<Integer, ArrayList<int[]>> entry : snakesPos.entrySet()){
			if(entry.getKey() == snakeID){
				g.setColor(Color.WHITE);
			} else
				g.setColor(Color.CYAN);
			for(int[] pos : entry.getValue()){
				xi = pos[0] * sizeOfCell;
				yi = pos[1] * sizeOfCell;
				g.fillRect(xi, yi, sizeOfCell, sizeOfCell);
			}
		}
		
		/*
		 * fill selected snakes background
		 */
		g.setColor(Color.RED);
		for(Map.Entry<Integer, ArrayList<int[]>> entry : selectedSnakesPos.entrySet()){
			for(int[] pos : entry.getValue()){
				xi = pos[0] * sizeOfCell;
				yi = pos[1] * sizeOfCell;
				g.fillRect(xi, yi, sizeOfCell, sizeOfCell);
			}
		}
		
		/*
		 * draw barriers
		 */
		g.setColor(Color.GRAY);
		for(int[] pos : listOfBarriersPos){
			xi = pos[0] * sizeOfCell;
			yi = pos[1] * sizeOfCell;
			g.fillRect(xi, yi, sizeOfCell, sizeOfCell);
		}
		
		/*
		 * draw jumps
		 */
		g.setColor(Color.BLUE);
		for(int[] pos : jumpsPos){
			xi = pos[0] * sizeOfCell;
			yi = pos[1] * sizeOfCell;
			g.fillRect(xi, yi, sizeOfCell, sizeOfCell);
		}
		
		/*
		 * draw turboboosts
		 */
		g.setColor(Color.GREEN);
		for(int[] pos : listOfTurboBoostsPos){
			xi = pos[0] * sizeOfCell;
			yi = pos[1] * sizeOfCell;
			g.fillRect(xi, yi, sizeOfCell, sizeOfCell);
		}
		
		/*
		 * draw foods
		 */
		g.setColor(Color.MAGENTA);
		for(int[] pos : listOfFoodsPos) {
			xi = pos[0] * sizeOfCell;
			yi = pos[1] * sizeOfCell;
			g.fillRect(xi, yi, sizeOfCell, sizeOfCell);
		}
		
		/*
		 * draw targetcell
		 */
//		if(targetCell != null){
//			g.setColor(Color.GREEN);
//			int xii = targetCell[0] * sizeOfCell;
//			int yii = targetCell[1] * sizeOfCell;
//			g.fillRect(xii, yii, sizeOfCell, sizeOfCell);
//		}
		
		/*
		 * draw grid
		 */
		g.setColor(Color.BLACK);
		for(int x = 0; x <= sizeOfBoard; x+=sizeOfCell){
			g.drawLine(x, 0, x, sizeOfBoard);
		}
		for(int y = 0; y<= sizeOfBoard; y+=sizeOfCell){
			g.drawLine(0, y, sizeOfBoard, y);
		}
		
		/*
		 * draw snake points
		 */
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(5));
		for(Map.Entry<Integer, ArrayList<int[]>> entry : snakesPos.entrySet()){
			for(int[] pos : entry.getValue()){
				xi = pos[0] * sizeOfCell + (sizeOfCell/2);
				yi = pos[1] * sizeOfCell + (sizeOfCell/2);
				xf = xi;
				yf = yi;
				g2.drawLine(xi, yi, xf, yf);
			}
		}
		for(Map.Entry<Integer, ArrayList<int[]>> entry : selectedSnakesPos.entrySet()){
			for(int[] pos : entry.getValue()){
				xi = pos[0] * sizeOfCell + (sizeOfCell/2);
				yi = pos[1] * sizeOfCell + (sizeOfCell/2);
				xf = xi;
				yf = yi;
				g2.drawLine(xi, yi, xf, yf);
			}
		}
	}
    
	public void updateUnselectedSnakesPos(Map<Integer, ArrayList<int[]>> listOfSnakes){
		snakesPos = listOfSnakes;
	}
	
	public void updateSelectedSnakesPos(Map<Integer, ArrayList<int[]>> listOfSelectedSnakes){
		selectedSnakesPos = listOfSelectedSnakes;
	}

	public void updateJumpsPos(List<int[]> listOfJumpsPos) {
		jumpsPos = listOfJumpsPos;
	}

	public void updateBarriersPos(List<int[]> listOfBarriersPos) {
		this.listOfBarriersPos = listOfBarriersPos;
	}

	public void updateTurboBoostsPos(List<int[]> listOfTurboBoostsPos) {
		this.listOfTurboBoostsPos = listOfTurboBoostsPos;
	}
	
	public void updateFoodsPos(List<int[]> listOfFoodsPos){
		this.listOfFoodsPos = listOfFoodsPos;
	}
}
