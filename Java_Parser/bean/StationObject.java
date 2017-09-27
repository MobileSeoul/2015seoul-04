package bean;

import java.io.Serializable;
import java.util.ArrayList;

public class StationObject implements Serializable{
	private static final long serialVersionUID = 5053907361811735971L;
	
	private int stationCode;
	private char lineNum;
	private ArrayList<String> lineData;
	private String stationName;
	private ArrayList<MovableStation> movableStations;
	private double latitude;
	private double longitude;
	
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public StationObject() {
		movableStations = new ArrayList<MovableStation>();
		lineData = new ArrayList<String>();
	}

	public void addLineData(char c){
		lineData.add(String.valueOf(c));
	}
	
	public ArrayList<String> getLineData(){
		return lineData;
	}
	
	
	public int getStationCode() {
		return stationCode;
	}

	public void setStationCode(int stationCode) {
		this.stationCode = stationCode;
	}

	public char getLineNum() {
		return lineNum;
	}

	public void setLineNum(char lineNum) {
		this.lineNum = lineNum;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public ArrayList<MovableStation> getMovableStations() {
		return movableStations;
	}

}

