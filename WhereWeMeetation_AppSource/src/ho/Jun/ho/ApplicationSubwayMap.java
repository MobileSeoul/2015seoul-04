package ho.Jun.ho;

import java.util.ArrayList;

import android.app.Application;
import bean.ResultObject;
import map.SubwayMap;

public class ApplicationSubwayMap extends Application{
	private SubwayMap subwayMap = null;
	private boolean useNetwork = true;
	private int maxSize = 60;
	private ArrayList<String> stationNames;
	
	public ArrayList<String> getStationNames() {
		return stationNames;
	}

	public void setStationNames(ArrayList<String> stationNames) {
		this.stationNames = stationNames;
	}

	public boolean isUseNetwork() {
		return useNetwork;
	}

	public void setUseNetwork(boolean useNetwork) {
		this.useNetwork = useNetwork;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public SubwayMap getSubwayMap() {
		return subwayMap;
	}

	public void setSubwayMap(SubwayMap subwayMap) {
		this.subwayMap = subwayMap;
	}

}
