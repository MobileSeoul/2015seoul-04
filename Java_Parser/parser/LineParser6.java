package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import map.SubwayMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import parser.OnlyDigit;
import bean.MovableStation;
import bean.StationObject;

public class LineParser6 {
	HashSet<String> transferList;
	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder documentBuilder;
	Document xml = null;
	XPath xpath = XPathFactory.newInstance().newXPath();
	
	int k = 0, index = 0;
	boolean flag = true;
	
	String key = "4c63636d6670686a383758586a764a";
	String Url;
	String rowPath = "SearchSTNBySubwayLineService/row";
	StationObject nowStation;
	ArrayList<StationObject> listOfStation = new ArrayList<StationObject>();
	
	
	public LineParser6(HashSet<String> l){
		this.transferList = l;
	}
	
	public boolean doParse(SubwayMap subwaymap, char lineCode) {
		listOfStation.clear();
		Url = "http://openapi.seoul.go.kr:8088/" + key + "/xml/SearchSTNBySubwayLineService/0/110/" + lineCode;
		try {
			if (documentBuilder == null)
				documentBuilder = documentBuilderFactory.newDocumentBuilder();
			xml = documentBuilder.parse(Url);

			NodeList cols = (NodeList) xpath.evaluate(rowPath, xml, XPathConstants.NODESET);
			for (int idx = 0; idx < cols.getLength(); idx++) {
				String[] stationData = cols.item(idx).getTextContent().split("\n");
				nowStation = new StationObject();
				
				String [] tempArr;
				String tempCode;
				
				tempCode = stationData[1];
				tempArr = LocationParser.doParse(tempCode);
				
				nowStation.setLatitude(Double.parseDouble(tempArr[34])); // 위도
				nowStation.setLongitude(Double.parseDouble(tempArr[35])); // 경도
				nowStation.setLineNum(stationData[3].charAt(0));
				nowStation.setStationName(stationData[2]);
				nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])));
				listOfStation.add(nowStation);
			}
		} catch (Exception e) {
			return false;
		}
		sortOflist(listOfStation);
		setPeripheral();
		setTransfer(subwaymap);
		return true;
	}
	
	//frcode순으로 정렬
	private void sortOflist(ArrayList<StationObject> listOfStation) {
		for (int i = 0; i < listOfStation.size() - 1; i++) {
			for (int j = i + 1; j < listOfStation.size(); j++) {
				if (listOfStation.get(i).getStationCode() > listOfStation.get(j).getStationCode()) {
					Collections.swap(listOfStation, i, j);
				}
			}
		}
	}	
	
	public void setPeripheral() {
		for (int i = 0; i < listOfStation.size(); i++) {
			nowStation = listOfStation.get(i);
			
			if (i == 0) {
				nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3)); // 역촌역
				nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 5).getStationName(), 3)); // 구산역
				nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 6).getStationName(), 3)); // 새절역
			} else if (0 < i && i < listOfStation.size() - 1) {
				if (listOfStation.get(i).getStationName().equals("구산")) {
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(0).getStationName(), 3));
					
					listOfStation.get(0).getMovableStations().add(new MovableStation(listOfStation.get(i).getStationName(), 3));
				}
				else if (listOfStation.get(i).getStationName().equals("새절")) {
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(0).getStationName(), 3));
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					
					listOfStation.get(0).getMovableStations().add(new MovableStation(listOfStation.get(i).getStationName(), 3));
				}
				else {
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
				}
			} else if (i == listOfStation.size() - 1) {
				nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
			}
		}
	}
	
	// 7호선 순환선 (응암역 ~ 구산역) -> 모든 지하철역을 하나로의 선으로 전부 연결 
	public void setTransfer(SubwayMap subwaymap) {
		for (int i = 0; i < listOfStation.size(); i++) {
			nowStation = listOfStation.get(i);

			if (nowStation.getLineNum() == 'T') {
				nowStation.addLineData('6'); // 호선 정보 등록
				transferList.add(nowStation.getStationName());
				subwaymap.put(nowStation.getStationName(), nowStation);
			} else {
				if (i == 0) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // 해당역이 기존 subwaymap에 존재 O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('6'); // 호선 정보 등록
						
						if (tr.getStationName().equals("응암")) { // 응암역 같은 호선에서 갈림길 有
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else { // 해당역이 기존 subwaymap에 존재 X
						if (nowStation.getStationName().equals("응암")) { // 응암역 같은 호선에서 갈림길 有
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('6'); // 호선 정보 등록
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				}
				else if (0 < i && i < listOfStation.size() - 1) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // 해당역이 기존 subwaymap에 존재 O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('6'); // 호선 정보 등록
						
						if (tr.getStationName().equals("응암")) { // 응암역 같은 호선에서 갈림길 有
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else { // 해당역이 기존 subwaymap에 존재 X
						if (nowStation.getStationName().equals("응암")) { // 응암역 같은 호선에서 갈림길 有
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('6'); // 호선 정보 등록
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				} 
				else if (i == listOfStation.size() - 1) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // 해당역이 기존 subwaymap에 존재 O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('6'); // 호선 정보 등록
						
						if (tr.getStationName().equals("응암")) { // 응암역 같은 호선에서 갈림길 有
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					} else { // 해당역이 기존 subwaymap에 존재 X
						if (nowStation.getStationName().equals("응암")) { // 응암역 같은 호선에서 갈림길 有
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('6'); // 호선 정보 등록
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				}
			}		
			
//			for (int j = 0; j < subwaymap.size(); j++)
//				System.out.println(j + " " + subwaymap.get(listOfStation.get(j).getStationName()).getStationName()
//						 + " " + subwaymap.get(listOfStation.get(j).getStationName()).getLineNum());
			
//			System.out.println(nowStation.getStationName());
//			for(int z=0;z<nowStation.getMovableStations().size();z++){
//				System.out.println(nowStation.getMovableStations().get(z).getStationList());
//			}
//			System.out.println();
			
		}
		
//		for (int j = 0; j < subwaymap.size(); j++) {
//			try {
//				System.out.println(j + " " + subwaymap.get(listOfStation.get(j).getStationName()).getStationName()
//						+ " " + subwaymap.get(listOfStation.get(j).getStationName()).getLineNum());
//			} catch (Exception e) {
//
//			}
//		}
	}
}
