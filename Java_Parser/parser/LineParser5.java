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

public class LineParser5 {
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
	
	
	public LineParser5(HashSet<String> l){
		this.transferList = l;
	}
	
	
	public boolean doParse(SubwayMap subwaymap, char lineCode) {
		listOfStation.clear();
		
		Url = "http://openapi.seoul.go.kr:8088/" + key + "/xml/SearchSTNBySubwayLineService/0/110/" + lineCode;
		
		try {
			if(documentBuilder == null)
				documentBuilder = documentBuilderFactory.newDocumentBuilder();
			xml = documentBuilder.parse(Url);

			NodeList cols = (NodeList) xpath.evaluate(rowPath, xml,	XPathConstants.NODESET);
			for (int idx = 0; idx < cols.getLength(); idx++) {
				String[] stationData = cols.item(idx).getTextContent().split("\n");
			
				nowStation = new StationObject();
				
				String [] tempArr;
				String tempCode, tempStr;
				
				tempCode = stationData[1];
				tempArr = LocationParser.doParse(tempCode);
				
				if (stationData[4].charAt(0) == 'P') {
					tempStr = OnlyDigit.getOnlyDigit(stationData[4]);
					tempStr = "548" + tempStr;
				} else
					tempStr = OnlyDigit.getOnlyDigit(stationData[4]);
				
				nowStation.setLatitude(Double.parseDouble(tempArr[34])); // 위도
				nowStation.setLongitude(Double.parseDouble(tempArr[35])); // 경도
				nowStation.setLineNum(stationData[3].charAt(0));
				nowStation.setStationName(stationData[2]);
				nowStation.setStationCode(Integer.parseInt(tempStr));		
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

			if (Integer.toString(nowStation.getStationCode()).length() == 3) {
				if (i == 0) {
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
				} else if (0 < i && i < listOfStation.size() - 1) {
					if (i != 43) {
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else // 상일동역인 경우
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
				} else if (i == listOfStation.size() - 1) {
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
				}
			} else if (Integer.toString(listOfStation.get(i).getStationCode()).length() == 6) {
				if (i == 0) {
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
				} else if (0 < i && i < listOfStation.size() - 1) {
					for (k = index; k < listOfStation.size(); k++) {
						StationObject temp;
						nowStation = listOfStation.get(i);
						index = i;
						temp = nowStation;
						
						if (Integer.toString(nowStation.getStationCode()).substring(0, 3).equals(Integer.toString(listOfStation.get(k).getStationCode()).substring(0, 3))) {
							if (flag == true) {
								nowStation = listOfStation.get(k);
								
								nowStation.getMovableStations().add(new MovableStation(temp.getStationName(), 3));
								if (Integer.toString(nowStation.getStationCode()).length() != Integer.toString(temp.getStationCode()).length()) {
									temp.getMovableStations().add(new MovableStation(nowStation.getStationName(), 3));
									temp.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
								}
								
								k = index;
								flag = false;
								break;
							} else if (flag == false) {
								if ((Integer.toString(listOfStation.get(k).getStationCode()).length() == 6 && Integer.toString(nowStation.getStationCode()).substring(0, 3).equals(Integer.toString(listOfStation.get(k).getStationCode()).substring(0, 3)))) {
									
									if (Integer.toString(nowStation.getStationCode()).substring(0, 3).equals(Integer.toString(listOfStation.get(i + 1).getStationCode()).substring(0, 3))) {
										nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
										nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
										k = index;
										break;
									}
									else {
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
				
				else if ((i == listOfStation.size() - 1 && Integer.toString(nowStation.getStationCode()).substring(0, 3).equals(Integer.toString(listOfStation.get(k).getStationCode()).substring(0, 3)))) {
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					break;
				}
			}
		}
	}

	public void setTransfer(SubwayMap subwaymap) {
		for (int i = 0; i < listOfStation.size(); i++) {
			nowStation = listOfStation.get(i);

			if (nowStation.getLineNum() == 'T') {
				nowStation.addLineData('5'); // 호선 정보 등록
				transferList.add(nowStation.getStationName());
				subwaymap.put(nowStation.getStationName(), nowStation);
			} else {
				if (i == 0) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // 해당역이 기존 subwaymap에 존재 O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('5'); // 호선 정보 등록
						
						if (tr.getStationName().equals("강동")) { // 강동역, 같은 호선에서 갈림길 有
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else { // 해당역이 기존 subwaymap에 존재 X
						if (nowStation.getStationName().equals("강동")) { // 강동역, 같은 호선에서 갈림길 有
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('5'); // 호선 정보 등록
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				}
				else if (0 < i && i < listOfStation.size() - 1) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // 해당역이 기존 subwaymap에 존재 O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('5'); // 호선 정보 등록
						
						if (tr.getStationName().equals("강동")) { // 강동역, 같은 호선에서 갈림길 有
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else { // 해당역이 기존 subwaymap에 존재 X
						if (nowStation.getStationName().equals("강동")) { // 강동역, 같은 호선에서 갈림길 有
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('5'); // 호선 정보 등록
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				} 
				else if (i == listOfStation.size() - 1) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // 해당역이 기존 subwaymap에 존재 O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('5'); // 호선 정보 등록
						
						if (tr.getStationName().equals("강동")) {// 강동역, 같은 호선에서 갈림길 有
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					} else { // 해당역이 기존 subwaymap에 존재 X
						if (nowStation.getStationName().equals("강동")) { // 강동역, 같은 호선에서 갈림길 有
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('5'); // 호선 정보 등록
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
