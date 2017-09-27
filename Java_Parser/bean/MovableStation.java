package bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MovableStation implements Serializable{
	private static final long serialVersionUID = -2244716580794699174L;

	
	private ArrayList<String> stationName;
	private int cost;
	
	
	//서치부분에서 사용되는 생성자. 이미 찾아둔 최단거리를 이용하는 경우임.
	public MovableStation(ArrayList<String> last, int c) {
		stationName = (ArrayList<String>) last.clone();
		cost = c;
	}
	
	//파싱하는 부분에서만 사용하는 생성자
	public MovableStation(String name, int c) {
		stationName = new ArrayList<String>();
		stationName.add(name);
		cost = c;
	}
	
	public ArrayList<String> getStationList(){
		return stationName;
	}
	
	public String getStationName() {
		return stationName.get(stationName.size()-1);
	}
	public void setStationName(String stationName) {
		this.stationName.add(stationName);
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	
}
