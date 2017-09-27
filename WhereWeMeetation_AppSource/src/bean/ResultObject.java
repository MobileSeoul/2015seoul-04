package bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ResultObject implements Serializable, Cloneable {
	private String midStationName;
	private int maxCost;
	private ArrayList<Checker> details;
	
	public int getMaxCost() {
		return maxCost;
	}


	public void setMaxCost(int maxCost) {
		this.maxCost = maxCost;
	}


	public ResultObject() {
		details = new ArrayList<Checker>();
	}
	
	
	public String getMidStationName() {
		return midStationName;
	}
	public void setMidStationName(String midStationName) {
		this.midStationName = midStationName;
	}
	public ArrayList<Checker> getDetails() {
		return details;
	}


	public ResultObject Clone() {
		try {
			return (ResultObject) this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
