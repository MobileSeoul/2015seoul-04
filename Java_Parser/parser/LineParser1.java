package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.sound.sampled.Line;
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

public class LineParser1 {
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

	public LineParser1(HashSet<String> l){
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

				if (stationData[2].equals("마전(무정차)") || stationData[2].equals("탕정") || stationData[2].equals("풍기")) // 현재 지하철 노선도에 없는 역
					;
				else {
					String [] tempArr;
					String tempCode, tempStr;
					
					tempCode = stationData[1];
					tempArr = LocationParser.doParse(tempCode);
					
					if (stationData[4].charAt(0) == 'P') {
						if (stationData[2].equals("서동탄")) {
							tempStr = OnlyDigit.getOnlyDigit(stationData[4]);
							tempStr = "1571" + tempStr;
						} else {
							tempStr = OnlyDigit.getOnlyDigit(stationData[4]);
							tempStr = "141" + tempStr;
						}
					} else if (stationData[4].charAt(0) == 'K') { // 광명역 처리
						tempStr = OnlyDigit.getOnlyDigit(stationData[4]);
					} else
						tempStr = OnlyDigit.getOnlyDigit(stationData[4]);
					
					nowStation.setLatitude(Double.parseDouble(tempArr[34])); // 위도
					nowStation.setLongitude(Double.parseDouble(tempArr[35])); // 경도
					nowStation.setLineNum(stationData[3].charAt(0));
					nowStation.setStationName(stationData[2]);
					nowStation.setStationCode(Integer.parseInt(tempStr));
					listOfStation.add(nowStation);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		sortOflist(listOfStation);
		setPeripheral();
		setTransfer(subwaymap);
		return true;
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

	// 주변역의 대한  정보 입력
	public void setPeripheral() {
		for (int i = 0; i < listOfStation.size() - 1; i++) { // 서동탄역 제외(수동 입력)
			nowStation = listOfStation.get(i);

			if (Integer.toString(nowStation.getStationCode()).length() == 3) { // 길이가 3인 경우
				if (i == 0) { // 시작역
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
				} else if (0 < i && i < listOfStation.size() - 1) { // 중간역
					if (i != 61 && i != 62) { // 인천역과 광명역이 아닌 경우
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else if (i == 61) { // 인천역인 경우
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					} else if (i == 62) { // 광명역인 경우
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(65).getStationName(), 3)); // 광명역 -> 금천구청역 연결
						listOfStation.get(65).getMovableStations().add(new MovableStation(nowStation.getStationName(), 3)); // 금천구청역 -> 광명역 연결
					}
				} else if (i == listOfStation.size() - 1) { // 종착역
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
										nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1)	.getStationName(), 3));
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
				} else if ((i == listOfStation.size() - 1 && Integer.toString(nowStation.getStationCode()).substring(0, 3).equals(Integer.toString(listOfStation.get(k).getStationCode()).substring(0, 3)))) {
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					break;
				}
			}
		}
		listOfStation.get(78).getMovableStations().add(new MovableStation(listOfStation.get(97).getStationName(), 3)); // 병점역 -> 서동탄역 연결
		listOfStation.get(97).getMovableStations().add(new MovableStation(listOfStation.get(78).getStationName(), 3)); // 서동탄역역 -> 병점역 연결
	}

	// 환승역 처리
	public void setTransfer(SubwayMap subwaymap) {
		for (int i = 0; i < listOfStation.size(); i++) {
			nowStation = listOfStation.get(i);
			
			if (nowStation.getLineNum() == 'T') {
				nowStation.addLineData('1'); // 호선 정보 등록
				transferList.add(nowStation.getStationName());
				subwaymap.put(nowStation.getStationName(), nowStation);
			} else {
				if (i == 0) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // 해당역이 기존 subwaymap에 존재 O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('1'); // 호선 정보 등록
						
						if (tr.getStationName().equals("구로") || tr.getStationName().equals("금천구청") || tr.getStationName().equals("벙점")) { // 구로역, 금천구청역과 병점역, 같은 호선에서 갈림길 有
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else { // 해당역이 기존 subwaymap에 존재 X
						if (nowStation.getStationName().equals("구로") || nowStation.getStationName().equals("금천구청") || nowStation.getStationName().equals("벙점")) { // 구로역, 금천구청역과 병점역, 같은 호선에서 갈림길 有			
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('1'); // 호선 정보 등록
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				} else if (0 < i && i < listOfStation.size() - 1) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // 해당역이 기존 subwaymap에 존재 O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('1'); // 호선 정보 등록
						
						if (tr.getStationName().equals("구로") || tr.getStationName().equals("금천구청") || tr.getStationName().equals("벙점")) { // 구로역, 금천구청역과 병점역, 같은 호선에서 갈림길 有
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else { // 해당역이 기존 subwaymap에 존재 X
						if (nowStation.getStationName().equals("구로") || nowStation.getStationName().equals("금천구청") || nowStation.getStationName().equals("병점")) { // 구로역, 금천구청역과 병점역, 같은 호선에서 갈림길 有			
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('1'); // 호선 정보 등록
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				} else if (i == listOfStation.size() - 1) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // 해당역이 기존 subwaymap에 존재 O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('1'); // 호선 정보 등록
						
						if (tr.getStationName().equals("구로") || tr.getStationName().equals("금천구청") || tr.getStationName().equals("벙점")) { // 구로역, 금천구청역과 병점역, 같은 호선에서 갈림길 有
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					} else { // 해당역이 기존 subwaymap에 존재 X
						if (nowStation.getStationName().equals("구로") || nowStation.getStationName().equals("금천구청") || nowStation.getStationName().equals("벙점")) { // 구로역, 금천구청역과 병점역, 같은 호선에서 갈림길 有			
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('1'); // 호선 정보 등록
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
