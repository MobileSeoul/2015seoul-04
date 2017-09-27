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
		
		if (lineCode == 'Y') // ���μ�
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
				
				if (lineCode == '3') { // 3ȣ���� ���
					if (subwaymap.containsKey(stationData[2])) { // �ش翪�� �̹� ���� O
						transferList.add(stationData[2]);
						if (stationData[2].equals("����")) {
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 9); // ����(309) -> ����(318)
						} else if (Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) >= 318 && stationData[2].equals("����") != true) { // ��ۿ�  fr_code : 318
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 1); // ��ۿ�����  frcode 1�� ����
							nowStation.setLineNum('T'); // ȯ�¿����� ǥ�� ��
						} else {
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4]))); // �������ؾ��ϹǷ� frcode��  �缳��
							nowStation.setLineNum('T'); // ȯ�¿����� ǥ�� ��
						}
					} else { // �ش翪�� �̹� ���� X
						nowStation = new StationObject();
						
						if (stationData[2].equals("����")) {
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 9); // ����(317) -> ����(318)
						} else if (Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) >= 318 && stationData[2].equals("����") != true) { // ��ۿ�  fr_code : 318
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 1); // ��ۿ�����  fr_code 1�� ����
						} else {
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4]))); // �������ؾ��ϹǷ� frcode��  �缳��
						}
					}
				}

				else if (lineCode == 'A') { // ����ö���� ���
					if (subwaymap.containsKey(stationData[2])) { // �ش翪�� �̹� ���� O
						if (stationData[2].equals("û��������")) {
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(8);
							nowStation.setLineNum('T'); // ȯ�¿����� ǥ�� ��
						} else if (Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) >= 8 && stationData[2].equals("û��������") != true) {
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 1); // ��ۿ�����  frcode 1�� ����
							nowStation.setLineNum('T'); // ȯ�¿����� ǥ�� ��
						} else {
							nowStation = subwaymap.get(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4]))); // �������ؾ��ϹǷ� frcode��  �缳��
							nowStation.setLineNum('T'); // ȯ�¿����� ǥ�� ��
						}
					} else {
						nowStation = new StationObject();
						
						if (stationData[2].equals("û��������")) {
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(8);
						} else if (Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) >= 8 && stationData[2].equals("û��������") != true) {
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])) + 1); // ��ۿ�����  frcode 1�� ����
						} else {
							nowStation.setLineNum(stationData[3].charAt(0));
							nowStation.setStationName(stationData[2]);
							nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4]))); // �������ؾ��ϹǷ� frcode��  �缳��
						}
					}
				}
				
				else { // 3ȣ���� ����ö������ �ƴ� ȣ��
					String str = stationData[2];
					if(str.equals("�뷮��(9ȣ��)"))
						str = "�뷮��";
					if (subwaymap.containsKey(str) ) { // �̹� �� ���� �����ϸ�
						transferList.add(str);
						nowStation = subwaymap.get(str);
						nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4]))); // �������ؾ��ϹǷ�  frcode��  �缳��
						nowStation.setLineNum('T'); // ȯ�¿����� ǥ�� ��
					} else {
						nowStation = new StationObject();
						nowStation.setLineNum(stationData[3].charAt(0));
						nowStation.setStationName(str);
						nowStation.setStationCode(Integer.parseInt(OnlyDigit.getOnlyDigit(stationData[4])));
					}
				}
				nowStation.setLatitude(Double.parseDouble(tempArr[34])); // ����
				nowStation.setLongitude(Double.parseDouble(tempArr[35])); // �浵
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
	
	
	// frcode������ ����
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
			// nowStation�� listOfStation ���ؼ� cost ����ؾ� ��
			// cost~~

			if (nowStation.getLineNum() == 'T') { // ȯ�¿��̸� ���� cost�� +2�ؼ� �����
				cost += 2;
			} 
			///////////���� else ������
				if (i == 0) { // �������ΰ��
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), cost));
					subwaymap.put(nowStation.getStationName(), nowStation);
				} else if (i == listOfStation.size() - 1) { // ���������ΰ��
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), cost));
					subwaymap.put(nowStation.getStationName(), nowStation);
				} else { // �߰����
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
