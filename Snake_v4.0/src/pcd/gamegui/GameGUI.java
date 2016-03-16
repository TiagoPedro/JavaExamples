package pcd.gamegui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import pcd.comm.client.ClientComm;

public class GameGUI implements Observer {
	private BoardGUI boardGui;
	private JFrame frame;
	private final int snakeID;
	private List<int[]> snakePos;
	private boolean isSelected;
	private ClientComm clientComm;
	
	public GameGUI(ClientComm clientComm){
		this.clientComm = clientComm;
		clientComm.getConstructionInfoFromServer();
		snakeID = clientComm.getConstructorMessage().getID();
		frame = new JFrame("Snakes - Player " + (snakeID + 1));
		configureFrame(clientComm.getConstructorMessage().getNumberOfCells());
	}
	
	public void configureFrame(int numberOfCells){
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(200, 200);
		boardGui = new BoardGUI(numberOfCells, frame.getWidth(), snakeID);
		frame.add(boardGui);
		boardGui.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			
			private boolean containsPosition(int[] pos){
				if(snakePos != null){
					for(int[] position : snakePos){
						if(position[0] == pos[0] && position[1] == pos[1])
							return true;
					}
				}
				return false;
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX() / boardGui.getSizeOfCell();
				int y = e.getY() / boardGui.getSizeOfCell();
				int[] pos = {x, y};
//				System.out.println("x: " + x + " y: " + y);

				if(!isSelected){
					if(containsPosition(pos)){
						clientComm.sendSelectedIdToServer(snakeID);
						isSelected = true;
						boardGui.updateUnselectedSnakesPos(clientComm.getUpdateMessage().getUnselectedSnakesPos());
						boardGui.updateSelectedSnakesPos(clientComm.getUpdateMessage().getSelectedSnakesPos());
						boardGui.repaint();
					} else
						System.out.println("Please click on the snake!");
				} else {
					if(containsPosition(pos)) {
						System.out.println("Don't click on the snake.");
					} else {
						isSelected = false;
						clientComm.sendTargetCellInfo(pos, snakeID);
						boardGui.updateUnselectedSnakesPos(clientComm.getUpdateMessage().getUnselectedSnakesPos());
						boardGui.updateSelectedSnakesPos(clientComm.getUpdateMessage().getSelectedSnakesPos());
						boardGui.repaint();
					}
				}
			}
		});
	}
	
	public int getSnakeID() {
		return snakeID;
	}

	public void execute(){
		frame.setVisible(true);
	}
	
	@Override
	public void update(Observable o, Object list) {
		if(o == clientComm) {
			for(Map.Entry<Integer, ArrayList<int[]>> entry : clientComm.getUpdateMessage().getSelectedSnakesPos().entrySet()){
				if(entry.getKey() == snakeID){
					snakePos = entry.getValue();
					break;
				}
			}
			for(Map.Entry<Integer, ArrayList<int[]>> entry : clientComm.getUpdateMessage().getUnselectedSnakesPos().entrySet()){
				if(entry.getKey() == snakeID){
					snakePos = entry.getValue();
					break;
				}
			}
			boardGui.updateBarriersPos(clientComm.getUpdateMessage().getBarriersPos());
			boardGui.updateUnselectedSnakesPos(clientComm.getUpdateMessage().getUnselectedSnakesPos());
			boardGui.updateSelectedSnakesPos(clientComm.getUpdateMessage().getSelectedSnakesPos());
			boardGui.updateJumpsPos(clientComm.getUpdateMessage().getJumpsPos());
			boardGui.updateTurboBoostsPos(clientComm.getUpdateMessage().getTurboBoostsPos());
			boardGui.updateFoodsPos(clientComm.getUpdateMessage().getFoodPos());
			boardGui.repaint();
		}
	}
}
