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

import bean.MovableStation;
import bean.StationObject;

public class LineParser {
	HashSet<String> transferList;
	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder documentBuilder;
	Document xml = null;
	XPath xpath = XPathFactory.newInstance().newXPath();
	
	String key = "4c63636d6670686a383758586a764a";
	String Url;
	String rowPath = "SearchSTNBySubwayLineService/row";
	ArrayList<StationObject> listOfStation = new ArrayList<StationObject>();
	
	public LineParser(HashSet<String> l){
		this.transferList = l;
	}
	
	public boolean doParse(SubwayMap subwaymap, char lineCode) {
		listOfStation.clear();
		
		if (lineCode == 'Y') // 수인선
			Url = "http://openapi.seoul.go.kr:8088/" + key + "/xml/SearchSTNBySubwayLineService/0/110/SU";
		else
			Url = "http://openapi.seoul.go.kr:8088/" + key + "/xml/SearchSTNBySubwayLineService/0/110/" + lineCode;
		
		try {
			if (documentBuilder == null)
				documentBuilder = documentBuilderFactory.newDocumentBuilder();
			xml = documentBuilder.parse(Url);

			NodeList cols = (NodeList) xpath.evaluate(rowPath, xml, XPathConstants.NODESET);
			
			for (int idx = 0; idx < cols.getLength(); idx++) {
				String[] stationData = cols.item(idx).getTextContent().split("\n");
				StationObject nowStation = null;

				String [] tempArr;
				String tempCode;
				
				tempCode = stationData[1];
				tempArr = LocationParser.doParse(tempCode);				
				
				if (lineCode == '3') { // 3호선인 경우
					if (subwaymap.containsKey(stationData[2])) { // 해당역이 이미 존재 O
						transferList.add(stationData[2]);
						if (stationData[2].equals("원흥")) {
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 9); // 원흥(309) -> 원흥(318)
						} else if (Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) >= 318 && stationData[2].equals("원흥") != true) { // 삼송역  fr_code : 318
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 1); // 삼송역부터  frcode 1씩 증가
							nowStation.setLineNum('T'); // 환승역으로 표기 후
						} else {
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4]))); // 재정렬해야하므로 frcode를  재설정
							nowStation.setLineNum('T'); // 환승역으로 표기 후
						}
					} else { // 해당역이 이미 존재 X
						nowStation = new StationObject();
						
						if (stationData[2].equals("원흥")) {
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 9); // 원당(317) -> 원흥(318)
						} else if (Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) >= 318 && stationData[2].equals("원흥") != true) { // 삼송역  fr_code : 318
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 1); // 삼송역부터  fr_code 1씩 증가
						} else {
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4]))); // 재정렬해야하므로 frcode를  재설정
						}
					}
				}

				else if (lineCode == 'A') { // 공항철도인 경우
					if (subwaymap.containsKey(stationData[2])) { // 해당역이 이미 존재 O
						if (stationData[2].equals("청라국제도시")) {
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(8);
							nowStation.setLineNum('T'); // 환승역으로 표기 후
						} else if (Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) >= 8 && stationData[2].equals("청라국제도시") != true) {
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 1); // 삼송역부터  frcode 1씩 증가
							nowStation.setLineNum('T'); // 환승역으로 표기 후
						} else {
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4]))); // 재정렬해야하므로 frcode를  재설정
							nowStation.setLineNum('T'); // 환승역으로 표기 후
						}
					} else {
						nowStation = new StationObject();
						
						if (stationData[2].equals("청라국제도시")) {
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(8);
						} else if (Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) >= 8 && stationData[2].equals("청라국제도시") != true) {
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 1); // 삼송역부터  frcode 1씩 증가
						} else {
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4]))); // 재정렬해야하므로 frcode를  재설정
						}
					}
				}
				
				else { // 3호선과 공항철도선이 아닌 호선
					String str = stationData[2];
					if(str.equals("노량진(9호선)"))
						str = "노량진";
					if (subwaymap.containsKey(str) ) { // 이미 그 역이 존재하면
						transferList.add(str);
						nowStation = subwaymap.get(str);
						nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4]))); // 재정렬해야하므로  frcode를  재설정
						nowStation.setLineNum('T'); // 환승역으로 표기 후
					} else {
						nowStation = new StationObject();
						nowStation.setLineNum(stationData[3].charAt(0));
						nowStation.setStationName(str);
						nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])));
					}
				}
				nowStation.setLatitude(Double.parseDouble(tempArr[34])); // 위도
				nowStation.setLongitude(Double.parseDouble(tempArr[35])); // 경도
				nowStation.addLineData(stationData[3].charAt(0));
				listOfStation.add(nowStation);
			}

			sortOflist(listOfStation);			
			setTransfer(subwaymap, listOfStation, lineCode);
			return true;

		} catch (Exception e) {
			return false;
		}
	}
	
	
	// frcode순으로 정렬
	private void sortOflist(ArrayList<StationObject> listOfStation) {
		for (int i = 0; i < listOfStation.size() - 1; i++) {
			for (int j = i + 1; j < listOfStation.size(); j++) {
				if (listOfStation.get(i).getStationCode() > listOfStation.get(j).getStationCode()) {
					Collections.swap(listOfStation, i, j);
				}
			}
		}
	}	
	
	
	private void setTransfer(SubwayMap subwaymap, ArrayList<StationObject> listOfStation, char lineCode) {
		for (int i = 0; i < listOfStation.size(); i++) {
			StationObject nowStation = listOfStation.get(i);
			int cost = 3;
			// nowStation과 listOfStation 비교해서 cost 계산해야 함
			// cost~~

			if (nowStation.getLineNum() == 'T') { // 환승역이면 계산된 cost에 +2해서 등록함
				cost += 2;
			} 
			///////////여기 else 삭제함
				if (i == 0) { // 시작점인경우
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), cost));
					subwaymap.put(nowStation.getStationName(), nowStation);
				} else if (i == listOfStation.size() - 1) { // 마지막점인경우
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), cost));
					subwaymap.put(nowStation.getStationName(), nowStation);
				} else { // 중간경우
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), cost));
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), cost));
					subwaymap.put(nowStation.getStationName(), nowStation);
				}
			//////////
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
