package bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Checker implements Serializable{
	private static final long serialVersionUID = 557446484744269655L;

	private ArrayList<String> lastStationList;
	private int cost;

	public Checker(ArrayList<String> ar){
		lastStationList = (ArrayList<String>)ar.clone();
	}
	
	public Checker(String start) {
		lastStationList = new ArrayList<String>();
		lastStationList.add(start);
		cost = 0;
	}

	
	public void addListtoLastStationList(ArrayList<String> arr){
			for(int i=0; i<arr.size(); i++){
				if( !lastStationList.contains(arr.get(i)) ){
					lastStationList.add(arr.get(i));
				}
			}
		
	}

	public ArrayList<String> getLastStationList() {
		return lastStationList;
	}
	
	
	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public Checker clone(){
		Checker newC = new Checker(this.getLastStationList());
		newC.setCost(this.getCost());
		return newC;
	}

}
