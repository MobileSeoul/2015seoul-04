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

				if (stationData[2].equals("����(������)") || stationData[2].equals("����") || stationData[2].equals("ǳ��")) // ���� ����ö �뼱���� ���� ��
					;
				else {
					String [] tempArr;
					String tempCode, tempStr;
					
					tempCode = stationData[1];
					tempArr = LocationParser.doParse(tempCode);
					
					if (stationData[4].charAt(0) == 'P') {
						if (stationData[2].equals("����ź")) {
							tempStr = OnlyDigit.getOnlyDigit(stationData[4]);
							tempStr = "1571" + tempStr;
						} else {
							tempStr = OnlyDigit.getOnlyDigit(stationData[4]);
							tempStr = "141" + tempStr;
						}
					} else if (stationData[4].charAt(0) == 'K') { // ���� ó��
						tempStr = OnlyDigit.getOnlyDigit(stationData[4]);
					} else
						tempStr = OnlyDigit.getOnlyDigit(stationData[4]);
					
					nowStation.setLatitude(Double.parseDouble(tempArr[34])); // ����
					nowStation.setLongitude(Double.parseDouble(tempArr[35])); // �浵
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

	// �ֺ����� ����  ���� �Է�
	public void setPeripheral() {
		for (int i = 0; i < listOfStation.size() - 1; i++) { // ����ź�� ����(���� �Է�)
			nowStation = listOfStation.get(i);

			if (Integer.toString(nowStation.getStationCode()).length() == 3) { // ���̰� 3�� ���
				if (i == 0) { // ���ۿ�
					nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
				} else if (0 < i && i < listOfStation.size() - 1) { // �߰���
					if (i != 61 && i != 62) { // ��õ���� ������ �ƴ� ���
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else if (i == 61) { // ��õ���� ���
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					} else if (i == 62) { // ������ ���
						nowStation.getMovableStations().add(new MovableStation(listOfStation.get(65).getStationName(), 3)); // ���� -> ��õ��û�� ����
						listOfStation.get(65).getMovableStations().add(new MovableStation(nowStation.getStationName(), 3)); // ��õ��û�� -> ���� ����
					}
				} else if (i == listOfStation.size() - 1) { // ������
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
		listOfStation.get(78).getMovableStations().add(new MovableStation(listOfStation.get(97).getStationName(), 3)); // ������ -> ����ź�� ����
		listOfStation.get(97).getMovableStations().add(new MovableStation(listOfStation.get(78).getStationName(), 3)); // ����ź���� -> ������ ����
	}

	// ȯ�¿� ó��
	public void setTransfer(SubwayMap subwaymap) {
		for (int i = 0; i < listOfStation.size(); i++) {
			nowStation = listOfStation.get(i);
			
			if (nowStation.getLineNum() == 'T') {
				nowStation.addLineData('1'); // ȣ�� ���� ���
				transferList.add(nowStation.getStationName());
				subwaymap.put(nowStation.getStationName(), nowStation);
			} else {
				if (i == 0) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // �ش翪�� ���� subwaymap�� ���� O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('1'); // ȣ�� ���� ���
						
						if (tr.getStationName().equals("����") || tr.getStationName().equals("��õ��û") || tr.getStationName().equals("����")) { // ���ο�, ��õ��û���� ������, ���� ȣ������ ������ ��
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else { // �ش翪�� ���� subwaymap�� ���� X
						if (nowStation.getStationName().equals("����") || nowStation.getStationName().equals("��õ��û") || nowStation.getStationName().equals("����")) { // ���ο�, ��õ��û���� ������, ���� ȣ������ ������ ��			
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('1'); // ȣ�� ���� ���
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				} else if (0 < i && i < listOfStation.size() - 1) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // �ش翪�� ���� subwaymap�� ���� O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('1'); // ȣ�� ���� ���
						
						if (tr.getStationName().equals("����") || tr.getStationName().equals("��õ��û") || tr.getStationName().equals("����")) { // ���ο�, ��õ��û���� ������, ���� ȣ������ ������ ��
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i + 1).getStationName(), 3));
					} else { // �ش翪�� ���� subwaymap�� ���� X
						if (nowStation.getStationName().equals("����") || nowStation.getStationName().equals("��õ��û") || nowStation.getStationName().equals("����")) { // ���ο�, ��õ��û���� ������, ���� ȣ������ ������ ��			
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('1'); // ȣ�� ���� ���
						subwaymap.put(nowStation.getStationName(), nowStation);
					}
				} else if (i == listOfStation.size() - 1) {
					if (subwaymap.get(nowStation.getStationName()) != null) { // �ش翪�� ���� subwaymap�� ���� O
						StationObject tr = subwaymap.get(nowStation.getStationName());
						tr.addLineData('1'); // ȣ�� ���� ���
						
						if (tr.getStationName().equals("����") || tr.getStationName().equals("��õ��û") || tr.getStationName().equals("����")) { // ���ο�, ��õ��û���� ������, ���� ȣ������ ������ ��
							tr.setLineNum('P');
							transferList.add(tr.getStationName());
						} else {
							tr.setLineNum('T');
							transferList.add(tr.getStationName());
						}
						tr.getMovableStations().add(new MovableStation(listOfStation.get(i - 1).getStationName(), 3));
					} else { // �ش翪�� ���� subwaymap�� ���� X
						if (nowStation.getStationName().equals("����") || nowStation.getStationName().equals("��õ��û") || nowStation.getStationName().equals("����")) { // ���ο�, ��õ��û���� ������, ���� ȣ������ ������ ��			
							nowStation.setLineNum('P');
							transferList.add(nowStation.getStationName());
						}
						nowStation.addLineData('1'); // ȣ�� ���� ���
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
