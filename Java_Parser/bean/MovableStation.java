package bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MovableStation implements Serializable{
	private static final long serialVersionUID = -2244716580794699174L;

	
	private ArrayList<String> stationName;
	private int cost;
	
	
	//��ġ�κп��� ���Ǵ� ������. �̹� ã�Ƶ� �ִܰŸ��� �̿��ϴ� �����.
	public MovableStation(ArrayList<String> last, int c) {
		stationName = (ArrayList<String>) last.clone();
		cost = c;
	}
	
	//�Ľ��ϴ� �κп����� ����ϴ� ������
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
