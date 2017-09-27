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

public class LineParserK {
	HashSet<String> transferList;
	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
			.newInstance();
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

	public LineParserK(HashSet<String> l){
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
				
				if (stationData[2].equals("신촌(경의중앙선)") || stationData[2].equals("서울(경의중앙선)")) {
					if (stationData[2].equals("신촌(경의중앙선)")) {
						nowStation.setLineNum(stationData[3].charAt(0));
						nowStation.setStationName(stationData[2]);
						nowStation.setStationCode(3360);
					}
					if (stationData[2].equals("서울(경의중앙선)")) {
						nowStation.setLineNum(stationData[3].charAt(0));
						nowStation.setStationName(stationData[2]);
						nowStation.setStationCode(3370);
					}
				} else {
					if (Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) > 312) { // 서강대역부터
						nowStation.setLineNum(stationData[3].charAt(0));
						nowStation.setStationName(stationData[2]);
						nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 0);
					} else {
						nowStation.setLineNum(stationData[3].charAt(0));
						nowStation.setStationName(stationData[2]);
						nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])));
					}
				}
				nowStation.setLatitude(Double.parseDouble(tempArr[34])); // 위도
				nowStation.setLongitude(Double.parseDouble(tempArr[35])); // 경도
				nowStation.addLineData(stationData[3].charAt(0)); // 호선 정보 입력
				listOfStation.add(nowStation);
			}
			
			sortOflist(listOfStation);
			setPeripheral();
			setTransfer(subwaymap);
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	// frcode순으로 정렬
	private void sortOflist(ArrayList<StationObject> listOfStation) {
		for (int i = 0; i < listOfStation.size() - 1; i++) {
			for (int j = i + 1; j < listOfStation.size(); j++) {
				if (listOfStation.get(i).getStationCode() > listOfStation
						.get(j).getStationCode()) {
					Collections.swap(listOfStation, i, j);
				}
			}
		}
	}

	// 해당역의 주변역 입력
	public void setPeripheral() {
		for (int i = 0; i < listOfStation.size() - 2; i++) { // 신촌역과 서�M역에 대한 정보를 따로 입력하기 위해
			nowStation = listOfStation.get(i);
			
			if (Integer.toString(nowStation.getStationCode()).length() == 3) {
				if (i == 0) { // 용산역
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(28).getStationName(), 3));    // 공덕역
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3)); // 이촌역
				} else if (0 < i && i < listOfStation.size() - 1) {
					if (i != 27 && i != 28 && i != 49) { // 용문역, 공덕역과 문산역이 아닌 경우
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else if (i == 27 || i == 49) { // 용문역이나 문산역인 경우
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					} else if (i == 28) { // 공덕역인 경우
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(0).getStationName(), 3));     // 용산역
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3)); // 서강대역
					}
				} else if (i == listOfStation.size() - 1) {					
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
				}
			} else if (Integer.toString(listOfStation.get(i).getStationCode()).length() == 4) {
				if (i == 0) {
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
				} else if (0 < i && i < listOfStation.size() - 1) {
					for (k = index; k < listOfStation.size(); k++) {
						StationObject temp;
						nowStation = listOfStation.get(i);
						index = i;
						temp = nowStation;

						if (Integer.toString(nowStation.getStationCode()).substring(0, 2).equals(Integer.toString(listOfStation.get(k).getStationCode()).substring(0, 2))) {
							if (flag == true) {
								nowStation = listOfStation.get(k);
								nowStation.setLineNum('T');

								nowStation.getMovableStations().add(new MovableStation(temp.getStationName(), 3));
								if (Integer.toString(nowStation.getStationCode()).length() != Integer.toString(temp.getStationCode()).length()) {
									temp.getMovableStations().add(new MovableStation(nowStation.getStationName(), 3));
									temp.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
								}

								k = index;
								flag = false;
								break;
							} else if (flag == false) {
								if ((Integer.toString(listOfStation.get(k).getStationCode()).length() == 4 && Integer.toString(nowStation.getStationCode()).substring(0, 2).equals(Integer.toString(listOfStation.get(k).getStationCode()).substring(0, 2)))) {
									if (Integer.toString(nowStation.getStationCode()).substring(0, 2).equals(Integer.toString(listOfStation.get(i + 1).getStationCode()).substring(0, 2))) {
										nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
										nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
										k = index;
										break;
									} else {
										nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
										index = 0;
										flag = true;
										break;
									}
								}
							}
						}
					}
				}
				else if ((i == listOfStation.size() - 1 && Integer.toString(nowStation.getStationCode()).substring(0, 2).equals(Integer.toString(listOfStation.get(k).getStationCode()).substring(0, 2)))) {
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					break;
				}
			}
		}
		listOfStation.get(31).getMovableStations().add(new MovableStation(listOfStation.get(50).getStationName(), 3)); // 가좌역 -> 신촌역 연결
		listOfStation.get(50).getMovableStations().add(new MovableStation(listOfStation.get(31).getStationName(), 3)); // 신촌역 -> 가좌역 연결
		
		listOfStation.get(50).getMovableStations().add(new MovableStation(listOfStation.get(51).getStationName(), 3)); // 신촌역 -> 서울역 연결
		listOfStation.get(51).getMovableStations().add(new MovableStation(listOfStation.get(50).getStationName(), 3)); // 서울역 -> 신촌역 연결
	}

	public void setTransfer(SubwayMap subwaymap) {
		for (int i = 0; i < listOfStation.size(); i++) {
			nowStation = listOfStation.get(i);

			if (nowStation.getLineNum() == 'T') {
				subwaymap.put(nowStation.getStationName(), nowStation);
			} else {
				if (i == 0) {
					if (subwaymap.get(nowStation.getStationName()) != null) {
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.setLineNum('T');
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else {
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				} else if (0 < i && i < listOfStation.size() - 1) {
					if (subwaymap.get(nowStation.getStationName()) != null) {
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.setLineNum('T');
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else {
						// Newly added subway station
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				} else if (i == listOfStation.size() - 1) {
					if (subwaymap.get(nowStation.getStationName()) != null) {
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.setLineNum('T');
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					} else {
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				}
			}
			
//			System.out.println(nowStation.getStationName());
//			for(int z=0;z<nowStation.getMovableStations().size();z++){
//				System.out.println(nowStation.getMovableStations().get(z).getStationList());
//			}
//			System.out.println();
		}
	}
}
