package pcd.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerSideMessage extends Message {
	private Map<Integer, ArrayList<int[]>> unselectedSnakesPos;
	private Map<Integer, ArrayList<int[]>> selectedSnakesPos;
	private List<int[]> listOfJumpsPos;
	private List<int[]> listOfBarriersPos;
	private List<int[]> listOfTurboBoostsPos;
	private List<int[]> listOfFoodPos;
	
	public ServerSideMessage(Map<Integer, ArrayList<int[]>> unselectedSnakesPos, 
			Map<Integer, ArrayList<int[]>> selectedSnakesPos, List<int[]> listOfJumpsPos, List<int[]> listOfBarriersPos,
			List<int[]> listOfTurboBoostsPos, List<int[]> listOfFoodPos){
		this.unselectedSnakesPos = unselectedSnakesPos;
		this.selectedSnakesPos = selectedSnakesPos;
		this.listOfJumpsPos = listOfJumpsPos;
		this.listOfBarriersPos = listOfBarriersPos;
		this.listOfTurboBoostsPos = listOfTurboBoostsPos;
		this.listOfFoodPos = listOfFoodPos;
	}
	
	public List<int[]> getBarriersPos() {
		return listOfBarriersPos;
	}

	public Map<Integer, ArrayList<int[]>> getUnselectedSnakesPos() {
		return unselectedSnakesPos;
	}

	public Map<Integer, ArrayList<int[]>> getSelectedSnakesPos() {
		return selectedSnakesPos;
	}

	public List<int[]> getJumpsPos() {
		return listOfJumpsPos;
	}

	public List<int[]> getTurboBoostsPos() {
		return listOfTurboBoostsPos;
	}

	public List<int[]> getFoodPos() {
		return listOfFoodPos;
	}
}
