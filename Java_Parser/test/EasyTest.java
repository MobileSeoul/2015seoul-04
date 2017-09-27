package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import bean.Checker;
import bean.MovableStation;
import bean.ResultObject;
import bean.StationObject;
import map.SubwayMap;
import parser.LineParser;
import parser.LineParser1;
import parser.LineParser2;
import parser.LineParser5;
import parser.LineParser6;
import parser.LineParserK;

public class EasyTest {
	public static void main(String[] args) {
		
		
		HashSet<String> transferList = new HashSet<String>();
		SubwayMap n = new SubwayMap();
		
		LineParser lp = new LineParser(transferList);
		LineParser1 lp1 = new LineParser1(transferList);
		LineParser2 lp2 = new LineParser2(transferList);
		LineParser5 lp5 = new LineParser5(transferList);
		LineParser6 lp6 = new LineParser6(transferList);
		LineParserK lpK = new LineParserK(transferList); // �����߾Ӽ�
		
		lp1.doParse(n, '1'); // 1ȣ��
		lp2.doParse(n, '2'); // 2ȣ��
		lp5.doParse(n, '5'); // 5ȣ��
		lp6.doParse(n, '6'); // 6ȣ��
		lpK.doParse(n, 'K'); // �����߾�

		lp.doParse(n, '3'); // 3ȣ��
		lp.doParse(n, '4'); // 4ȣ��
		lp.doParse(n, '7'); // 7ȣ��
		lp.doParse(n, '8'); // 8ȣ��
		lp.doParse(n, '9'); // 9ȣ��
		
		lp.doParse(n, 'I'); // ��õ1ȣ��
		lp.doParse(n, 'Y'); // ���μ� SU(�����ڵ�)
		lp.doParse(n, 'A'); // ����ö��
		lp.doParse(n, 'B'); // �д缱
		lp.doParse(n, 'S'); // �źд缱
		lp.doParse(n, 'E'); // ��������
		lp.doParse(n, 'U'); // �����μ�

		n.transferStationMovableRefresh(transferList);//ȯ�¿��� ������ ����

		/*
		//getdata
		SubwayMap n = null;
        try {
    		FileInputStream fis = new FileInputStream("stationMap.data");
            ObjectInputStream ois = new ObjectInputStream(fis);
            n = (SubwayMap) ois.readObject();
			ois.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		long start = System.currentTimeMillis();
	
		/*
		try {
			
			try{
				FileOutputStream fos = new FileOutputStream("stationMap.data");
		        ObjectOutputStream oos = new ObjectOutputStream(fos);
		        oos.writeObject(n);
		 
		        fos.close();
				}catch(Exception e){
					e.printStackTrace();
				}
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			
		}
	*/
		n.MAXCHECKABLE = 50;
			ArrayList<String> a = new ArrayList<String>();
			a.add("�븲");
			a.add("�ѽŴ��Ա�(�̼�)");
			a.add("������");
			ResultObject ro = n.searchMidStation(a);
			
			System.out.println(ro.getMidStationName());
			
		long end = System.currentTimeMillis();
		System.out.println("���� �ð� : " + (end - start) / 1000.0);
		System.out.println("��ġ ����: " + n.get("��ġ").getLatitude());
		System.out.println("��ġ �浵: " + n.get("��ġ").getLongitude());
		
		System.out.println("������ ����: " + n.get("������").getLatitude());
		System.out.println("������ �浵: " + n.get("������").getLongitude());
	}
		
}
